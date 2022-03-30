package Exchange.Matching.server;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

public class XMLgenerator {
    private Element result;//root element of response
    private Document document;

    public XMLgenerator() {
        try{
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder bd=factory.newDocumentBuilder();
            document=bd.newDocument();
            result=document.createElement("result");
            document.appendChild(result);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void DOMtoXML(){
        try{    
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(System.out);

            transformer.transform(domSource, streamResult);
            System.out.println("Done creating XML File");
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }


    public void lineXML(XMLObject XMLobject,String status){
        
        Element accountStatus=document.createElement(status);
        if (status=="error"){
            accountStatus.appendChild(document.createTextNode(XMLobject.getErrorMessage()));
        }
        Map<String,String> arrMap=XMLobject.getAttribute();
        for (String arr:arrMap.keySet()){
            Attr attr = document.createAttribute(arr);
            attr.setValue(arrMap.get(arr));
            accountStatus.setAttributeNode(attr);
        }
        result.appendChild(accountStatus);
    }

}


