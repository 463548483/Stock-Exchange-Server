package Exchange.Matching.server;

public class Account {
    private int id;
    private int balance;

    public Account(int id, int balance){
        this.id = id;
        this.balance = balance;
    }

    public int getID(){
        return this.id;
    }
    
    public int getBalance(){
        return this.balance;
    }
}


