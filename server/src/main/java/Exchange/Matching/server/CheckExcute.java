package Exchange.Matching.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.naming.directory.SearchControls;
import javax.naming.spi.DirStateFactory.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.xml.XmlEscapers;

import org.w3c.dom.Element;

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
                TransactionId queId=new TransactionId(transactions_id);
                String errmsg = "Error: The queried Order does not exist.";
                queId.setErrorMessage(errmsg); 
                xmLgenerator.lineXML(queId, "error");
            }
            else{
                String msg = "Found the query Order.";
                System.out.println(msg);
                // order_list: open/cancel orders in order_all
                ArrayList<Order> order_list = stockDB.searchOrder(transactions_id);
                TransactionId queId=new TransactionId(transactions_id);
                Element status=xmLgenerator.lineXML(queId, "status");
                for(Order order: order_list){
                    queId.updateOrder(order.getAmount(), order.getLimit(), order.getTime(), order.getStatus());
                    xmLgenerator.lineXML(status,queId, queId.getStatus());
                }
                // execute_list: executed orders in order_execute
                Order order = order_list.get(0);
                ArrayList<ExecuteOrder> execute_list = stockDB.searchExecuteOrder(transactions_id, order.getType());
                for(ExecuteOrder eorder: execute_list){
                    queId.updateOrder(eorder.getAmount(), eorder.getPrice(), eorder.getTime(), "executed");
                    xmLgenerator.lineXML(status,queId, queId.getStatus());
                }
            }
        }
        if (action_flag == cancel_flag){
            ResultSet res = stockDB.search(transactions_id);
            if (!res.next()) {
                TransactionId calId=new TransactionId(transactions_id);
                String errmsg = "Error: Fail to cancel the Order, the order does not exist.";
                calId.setErrorMessage(errmsg); 
                xmLgenerator.lineXML(calId, "error");
            } else {
                TransactionId calId=new TransactionId(transactions_id);
                Element canceled=xmLgenerator.lineXML(calId, "canceled");
                ArrayList<Order> cancel_list = stockDB.cancelOrder(transactions_id);
                String msg = "Successfully canceled the Order.";
                System.out.println(msg);
                // add responses
                for(Order order: cancel_list){
                    calId.updateOrder(order.getAmount(), order.getLimit(), order.getTime(), order.getStatus());
                    xmLgenerator.lineXML(canceled,calId, calId.getStatus());
                }
            }
        }
    }

    public void visit(Account account) throws SQLException, TransformerConfigurationException {
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
        Account account_temp = new Account(position.getAccountID(), 0);
        Symbol symbol_temp = new Symbol(position.getSym());
        
        ResultSet res_account = stockDB.search(account_temp);
        ResultSet res_sym = stockDB.search(symbol_temp);

        ResultSet res_postion = stockDB.search(position);

        if (!res_account.next()) {
            position.setErrorMessage("Error: Account does not exist"); 
            xmLgenerator.lineXML(position, "error");
        } else {
            // create symbol
            if (!res_sym.next()) {
                stockDB.insertData(symbol_temp);
            }
            if(!res_postion.next()){
                // create position
                stockDB.insertData(position);
                xmLgenerator.lineXML(position, "created");    
            }
            else{
                stockDB.updateData(position);
                xmLgenerator.lineXML(position, "created");
            }
        }
        
    }

    // handle new Order
    public void visit(Order order) throws SQLException {
        // check if the Order is Valid or Not
        // System.out.println("The type of the new order is: " + order.getType());
        // Buy Order: account, symbol
        if(order.getType() == "buy"){
            String msg = stockDB.checkBuyOrder(order);
            if(msg.equals("The Buy Order is valid.")){
                stockDB.insertData(order);
                // returned transaction_id
                int response_trans_id = stockDB.getResponseID();
                // To Do: add trans_id field to response
                order.setOrderID(response_trans_id);
                xmLgenerator.lineXML(order, "opened");
            }
            else{
                order.setErrorMessage(msg); 
                xmLgenerator.lineXML(order, "error");
            };
            System.out.println("yy-test"+msg);
        }
        // Sell Order: check account, sym, amount
        else{
            String msg = stockDB.checkSellOrder(order);
            if(msg.equals("The Sell Order is valid.")){
                stockDB.insertData(order);
                int response_trans_id = stockDB.getResponseID();
                // To Do: add trans_id field to response
                order.setOrderID(response_trans_id);
                xmLgenerator.lineXML(order, "opened");
            }
            else{
                order.setErrorMessage(msg); 
                xmLgenerator.lineXML(order, "error");  
            }
            System.out.println(msg);
        }

        // Handle Order Matching
        ResultSet res = stockDB.search(order);
    }

}
