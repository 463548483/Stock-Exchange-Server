/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Exchange.Matching.server;
import Exchange.Matching.server.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Server {
    private ServerSocket socket;
    //private Proxy proxy;
    //private Checker checker;
    private static int task_id;
    private static db stockDB;

    public Server() throws IOException, SQLException{
        socket=new ServerSocket(12345);
        task_id=0;
        stockDB = new db();
    }

    public void listen() throws Exception{
        Socket socket=this.socket.accept();
        task_id++;
        new Thread(new Task(socket,task_id)).start();
    }

    class Task implements Runnable{
        private Socket socket;
        private DataInputStream Trans;
        private FileOutputStream fileout;
        private int task_id;
        public Task(Socket socket,int task_id){
            this.socket=socket;
            this.task_id=task_id;

        }
        
        @Override
        public void run(){
            try{
                // InputStreamReader reader=new InputStreamReader(socket.getInputStream());
                // System.out.println((char)reader.read());
                Trans=new DataInputStream(socket.getInputStream());
                long fileLen=Trans.readLong();
                System.out.println(fileLen);

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc=builder.parse(Trans);
                switch (doc.getFirstChild().getNodeName()){
                    case "create" :
                        create_parse(doc.getFirstChild());
                    case "transactions":
                        //transactions_parse(doc.getFirstChild());
                }


            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{

                    socket.close();
                }catch(Exception e){}
            }
        }
    }

    void create_parse(Node n) {
        for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
            switch (child.getNodeName()){
            case "account":
                NamedNodeMap account_attrs= child.getAttributes();
                for(int j=0;j<account_attrs.getLength();j++){
                    Node x=account_attrs.item(j);
                    System.out.println(x.getNodeName()+" "+x.getNodeValue());
                } 
                break;
            case "symbol":
                NamedNodeMap sym_attrs= child.getAttributes();
                for(int j=0;j<sym_attrs.getLength();j++){
                    Node x=sym_attrs.item(j);
                    System.out.println(x.getNodeName()+" "+x.getNodeValue());
                }
                for (Node sym_child = child.getFirstChild(); sym_child != null; sym_child = sym_child.getNextSibling()){
                    if (sym_child.getNodeName()=="account"){
                        NamedNodeMap sym_account=sym_child.getAttributes();
                        System.out.println(sym_child.getNodeValue()+sym_child.getTextContent());
                        for(int j=0;j<sym_account.getLength();j++){
                            Node x=sym_account.item(j);
                            System.out.println(x.getNodeName()+" "+x.getNodeValue());
                        }
                    }
                }

            }
        }
    }
    
    public static void main(String[] args) throws SQLException {
        try{
            Server server=new Server();
            server.listen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
