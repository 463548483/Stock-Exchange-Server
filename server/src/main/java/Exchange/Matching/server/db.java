package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.ObjLongConsumer;

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
                "SYMBOL_ID SERIAL PRIMARY KEY," +
                "SYMBOL VARCHAR);";

        String sql_position = "CREATE TABLE POSITION(" +
                "POSITION_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYMBOL VARCHAR," +
                "AMOUNT INT," +
                "CONSTRAINT POSITION_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        String sql_account = "CREATE TABLE ACCOUNT(" + "ACCOUNT_ID INT PRIMARY KEY," + "BALANCE INT);";

        String sql_order = "CREATE TABLE ORDER_ALL(" +
                "ORDER_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYMBOL VARCHAR," +
                "AMOUNT INT," +
                "BOUND INT," +
                "STATUS VARCHAR," +
                "TYPE VARCHAR," + 
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
        if (obj instanceof Symbol) {
            Symbol temp = (Symbol) obj;
            Statement st = connection.createStatement();
            String sql = "INSERT INTO SYM (SYMBOL) VALUES('" + temp.getSym() + "');";
            // System.out.println(sql);
            st.executeUpdate(sql);
            st.close();
        }
        if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement();
            String sql = "INSERT INTO ACCOUNT (ACCOUNT_ID, BALANCE) VALUES(" + temp.getID() + ", " + temp.getBalance()
                    + ");";
            // System.out.println(sql);
            st.executeUpdate(sql);
            st.close();
        }
        if (obj instanceof Position) {
            Position temp = (Position) obj;
            Statement st = connection.createStatement();
            // System.out.println(temp.getSym());
            String sql = "insert into position(account_id, symbol, amount) values(" + temp.getID() + ", '"
                    + temp.getSym() + "', " + temp.getAmount() + ");";
            st.executeUpdate(sql);
            st.close();
        }
        if(obj instanceof Order){
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            String sql = "insert into order(account_id, symbol, amount, bound, status, type)";
        }
    }

    public ResultSet search(Object obj) throws SQLException {
        ResultSet res = null;
        if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement();
            String sql = "select * from account where account_id = " + temp.getID() + ";";
            res = st.executeQuery(sql);
            return res;
        } else if (obj instanceof Position) {
            Position temp = (Position) obj;
            Statement st = connection.createStatement();
            String sql = "select * from position where position_d = " + temp.getID() + ";";
            res = st.executeQuery(sql);
            return res;
        } else if (obj.getClass().equals(Number.class)) {
            // query Order
            int temp = (int) obj;
            Statement st = connection.createStatement();
            String sql = "select * from order where order_id = " + temp + ";";
            res = st.executeQuery(sql);
            return res;
        } else if (obj instanceof Order) {
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            // Buy Order: Always valid
            if(temp.getAmount() >= 0){
                return null; 
            }
            // Sell Order
            else{
                // TO DO: Change the Algorithm of Matching Orders
                String sql = "select * from position where account_id = " + temp.getAccountID() + " and symbol = '"
                + temp.getSymbol() + "' and amount >= " + temp.getAmount() + " and bound > " + temp.getLimit() + " and status = 'open' and type = 'buy');";
                res = st.executeQuery(sql);
                return res;
            }
        }
        return res;
    }

    public String deleteData(Object obj) throws SQLException {
        if (obj instanceof Order) {
            // delete Order
            int temp = (Integer) obj;
            Statement st = connection.createStatement();
            try {
                String sql = "delete * from Order where order_id = " + temp + ";";
                st.executeUpdate(sql);
            } catch (Exception e) {
                String errmsg = "Error: Fail to delete the Order.";
                e.printStackTrace();
                return errmsg;
            }
            String msg = "Successfully delete the Order.";
            return msg;
        }
        return null;
    }
}
