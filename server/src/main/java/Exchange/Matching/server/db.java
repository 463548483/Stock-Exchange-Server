package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class db {
    Connection connection;

    public db() {
        this.connection = buildDBConnection();
    }

    /*
     * Build connection to DB
     * 
     * @return Connection
     */
    public Connection buildDBConnection() {
        String url = "jdbc:postgresql://localhost:5432/stockDB";
        String username = "postgres";
        String password = "zjwyy";
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to DataBase.");
            return connection;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void deleteTables() throws SQLException{
        Statement st = connection.createStatement();
        String sql_delete = "DROP TABLE IF EXISTS POSITION, ACCOUNT, ORDER_OPEN;";
        st.executeUpdate(sql_delete);
        st.close();
    }


    /*
     * Build Tables
     * 
     * @throws SQLException
     */
    public void buildTables() throws SQLException {
        Statement st = connection.createStatement();

        String sql_sym = "CREATE TABLE SYM(" +
                "SYM_ID SERIAL," +
                "SYM VARCHAR);";

        String sql_position = "CREATE TABLE POSITION(" +
                "POSITION_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYM VARCHAR," +
                "AMOUNT INT," +
                // "CONSTRAINT SYM_FK FOREIGN KEY (POSITION_ID) REFERENCES POSITION(POSITION_ID) ON DELETE SET NULL ON UPDATE CASCADE," +
                "CONSTRAINT POSITION_FK FOREIGN KEY (POSITION_ID) REFERENCES POSITION(POSITION_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        String sql_account = "CREATE TABLE ACCOUNT(" + "ACCOUNT_ID SERIAL PRIMARY KEY," + "BALANCE INT);";

        //String sql_order = "CREATE TABLE ORDER_OPEN(" + "ACCOUNT_ID SERIAL PRIMARY KEY," + "BALANCE INT);";
        String sql_order = "CREATE TABLE ORDER_OPEN(" + 
                           "ORDER_ID SERIAL PRIMARY KEY," + 
                           "ACCOUNT_ID INT," + 
                           "SYM VARCHAR," + 
                           "AMOUNT INT," + 
                           "BOUND INT," +
                           // "CONSTRAINT SYM_FK FOREIGN KEY (POSITION_ID) REFERENCES POSITION(POSITION_ID) ON DELETE SET NULL ON UPDATE CASCADE," +
                           "CONSTRAINT ACCOUNT_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";
        
        st.executeUpdate(sql_account);
        st.executeUpdate(sql_position);
        st.executeUpdate(sql_order);
        st.close();
    }

    /*
     * Close connection to DB
     * 
     * @throws SQLException
     */
    public void closeConnection() throws SQLException {
        connection.close();
    }
}
