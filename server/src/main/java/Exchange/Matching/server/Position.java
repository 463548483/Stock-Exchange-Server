package Exchange.Matching.server;

import java.util.LinkedHashMap;
import java.util.Map;

public class Position {
    private String symbol;
    private double amount;
    private int account_id;
    private String errorMessage;
    public Position(String symbol,int sym_amount,int account_id){
        this.symbol=symbol;
        this.amount=sym_amount;
        this.account_id=account_id;
    }

    public String getSym(){
        return this.symbol;
    }

    public double getAmount(){
        return this.amount;
    }

    public int getID(){
        return this.account_id;
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
        map.put("ACCOUNT_ID",account_id);
        return map;
    }

}
