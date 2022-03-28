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
            case "newOder":
                return visit((Order) e.getValue());
            case "queryOrder":
                return visit((Integer)e.getValue(), query_flag);
            case "cancelOrder":
                return visit((Integer)e.getValue(), cancel_flag);
            default:
                return null;
        }
    }

    // For query Order & delete Order
    public String visit(int transactions_id, int action_flag) throws SQLException {
        if(action_flag == query_flag){
            ResultSet res = stockDB.search(transactions_id);
            if(res == null){
                return "The queried Order does not exist.";
            }
            else{
                return "Found the query Order.";
            }
        }
        if (action_flag == cancel_flag){
            String res = stockDB.deleteData(transactions_id);
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

        if (res_account == null) {
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
    public String visit(Order order) {
        // select amount,limit from user where account_id=,symbol=;
        Account account_temp = new Account(order.getAccountID(), 0);
        Symbol symbol_temp = new Symbol(order.getSymbol());
        // check if amount<=;limit
        // create order, consider to seperate?
        return null;
    }
}
