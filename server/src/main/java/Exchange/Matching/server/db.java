package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class db {
    private Connection connection;
    //private Matching matching;
    private int reponse_trans_id;

    public db() throws SQLException {
        this.connection = buildDBConnection();
        connection.setAutoCommit(false);
        deleteTables();
        buildTables();
        //this.matching = new Matching();
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
        String sql_delete = "DROP TABLE IF EXISTS SYM, POSITION, ACCOUNT, ORDER_ALL, ORDER_EXECUTE;";
        st.executeUpdate(sql_delete);
        st.close();
        connection.commit();
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
                "AMOUNT FLOAT," +
                "CONSTRAINT POSITION_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        String sql_account = "CREATE TABLE ACCOUNT(" + "ACCOUNT_ID INT PRIMARY KEY," + "BALANCE FLOAT CHECK(BALANCE>=0));";

        String sql_order = "CREATE TABLE ORDER_ALL(" +
                "ORDER_ID SERIAL PRIMARY KEY," +
                "ACCOUNT_ID INT," +
                "SYMBOL VARCHAR," +
                "AMOUNT FLOAT," +
                "BOUND FLOAT," +
                "STATUS VARCHAR," +
                "TYPE VARCHAR," +
                "TIME BIGINT," +
                //"constraint symbol_fk foreign key(symbol) references sym(symbol) on delete set null on update cascade," + 
                "CONSTRAINT ACCOUNT_FK FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        // Store trades info
        String sql_exectute_order = "CREATE TABLE ORDER_EXECUTE(" +
                "ORDER_ID SERIAL PRIMARY KEY," +
                "BUYER_ID INT," +
                "SELLER_ID INT," +
                "BUYER_TRANS_ID INT," +
                "SELLER_TRANS_ID INT," +
                "SYMBOL VARCHAR," +
                "AMOUNT FLOAT," +
                "PRICE FLOAT," +
                "TIME BIGINT," +
                //"constraint symbol_fk foreign key(symbol) references sym(symbol) on delete set null on update cascade," + 
                "CONSTRAINT BUYER_FK FOREIGN KEY (BUYER_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE,"
                +
                "CONSTRAINT SELLER_FK FOREIGN KEY (SELLER_ID) REFERENCES ACCOUNT(ACCOUNT_ID) ON DELETE SET NULL ON UPDATE CASCADE);";

        st.executeUpdate(sql_sym);
        st.executeUpdate(sql_account);
        st.executeUpdate(sql_position);
        st.executeUpdate(sql_order);
        st.executeUpdate(sql_exectute_order);
        st.close();
        connection.commit();
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
            //System.out.println(sql);
            st.executeUpdate(sql);
            // st.close();
            connection.commit();
        }
        if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement();
            String sql = "INSERT INTO ACCOUNT (ACCOUNT_ID, BALANCE) VALUES(" + temp.getID() + ", " + temp.getBalance()
                    + ");";
            // System.out.println(sql);
            st.executeUpdate(sql);
            // st.close();
            connection.commit();
        }
        if (obj instanceof Position) {
            Position temp = (Position) obj;
            Statement st = connection.createStatement();
            // System.out.println(temp.getSym());
            String sql = "insert into position(account_id, symbol, amount) values(" + temp.getAccountID() + ", '"
                    + temp.getSym() + "', " + temp.getAmount() + ");";
            st.executeUpdate(sql);
            // st.close();
            connection.commit();
        }
        if (obj instanceof Order) {
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            String sql = "insert into order_all(account_id, symbol, amount, bound, status, type, time) values("
                    + temp.getAccountID() + ", '" + temp.getSymbol() + "', " + temp.getAmount() + ", " + temp.getLimit()
                    + ", '" + temp.getStatus() + "', '" + temp.getType() + "', " +  temp.getTime() + ");";
            // System.out.printf(sql);
            st.executeUpdate(sql);
            String getTransactionID_sql = "select lastval();";
            ResultSet res = st.executeQuery(getTransactionID_sql);
            //trans_id : For reponse Order
            int trans_id = -1;
            if(res.next()){
                trans_id = res.getInt("lastval");
            }
            //System.out.println("The transaction id is :" + trans_id);
            // st.close();
            this.reponse_trans_id = trans_id;
            connection.commit();
        }
        if (obj instanceof ExecuteOrder) {
            ExecuteOrder temp = (ExecuteOrder) obj;
            Statement st = connection.createStatement();
            String sql = "insert into order_execute(buyer_id, seller_id, buyer_trans_id, seller_trans_id, symbol, amount, price, time) values("
                    + temp.getBuyerID() + ", " + temp.getSellerID() + ", " + temp.getBuyerOrderID() + ", "
                    + temp.getSellerOrderID() + ", '" + temp.getSymbol() + "', " + temp.getAmount() + ", "
                    + temp.getPrice()
                    + ", " + temp.getTime() + ");";
            // System.out.printf(sql);
            st.executeUpdate(sql);
            // st.close();
            connection.commit();
        }
    }

    /*
     * Search for specific data in tables. For buy & sell orders, also do price
     * matching & balance changement.
     * 
     * @return ResultSet
     */
    public ResultSet search(Object obj) throws SQLException {
        ResultSet res = null;
        if (obj instanceof Symbol) {
            Symbol temp = (Symbol) obj;
            Statement st = connection.createStatement();
            String sql = "select * from sym where symbol = '" + temp.getSym() + "';";
            res = st.executeQuery(sql);
            // st.close();
            connection.commit();
            return res;
        } else if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement();
            String sql = "select * from account where account_id = " + temp.getID() + ";";
            res = st.executeQuery(sql);
            // st.close();
            connection.commit();
            return res;
        } else if (obj instanceof Position) {
            Position temp = (Position) obj;
            Statement st = connection.createStatement();
            String sql = "select * from position where account_id = " + temp.getAccountID() + " and symbol = '" + temp.getSym() + "';";
            res = st.executeQuery(sql);
            // st.close();
            connection.commit();
            return res;
        } else if (obj instanceof Number) {
            // query Orders
            int temp = (int) obj;
            Statement st = connection.createStatement();
            String sql = "select * from order_all where order_id = " + temp + ";";
            res = st.executeQuery(sql);
            // String sql_execute = "select * from order_execute where "
            // st.close();
            connection.commit();
            return res;
        } else if (obj instanceof Order) {
            // buy Orders
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            String sql = "";
            if (temp.getType().equals("buy")) {
                sql = "select * from order_all where symbol = '"
                        + temp.getSymbol() + "' and bound <= " + temp.getLimit()
                        + " and status = 'open' and type = 'sell' order by bound asc, time asc for update;";
                //System.out.println(sql);
            } else if(temp.getType().equals("sell")){
                sql = "select * from order_all where symbol = '"
                        + temp.getSymbol() + "' and bound >= " + temp.getLimit() 
                        + " and status = 'open' and type = 'buy' order by bound desc, time asc for update;";
                //System.out.println(sql);
            }
            res = st.executeQuery(sql);
            // Order Matching
            Matching matching = new Matching(temp, res);
            // Fill Order_Execute Table
            ArrayList<ExecuteOrder> execute_list = matching.getExecuteList();
            for (ExecuteOrder eorder : execute_list) {
                insertData(eorder);
                double balance_change = eorder.getAmount() * eorder.getPrice();
                // update balance of Buyer & Seller
                System.out.println("Info of E Order: " + eorder.getBuyerID() + eorder.getSellerID()+ eorder.getBuyerOrderID()+eorder.getSellerOrderID()+eorder.getSymbol()+eorder.getAmount()+ eorder.getPrice());
                
                Account buyer_account_temp = new Account(eorder.getBuyerID(), -balance_change);
                Account seller_account_temp = new Account(eorder.getSellerID(), balance_change);
                updateData(buyer_account_temp);
                updateData(seller_account_temp);


                // update position
                Position buyer_position = new Position(eorder.getSymbol(), eorder.getAmount(), eorder.getBuyerID());
                System.out.println("Update position:" + eorder.getSymbol() + eorder.getAmount() +  eorder.getBuyerID() );
                Position seller_position = new Position(eorder.getSymbol(), -eorder.getAmount(), eorder.getSellerID());
                updateData(buyer_position);
                updateData(seller_position);
            }
            // Update Order in Order_all Table
            Order new_order = matching.getOrder();
            updateData(new_order);
            
            ArrayList<Order> sell_order_list = matching.getSellList();
            for (Order sorder : sell_order_list) {
                updateData(sorder);
            }
            // st.close();
            connection.commit();
            return res;
        }
        return res;
    }

    public ArrayList<Order> searchOrder(int transaction_id) throws SQLException {
        ArrayList<Order> query_order_list = new ArrayList<Order>();
        ResultSet res = search(transaction_id);
        Matching matching = new Matching();
        query_order_list = matching.mapOrder(res);
        return query_order_list;
    }

    public ArrayList<ExecuteOrder> searchExecuteOrder(int transaction_id, String type) throws SQLException {
        ArrayList<ExecuteOrder> query_execute_order = new ArrayList<ExecuteOrder>();
        Statement st = connection.createStatement();
        String sql = "";
        if (type.equals("buy")) {
            sql = "select * from order_execute where buyer_trans_id = " + transaction_id + ";";
            //System.out.println(sql);
            
        } else if (type.equals("sell")) {
            sql = "select * from order_execute where seller_trans_id = " + transaction_id + ";";
            //System.out.println(sql);
        }
        ResultSet res = st.executeQuery(sql);
        // st.close();
        Matching matching = new Matching();
        query_execute_order = matching.mapExecuteOrder(res);
        connection.commit();
        return query_execute_order;
    }

    /*
     * Updata Data in tables.
     */
    public void updateData(Object obj) throws SQLException {
        // update order amount
        if (obj instanceof Order) {
            Order temp = (Order) obj;
            Statement st = connection.createStatement();
            String sql = "update order_all set amount = " + temp.getAmount() + " where order_id = " + temp.getOrderID()
                    + ";";
            // System.out.println(sql);
            st.executeUpdate(sql);
            // st.close();
            //connection.commit();
        }
        // update balance
        else if (obj instanceof Account) {
            Account temp = (Account) obj;
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            //System.out.println("The ID of the account is: " + temp.getID());
            String sql = "select * from account where account_id = " + temp.getID() + " for update;";
            //System.out.println(sql);
            ResultSet res = st.executeQuery(sql);
            double balance = 0.0;
            if(res.next()){
                //res.previous();
                balance = res.getDouble("BALANCE");
            }
            
            //double balance = 0.0;
            System.out.println("The balance is --"+ balance);
            double new_balance = balance + temp.getBalance();
            String sql_update = "update account set balance = " + new_balance + " where account_id = " + temp.getID()
                    + ";";
            // System.out.println(sql);
            st.executeUpdate(sql_update);
            // st.close();
            connection.commit();
        }
        else if(obj instanceof Position){
            Position temp = (Position) obj;
            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "select * from position where symbol = '" + temp.getSym() +"' and account_id = " + temp.getAccountID() + " for update;";
            ResultSet res = st.executeQuery(sql);
            double amount = 0.0;
            if(res.next()){
                //res.previous();
                amount = res.getDouble("amount");
                //System.out.println("'Increased amount" + amount);
            }
            double new_amount = amount + temp.getAmount();
            String sql_update = "update position set amount = " + new_amount + " where symbol = '" + temp.getSym() +"' and account_id = " + temp.getAccountID() + ";";
            // System.out.println(sql);
            st.executeUpdate(sql_update);
            //st.close();
            connection.commit();
        }
    }

    // help check whether the buy order is valid or not.
    public String checkBuyOrder(Order order) throws SQLException {
        String msg = "";
        
        // Check if the symbol exsits.
        ResultSet res_temp_sym = search(new Symbol(order.getSymbol()));
        if(!res_temp_sym.next()){
            msg = "Error: The Symbol of the Buy Order does not exist"; 
            return msg;
        }
        //check if the buyer Account exists
        ResultSet res_account = search(new Account(order.getAccountID(),0));
        connection.commit();
        if(!res_account.next()){
            msg = "Error: The Account of the Buy Order does not exists."; 
            return msg;
        }

        double need_balance = order.getAmount() * order.getLimit();
        Statement st = connection.createStatement();
        String sql = "select * from account where account_id = " + order.getAccountID() + " and balance >= "
                + need_balance + ";";
        ResultSet res = st.executeQuery(sql);
        // st.close();
        connection.commit();
        if(!res.next()){
            msg = "Error: The balance of the Account is insufficient."; 
            return msg;
        }
        msg = "The Buy Order is valid.";
        return msg;
    }

    // help check whether the sell order is valid or not.
    public String checkSellOrder(Order order) throws SQLException {
        String msg = "";
        Statement st = connection.createStatement();
        ResultSet res_account = search(new Account(order.getAccountID(), 0));
        if(!res_account.next()){
            msg = "Error: The account of the sell order does not exist.";
            return msg;
        }
        String sql = "select * from position where account_id = " + order.getAccountID() + " and symbol = '"
                + order.getSymbol() + "' and amount >= " + order.getAmount() + ";";
        // System.out.println(sql);
        ResultSet res = st.executeQuery(sql);
        // st.close();
        connection.commit();
        if(!res.next()){
            msg = "Error: The Account of the sell order does not have enough position to sell."; 
            return msg;
        }
        msg = "The Sell Order is valid.";
        return msg;
    }

    // cancel order
    public ArrayList<Order> cancelOrder(int transaction_id) throws SQLException {
        Statement st = connection.createStatement();
        String sql_search = "select * from order_all where order_id = " + transaction_id + " for update;";
        ResultSet queryres = st.executeQuery(sql_search);
        String sql = "update order_all set status = 'canceled' where order_id = " + transaction_id + ";";
        st.executeUpdate(sql);
        String sql_search_cancel = "select * from order_all where order_id = " + transaction_id + ";";
        ResultSet query_cancel_res = st.executeQuery(sql_search_cancel);
        Matching matching = new Matching();
        ArrayList<Order> cancel_list = matching.mapOrder(query_cancel_res);
        // st.close();
        connection.commit();
        return cancel_list;
    }


    public int getResponseID(){
        return this.reponse_trans_id;
    }
}
