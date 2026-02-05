/**
 * Copied from LiteConfig (MIT license)
 */

package me.av306.keybindsgaloreplus.configmanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import me.av306.keybindsgaloreplus.KeybindsGalorePlus;


/**
 * Configuration manager. Handles reading/saving config file, and setting fields in confugurable class.
 */
public class ConfigManager
{
    private final Path configFileDirectory; /** Directory containing the config file */
    private final String configFileName; /** Name of the config file */
    private final Class<?> configurableClass; /** The Class object holding the config fields */

    /** A File object representing the config file, guaranteed to exist after checkConfigFile() is run */
    private File configFile;

    /**
     * True if there were errors when reading the config file.
     */
    public boolean errorFlag = false;
    
    /**
     * Constructor for a config manager that tries to find a default config file in the JAR resources section.
     * This constructor calls checkConfigFileExists() and readConfigFile().
     *
     * @param name: Name of the application, used in logging statements
     * @param configFilePath: Path to the config file
     * @param configFileName: Name of the config file (with extension, e.g. "app_config.properties") (this will be used both to name the newly created one, and to find the embedded default one)
     * @param configurableClass: {java.lang.Class} object that holds the configurable fields (use NameOfClass.class or classInstance.getClass())
     * @param configurableClassInstance: Instance of the previous configurable object, if instance fields are used. Pass NULL here if static fields are used
     */
    public ConfigManager(
        Path configFilePath, String configFileName,
        Class<?> configurableClass
    ) throws IOException
    {
        this.configFileDirectory = configFilePath;
        this.configFileName = configFileName;
        this.configurableClass = configurableClass;

        if ( !this.createConfigFileIfNeeded() )
            this.readConfigFile();
    }

    /**
     * Check for the existence of a config file, creating a new one if needed
     * @return true if a new config file was created, false otherwise
     */
    public final boolean createConfigFileIfNeeded() throws IOException
    {
        // TODO: I'm not too sure about how to handle closing all the streams, any help from more experienced devs would be much appreciated
        // https://stackoverflow.com/questions/38698182/close-java-8-stream about closing streams?
        // or https://stackoverflow.com/questions/76815547/if-an-ioexception-occurs-while-invoking-close-is-the-stream-closed-anyway

        this.configFile = this.configFileDirectory.resolve( this.configFileName ).toFile();

        if ( !this.configFile.exists() )
        {
            // No config file exists, create one
            
            // Create the file
            try (
                BufferedWriter writer = new BufferedWriter( new FileWriter( this.configFile ) )
            )
            {
                // Write the top-level comments, if any
                if ( this.configurableClass.isAnnotationPresent( ConfigComment.class ) )
                {
                    ConfigComment[] topLevelComments = this.configurableClass.getAnnotationsByType( ConfigComment.class );
                    for ( ConfigComment c : topLevelComments )
                    {
                        writer.write( "# " + c.value() + System.lineSeparator() );
                    }
                }

                // For each line in the config file, retrieve the field and
                // write its default value and comment (if any)
                for ( var field : this.configurableClass.getDeclaredFields() )
                {
                    // Ignore fields with the IgnoreConfig annotation
                    if ( field.isAnnotationPresent( IgnoreConfig.class ) ) continue;

                    if ( field.isAnnotationPresent( ConfigComment.class ) )
                    {
                        ConfigComment[] comments = field.getAnnotationsByType( ConfigComment.class );
                        for ( ConfigComment c : comments )
                            writer.write( "# " + c.value() + System.lineSeparator() );
                    }

                    // Write the default value
                    Class<?> fieldTypeClass = field.getType();
                    
                    try
                    {
                        if ( fieldTypeClass.isAssignableFrom( ArrayList.class ) )
                        {
                            // var arrayListTypeParams = fieldTypeClass.getTypeParameters();

                            // // Should only have 1 type param
                            // if ( arrayListTypeParams.length != 1 )
                            // {
                            //     KeybindsGalorePlus.LOGGER.warn(
                            //             "(KBG+ Config Manager) Arraylist config {} has {} type arguments ({}); expected 1. Skipping.",
                            //             field.getName(),
                            //             arrayListTypeParams.length,
                            //             java.util.Arrays.toString( arrayListTypeParams )
                            //      );
                            //      continue;
                            // }

                            // var arrayListType = arrayListTypeParams[0];

                            @SuppressWarnings( "unchecked" )
                            ArrayList<Integer> array = (ArrayList<Integer>) field.get( null );
                            StringBuilder arrayString = new StringBuilder( "[" );
                            for ( int i = 0; i < array.size(); i++ )
                            {
                                arrayString.append( array.get( i ) );
                                if ( i < array.size() - 1 ) arrayString.append( ", " );
                            }
                            arrayString.append( "]" );
                            
                            writer.write( String.format(
                                "%s=%s%s",
                                field.getName().toUpperCase(), 
                                arrayString.toString(),
                                System.lineSeparator()
                            ) );
                        }
                        else
                        {
                            // Non-array type
                            writer.write( String.format(
                                "%s=%s%s",
                                field.getName().toUpperCase(), 
                                formatFieldValue( field ),
                                System.lineSeparator()
                            ) );
                        }
                    }
                    catch ( IllegalAccessException illegal )
                    {
                        KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) Could not access field {} while creating config file", field.getName() );
                        illegal.printStackTrace();
                    }
                }
            }
            catch ( IOException ioe )
            {
                KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) IOException while creating config file: {}", ioe.getMessage() );
                throw ioe;
            }

