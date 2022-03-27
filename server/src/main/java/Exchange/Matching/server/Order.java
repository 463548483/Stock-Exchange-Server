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
}
