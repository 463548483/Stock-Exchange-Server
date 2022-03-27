package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class db {
    private Connection connection;

    public db() throws SQLException {
        this.connection = buildDBConnection();
        deleteTables();
        buildTables();
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

    /*
     * Delete Tables in DB
     * 
     */
    private void deleteTables() throws SQLException {
        Statement st = connection.createStatement();
        String sql_delete = "DROP TABLE IF EXISTS SYM, POSITION, ACCOUNT, ORDER_ALL;";
        st.executeUpdate(sql_delete);
        st.close();
    }

    /*
     * Build Tables
     * 
     * @throws SQLException
     */
    private void buildTables() throws SQLException {
        Statement st = connection.createStatement();

        String sql_sym = "CREATE TABLE SYM(" +
                "SYM_ID SERIAL PRIMARY KEY," +
                "SYM VARCHAR);";

        String sql_position = "CREATE TABLE POSITION(" +
                "POSITION_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYM VARCHAR," +
                "AMOUNT INT," +
                // "CONSTRAINT SYM_FK FOREIGN KEY (POSITION_ID) REFERENCES POSITION(POSITION_ID)
                // ON DELETE SET NULL ON UPDATE CASCADE," +
                "CONSTRAINT POSITION_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        String sql_account = "CREATE TABLE ACCOUNT(" + "ACCOUNT_ID INT PRIMARY KEY," + "BALANCE INT);";

        String sql_order = "CREATE TABLE ORDER_ALL(" +
                "ORDER_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYM VARCHAR," +
                "AMOUNT INT," +
                "BOUND INT," +
                // "CONSTRAINT SYM_FK FOREIGN KEY (POSITION_ID) REFERENCES POSITION(POSITION_ID)
                // ON DELETE SET NULL ON UPDATE CASCADE," +
                "STATUS VARCHAR," + 
                "CONSTRAINT ACCOUNT_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        st.executeUpdate(sql_sym);
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
    private void closeConnection() throws SQLException {
        connection.close();
    }

    /*
     * Insert Date into DB according to Object type
     */
    public void insertData(Object obj) throws SQLException {
        // if(obj instanceof Sym){
        //     Sym temp = (Sym) obj;
        //     Statement st = connection.createStatement();
        //     String sql = "INSERT INTO SYM (VARCHAR) VALUES(" + temp.getSym() + ");";
        //     st.executeUpdate(sql);
        //     st.close();
        // }
        if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement();
            String sql = "INSERT INTO ACCOUNT (ACCOUNT_ID, BALANCE) VALUES(" + temp.getID() + ", " + temp.getBalance()
                    + ");";
            st.executeUpdate(sql);
            st.close();
        }
        // if (obj instanceof )

    }
}