            KeybindsGalorePlus.LOGGER.info( "(KBG+ Config Manager) Created new config file at {}", this.configFile.getAbsolutePath() );
            return true;
        }
        else
        {
            KeybindsGalorePlus.LOGGER.info( "(KBG+ Config Manager) Config file already exists!" );
            return false;
        }
    }

    /**
     * Read configs from the config file. Sets hasCustomData if invalid config statements were read.
     * <br>
     * NOTE: entries in the config file MUST match field names EXACTLY (case-insensitive)
     */
    public final void readConfigFile() throws IOException
    {
        // Reset error flag
        this.errorFlag = false;

        try ( BufferedReader reader = new BufferedReader( new FileReader( this.configFile ) ) )
        {
            // Iterate over each line in the file
            for ( String line : reader.lines().toArray( String[]::new ) )
            {
                // Skip comments and blank lines
                if ( line.trim().startsWith( "#" ) || line.isBlank() ) continue;
                
                // Split it by the equals sign (.properties format)
                String[] entry = line.split( "=" );

                try
                {
                    // Trim lines so you can have spaces around the equals ("prop = val" as opposed to "prop=val")
                    entry[0] = entry[0].trim();
                    entry[1] = entry[1].trim();

                    // Set fields in configurable class
                    Field f = this.configurableClass.getDeclaredField( entry[0].toUpperCase( Locale.getDefault() ) );
                    Class<?> fieldTypeClass = f.getType();
                    
                    //System.out.println( f.getType().getName() );
                    if ( fieldTypeClass.isAssignableFrom( short.class ) )
                    {
                        // Short value (0x??)
                        f.setShort( null, Short.parseShort(
                                entry[1].replace( "0x", "" ),
                                16 )
                        );
                    }
                    else if ( fieldTypeClass.isAssignableFrom( int.class ) )
                    {
                        // Integer value
                        if ( entry[1].startsWith( "0x" ) )
                        {
                            // Hex literal
                            Integer.parseInt(
                                    entry[1].replace( "0x", "" ),
                                    16
                            );
                        }
                        else f.setInt(
                            null,
                            Integer.parseInt( entry[1] )
                        );
                    }
                    else if ( fieldTypeClass.isAssignableFrom( float.class ) )
                    {
                        f.setFloat(
                            null,
                            Float.parseFloat( entry[1] )
                        );
                    }
                    else if ( fieldTypeClass.isAssignableFrom( boolean.class ) )
                    {
                        f.setBoolean(
                            null,
                            Boolean.parseBoolean( entry[1] )
                        );
                    }
                    else if ( fieldTypeClass.isAssignableFrom( ArrayList.class ) )
                    {
                        // I HATE TYPE ERASURE GRRR
                        // Fck this i'm kicking the can down the road
                        // only supports int lists
                        // FIXME: someone help me with the stupid type thing

                        // Remove opening square brackets and commas
                        ArrayList<Integer> list = new ArrayList<>();
                                
                        for ( String e : entry[1].replaceAll( "[\\[\\]\\s]+", "" ).split( "," ) )
                            list.add( Integer.parseInt( e ) );
                        
                        //list.forEach( e -> KeybindsGalorePlus.LOGGER.info( "{}",e ) );

                        f.set( null, list );

                        //String typeParamName = ((Class<?>) ((ParameterizedType) fieldTypeClass.getGenericSuperclass()).getActualTypeArguments()[0]).getName();
                        //KeybindsGalorePlus.LOGGER.info( "FOund ArrayList of type {}", typeParamName );
                        /*switch( typeParamName )
                        {
                            case "java.lang.Integer" ->
                            {
                                // Remove opening square brackets and commas
                                ArrayList<Integer> list = new ArrayList<>();
                                
                                for ( String e : entry[1].replaceAll( "[],", "" ).split( " " ) )
                                    list.add( Integer.parseInt( e ) );
                                
                                KeybindsGalorePlus.LOGGER.info( list.toString() );

                                f.set( null, list );
                            }

                            case "java.lang.String" ->
                            {
                                f.set(
                                    null,
                                    new ArrayList<>( Arrays.asList( entry[1].replaceAll( "[],", "" ).split( " " ) ) )
                                );
                            }

                            default ->
                            {
                                KeybindsGalorePlus.LOGGER.error( "Unsupported array type {} for config entry {}", typeParamName, line );
                            }
                        }*/
                    }
                    else
                    {
                        KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) Unrecognised data type for config entry {}", line );
                    }
                }
                catch ( NoSuchFieldException nsfe )
                {
                    KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) No matching field found for config entry: {}", entry[0] );
                    this.errorFlag = true;
                }
                catch ( IllegalAccessException illegal )
                {
                    KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) Could not set field involved in: {}", line );
                    this.errorFlag = true;
                    illegal.printStackTrace();
                }
                catch ( /*ArrayIndexOutOfBoundsException | NumberFormatException*/ Exception e )
                {
                    KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) Malformed config entry: {}", line );
                    this.errorFlag = true;
                }

                //System.out.printf( "Set config %s to %s%n", entry[0], entry[1] );
            }
        }
        catch ( IOException ioe )
        {
            KeybindsGalorePlus.LOGGER.error( "(KBG+ Config Manager) IOException while reading config file: {}", ioe.getMessage() );
            throw ioe;
        }

        KeybindsGalorePlus.LOGGER.info( "(KBG+ Config Manager) Finished reading config file!" );
    }

    public void printAllConfigs()
    {
        KeybindsGalorePlus.LOGGER.info( "(KBG+ Config Manager) Dumping configs:" );
        for ( var f : this.configurableClass.getDeclaredFields() )
        {
            try { KeybindsGalorePlus.LOGGER.info( "\t{}: {}", f.getName(), f.get( null ) ); }
            catch ( IllegalAccessException | NullPointerException ignored ) {}
        }
    }


    private static String formatFieldValue( Field field ) throws IllegalAccessException
    {
        Class<?> fieldTypeClass = field.getType();
        if ( fieldTypeClass.isAssignableFrom( short.class ) )
        {
            return String.format( "0x%02X", field.getShort( null ) );
        }
        else if ( fieldTypeClass.isAssignableFrom( int.class ) )
        {
            return String.format( "%d", field.getInt( null ) );
        }
        else if ( fieldTypeClass.isAssignableFrom( float.class ) )
        {
            return String.format( "%f", field.getFloat( null ) );
        }
        else if ( fieldTypeClass.isAssignableFrom( boolean.class ) )
        {
            return String.format( "%b", field.getBoolean( null ) );
        }
        else return field.get( null ).toString();
    }
}
