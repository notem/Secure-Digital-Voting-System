import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseDriver
{
    private Connection connection;
    public DatabaseDriver()
    {
        System.out.println("-------- PostgreSQL JDBC ------------\n" +
                "Loading class module...");
        try
        {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Unable to load PostgreSQL JDBC Driver! "
                    + "The driver needs to be included in your library path!");
            e.printStackTrace();
            return;
        }
        System.out.println("PostgreSQL JDBC Driver Registered!");
    }

    /**
     * connects to a PostgreSQL database instance
     * @param uri   database address
     * @param user  username
     * @param pass  password
     * @return      true if connection was successful, otherwise false
     */
    public Boolean connect(String uri, String user, String pass)
    {
        try
        {
            this.connection = DriverManager.getConnection(uri, user, pass);
        }
        catch (SQLException e)
        {
            System.out.println("Connection Failed!");
            e.printStackTrace();
            return false;
        }
        return (this.connection != null);
    }

    public Boolean createBlockChainTable(Long id)
    {
        String statement = "CREATE TABLE IF NOT EXISTS e" + id.toString() +
                " (block_num bigserial PRIMARY KEY, block_content varchar (100) NOT NULL);";
        try
        {
            PreparedStatement st = connection.prepareStatement(statement);
            return 1 == st.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addBlock(Long id, String block)
    {
        String statement = "INSERT INTO e" + id.toString() + " VALUES (DEFAULT, ?);";
        try
        {
            PreparedStatement st = connection.prepareStatement(statement);
            st.setString(1, block);
            return 1 == st.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> readBlockChain(Long id)
    {
        String statement = "SELECT * FROM e" + id.toString() + ";";
        try
        {
            PreparedStatement st = connection.prepareStatement(statement);
            ResultSet rs = st.executeQuery();

            List<String> list = new LinkedList<String>();
            while(rs.next())
            {
                list.add(rs.getString("block_content"));
            }
            return list;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return new LinkedList<String>();
        }
    }

    public Boolean deleteChain(Long id)
    {
        try
        {
            PreparedStatement st = connection.prepareStatement("DROP TABLE e" + id.toString() + ";");
            return 1 == st.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
