package Exchange.Matching.server;

<<<<<<< HEAD
import java.time.Instant;
=======
import java.util.LinkedHashMap;
import java.util.Map;
>>>>>>> origin/updatethread

public class Order {
    private int order_id;
    private int account_id;
    private String symbol;
    private double amount;
    private double limit;
<<<<<<< HEAD
    private String status;
    private String type;
    private long time;

    // For create new Order
    public Order(int account_id,String symbol, double amount,double limit){
        this.account_id=account_id;
        this.symbol=symbol;
        this.amount= Math.abs(amount);
        this.limit=limit;
        this.status = "open"; // open/executed/cancel
        if(amount >= 0){
            this.type = "buy";
        }
        else{
            this.type = "sell";
        }
        this.time = Instant.now().getEpochSecond();
    }
=======
    private int transactions_id;
    private String errorMessage;
>>>>>>> origin/updatethread

    // For Mapping SQL 
    public Order(int account_id,String symbol, double amount,double limit, String status, String type){
        this.account_id=account_id;
        this.symbol=symbol;
        this.amount= Math.abs(amount);
        this.limit=limit;
<<<<<<< HEAD
        this.status = status;
        this.type = type;
    }
    
    // For separating Order
    public Order(int account_id, String symbol, double amount, double limit, String type, long time){
        this.account_id=account_id;
        this.symbol=symbol;
        this.amount= Math.abs(amount);
        this.limit=limit;
        this.status = "open";
        this.type = type;
        this.time = time;
    }

    // Get from Database
    public void setOrderID(int id){
        this.order_id = id;
=======
        this.transactions_id=-1;//init error id
>>>>>>> origin/updatethread
    }

    public void setAmount(double amount){
        this.amount = amount;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public int getAccountID(){
        return account_id;
    }

    public String getSymbol(){
        return symbol;
    }

    public double getAmount(){
        return amount;
    }

    public double getLimit(){
        return limit;
    }

<<<<<<< HEAD
    public String getStatus(){
        return status;
    }

    public String getType(){
        return type;
    }

    public long getTime(){
        return time;
    }

    public int getOrderID() {
        return order_id;
=======
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
>>>>>>> origin/updatethread
    }
}
