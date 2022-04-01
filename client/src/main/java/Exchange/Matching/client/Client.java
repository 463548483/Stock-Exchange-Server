package Exchange.Matching.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Socket implements Runnable {
    private static final String hostName = "127.0.0.1";
    private static final int portNum = 12345;
    private Socket socket;
    private DataOutputStream toTrans;
    private String filename;

    public Client(String filename) throws UnknownHostException, IOException {
        super(hostName, portNum);
        this.socket = this;
        System.out.println("Client connected");
        this.filename=filename;
    }

    public void send() throws Exception {

        try (Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream(filename))) {
            toTrans = new DataOutputStream(socket.getOutputStream());
            String str="";
            while (scanner.hasNextLine()) {
                str+=scanner.nextLine();
            }
            byte[] data = str.getBytes();
            int len = data.length + 4;
            toTrans.writeInt(len);
            toTrans.flush();
            toTrans.write(data);
            toTrans.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void receive() throws IOException {
        try {
            // DataInputStream dis=new DataInputStream(socket.getInputStream());
            // File file = new File("response.xml");
            // FileOutputStream fos=new FileOutputStream(file);
            // byte[] bytes = new byte[1024];
            // int length = 0;
            // while((length = dis.read(bytes, 0, bytes.length)) != -1) {
            //     fos.write(bytes, 0, length);
            //     fos.flush();
            // }
            // fos.close();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String str = bufferedReader.readLine();
                if (str == null) {
                    break;
                }
                System.out.println(str);
            }
            socket.close();
            toTrans.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            send();
            receive();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public static void main(String[] args) {
        for (int i=0;i<Integer.parseInt(args[1]);i++){
            Thread t;
            try {
                t = new Thread(new Client(args[0]));
                t.start();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            
            
        }

    }
}
