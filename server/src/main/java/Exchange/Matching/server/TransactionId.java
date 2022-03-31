package Exchange.Matching.server;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Spring;

public class TransactionId extends XMLObject {
    private int transactionId;
    private double shares;
    private double price;
    private long time;
    private String status;

    public TransactionId(int id){
        transactionId=id;
    }

    public TransactionId(int id,double shares,double price,long time,String status){
        transactionId=id;
        this.shares=shares;
        this.price=price;
        this.time=time;
        this.status=status;
    }

    public int getTransactionId(){
        return transactionId;
    }

    public String getStatus(){
        return status;
    }

    @Override
    public Map<String, String> getAttribute() {
        Map<String,String> map=new LinkedHashMap<String,String>();
        map.put("shares",Double.toString(shares));
        if (price!=0){
            map.put("price", Double.toString(price));
        }
        if (time!=0){
            map.put("time",Long.toString(time));
        }
        return map;
    }
    
}
