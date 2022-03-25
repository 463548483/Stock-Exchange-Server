package Exchange.Matching.server;

import java.util.IllformedLocaleException;

public class Sym {
    private int id;
    private String sym;

    public Sym(int id, String sym){
        this.id = id;
        this.sym = sym;
    }

    public String getSym(){
        return this.sym;
    }
}
