package Exchange.Matching.server;

public class Order {
    private int account_id;
    private String symbol;
    private int amount;
    private double limit;

    public Order(int account_id,String symbol, int amount,double limit){
        this.account_id=account_id;
        this.symbol=symbol;
        this.amount=amount;
        this.limit=limit;
    }
}
