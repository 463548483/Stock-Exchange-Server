package Exchange.Matching.server;

public class Position {
    private String symbol;
    private double amount;
    private int account_id;
    
    public Position(String symbol,double sym_amount,int account_id){
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
}
