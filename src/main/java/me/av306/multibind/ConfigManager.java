package me.av306.multibind;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;
import java.lang.reflect.Field;

public class ConfigManager
{
    private final String configFilePath;
    
    public ConfigManager( String configFilePath )
    {
        this.configFilePath = configFilePath;
        this.readConfigFile();
    }

    private void readConfigFile()
    {
        try ( BufferedReader reader = new BufferedReader( new FileReader( this.configFilePath ) ) )
        {
            // Iterate over each line in the file
            for ( String line : reader.lines().toArray( String[]::new ) )
            {
                // Skip comments
                if ( line.startsWith( "#" ) ) continue;
                
                // Split it by the equals sign (.properties format)
                String[] entry = line.split( "=" );

                try
                {
                    Field f = KeybindSelectorScreen.class.getDeclaredField( entry[0].toUpperCase( Locale.ENGLISH ) );
                    
                    if ( Short.class.isAssignableFrom( f.getClass() ) )
                    {
                        // Short value
                        f.setShort( null, Short.parseShort(
                                entry[1].replace( "0x", "" ),
                                16 )
                        );
                    }
                    else if ( Integer.class.isAssignableFrom( f.getClass() ) )
                    {
                        // Integer value
                        f.setInt(
                            null, 
                            Integer.parseInt( entry[1] )
                        );
                    }
                    else if ( Float.class.isAssignableFrom( f.getClass() ) )
                    {
                        f.setFloat(
                            null,
                            Float.parseFloat( entry[1] )
                        );
                    }
                }
                catch ( ArrayIndexOutOfBoundsException oobe )
                {
                    MultiBind.LOGGER.warn( "Malformed config entry: {}", line );
                }
                catch ( NoSuchFieldException nsfe )
                {
                    MultiBind.LOGGER.warn( "No matching field found for config entry: {}", line );
                }
                catch ( IllegalAccessException illegal )
                {
                    MultiBind.LOGGER.error( "Could not set field involved in: {}", line );
                    illegal.printStackTrace();
                }
            }
        }
        catch ( IOException ioe )
        {
            MultiBind.LOGGER.error( "IOException while reading config file: {}", ioe.getMessage() );
        }
    }
}