import java.util.List;

public class TestMain
{
    public static void main(String[] argv)
    {
        /* load config information from TOML file */
        ConfigurationLoader.Config config = (new ConfigurationLoader()).config;

        /* connect to the database */
        DatabaseDriver dbDriver = new DatabaseDriver();
        if (!dbDriver.connect(config.database_uri,
                config.database_user, config.database_pass))
        {
            System.exit(-1);
        }

        Long chainID = 1L;
        Boolean res;

        /* create new table test */
        System.out.print("Creating new 'blockchain' table: ");
        res = dbDriver.createBlockChainTable(chainID);
        System.out.println(res);

        /* add dummy entries to blockchain table */
        System.out.print("Adding block 'abc' to table:\t");
        res = dbDriver.addBlock(chainID, "abc");
        System.out.println(res);
        System.out.print("Adding block 'def' to table:\t");
        dbDriver.addBlock(chainID, "def");
        System.out.println(res);
        System.out.print("Adding block 'asdf' to table:\t");
        dbDriver.addBlock(chainID, "asdf");
        System.out.println(res);

        /* read the 'blockchain' */
        System.out.print("Reading blockchain:\t");
        List<String> blocks = dbDriver.readBlockChain(chainID);
        System.out.println("{ " + String.join(", ", blocks) + " } ");

        /* delete the blockchain table */
        System.out.print("Deleting blockchain table:\t");
        res = dbDriver.deleteChain(chainID);
        System.out.println(res);
    }
}
