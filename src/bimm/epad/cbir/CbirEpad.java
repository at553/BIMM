package bimm.epad.cbir;




import java.io.File;
import java.io.IOException; 
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bimm.epad.aim.AimToolsEpad;
import bimm.epad.distance.DummyDistanceEpad;

import com.pixelmed.dicom.DicomException;

import aim.AimTools;
import cbir.BimmImageAnnotation;
import cbir.BimmImageAnnotation;
import cbir.Cbir;
import cbir.distance.DummyDistance;
import cbir.structure.SortedHashTable;
import cbir.structure.StringCouple;

import edu.stanford.hakan.aim4api.base.AimException;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageAnnotation;

/**
 * The Content-Based Image retrieval Class
 * 
 * @author kurtz
 *
 */

public class CbirEpad {

	private ServletContext servletContext;
	/**
	 * The UID of the AIM input file
	 */
	private String inputAimUID = null;

	/**
	 * Where we store the generated images
	 */
	//private String resultingImagesPath="./webapps/bimm/img/temp/";
	private String resultingImagesPath;

	/**
	 * The bimm input object : the query
	 */
	private BimmImageAnnotationEpad bia=null;
 
	/**
	 * Storing the objects founded and their distances
	 */
	private SortedHashTable <BimmImageAnnotationEpad> distancesToObjects = null;
	
	
	public SortedHashTable<BimmImageAnnotationEpad> getDistancesToObjects() {
		return distancesToObjects;
	}
	 private HashMap<BimmImageAnnotationEpad, Double> biaToDistanceMap = 
			 new HashMap<BimmImageAnnotationEpad, Double>();

	public HashMap<BimmImageAnnotationEpad, Double> getBiaToDistanceMap() {
		return biaToDistanceMap;
	}

	public void setDistancesToObjects(
			SortedHashTable<BimmImageAnnotationEpad> distancesToObjects) {
		this.distancesToObjects = distancesToObjects;
	}


	/**
	 * Storing distance and jpegs for an object
	 */
	private SortedHashTable <String> distancesToJpegMap = null;

	public CbirEpad(String inputAimID, String pathToImages) throws HttpException, IOException, AimException{	
		inputAimUID = inputAimID;
		// Create a folder under img to store images retrieved from BIMM
		String updatedPath = createFolder(pathToImages);
		this.resultingImagesPath = updatedPath;
		//----Create the input bimm object
		//--From server
		ImageAnnotation imageAnnotation = AimToolsEpad.getAIMFileFromDatabase(inputAimUID);

		//--From Disk
		//ImageAnnotation imageAnnotation = AnnotationGetter.getImageAnnotationFromFile(pathAimFile,"/home/kurtz/workspace/toolsQIL/ressources/AIM_v3.xsd");

		//Create a BIMM object with the query
		bia=new BimmImageAnnotationEpad(imageAnnotation);
		bia.loadImagingContent();
		bia.saveImagingContent(resultingImagesPath);
		bia.printBimmImageAnnotation();

	}

	private String createFolder(String pathToImages) {
		String path = pathToImages + "img" + File.separator;
		File imageStore = new File(path + "imageStore");
		if (imageStore.exists())
		{
			//Clear contents
			if (imageStore.isDirectory() && imageStore.listFiles() != null)
	    	{
	    		File[] imageFiles = imageStore.listFiles();
	    		for (File imageFile: imageFiles)
	    		{
	    			imageFile.delete();
	    		}
	    	}
		}
		else
		// TODO Auto-generated method stub
			imageStore.mkdir();
		return path + "imageStore" + File.separator;
	}

