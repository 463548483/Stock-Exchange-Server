package Exchange.Matching.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client extends Socket {
    private static final String hostName = "127.0.0.1";
    private static final int portNum = 12345;
    private Socket socket;
    private DataOutputStream toTrans;

    public Client() throws UnknownHostException, IOException {
        super(hostName, portNum);
        this.socket = this;
        System.out.println("Client connected");
    }

    public void send(String filename) throws Exception {

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
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))){
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

    public static void main(String[] args) {
        try (Client client = new Client()) {
            client.send(args[0]);
            client.receive();
        } catch (Exception e) {
            e.printStackTrace();           
        }

    }
}
