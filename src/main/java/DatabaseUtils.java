import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
            // create an elections table to hold meta-information
            // on existing election block chains
            rst = "CREATE TABLE IF NOT EXISTS elections (" +
                    "public_key VARCHAR(683) PRIMARY KEY," +
                    "block_count BIGINT NOT NULL" +     // next block number
                    // TODO other useful information to keep handy?
                    ");";
            connection.prepareStatement(rst).executeUpdate();

            // create the new election block chain
            rst = "CREATE TABLE e"+publicKey+" (" +
                    "_id BIGSERIAL PRIMARY KEY, " +            // arbitrary, unique ID
                    "block_no BIGINT NOT NULL, " +             // block number
                    "block_content VARCHAR(638) NOT NULL, " +  // contents of the block -> urlbase64 encoded
                    "_time BIGINT NOT NULL," +                 // epoch time
                    "current_hash VARCHAR(43) NOT NULL" +      // hash(content||prev_hash||time) -> urlbase64 encoded
                    ");";
            connection.prepareStatement(rst).executeUpdate();

            // insert the genesis block into the table
            rst = "INSERT INTO e"+publicKey+" VALUES(NULL, ?, ?, '', ?);";
            pst = connection.prepareStatement(rst);
            pst.setInt(1, 0);
            pst.setString(2, publicKey);
            pst.setString(3, "[signature]");   // TODO create function to generate hash and sign with public key

            // insert an entry into the table of elections
            rst = "INSERT INTO elections VALUES(?,?);";
            pst = connection.prepareStatement(rst);
            pst.setString(1, publicKey);
            pst.setInt(2, 1);

            return 1 == pst.executeUpdate(); // return true if the entry was created
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
