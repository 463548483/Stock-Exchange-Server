/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Exchange.Matching.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import javax.xml.parsers.*;
import org.w3c.dom.Document;


public class Server {
    private ServerSocket socket;
    private static int task_id;
    private static db stockDB;

    public Server() throws IOException, SQLException{
        socket=new ServerSocket(12345);
        task_id=0;
        stockDB = new db();

    }

    public void listen() throws Exception {
        while(true){
            Socket socket = this.socket.accept();
            task_id++;
            new Thread(new Task(socket, task_id)).start();
        } 
    }

    class Task implements Runnable {
        private Socket socket;
        private DataInputStream Trans;
        private int task_id;
        private Proxy proxy;

        public Task(Socket socket, int task_id) {
            this.socket = socket;
            this.task_id = task_id;
            proxy=new Proxy(stockDB,socket);

        }

        public void send() throws Exception {
            try (OutputStream response = socket.getOutputStream()){
                //response.write(proxy.ge.getBytes());
                response.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // InputStreamReader reader=new InputStreamReader(socket.getInputStream());
                // System.out.println((char)reader.read());
                Trans = new DataInputStream(socket.getInputStream());
                
                int fileLen = Trans.readInt();
                byte[] data = new byte[fileLen - 4];
                Trans.readFully(data);
                System.out.println(fileLen);
                

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new ByteArrayInputStream(data));
                switch (doc.getFirstChild().getNodeName()) {
                    case "create":
                        proxy.create_parse(doc.getFirstChild());
                        break;
                    case "transactions":
                        proxy.transactions_parse(doc.getFirstChild());
                        break;
                }
                System.out.println("finish generate response");
                send();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    Trans.close();
                } catch (Exception e) {

                }
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        try {
            Server server = new Server();
            while (true){
                server.listen();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
