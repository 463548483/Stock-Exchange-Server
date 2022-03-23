package Exchange.Matching.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Server {


    /*
     * Build Connection with the DataBase "stockDB"
     * 
     * @return Connection
     */
    public Connection buildDBConnection(){
        String url = "jdbc:postgresql://localhost:5432/stockDB";
        String username = "postgres";
        String password = "zjwyy";
        try{
            Class.forName("org.postgresql.Drvier");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Successfully connected to DataBase.");
            return connection;
        }catch(Exception e){
            System.out.print(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void main() {
        
    }
}
