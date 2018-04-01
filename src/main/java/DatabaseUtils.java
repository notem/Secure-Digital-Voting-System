import java.security.KeyPair;
import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseUtils
{
    // form a database connection every time a function in this class is called
    // terribly inefficient, however I don't know a technique to establish a persistent connection
    private static Connection connection = null;
    static
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Unable to load PostgreSQL JDBC Driver! "
                    + "The driver needs to be included in your library path!");
            e.printStackTrace();
        }

        try
        {
            String uri  = System.getenv("dbURI");
            String user = System.getenv("dbUser");
            String pass = System.getenv("dbPassword");
            if (uri!=null && user!=null && pass!=null)
                connection = DriverManager.getConnection(uri, user, pass);
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
    }

    /**
     * creates a new voter
     * @param fname  registrant's first name
     * @param lname  registrant's last name
     * @param pubKey unique public key to associate with the registration name
     * @return true if registration was successful
     */
    public static Boolean registerVoter(String pubKey, String fname, String lname)
    {
        if (connection == null) return false;
        String rst; PreparedStatement pst;
        try
        {   // create voter table if not exists
            rst = "CREATE TABLE IF NOT EXISTS voters (" +
                    "fname varchar (40) NOT NULL, " +
                    "lname varchar (40) NOT NULL, " +
                    "key varchar (344) PRIMARY KEY, " +
                    "UNIQUE (lname,fname)" +
                  ");";
            connection.prepareStatement(rst).executeUpdate();

            // insert new voter record into the table
            rst = "INSERT INTO voters VALUES(?, ?, ?);";
            pst = connection.prepareStatement(rst);
            pst.setString(1, fname);
            pst.setString(2, lname);
            pst.setString(3, pubKey);
            return 1 == pst.executeUpdate(); // return true if one entry was update
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * retrieves list of currently registered voters
     * @return sorted list of voter names
     */
    public static List<String> getVoters()
    {
        String st; ResultSet res;
        List<String> list = new LinkedList<String>();
        if (connection == null) return list;
        try
        {
            st  = "SELECT * FROM voters;";
            res = connection.prepareStatement(st).executeQuery();
            while(res.next())
            {
                String name = res.getString("fname") + " " + res.getString("lname");
                list.add(name);
            }
            Collections.sort(list);
            return list;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return list;
        }
    }

    public static List<String> getElections()
    {
    	String st; ResultSet res;
    	List<String> list = new LinkedList<String>();
    	if (connection == null) return list;
    	try{
    		st	= "SELECT * FROM elections";
    		res = connection.prepareStatement(st).executeQuery();
    		while(res.next()){
    			String election = res.getString("election_name") + " | " + 
    					res.getString("start_date") + " | " + res.getString("duration");
    			list.add(election);
    		}
    		Collections.sort(list);
    		return list;
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		return list;
    	}
    }
    
    /**
     * retrieves the list of all registered public keys
     * @return sorted list of registered 2048-bit RSA keys (base64url encoded)
     */
    public static List<String> getPublicKeys()
    {
        String st; ResultSet res;
        List<String> list = new LinkedList<String>();
        if (connection == null) return list;
        try
        {
            st  = "SELECT * FROM voters;";
            res = connection.prepareStatement(st).executeQuery();
            while(res.next())
            {
                list.add(res.getString("key"));
            }
            Collections.sort(list); // sort list so as to hide ownership
            return list;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return list;
        }
    }

    /**
     * creates a new table to hold an elections blockchain
     * adds an entry for that election blockchain to the elections table (for easier referencing)
     * inserts the genesis block (contains blockchain encryption key)
     * @param publicKey the encryption key (RSA-4096) for ballots urlbase64 encoded
     * @return true is election creation was successful
     */
    public static Boolean initializeElectionBlockchain(String publicKey)
    {
        if (connection == null) return false;
        String rst; PreparedStatement pst;
        try
        {
            // create the new election block chain
            rst = "CREATE TABLE e"+publicKey.substring(0, 32)+" (" +
                    "_id BIGSERIAL PRIMARY KEY, " +            // arbitrary, unique ID
                    "block_no BIGINT NOT NULL, " +             // block number
                    "block_content VARCHAR(8192) NOT NULL, " +  // contents of the block OR election key -> urlbase64 encoded
                    "timestamp BIGINT NOT NULL," +                 // epoch time
                    "current_hash VARCHAR(43) NOT NULL" +      // hash(content||prev_hash||time) -> urlbase64 encoded
                    ");";
            connection.prepareStatement(rst).executeUpdate();

            // insert the genesis block into the table
            rst = "INSERT INTO e"+publicKey.substring(0, 32)+" VALUES(NULL, ?, ?, '', ?);";
            pst = connection.prepareStatement(rst);
            pst.setInt(1, 0);
            pst.setString(2, publicKey);
            pst.setString(3, "[signature]");   // TODO create function to generate hash and sign with public key

            // update block number in the elections table
            rst = "UPDATE elections SET block_count=? WHERE public_key=?;";	// TODO way to update block count w/o modifying others?
            pst = connection.prepareStatement(rst);
            pst.setInt(0, 1);
            pst.setString(1, publicKey);

            return 1 == pst.executeUpdate(); // return true if the entry was created
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds records to the Elections and PrivateKeys tables to create a new election.
     * Handles creating the key pair for the election.
     * @param electionName Identifier for the election
     * @param startDate Date on which the genesis block for the election should be created
     * @param duration Time between genesis and terminus for the election
     * @return True if the election is successfully created
     */
    public static Boolean createElection(String electionName, String startDate, String duration)
    {
    	if(connection == null) return false;
    	String rst; PreparedStatement pst;
    	KeyPair electionKeys; String pk, sk;
    	try{
    		// create an elections table to hold meta-information
            // on existing election block chains
            rst = "CREATE TABLE IF NOT EXISTS elections (" +
                    "public_key VARCHAR(8192) PRIMARY KEY," + // public key -> urlbase64 encoded
                    "block_count BIGINT NOT NULL," +     	// next block number
                    "election_name VARCHAR(128)," +			// readable identifier, TODO unique?
                    "start_date VARCHAR(10)," +				// YYYY-MM-DD
                    "duration VARCHAR(20)" +				// "1 day", etc
                    // other useful information to keep handy?
                    ");";
            connection.prepareStatement(rst).executeUpdate();
            
            // create a separate table to store private keys
            rst = "CREATE TABLE IF NOT EXISTS private_keys (" +
            		"public_key VARCHAR(32) PRIMARY KEY," +	// public key -> urlbase64 encoded -> [0:32]
            		"private_key VARCHAR(8192)" +			// private key -> urlbase64 encoded
            		");";	//TODO base64 encoding probably increases string length
            connection.prepareStatement(rst).executeUpdate();
            
            // generate a key pair for the election
            electionKeys = CryptoUtils.generateKeys();
            pk = CryptoUtils.exportKey(electionKeys.getPublic());
            sk = CryptoUtils.exportKey(electionKeys.getPrivate());
            
            //TEST
            System.out.println("Election: " + electionName + "\nPK: " + pk);
            
            // store record for the Elections table
            rst = "INSERT INTO elections VALUES (?, ?, ?, ?, ?)";
            pst = connection.prepareStatement(rst);
            pst.setString(1, pk);
            pst.setInt(2, 0);
            pst.setString(3, electionName);
            pst.setString(4, startDate);	//TODO possibly convert to Date obj
            pst.setString(5, duration);
            pst.executeUpdate();
            
            // store record for the PrivateKeys table
            rst = "INSERT INTO private_keys VALUES (?, ?)";
            pst = connection.prepareStatement(rst);
            pst.setString(1, pk.substring(0, 32));
            pst.setString(2, sk);
            pst.executeUpdate();
            
            return true;
    	}
    	catch(SQLException e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Processes a valid ballot and adds it to an election's blockchain
     * @param ballot Base 64 encoded encrypted ballot
     * @param electionKey	Primary key to identify the election
     * @return True if the block was successfully added
     */
    public static boolean addToBlockchain(String ballot, String electionKey)
    {
    	if (connection == null) return false;
    	
    	try
    	{
    		//TODO
    		return true;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Reads through an election blockchain and returns a formatted list of blocks.
     * @param electionKey Public key to identify an election
     * @return List of blocks, each represented as a String
     */
    public static List<String> viewBlockchain(String electionKey)
    {
    	if (connection == null) return null;
    	
    	try
    	{
    		//TODO
    		return null;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * Creates the terminus block for an election, releasing the private key.
     * @param publicKey Public key to identify an election
     * @return True if terminus block is successfully added
     */
    public static boolean terminateBlockChain(String publicKey)
    {
    	if (connection == null) return false;
    	
    	try
    	{
    		//TODO
    		return true;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }
    
    /**
     * Reads through a terminated election to determine its results.
     * @param electionKey Public key to identify an election
     * @return Map of each candidate to their tallied vote count.
     */
    public static Map<String, Integer> evaluateBlockchain(String electionKey)
    {
    	if (connection == null) return null;
    	
    	try
    	{
    		//TODO
    		return null;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return null;
    	}
    }
}
