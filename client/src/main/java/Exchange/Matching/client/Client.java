package Exchange.Matching.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class Client extends Socket {
    private static final String hostName="127.0.0.1";
    private static final int portNum=12345;
    private Socket socket;
    private InputStream filein;
    private DataOutputStream toTrans;

    public Client() throws UnknownHostException, IOException {
        super(hostName,portNum);
        this.socket=this;
        System.out.println("Client connected");
    }

    public void send(String filename) throws Exception {
        try{

            filein=getClass().getClassLoader().getResourceAsStream(filename);//new FileInputStream(file);
            int fileLen=filein.available();
            toTrans=new DataOutputStream(socket.getOutputStream());

            System.out.println("send len "+fileLen);
            toTrans.writeLong(fileLen);
            toTrans.flush();
            

            System.out.println("start send xml");
            byte[] bytes=new byte[1024];
            for (;;){
                int n=filein.read(bytes,0,bytes.length);
                if (n==-1){
                    break;
                }
                toTrans.write(bytes,0,n);
                toTrans.flush();
            }
            System.out.println("finish client send");

        }catch(Exception e){
                e.printStackTrace();
        }finally{
                if(filein!=null){
                    filein.close();
                }
                if (toTrans!=null){
                    toTrans.close();
                }
        }
    }
    
    public void receive() throws IOException{
        try{
            InputStream toReceive=new DataInputStream(socket.getInputStream());
            byte[] bytes=new byte[1024]; 
            for(;;){
                int n=toReceive.read(bytes,0,bytes.length);
                if (n==-1){
                    break;
                }
                System.out.print(toReceive);
            }
            System.out.println("Client Receive Success");
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try(Client client=new Client()){
            client.send("empty.xml");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
