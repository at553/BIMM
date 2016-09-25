package bimm.tools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.xml.sax.SAXException;

import bimm.main.Launcher;

import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;



public class Query {

	/* Global variables */
	static XmlManager xmlManager = null;
	static XmlContainer xmlContainer = null;
	static String strResults = "";

	static String locLiverFile = "../bimm/databases/liver.dbxml";

	// NOTE: Change to your version of dbxml
	// -- CAMILLE local
	static String javaLibPath ="/home/kurtz/Software/dbxml-2.5.16/install/lib/";
	// -- RUFUS server
	//static String javaLibPath ="/home/bimm/dbxml-2.5.16/install/lib/";
	
	
	
	/**
	 * Given a String that is a list of lists of patient IDs:
	 * e.g. [[param1, param2, ...], [param1, param2, ...], ...]
	 * 
	 * If this is an empty string, return all images (i.e., all IDs)
	 * 
	 * parse the list and 
	 * @param listListsPatientIds
	 * @throws XmlException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws TransformerFactoryConfigurationError 
	 * @throws TransformerException 
	 */
	public static String crossProductQueries(String listListsPatientIds) throws XmlException, SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		// if this is an empty string, return all images
		if(listListsPatientIds.length() == 0 || listListsPatientIds.equals("[[]]"))
			return makeQuery(null);

		// remove last two characters
		listListsPatientIds = listListsPatientIds.substring(1, listListsPatientIds.length() - 1);

		/* Construct an ArrayList of lists of Strings */
		ArrayList<ArrayList<String>> listListsIds = new ArrayList<ArrayList<String>>();

		ArrayList<String> splitString;
		while(listListsPatientIds.length() > 0) {
			// strip off everything up to the first '['
			int indexBegList = listListsPatientIds.indexOf('[');
			listListsPatientIds = listListsPatientIds.substring(indexBegList);

			// look for the first end character, ']'
			int indexEndList = listListsPatientIds.indexOf(']');

			if(indexEndList <= 0)
				break;

			// get this list
			String list = listListsPatientIds.substring(1, indexEndList);

			// convert the list to an array of Strings and trim
			splitString = new ArrayList<String>();
			String[] arraySplitString = list.split(",");
			for(int i = 0; i < arraySplitString.length; i++) {
				splitString.add(arraySplitString[i].trim());
			}
			listListsIds.add(splitString);

			// remove this part from the big string
			listListsPatientIds = listListsPatientIds.substring(indexEndList + 1);
		}

		/* Get Cartesian Product of this list */
		ArrayList<ArrayList<String>> cartProduct = cartesianProduct(listListsIds);

		/* Go through each of these and make a query */
		StringBuilder sb = new StringBuilder();
		for(ArrayList<String> query : cartProduct) {
			System.out.println("query : "+query);
			sb.append(makeQuery(query));
		}

		return "<QueryResults>"+sb.toString()+"</QueryResults>";
	}

	/** 
	 * Given a list of ArrayLists, take the Cartesian product of the these lists
	 * @param list
	 * @return
	 */
	static ArrayList<ArrayList<String>> cartesianProduct(List<ArrayList<String>> list) {
		ArrayList<ArrayList<String>> resultLists = new ArrayList<ArrayList<String>>();
		if (list.size() == 0) {
			resultLists.add(new ArrayList<String>());
			return resultLists;
		} else {
			ArrayList<String> firstList = list.get(0);
			ArrayList<ArrayList<String>> remainingLists = cartesianProduct(list.subList(1, list.size()));
			for (String condition : firstList) {
				for (ArrayList<String> remainingList : remainingLists) {
					ArrayList<String> resultList = new ArrayList<String>();
					resultList.add(condition);
					resultList.addAll(remainingList);
					resultLists.add(resultList);
				}
			}
		}
		return resultLists;
	}

	/** 
	 * Set up the XML manager -- 
	 * @return
	 * @throws Exception
	 */
	public static void setUpXmlManager() throws Exception  {

		// declare XmlManager and XmlContainer
		xmlManager = new XmlManager();
		xmlManager.setDefaultContainerType(XmlContainer.NodeContainer);
		if (xmlManager.existsContainer(locLiverFile) != 0) {
			System.out.println("Exists!");
		}
		else {
			System.out.println("Does not exist!");
			System.exit(1);
		}

		xmlContainer = xmlManager.openContainer(locLiverFile);

	}

	/**
	 * Given a list of ids, construct the query and return the response.
	 * @param ids
	 * @return
	 */
	protected static String makeQuery(ArrayList<String> ids) throws XmlException{

		/* Construct query based on ids */
		/*String QUERY = "for $x in collection(\"" + locLiverFile + "\")" + 
		"where $x//ImagingObservationCharacteristic[@codeValue=\"RID5800\"]" + 
		"and $x//ImagingObservationCharacteristic[@codeValue=\"RID5707\"]" +
		"return $x";*/
		String QUERY = constructQuery(ids);

		// Querying requires an XmlQueryContext
		XmlQueryContext qc = xmlManager.createQueryContext();
		// Set the evaluation type to Lazy.
		qc.setEvaluationType(XmlQueryContext.Lazy);

		XmlQueryExpression qe = xmlManager.prepare(QUERY, qc);
		XmlResults results = xmlManager.createResults();
		results = qe.execute(qc);

		StringBuilder sb = new StringBuilder();
		while (results.hasNext()) {
			XmlValue xmlValue = results.next();

			// convert to document
			//XmlDocument doc = xmlValue.asDocument();
			//System.out.println("Document name: " + doc.getName());

			sb.append(xmlValue.asString());
			//System.out.println(xmlValue.asString());
		}
		return sb.toString();

	}

	/**
	 * Given a list of IDs, construct the query
	 * 
	 * If ids is null, then make a query to return all the queries
	 * 
	 * @param ids
	 * @return
	 */
	protected static String constructQuery(ArrayList<String> ids) {
		String QUERY = "for $x in collection(\"" + locLiverFile + "\")";

		if(ids == null || ids.size() == 0) {
			// just return all images
			QUERY += " return $x";
			return QUERY;
		}

		QUERY += " where ";

		String templates = "";
		int countTemplates = 0;
		for(String id : ids) {
			// if this is the first time, just init. the list
			if(countTemplates == 0) {
				templates = "$x//ImagingObservationCharacteristic[@codeValue=\"" + id + "\"]";
			}
			else {
				//templates += " and " + "$x//ImagingObservationCharacteristic[@codeValue=\"" + id + "\"]";
				templates += " or " + "$x//ImagingObservationCharacteristic[@codeValue=\"" + id + "\"]";
			}
			countTemplates++;
		}
		QUERY += templates + " return $x";

		System.out.println(QUERY);
		return QUERY;
	}
	
	
	public static void main(String[] args) throws Exception {
		// TODO: comment out
		Launcher.setJavaLibPath(javaLibPath);

		setUpXmlManager();

		/* Try out cross product */
		String dummyQueryList = "[[RID5800, RID5707], [RID5800, RID5707]]";
		
		// simulate 'Margin' query
		//String dummyQueryList = "[[RID5972,RID5713,RID34355,RID5712,RID5711,RID5710,RID5715,RID5709,RID34354,RID5714,RID5707]]";
//		String dummyQueryList = "[[]]";
		String result = crossProductQueries(dummyQueryList);                       
		             
		System.out.println(result);
		// dummy ArrayList
		/*ArrayList<String> exampleQuery = new ArrayList<String>();
		exampleQuery.add("RID5800");
		exampleQuery.add("RID5707");
		makeQuery(exampleQuery);*/
	}

}
