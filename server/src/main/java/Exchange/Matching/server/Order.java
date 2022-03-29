package Exchange.Matching.server;

import java.util.LinkedHashMap;
import java.util.Map;

public class Order {
    private int account_id;
    private String symbol;
    private int amount;
    private double limit;
    private int transactions_id;
    private String errorMessage;

    public Order(int account_id,String symbol, int amount,double limit){
        this.account_id=account_id;
        this.symbol=symbol;
        this.amount=amount;
        this.limit=limit;
        this.transactions_id=-1;//init error id
    }

    public int getAccountid(){
        return account_id;
    }

    public String getSymbol(){
        return symbol;
    }

    public int getAmount(){
        return amount;
    }

    public double getLimit(){
        return limit;
    }

    public void setTransactionId(int id){
        transactions_id=id;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public void setErrorMessage(String msg){
        errorMessage=msg;
    }

    public Map<String,Object> getAttribute(){
        Map<String,Object> map=new LinkedHashMap<String,Object>();
        map.put("SYM",symbol);
        map.put("AMT", amount);
        map.put("LMT",limit);
        if (transactions_id!=-1){
            map.put("TRANS_ID",transactions_id);
        }
        return map;
    }
}
