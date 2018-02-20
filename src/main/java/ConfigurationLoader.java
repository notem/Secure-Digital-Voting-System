import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.*;

public class ConfigurationLoader
{
    public class Config
    {
        public String database_uri;
        public String database_user;
        public String database_pass;
        public Config()
        {
            database_uri  = "jdbc:postgresql://localhost:5432/mydb";
            database_user = "username";
            database_pass = "password";
        }
    }

    public Config config;
    public ConfigurationLoader()
    {
        System.out.println("-------- Configuration Loader ------------\n" +
                "Attempting to read configuration file...");

        InputStream input = null;
        /* try to open the configuration file */
        try
        {
            input = new FileInputStream("./config.toml");
            config = (new Toml()).read(input).to(Config.class);
            System.out.println("Configuration file has been loaded.");
        }
        /* if unable to open file, try to create a new config file */
        catch (IOException ex)
        {
            this.generate();
            config = new Config();
            System.out.println("Unable to read file. A new config file has been created.");
        }
        /* close the file if open */
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /** create a new TOML configuration file in the local directory */
    private void generate()
    {
        OutputStream output = null;
        try
        {
            output = new FileOutputStream("config.toml");
            (new TomlWriter()).write(new Config(), output);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
