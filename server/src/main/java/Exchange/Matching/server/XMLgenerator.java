package Exchange.Matching.server;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class XMLgenerator {
    public static void createResponseXml(Map<String,Object> map){
        try{
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder bd=factory.newDocumentBuilder();
            Document document=bd.newDocument();
            //document.setXmlStandalone(true);
            Element result=document.createElement("result");
            for (String status:map.keySet()){
                if(status.equals("create")){
                    Element error=document.createElement("created");
                }
                if(status.equals("error")){
                    Element error=document.createElement("error");
                    error.setTextContent("MSG");//TODO
                }
            }
        }
    }
}
