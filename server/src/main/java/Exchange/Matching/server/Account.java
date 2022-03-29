package Exchange.Matching.server;

import java.util.ArrayList;

public class Account {
    private int id;
    private double balance;
    private ArrayList<Position> positions;

    public Account(int id, double balance){
        this.id = id;
        this.balance = balance;
        this.positions=new ArrayList<Position>();
    }

    public int getID(){
        return this.id;
    }
    
    public double getBalance(){
        return this.balance;
    }

    public void insertPosition(Position p){
        positions.add(p);
    }
}


