package me.av306.keybindsgaloreplus.customdata;

import me.av306.keybindsgaloreplus.KeybindsGalorePlus;

import java.io.*;
import java.nio.file.Path;
import java.util.Hashtable;

public class DataManager
{
    private final File dataFile;

    public final Hashtable<String, KeybindData> customData = new Hashtable<>();

    public DataManager( Path dataFilePath, String dataFileName )
    {
        this.dataFile = dataFilePath.resolve( dataFileName ).toFile();

        if ( !this.dataFile.exists() )
        {
            KeybindsGalorePlus.LOGGER.warn( "No custom keybind data file found!" );
            return;
        }

        this.readDataFile();
    }

    public void readDataFile()
    {
        try ( BufferedReader fileReader = new BufferedReader( new FileReader( this.dataFile ) ); )
        {
            String line;
            String currentKeybind = null;
            while ( true )
            {
                line = fileReader.readLine();

                if ( line == null ) break;
                // Skip blank lines
                if ( line.isBlank() ) continue;

                // Indented line -- is property
                // Python moment
                if ( !line.endsWith( ":" ) )
                {
                    String[] lines = line.trim().split( "=" );
                    try
                    {
                        switch ( lines[0] )
                        {
                            case "custom_name" -> this.customData.get( currentKeybind ).setDisplayName( lines[1] );
                            default -> KeybindsGalorePlus.LOGGER.info( "Unknown custom data field: {}", lines[0] );
                        }

                        KeybindsGalorePlus.LOGGER.info( "Set property {} of {} to {}", lines[0], currentKeybind, lines[1] );
                    }
                    catch ( ArrayIndexOutOfBoundsException oobe )
                    {
                        KeybindsGalorePlus.LOGGER.warn( "Skipped invalid data line: {}", line );
                    }
                }
                else
                {
                    // Non-indented line -- is header
                    currentKeybind = line.replace( ":", "" ).trim();
                    this.customData.put( currentKeybind, new KeybindData() );
                    KeybindsGalorePlus.LOGGER.info( "Reading custom data for keybind: {}", currentKeybind );
                }
            }
        }
        catch ( IOException ioe )
        {
            KeybindsGalorePlus.LOGGER.error( "IOException while reading custom data: {}", ioe.getMessage() );
            ioe.printStackTrace();
        }
    }
}
