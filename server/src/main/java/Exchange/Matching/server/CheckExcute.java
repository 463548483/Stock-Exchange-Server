package Exchange.Matching.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class CheckExcute {
    private int query_flag=0;
    private int cancel_flag=1;
    public String visit(Entry<String, Object> entry){
        switch (entry.getKey()){
            case "createAccount":
                return visit((Account)entry.getValue());
            case "createSym":
                return visit((Position)entry.getValue());
            case "newOder":
                return visit((Order)entry.getValue());
            case "queryOrder":
                return visit((Integer)entry.getValue(),query_flag);
            case "cancelOrder":
                return visit((Integer)entry.getValue(),cancel_flag);
            default:
                return null;
        }
    }
    
    public String visit(int transactions_id,int action_flag){
        //if (action_flag==query_flag)
        //select * from Order where id=transaction_id;
        //if exist, 
        //if not exist, error build
        //if (action_flag==cancel_flag)
        //select * from Order where id=transaction_id;
        //if not exist, error build
        //canceled all open transaction
        return null;
    }

    public String visit(Account account){
        //select count(*) from account where account_id=;
        //if exist, error build
        //create account
        return null;
    }

    public String visit(Position position){
        //select count(*) from account where account_id=;
        //if not exist, error build
        //create position
        return null;
    }

    public String visit(Order order){
        //select amount,limit from user where account_id=,symbol=;
        //check if amount<=;limit
        //create order, consider to seperate?
        return null;
    }
}
