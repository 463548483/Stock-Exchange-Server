package Exchange.Matching.server;

public class Order {
    //private int order_id; // for query & cancel
    private int account_id;
    private String symbol;
    private int amount;
    private double limit;
    private String status;
    private String type;

    // For create new Order
    public Order(int account_id,String symbol, int amount,double limit){
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
    }

    // For query & cancel Order
    /*public Order(int order_id){
        this.order_id = order_id;
    }*/

    public int getAccountID(){
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

    public String getStatus(){
        return status;
    }

    /*public int getOrderID(){
        return order_id;
    }*/

    public String getType(){
        return type;
    }
}
