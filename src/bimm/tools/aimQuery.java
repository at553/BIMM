package bimm.tools;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import edu.stanford.hakan.aim3api.base.AimException;
import edu.stanford.hakan.aim3api.base.ImageAnnotation;
import edu.stanford.hakan.aim3api.base.Person;
import edu.stanford.hakan.aim3api.usage.AnnotationGetter;

public class aimQuery {

	// Class variables, filled by constructor
	private String namespace;
	private String serverUrlUpload;
	private String serverUrlDownload;
	
	/**
	 * @author Vanessa Sochat
	 *         Nifti Annotated Image Markup
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws TransformerException 
	 * @throws ParserConfigurationException 
	 * @throws AimException 
	 * @throws TransformerConfigurationException 
	 */
	public aimQuery(String namesp, String serverUp, String serverDown){
		namespace = namesp;
		serverUrlUpload = serverUp;
		serverUrlDownload = serverDown;
	    }

	// NOTE: We are not currently needing this functionality - ignore this method
	public void add(String aimfile) throws TransformerConfigurationException, AimException, ParserConfigurationException, TransformerException, SAXException, IOException {
						
		// check for duplicate image in database "test_db"
		for (int i =1; i<=230; i++) {
		
			// Read in image annotation file
			ImageAnnotation imageAnnotation = AnnotationGetter.getImageAnnotationFromFile(aimfile,"/home/vanessa/Documents/Code/Applications/AIM/AIM_v3.xsd");
			String identifier = imageAnnotation.getUniqueIdentifier();
			
			/*if (!AnnotationGetter.isExistInTheServer(serverUrlDownload, namespace, "liver", imageAnnotation.getUniqueIdentifier())) {
				// send ImageAnnotation to the server.
				// String res = imageAnnotation.savetoServer(serverUrlUpload, serverUrlDownload, namespace, "test_db","/home/vanessa/Documents/Code/Applications/AIM/AIM_v3.xsd");
				// Displaying Server's response.
				// System.out.println(res);
			}
		else {
			System.out.println(identifier + " already exists in database.");
		}*/
			}
	}
	
	// query takes a collection (the database?) and a codeValue (from the .aim file) and will
	// print a list of hits (and eventually return the list for display to the user)
	public void query(String collection, String codeValue) throws Exception {
		// collection refers to the database name, and codeValue to the tag to query	
		// Required Parameters to be able to access BerkeleyDB
	
		System.out.println("\nServerUrlDownload is: " + serverUrlDownload);
		System.out.println("namespace is: " + namespace);
		
		// NOT MOVING BEYOND THIS STEP
		/*List<ImageAnnotation> listAnno = AnnotationGetter.getImageAnnotationsFromServerByCodeValueEqual(serverUrlDownload, namespace, collection, codeValue, "/home/vanessa/Documents/Code/Application/AIM/AIM_v3.xsd");
		System.out.println("List of annotations is: " + listAnno);
		System.out.println("After AIM");
		
		
		for (int i = 0; i < listAnno.size(); i++) {
			ImageAnnotation anno = listAnno.get(i);
			System.out.println("Image Annotation UniqueIdentifier: " + anno.getUniqueIdentifier());
			System.out.println("Image Annotation CodeMeaning: " + anno.getCodeMeaning());
			System.out.println("Image Annotation DateTime: " + anno.getDateTime());
			List<Person> listPerson = anno.getListPerson();
			for (int j = 0; j < listPerson.size(); j++) {
				Person person = listPerson.get(j);
				System.out.println("Person Name: " + person.getName());
				System.out.println("Person BirthDate: " + person.getBirthDate());
				System.out.println("Person Sex: " + person.getSex());
				}
			}

		
		// OLD CODE: ignore!		
		/**
		for (int i = 0; i < geometricShapeCollection.getGeometricShapeList().size(); i++) {
			 GeometricShape geometricShape = geometricShapeCollection.getGeometricShapeList().get(i);
			 System.out.println(geometricShape.getXsiType());
			for (int j = 0; j < geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().size();
		j++) {
		SpatialCoordinate spatialCoordinate = geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().get(i);
		if ("TwoDimensionSpatialCoordinate".equals(spatialCoordinate.getXsiType())) {
		TwoDimensionSpatialCoordinate twoDimensionSpatialCoordinate = (TwoDimensionSpatialCoordinate)
		spatialCoordinate;
		System.out.println(twoDimensionSpatialCoordinate.getCoordinateIndex());
		System.out.println(twoDimensionSpatialCoordinate.getX());
		System.out.println(twoDimensionSpatialCoordinate.getY());
		}
		**/
	}
}
