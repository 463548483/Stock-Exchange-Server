package Exchange.Matching.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.naming.directory.SearchControls;

public class CheckExcute {

    private db stockDB;
    private int query_flag = 0;
    private int cancel_flag = 1;

    public CheckExcute(db stockDB) {
        this.stockDB = stockDB;
    }

    public String visit(Entry<String, Object> e) throws SQLException {
        switch (e.getKey()) {
            case "createAccount":
                //System.out.println("Create Account case");
                return visit((Account) e.getValue());
            case "createPosition":
                //System.out.println("Create Position case");
                return visit((Position) e.getValue());
            case "newOrder":
                //System.out.println("Create New Order");
                return visit((Order) e.getValue());
            case "queryOrder":
                //System.out.println("Query Order");
                return visit((Integer)e.getValue(), 0);
            case "cancelOrder":
                //System.out.println("Cancel Order");
                return visit((Integer)e.getValue(), 1);
            default:
                return null;
        }
    }

    // For query Order & delete Order
    public String visit(int transactions_id, int action_flag) throws SQLException {
        if(action_flag == query_flag){
            ResultSet res = stockDB.search(transactions_id);
            if(!res.next()){
                String errmsg = "Error: The queried Order does not exist.";
                System.out.println(errmsg);
                return errmsg;
            }
            else{
                String msg = "Found the query Order.";
                System.out.println(msg);
                return msg;
            }
        }
        if (action_flag == cancel_flag){
            String res = stockDB.cancelOrder(transactions_id);
            System.out.println(res);
            if(res == null){
                System.out.println("The res is null.");
            }
            return res;
        }
        // canceled all open transaction
        return null;
    }

    public String visit(Account account) throws SQLException {
        ResultSet res = stockDB.search(account);
        if (res.next()) {
            String errmsg = "Error: Account already exists.";
            return errmsg;
        }
        // create account
        else {
            stockDB.insertData(account);
        }
        return null;
    }

    public String visit(Position position) throws SQLException {
        Account account_temp = new Account(position.getID(), 0);
        Symbol symbol_temp = new Symbol(position.getSym());
        
        ResultSet res_account = stockDB.search(account_temp);
        ResultSet res_sym = stockDB.search(symbol_temp);

        if (!res_account.next()) {
            String errmsg = "Error: Account does not exist.";
            return errmsg;
        } else {
            // create symbol
            if (res_sym == null) {
                stockDB.insertData(symbol_temp);
            }
            // create position
            stockDB.insertData(position);
        }
        return null;
    }

    // handle new Order
    public String visit(Order order) throws SQLException {
        // check if the Order is Valid or Not
        // System.out.println("The type of the new order is: " + order.getType());
        // Buy Order: The account must exist.
        if(order.getType() == "buy"){
            Account account_temp = new Account(order.getAccountID(),0);
            System.out.println("AccountID: " + account_temp.getID());
            ResultSet res_temp = stockDB.search(account_temp);
            if(res_temp.next()){
                res_temp.previous();
                String msg = "The Buy Order is valid.";
                System.out.println(msg);
                stockDB.insertData(order);
            }
            else{
                String errmsg = "Error: The Account of the Order does not exist.";
                System.out.println(errmsg);
                return errmsg;  
            }
        }
        // Sell Order: check account, sym, amount
        else{
            ResultSet res_temp = stockDB.checkSellOrder(order);
            if(res_temp.next()){
                res_temp.previous();
                String msg = "The Sell Order is valid.";
                System.out.println(msg);
                stockDB.insertData(order);
            }
            else{
                String errmsg = "Error: The Sell Order is invalid.";
                System.out.println(errmsg);
                return errmsg;     
            }
        }
        // Order Matching
        ResultSet res = stockDB.search(order);
        while(order.getStatus() == "open"){

        }
        // update balance of Buyer & Seller

        return null;
    }

}
