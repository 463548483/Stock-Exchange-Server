package Exchange.Matching.server;

public class Position {
    private String symbol;
    private int amount;
    private int account_id;
    public Position(String symbol,int amount,int account_id){
        this.symbol=symbol;
        this.amount=amount;
        this.account_id=account_id;
    }

}