	/*public void run() throws HttpException, IOException, AimException {	

		//-----------Query the database to find the same kind of AIM files (with the same CodeValue)
		//String codeValueOfTheQueryAimFile = bia.getCodeValueOfImageAnnotation();
		//String whereRequest="where ($x//ns:ImageAnnotation[@codeValue='"+codeValueOfTheQueryAimFile+"'])";

		String whereRequest = bia.getCodeValueOfImageAnnotation();
		System.out.println(whereRequest);
		//ask camille how we can change this to use epad instead of dbxml (Pass in as parameter!!!)
		List<ImageAnnotation> imageAnnotationsFounded = AimToolsEpad.getAIMFilesFromDatabaseWithXQuery(whereRequest);
		
		//--Create BIMM objects with query results
		ArrayList<BimmImageAnnotationEpad> biaList=new ArrayList<BimmImageAnnotationEpad>();
		for(ImageAnnotation ia:imageAnnotationsFounded){
			BimmImageAnnotationEpad biaFounded = new BimmImageAnnotationEpad(ia);
			
			//We check to not add the query in the results !
			if(!biaFounded.getAimUID().equals(bia.getAimUID()))
				biaList.add(biaFounded);
		}

		//--Load the bimm objects content
		for(BimmImageAnnotationEpad biaFounded:biaList){
			biaFounded.loadImagingContent();
			biaFounded.saveImagingContent(resultingImagesPath);
			//biaFounded.printBimmImageAnnotation();
		}

		//---------Compute the distances between the query object and the similar ones
		//Initialize the distance
		ArrayList<TreeMap<StringCouple, TreeSet<StringCouple>>> listofSetsOfImagingObservations =new ArrayList<TreeMap<StringCouple,TreeSet<StringCouple>>>();
		for(BimmImageAnnotationEpad biaFounded:biaList){
			listofSetsOfImagingObservations.add(biaFounded.getImagingObservations());
		}
		
		DummyDistanceEpad dd=new DummyDistanceEpad(listofSetsOfImagingObservations);
		//EMDDistance dd=new EMDDistance(listofSetsOfImagingObservations);
		
		//Compute the distances and store them
		distancesToObjects = new SortedHashTable<BimmImageAnnotationEpad>();
		for(BimmImageAnnotationEpad biaFounded:biaList){
			//Compute distance
			double distance=dd.computeDistance(bia.getImagingObservations(), biaFounded.getImagingObservations());
			//Store it
			distancesToObjects.put(distance, biaFounded);
			this.biaToDistanceMap.put(biaFounded, distance);
			//distancesToJpegMap.put(distance, resultingImagesPath+""+biaFounded.getAimUID()+".jpg");
			//print the distance
			System.out.println("Distance between "+bia.getAimUID()+" and "+biaFounded.getAimUID()+" = "+distance);
		}
		//Print the ordered distance
		//distancesToObjects.print();*/
//	} */
	public void run() throws IOException{ //throws HttpException, IOException, AimException {	
		List<String> aimUIDMatches = new ArrayList<String>();
		//-----------Query the database to find the same kind of AIM files (with the same CodeValue)
		//String codeValueOfTheQueryAimFile = bia.getCodeValueOfImageAnnotation();
		//String whereRequest="where ($x//ns:ImageAnnotation[@codeValue='"+codeValueOfTheQueryAimFile+"'])";

		String whereRequest = bia.getCodeValueOfImageAnnotation();
		System.out.println(whereRequest);
		
		List<ImageAnnotation> imageAnnotationsFounded = AimToolsEpad.getAIMFilesFromDatabaseWithXQuery(whereRequest);
		
		//--Create BIMM objects with query results
		ArrayList<BimmImageAnnotationEpad> biaList=new ArrayList<BimmImageAnnotationEpad>();
		for(ImageAnnotation ia:imageAnnotationsFounded){
			BimmImageAnnotationEpad biaFounded = new BimmImageAnnotationEpad(ia);
			
			//We check to not add the query in the results !
			if(!biaFounded.getAimUID().equals(bia.getAimUID()))
			{
				// TEMP LIMIT SIZE __ AVINASH ADDED TO AVOID MEM ISSUES
				// REMOVE THIS LIMIT
			if ( biaList.size() < 50)
				biaList.add(biaFounded);
			}
		}

	
		//--Load the bimm objects content
		for(BimmImageAnnotationEpad biaFounded:biaList){
			try {
				biaFounded.loadImagingContent();
				biaFounded.saveImagingContent(resultingImagesPath);
				aimUIDMatches.add(biaFounded.getAimUID());
			}
			catch(Throwable t)
			{
				System.out.println("Failed to load image for " + biaFounded.getAimUID() + " due to an internal error");
			}
			//biaFounded.printBimmImageAnnotation();
		}

//		//---------Compute the distances between the query object and the similar ones
//		//Initialize the distance
//		ArrayList<TreeMap<StringCouple, TreeSet<StringCouple>>> listofSetsOfImagingObservations =new ArrayList<TreeMap<StringCouple,TreeSet<StringCouple>>>();
//		for(BimmImageAnnotationEpad biaFounded:biaList){
//			listofSetsOfImagingObservations.add(biaFounded.getImagingObservations());
//		}
//		
//		DummyDistanceEpad dd=new DummyDistanceEpad(listofSetsOfImagingObservations);
//		//EMDDistance dd=new EMDDistance(listofSetsOfImagingObservations);
//		
//		//Compute the distances and store them
//		distancesToObjects = new SortedHashTable<BimmImageAnnotationEpad>();
//		for(BimmImageAnnotationEpad biaFounded:biaList){
//			//Compute distance
//			double distance=dd.computeDistance(bia.getImagingObservations(), biaFounded.getImagingObservations());
//			//Store it
//			distancesToObjects.put(distance, biaFounded);
//			this.biaToDistanceMap.put(biaFounded, distance);
//			//print the distance
//			System.out.println("Distance between "+bia.getAimUID()+" and "+biaFounded.getAimUID()+" = "+distance);
//		}
//		//Print the ordered distance
//		distancesToObjects.print();
//		//return aimUIDMatches;
	}



