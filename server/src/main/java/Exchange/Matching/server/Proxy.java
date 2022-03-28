package Exchange.Matching.server;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Proxy {
    private LinkedHashMap<String,Object> toCheck;

    public Proxy(){
        toCheck=new LinkedHashMap<String,Object>();
    }

    public LinkedHashMap<String,Object> getTocheck(){
        return toCheck;
    }

    public void create_parse(Node n) {
        for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
            switch (child.getNodeName()){
            case "account":
                int id=-1;
                int balance=-1;
                NamedNodeMap account_attrs= child.getAttributes();
                for(int j=0;j<account_attrs.getLength();j++){
                    Node x=account_attrs.item(j);
                    switch (x.getNodeName()){
                        case "id":
                            id=Integer.parseInt(x.getNodeValue());
                            break;
                        case "balance":
                            balance=Integer.parseInt(x.getNodeValue());
                            break;
                    }
                } 
                
                Account account=new Account(id,balance);
                //System.out.println("id: " + id);
                //System.out.println("balance: " + balance);
                toCheck.put("createAccount",account);
                break;
            case "symbol":
                NamedNodeMap sym_attrs= child.getAttributes();
                String symbol_name=sym_attrs.item(0).getNodeValue();
                for (Node sym_child = child.getFirstChild(); sym_child != null; sym_child = sym_child.getNextSibling()){
                    if (sym_child.getNodeName()=="account"){
                        NamedNodeMap sym_account=sym_child.getAttributes();
                        int sym_amount=Integer.parseInt(sym_child.getTextContent());
                        System.out.println("balance" + " " + sym_child.getTextContent());
                        for(int j=0;j<sym_account.getLength();j++){
                            Node x=sym_account.item(j);
                            int sym_accountid=Integer.parseInt(x.getNodeValue());
                            System.out.println(x.getNodeName()+" "+x.getNodeValue());
                            Position position=new Position(symbol_name, sym_amount, sym_accountid);
                            toCheck.put("createPosition",position);
                        }
                    }
                }
                break;
            }
        }
    }

    public void transactions_parse(Node n) {
        int account_id=Integer.parseInt(n.getAttributes().item(0).getNodeValue());
        System.out.println(account_id);
        for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
            switch (child.getNodeName()){
            case "order":
                NamedNodeMap account_attrs= child.getAttributes();
                String symbol="error";
                int amount=0;
                Double limit=0.0;
                for(int j=0;j<account_attrs.getLength();j++){
                    Node x=account_attrs.item(j);
                    switch (x.getNodeName()){
                        case "sym":
                            symbol=x.getNodeValue();
                            System.out.println(symbol);
                            break;
                        case "amount":
                            amount=Integer.parseInt(x.getNodeValue());
                            System.out.println(amount);
                            break;
                        case "limit":
                            limit=Double.parseDouble(x.getNodeValue());
                            System.out.println(limit);
                            break;
                    }
                }                               
                Order order=new Order(account_id,symbol,amount,limit);
                toCheck.put("newOrder",order);
                break;
            case "query":
                int query_transaction_id=Integer.parseInt(child.getAttributes().item(0).getNodeValue());
                System.out.println(query_transaction_id);
                toCheck.put("queryOrder",query_transaction_id);
                break;
            case "cancel":
                int cancel_transaction_id=Integer.parseInt(child.getAttributes().item(0).getNodeValue());
                System.out.println(cancel_transaction_id);
                toCheck.put("cancelOrder",cancel_transaction_id);
                break;
            }
        }
    }
}
