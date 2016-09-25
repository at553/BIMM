package bimm.epad.aim;

import java.io.File;  
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bimm.epad.ClientAuthentication;
import bimm.epad.utils.UrlBuilder;

import com.pixelmed.dicom.DicomException;

import xml.XmlNamespaceTranslator;
import xml.XmlTools;
import edu.stanford.hakan.aim4api.base.AimException;
import edu.stanford.hakan.aim4api.base.ImageAnnotationCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageAnnotation;
import edu.stanford.hakan.aim4api.usage.AnnotationGetter;
import file.FileTools;

public class AimToolsEpad {

	// Exist and epad-dev2
	// public static String
	// serverPathAimFiles="http://epad-dev2.stanford.edu:8899/exist/rest/";
	public static String serverPathAimFiles = "http://epad-prod1.stanford.edu:8899/exist/rest/";
	// public static String collection="liver_v3.dbxml";
	public static String collection = "aim.dbxml";
	public static String xsdFile = "/home/kurtz/workspace/bimm/ressources/AIM_v3.xsd";

	// http://epad-dev2.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin
	// public static String xsdFile= "/home/bimm/bimm/ressources/AIM_v3.xsd";

	/**
	 * Giving a unique AIM id, the method return the AIM file contained in the
	 * exist database
	 * 
	 * @param aimUniqueId
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ImageAnnotation getAIMFileFromDatabase(String aimUniqueId)
			throws HttpException, IOException, AimException {

		// --Get the AIM file from REST of the exist database on epad-dev2
		CloseableHttpClient client = HttpClientBuilder.create().build();
		String sessionID = ClientAuthentication.getID("admin", "admin");
		HttpGet httpget = new HttpGet(UrlBuilder.getInstance().getAimByUID(
				aimUniqueId));
		httpget.setHeader("Cookie", "JSESSIONID=" + sessionID);
		String requestQuery = UrlBuilder.getInstance().getAimByUID(aimUniqueId);

		HttpResponse response = client.execute(httpget);

		System.out.println(response.getStatusLine().getStatusCode());
		String val = null;
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				val = EntityUtils.toString(responseEntity);
				System.out.println(val);
			}
		}
		// Storing the result
		List<ImageAnnotationCollection> resAIMC = null;
		List<ImageAnnotation> resAIM = new ArrayList<ImageAnnotation>();

		/*
		 * if(statusCode != -1 ) { try { //Get the xml result as string String
		 * res = method.getResponseBodyAsString(); //System.out.println(res);
		 * 
		 * //Convert it in valid stream InputStream rawStream =
		 * XmlTools.convertStringToValidAimXMLStream(res);
		 * 
		 * //Convert into an AIM object resAIM =
		 * AnnotationGetter.getImageAnnotationFromInputStream
		 * (rawStream,xsdFile);
		 * 
		 * //Close connection method.releaseConnection(); } catch( Exception e )
		 * { e.printStackTrace(); } }
		 */
		//try {
			resAIMC = AnnotationGetter.getImageAnnotationCollectionsFromString(
					val, "");
			for (ImageAnnotationCollection iac : resAIMC)
				resAIM.add(new ImageAnnotation(iac));