	public String generateXMLResults() throws TransformerException, ParserConfigurationException{

		//----------create an xml document storing the result
		//---We need a Document
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		//---create the root element and add it to the document
		Element root = doc.createElement("BimmResults");
		doc.appendChild(root);

		//---query
		Element query = doc.createElement("QueryCollection");
		root.appendChild(query);

		Element child = doc.createElement("query");
		child.setAttribute("aimUID", bia.getAimUID());
		child.setAttribute("imgPath",resultingImagesPath+bia.getAimUID()+".jpg");
		query.appendChild(child);
		
		for(Entry<StringCouple, TreeSet<StringCouple>>  entry: bia.getImagingObservations().entrySet()) {
			StringCouple couple = entry.getKey();
			
			//We store these ImageObservation info into the xml
			Element obs = doc.createElement("imagingObservation");
			obs.setAttribute("codeValue", couple.getElement1());
			obs.setAttribute("codeMeaning", couple.getElement2());
			
			//If some ImageObservationCharacteristic are available for this specific ImageObservation
			if(entry.getValue()!=null){
				for(StringCouple c:entry.getValue()){
					Element obs_sub = doc.createElement("imagingObservationCharacteristic");
					obs_sub.setAttribute("codeValue", c.getElement1());
					obs_sub.setAttribute("codeMeaning", c.getElement2());
					obs.appendChild(obs_sub);
				}
			}
			query.appendChild(obs);
		}
		
		//---query results
		Element results = doc.createElement("QueryResults");
		results.setAttribute("numberOfResults", new Long(this.distancesToObjects.size()).toString());
		root.appendChild(results);

		//We parse the results to create xml entries
		int cpt=1;
		for (Map.Entry<Double, TreeSet <BimmImageAnnotationEpad>> entree : distancesToObjects.structure.entrySet()) {
			TreeSet <BimmImageAnnotationEpad> arbre=entree.getValue();
			for(BimmImageAnnotationEpad obj:arbre){
				Element result = doc.createElement("result");
				result.setAttribute("aimUID", obj.getAimUID());
				result.setAttribute("imgPath",resultingImagesPath+obj.getAimUID()+".jpg");
				result.setAttribute("score",entree.getKey().toString());
				result.setAttribute("ranking",new Integer(cpt).toString());

				results.appendChild(result);	
			}
			cpt++;
		}


		//Output the XML

		//set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		//trans.setOutputProperty(OutputKeys.INDENT, "yes");

		//create string from xml tree
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		trans.transform(source, result);
		String xmlString = sw.toString();

		//print xml
		//System.out.println("Here's the xml:" + xmlString); 

		return xmlString;


	}


	public static void main(String[] args) throws AimException, IOException, DicomException, ParserConfigurationException, TransformerException {
		String aimID = "263431782.0"; //epad1

		Cbir cbi = new Cbir(aimID);
		//cbi.run();
		//*** To-DO AIM V4 - Hakan
		cbi.generateXMLResults();

	}

}
