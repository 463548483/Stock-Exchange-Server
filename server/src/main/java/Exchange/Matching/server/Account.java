package Exchange.Matching.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Account {
    private int id;
    private double balance;
    private String errorMessage;


    public Account(int id, double balance){
        this.id = id;
        this.balance = balance;
        this.errorMessage=null;
    }

    public int getID(){
        return this.id;
    }
    
    public double getBalance(){
        return this.balance;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public void setErrorMessage(String msg){
        errorMessage=msg;
    }

    public Map<String,Object> getAttribute(){
        Map<String,Object> map=new LinkedHashMap<String,Object>();
        map.put("id",id);
        return map;
    }


}