	/*	} catch (AimException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw e;
		}*/
		return resAIM.get(0);
	}

	/**
	 * Giving an xQuery, the method return the AIM files contained in the exist
	 * database returned by the query
	 * 
	 * @param request
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * 
	 **/
	public static List<ImageAnnotation> getAIMFilesFromDatabaseWithXQuery(
			String whereRequest) throws HttpException, IOException {

		// --Get the AIM file from REST of the exist database on epad-dev2
		HttpClient client = new HttpClient();

		String nameSpace = "gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM";
		String nameSpaceQuery = "declare namespace ns=\"" + nameSpace + "\";";
		String xQuery = nameSpaceQuery + "for $x in collection('" + collection
				+ "') " + whereRequest + " return $x";

		ArrayList<ImageAnnotation> resAIM = new ArrayList<ImageAnnotation>();
		HttpGet httpget = new HttpGet(
				//"http://epad-dev5.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin
				"http://epad-dev5.stanford.edu:8080/epad/v2/aims/?start=0&count=50");
		// PostMethod method = new
		// PostMethod("http://epad-dev5.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin");

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String sessionID = ClientAuthentication.getID("admin", "admin");
		httpget.setHeader("Cookie", "JSESSIONID=" + sessionID);
		HttpResponse response = httpClient.execute(httpget);
		// method.setParameter("_namespace",myNameSpace);
		// only the one above was commented out originally
		// method.setParameter("_query",xQuery);
		// method.setParameter("_howmany","1000");
		// method.setParameter("codeValue", whereRequest);
		// httpget.setParameter("annnotationUID", "all");
		// httpget.setParameter("user", "admin");

		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String val = EntityUtils.toString(responseEntity);

				try {

//					/*List<ImageAnnotationCollection> tempList = AnnotationGetter.getImageAnnotationCollectionsFromString(val, "");
//					
//					for (ImageAnnotationCollection tempImageAnno: tempList)
//					{
//						resAIM.add(new ImageAnnotation(tempImageAnno));
//						
//						
//					}*/
					List<ImageAnnotationCollection> tempList2;
					//premature end of file
					tempList2 = AnnotationGetter.getImageAnnotationCollectionByImageAnnotationCodeEqual(
						       "http://epad-dev5.stanford.edu:8899/exist",
						       "gme://caCORE.caCORE/4.4/edu.northwestern.radiology.AIM",
						       "aimV4.dbxml", "epaduser", "3p4dus3r",
						       whereRequest);
						      
					
					for (ImageAnnotationCollection tempImageAnno: tempList2)
					{
						resAIM.add(new ImageAnnotation(tempImageAnno));
						
						
					}
					
					
				
					System.out.println("Found " + resAIM.size()
							+ " items with the same CodeValue !");
				} catch (AimException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return resAIM;

	}

	/**
	 * The method return the AIM files contained in the exist database
	 * 
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static ArrayList<ImageAnnotation> getAIMFilesFromDatabaseWithXQuery()
			throws HttpException, IOException {

		/*--Get the AIM file from REST of the exist database on epad-dev2*/
		HttpClient client = new HttpClient();

		String nameSpace = "gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM";
		String nameSpaceQuery = "declare namespace ns=\"" + nameSpace + "\";";
		String xQuery = nameSpaceQuery + "for $x in collection('" + collection
				+ "') return $x";

		PostMethod method = new PostMethod(
				//"http://epad-dev5.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin
				"http://epad-dev5.stanford.edu:8080/epad/v2/aims/?start=0&count=50");

		String sessionID = ClientAuthentication.getID("admin", "admin");
		method.setRequestHeader("Cookie", "JSESSIONID=" + sessionID);
		// method.setParameter("_namespace",myNameSpace);

		// method.setParameter("_query",xQuery);
		// method.setParameter("_howmany","1000");

		// Execute the POST method
		int statusCode = client.executeMethod(method);

		// Storing the result
		ArrayList<ImageAnnotation> resAIM = null;

		if (statusCode != -1) {
			try {
				resAIM = new ArrayList<ImageAnnotation>();

				// Get the xml results as string
				String res = method.getResponseBodyAsString();

				// Convert it into an xml file
				Document doc = XmlTools.getDocFromString(res);
				Node n = doc.getFirstChild();

				NodeList children = n.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					Node child = children.item(i);

					// Convert each child into an ImageAnnotaion object
					if (child != null
							&& child.getLocalName() == "ImageAnnotation") {
						InputStream stream = XmlTools.nodeToInputStream(child);
						// Same stuff here -- getImageAnnotation FromInputStream
						// is weird...
						// ImageAnnotation ia=
						// AnnotationGetter.getImageAnnotationFromInputStream(stream,xsdFile);
						ImageAnnotation ia = null;
						// Adding this AIM file to the result
						resAIM.add(ia);
					}
				}

				// Close connection
				method.releaseConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return resAIM;

	}

	/**
	 * 
	 * Get all UID of the AIM files contained in a database return an xml file
	 * which contains the answers
	 * 
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public static String getALlAimUIDFromDatabase() throws HttpException,
			IOException, ParserConfigurationException, TransformerException {
		String xmlRes = null;

		// Get the AIM files from the database
		ArrayList<ImageAnnotation> listIA = getAIMFilesFromDatabaseWithXQuery();

		// ----------create an xml document storing the result
		// ---We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		// ---create the root element and add it to the document
		Element root = doc.createElement("AimFilesCollection");
		doc.appendChild(root);

		// We parse the results to create the nodes
		for (ImageAnnotation ia : listIA) {
			Element file = doc.createElement("aimFile");
			file.setAttribute("aimUID", ia.getUniqueIdentifier());
			file.setAttribute("date", ia.getDateTime());
			root.appendChild(file);
		}

		// Output the XML

		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		// trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		xmlRes = sw.toString();

		// print xml
		// System.out.println("Here's the xml:" + xmlRes);

		return xmlRes;
	}

	private static String clean(String input, String removeChar) {
		if (input == null)
			return null;
		if (removeChar == null)
			return input;

		return input.replaceAll(removeChar, " ").trim();
	}

	public static void main(String[] args) throws IOException, DicomException,
			AimException, SAXException, ParserConfigurationException,
			TransformerException {

		TreeSet<String> classes = new TreeSet<String>();

		classes.add("HJ0016327LT");
		classes.add("HJ003443942LT");
		classes.add("HJ0076068LT");
		classes.add("HJ03275211LT");
		classes.add("HJ040494LT");
		classes.add("HJ0595468LT");
		classes.add("HJ0619910LT");
		classes.add("HJ0639178LT");
		classes.add("HJ0699636LT");
		classes.add("HJ0849044LT");
		classes.add("HJ0852575LT");
		classes.add("HJ0866137LT");
		classes.add("HJ0876739LT");
		classes.add("HJ0906010LT");
		classes.add("HJ0908586LT");
		classes.add("HJ0949415LT");
		classes.add("HJ096081LT");
		classes.add("HJ0963835LT");
		classes.add("HJ09982174LT");
		classes.add("HJ1003564LT");
		classes.add("HJ10337418LT");
		classes.add("HJ1043514LT");
		classes.add("HJ1062614LT");
		classes.add("HJ10729333LT");
		classes.add("HJ1086548LT");
		classes.add("HJ1088557LT");
		classes.add("HJ10907707LT");
		classes.add("HJ10918969LT");
		classes.add("HJ1093999LT");
		classes.add("HJ12205LT");
		classes.add("HJ12862LT");
		classes.add("HJ12965505LT");
		classes.add("HJ13400106LT");
		classes.add("HJ13541LT");
		classes.add("HJ177389LT");
		classes.add("HJ19517LT");
		classes.add("HJ2043117LT");
		classes.add("HJ2093664LT");
		classes.add("HJ209739LT");
		classes.add("HJ211955LT");
		classes.add("HJ242741LT");
		classes.add("HJ2478LT");
		classes.add("HJ338989LT");
		classes.add("HJ370551LT");
		classes.add("HJ410999LT");
		classes.add("HJ425411LT");
		classes.add("HJ517818LT");
		classes.add("HJ526875LT");
		classes.add("HJ5291LT");
		classes.add("HJ5472773LT");
		classes.add("HJ551836LT");
		classes.add("HJ5570335310LT");
		classes.add("HJ568768606LT");
		classes.add("HJ5978017LT");
		classes.add("HJ60123452LT");
		classes.add("HJ6024160LT");
		classes.add("HJ6026055LT");
		classes.add("HJ60366416LT");
		classes.add("HJ60440468LT");
		classes.add("HJ6057160LT");
		classes.add("HJ60847035LT");
		classes.add("HJ609579LT");
		classes.add("HJ63551LT");
		classes.add("HJ6395511LT");
		classes.add("HJ649170LT");
		classes.add("HJ660650LT");
		classes.add("HJ67567LT");
		classes.add("HJ697783LT");
		classes.add("HJ717516LT");
		classes.add("HJ725944LT");
		classes.add("HJ768699LT");
		classes.add("HJ780536LT");
		classes.add("HJ813700LT");
		classes.add("HJ8412515LT");
		classes.add("HJ8417155LT");
		classes.add("HJ847809LT");
		classes.add("HJ8516907LT");
		classes.add("HJ856034LT");
		classes.add("HJ8715415LT");
		classes.add("HJ8720502LT");
		classes.add("HJ8810678LT");
		classes.add("HJ894687LT");
		classes.add("HJ9020085LT");
		classes.add("HJ90924LT");
		classes.add("HJ9128189LT");
		classes.add("HJ914693LT");
		classes.add("HJ920011LT");
		classes.add("HJ931006LT");
		classes.add("HJ933125LT");
		classes.add("HJ9524731LT");
		classes.add("HJ956305LT");
		classes.add("HJ9600070LT");
		classes.add("HJ9830990LT");

		File dir_aim = new File("/home/kurtz/annot/db/aim.dbxml");
		String dirSave = "/home/kurtz/Documents/Backup_vintia/Documents/Travail_Postdoc/chris/bones tumor/RSNA/full_2";

		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		};

		File[] files = dir_aim.listFiles(fileFilter);
		if (files == null) {
			System.out
					.println("Directory does not exist or is not a Directory");
		} else {
			for (int i = 0; i < files.length; i++) { // Get filename of file or
														// directory
				File file = files[i];
				System.out.println(file.getName());

				String aimUID = FileTools.stripExtension(file.getName());
				System.out.println("aimUID =" + aimUID);

				// Get position of last '.'.
				int pos = aimUID.lastIndexOf("_");
				// Otherwise return the string, up to the dot.
				aimUID = aimUID.substring(pos + 1, aimUID.length());
				System.out.println("new aimUID =" + aimUID);

				String pathAimFile = file.getPath();

				ImageAnnotation imageAnnotation = new ImageAnnotation(
						AnnotationGetter
								.getImageAnnotationCollectionFromFile(
										pathAimFile,
										"/home/kurtz/workspace/toolsQIL/ressources/AIM_v3.xsd"));

				String patientName = imageAnnotation.getListPerson().get(0)
						.getName();
				String cleanedName = clean(patientName, "\\^");

				if (classes.contains(cleanedName)
						&& imageAnnotation.getCodeValue().equals("RID35406-2")) {
					System.out.println(cleanedName);

					FileTools.copyFile(file, new File(dirSave + "/" + aimUID
							+ ".xml"));
				}

			}
		}

	}

}
