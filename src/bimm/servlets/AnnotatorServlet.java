package bimm.servlets;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class AnnotatorServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
			doPost(req, resp);
		}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
		try{
			//Get the value from freeTextArea (index.jsp)
			String medicalSentence = "";
			medicalSentence = req.getParameter("freeTextArea");
			//System.out.println("medicalSentence = "+medicalSentence);
			
			//Code client for Biomedical Annotator Client Example - Extract terms included in RadLex ontology
			HttpClient client = new HttpClient();
            client.getParams().setParameter(
            HttpMethodParams.USER_AGENT,"Annotator Client Example - Annotator");  //Set this string for your application 
            
            String annotatorUrl = "http://rest.bioontology.org/obs/annotator";
           
            PostMethod method = new PostMethod(annotatorUrl);
            
            // Configure the form parameters
            method.addParameter("longestOnly","false");
            method.addParameter("wholeWordOnly","true");
            method.addParameter("filterNumber", "true");
            //method.addParameter("stopWords","I,a,above,after,against,all,alone,always,am,amount,an,and,any,are,around,as,at,back,be,before,behind,below,between,bill,both,bottom,by,call,can,co,con,de,detail,do,done,down,due,during,each,eg,eight,eleven,empty,ever,every,few,fill,find,fire,first,five,for,former,four,from,front,full,further,get,give,go,had,has,hasnt,he,her,hers,him,his,i,ie,if,in,into,is,it,last,less,ltd,many,may,me,mill,mine,more,most,mostly,must,my,name,next,nine,no,none,nor,not,nothing,now,of,off,often,on,once,one,only,or,other,others,out,over,part,per,put,re,same,see,serious,several,she,show,side,since,six,so,some,sometimes,still,take,ten,then,third,this,thick,thin,three,through,to,together,top,toward,towards,twelve,two,un,under,until,up,upon,us,very,via,was,we,well,when,while,who,whole,will,with,within,without,you,yourself,yourselves");
            method.addParameter("withDefaultStopWords","true");
            method.addParameter("isTopWordsCaseSensitive","false");
            method.addParameter("mintermSize","3");
            method.addParameter("scored", "true");
            method.addParameter("withSynonyms","true"); 
            method.addParameter("ontologiesToExpand", "1057");
            method.addParameter("ontologiesToKeepInResult", "1057"); 
            method.addParameter("isVirtualOntologyId", "true"); 
            method.addParameter("semanticTypes", ""); 
            method.addParameter("levelMax", "0");
            method.addParameter("mappingTypes", "null"); //null, Automatic, Manual 
            method.addParameter("textToAnnotate", medicalSentence);  //"Melanoma is a malignant tumor of melanocytes which are found predominantly in skin but also in the bowel and the eye");
            method.addParameter("format", "xml"); //Options are 'text', 'xml', 'tabDelimited'   
            method.addParameter("apikey", "407b462c-4181-4bcf-9f83-c736b57b56b9 ");

            // Execute the POST method
            int statusCode = client.executeMethod(method);
            
            //Storing the result
            String res=null;
            
            if(statusCode != -1 ) {
                try {
                	//Get the xml result as string
    	            String contents = method.getResponseBodyAsString();
    	            //System.out.println(contents);
    	            
    	            //Close connection with bioportal
    	            method.releaseConnection();
    	            
    	            //convert the xml string result as xml
    	            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    	            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    	            
    	            InputSource is = new InputSource();
    	            is.setCharacterStream(new StringReader(contents));
    	            
    	            Document doc = docBuilder.parse(is);
    	            
    	            //parse the xml file to get the semantic terms and their ids
    	            NodeList nodes = doc.getElementsByTagName("annotationBean");
    	            
    	            //if some results have been found
    	            if(nodes.getLength()>=1){
    	            	res=""; //initialize the res
	    	            for (int i = 0; i < nodes.getLength(); i++) {
	    	                Element element = (Element) nodes.item(i);
	    	                
	    	                //parse the xml nodes
	    	                NodeList name = element.getElementsByTagName("preferredName");
	    	                Element line = (Element) name.item(0);
	    	                String nameConcept=getCharacterDataFromElement(line);
	    	                //System.out.println("preferredName: " + nameConcept);
	    	                
	    	                NodeList localConceptId = element.getElementsByTagName("localConceptId");
	    	                line = (Element) localConceptId.item(0);
	    	                String id=getCharacterDataFromElement(line);
	    	            
	    	                String[] id_transforms=id.split("/");
	    	                String id_transform=id_transforms[1];
	    	                //System.out.println("new localConceptId: " + id_transform);
	    	                
	    	                res+=id_transform+"@"+nameConcept+",";
	    	            }
	    	            //create a string containing the ids and the names of the terms
	    	            res=res.substring(0, res.length()-1);
    	            }else{
    	            	res="nothing";
    	            }
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
			
            //Res
			req.setAttribute("freeText", res);

		} catch(Exception e) {
			e.printStackTrace();
		}
		req.getRequestDispatcher("/index.jsp").forward(req, resp);
        return;
	}
	
	
	public static String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	      CharacterData cd = (CharacterData) child;
	      return cd.getData();
	    }
	    return "";
  }
	
	
	
	 
	
	
}
