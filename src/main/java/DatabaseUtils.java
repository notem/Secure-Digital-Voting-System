import java.sql.*;
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

        if (connection == null)
            System.exit(-1);
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
        String rst; PreparedStatement pst;
        try
        {   // create voter table if not exists
            rst = "CREATE TABLE IF NOT EXISTS voters " +
                    "(key varchar (100) PRIMARY KEY, " +
                        "fname varchar (40) NOT NULL, " +
                        "lname varchar (40), " +
                        "UNIQUE (lname,fname));";
            connection.prepareStatement(rst).executeUpdate();

            // insert new voter record into the table
            rst = "INSERT INTO voters VALUES(?, ?, ?);";
            pst = connection.prepareStatement(rst);
            pst.setString(1, pubKey);
            pst.setString(2, fname);
            pst.setString(3, lname);
            return 1 == pst.executeUpdate(); // return true if one entry was update
        }
        catch (SQLException e)
        {   // bleh
            return false;
        }
    }

    /**
     * retrieves list of currently registered voters
     * @return list of voter names
     */
    public static List<String> getVoters()
    {
        String st; ResultSet res;
        List<String> list = new LinkedList<String>();
        try
        {
            st  = "SELECT * FROM voters;";
            res = connection.prepareStatement(st).executeQuery();
            while(res.next())
            {
                String name = res.getString("fname") + " " + res.getString("lname");
                list.add(name);
            }
            return list;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return list;
        }
    }
}
