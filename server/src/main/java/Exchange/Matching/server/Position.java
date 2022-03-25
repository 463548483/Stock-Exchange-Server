package Exchange.Matching.server;

public class Position {
    private Sym symbol;
    private int amount;
    public Position(Sym symbol,int amount){
        this.symbol=symbol;
        this.amount=amount;
    }

}
