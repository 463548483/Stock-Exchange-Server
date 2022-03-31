package Exchange.Matching.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.naming.directory.SearchControls;
import javax.naming.spi.DirStateFactory.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.xml.XmlEscapers;

public class CheckExcute {

    private db stockDB;
    private final int query_flag = 0;
    private final int cancel_flag = 1;
    private Matching matching;
    private XMLgenerator xmLgenerator;

    public CheckExcute(db stockDB) {
        this.stockDB = stockDB;
        xmLgenerator=new XMLgenerator();
    }

    public XMLgenerator getXmLgenerator(){
        return xmLgenerator;
    }

    // For query Order & cancel Order
    public void visit(int transactions_id, int action_flag) throws SQLException {
        if(action_flag == query_flag){
            ResultSet res = stockDB.search(transactions_id);
            if(!res.next()){
                Order queOrder=new Order(transactions_id);
                queOrder.setErrorMessage("Error: The queried Order does not exist."); 
                xmLgenerator.lineXML(queOrder, "error");
            }
            else{
                String msg = "Found the query Order.";
                System.out.println(msg);
                // order_list: open/cancel orders in order_all
                ArrayList<Order> order_list = matching.mapOrder(res);
                for(Order order: order_list){
                    xmLgenerator.lineXML(order, order.getStatus());
                }
                // execute_list: executed orders in order_execute
                
            }
        }
        if (action_flag == cancel_flag){
            String res = stockDB.cancelOrder(transactions_id);
            System.out.println(res);
            if(res == null){
                System.out.println("The res is null.");
            }
            // for(Order order: order_list){
            //     xmLgenerator.lineXML(order, order.getStatus());
            // }
        }
        // canceled all open transaction
    }


    public void visit(Account account) throws SQLException, TransformerException {
        ResultSet res = stockDB.search(account);
        if (res.next()) {
            account.setErrorMessage("Error: Account already exists"); 
            xmLgenerator.lineXML(account, "error");
        }
        // create account
        else {
            stockDB.insertData(account);
            xmLgenerator.lineXML(account, "created");
        }
        
    }

    public void visit(Position position) throws SQLException {
        Account account_temp = new Account(position.getID(), 0);
        Symbol symbol_temp = new Symbol(position.getSym());
        
        ResultSet res_account = stockDB.search(account_temp);
        ResultSet res_sym = stockDB.search(symbol_temp);

        if (!res_account.next()) {
            position.setErrorMessage("Error: Account does not exist"); 
            xmLgenerator.lineXML(position, "error");
        } else {
            // create symbol
            if (res_sym == null) {
                stockDB.insertData(symbol_temp);
            }
            // create position
            stockDB.insertData(position);
            xmLgenerator.lineXML(position, "created");
        }
        
    }

    // handle new Order
    public void visit(Order order) throws SQLException {
        // check if the Order is Valid or Not
        // System.out.println("The type of the new order is: " + order.getType());
        // Buy Order: account, symbol
        if(order.getType() == "buy"){
            // Check if the Buyer Account exists and the balance is enough.
            ResultSet res_temp = stockDB.checkBuyOrder(order);
            // Check if the symbol exsits.
            ResultSet res_temp_sym = stockDB.search(new Symbol(order.getSymbol()));
            if(!res_temp.next()){
                order.setErrorMessage("Error: The Buy Order is not valid"); 
                xmLgenerator.lineXML(order, "error");
            }else if(!res_temp_sym.next()){
                order.setErrorMessage("Error: The Symbol of the Buy Order does not exist"); 
                xmLgenerator.lineXML(order, "error"); 
            }
            else{
                res_temp.previous(); 
                String msg = "The Buy Order is valid.";
                System.out.println(msg);
                stockDB.insertData(order);
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
                order.setErrorMessage("Error: The Sell Order is invalid"); 
                xmLgenerator.lineXML(order, "error");  
            }
        }

        // Handle Order Matching
        ResultSet res = stockDB.search(order);

        xmLgenerator.lineXML(order, "opened");
    }

}
