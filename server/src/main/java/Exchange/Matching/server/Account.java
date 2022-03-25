package Exchange.Matching.server;

import java.util.ArrayList;

public class Account {
    private int id;
    private int balance;
    private ArrayList<Position> positions;

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


