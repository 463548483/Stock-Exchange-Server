package Exchange.Matching.server;

public class Position {
    private Sym symbol;
    private int amount;
    private int account_id;
    public Position(Sym symbol,int amount,int account_id){
        this.symbol=symbol;
        this.amount=amount;
        this.account_id=account_id;
    }

}
