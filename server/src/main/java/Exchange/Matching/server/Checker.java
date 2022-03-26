package Exchange.Matching.server;
public class Checker {
    public String visit(int transactions_id){
        //select transaction_id from db;
        //if not exist, throw exception
        return null;
    }

    public String visit(Account account){
        //select account_id from db;
        //if exist, throw exception
        return null;
    }

    public String visit(Position position){
        //nothing to check;
        return null;
    }

    public String visit(Order order){
        //select ccount_id,symbol,amount,limit from db;
        //check if valid
        return null;
    }
}
