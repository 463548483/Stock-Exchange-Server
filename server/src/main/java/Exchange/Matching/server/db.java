package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.ObjLongConsumer;

import com.google.common.collect.Ordering;

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
        if (obj instanceof Order) {
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            String sql = "insert into order_all(account_id, symbol, amount, bound, status, type) values("
                    + temp.getAccountID() + ", '" + temp.getSymbol() + "', " + temp.getAmount() + ", " + temp.getLimit()
                    + ", '" + temp.getStatus() + "', '" + temp.getType() + "');";
            // System.out.printf(sql);
            st.executeUpdate(sql);
            st.close();
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
        } else if (obj instanceof Number) {
            // query Orders
            int temp = (int) obj;
            Statement st = connection.createStatement();
            String sql = "select * from order_all where order_id = " + temp + ";";
            res = st.executeQuery(sql);
            return res;
        } else if (obj instanceof Order) {
            // buy Orders
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            if (temp.getType() == "buy") {
                String sql = "select * from order_all where symbol = '"
                        + temp.getSymbol() + " and bound <= " + temp.getLimit()
                        + " and status = 'open' and type = 'sell' order by bound asc;";
                res = st.executeQuery(sql);
                return res;
            }
            // Sell Order
            else {
                String sql = "select * from order_all where symbol = '"
                        + temp.getSymbol() + " and bound >= " + temp.getLimit()
                        + " and status = 'open' and type = 'buy' order by bound desc;";
                res = st.executeQuery(sql);
                return res;
            }
        }
        return res;
    }

    public ResultSet checkSellOrder(Order order) throws SQLException {
        ResultSet res = null;
        Statement st = connection.createStatement();
        String sql = "select * from position where account_id = " + order.getAccountID() + " and symbol = '"
                + order.getSymbol() + "' and amount >= " + order.getAmount() + ";";
        //System.out.println(sql);
        res = st.executeQuery(sql);
        return res;
    }

    public String cancelOrder(Object obj) throws SQLException {
        if (obj instanceof Number) {
            // delete Order
            int temp = (Integer) obj;
            ResultSet res = search(temp);
            if (!res.next()) {
                String errmsg = "Error: Fail to cancel the Order, the order does not exist.";
                return errmsg;
            } else {
                Statement st = connection.createStatement();
                // String sql = "delete from order_all where order_id = " + temp + ";";
                String sql = "update order_all set status = 'cancal' where order_id = " + temp + ";";
                st.executeUpdate(sql);
                String msg = "Successfully cancelled the Order.";
                return msg;
            }
        }
        return null;
    }
}
