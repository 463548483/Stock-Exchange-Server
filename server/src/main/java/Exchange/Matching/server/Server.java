/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package Exchange.Matching.server;
import Exchange.Matching.server.*;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


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


    public Server() throws IOException{
        socket=new ServerSocket(12345);
        task_id=0;
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
                Trans=new DataInputStream(socket.getInputStream());
                long fileLen=Trans.readLong();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc=builder.parse(Trans);
                switch (doc.getFirstChild().getNodeName()){
                    case "create" :
                        create_parse(doc.getFirstChild());
                    case "transactions":
                        transactions_parse(doc.getFirstChild());
                }
                //printNode(doc, 1);
                NodeList create_list=doc.getElementsByTagName("create");
                NodeList account_list=doc.getElementsByTagName("account");
                NodeList symbol_list=doc.getElementsByTagName("symbol");

                for (int i = 0; i < account_list.getLength(); i++) {
                    Node account = account_list.item(i);
                    System.out.println("Name: "+account.getNodeName()+" Value: "+account.getNodeValue()+" Type: "+account.getNodeType());
                    NamedNodeMap attrs= account.getAttributes();
                    for(int j=0;j<attrs.getLength();j++){
                        Node x=attrs.item(j);
                        System.out.println(x.getNodeName()+" "+x.getNodeValue()+" "+x.getNodeType());
                    }  
                }
                for (int i = 0; i < symbol_list.getLength(); i++) {
                    Node symbol = symbol_list.item(i);
                    System.out.println("Name: "+symbol.getNodeName()+" Value: "+symbol.getNodeValue()+" Type: "+symbol.getNodeType());
                    NamedNodeMap attrs= symbol.getAttributes();
                    for(int j=0;j<attrs.getLength();j++){
                        Node x=attrs.item(j);
                        System.out.println(x.getNodeName()+" "+x.getNodeValue()+" "+x.getNodeType());
                    }  
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

    void create_parse(Node n, int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print(' ');
        }
        switch (n.getNodeType()) {
        case Node.DOCUMENT_NODE: // Document节点
            System.out.println("Document: " + n.getNodeName());
            break;
        case Node.ELEMENT_NODE: // 元素节点
            System.out.println("Element: " + n.getNodeName());
            break;
        case Node.TEXT_NODE: // 文本
            System.out.println("Text: " + n.getNodeName() + " = " + n.getNodeValue());
            break;
        case Node.ATTRIBUTE_NODE: // 属性
            System.out.println("Attr: " + n.getNodeName() + " = " + n.getNodeValue());
            break;
        default: // 其他
            System.out.println("NodeType: " + n.getNodeType() + ", NodeName: " + n.getNodeName());
        }
        // for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling()) {
        //     switch (child.getNodeName()){
        //         case "account":
        //         case "symbol"
        //     }
        // }
    }

    public static void main(String[] args) {
        try{
            Server server=new Server();
            server.listen();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
