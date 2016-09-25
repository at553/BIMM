package bimm.epad.cbir;


import image.ImageEnhancer;  
import image.ImageTools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import shape.ShapeTools;
import aim.AimTools;
import bimm.epad.image.ImageToolsEpad;
import cbir.BimmImageAnnotation;
import cbir.structure.StringCouple;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SourceImage;

import edu.stanford.hakan.aim4api.compability.aimv3.ImagingObservationCollection;

import edu.stanford.hakan.aim4api.base.AimException; 
import edu.stanford.hakan.aim4api.compability.aimv3.DICOMImageReference;
import edu.stanford.hakan.aim4api.compability.aimv3.GeometricShape;
import edu.stanford.hakan.aim4api.compability.aimv3.GeometricShapeCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.Image;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageAnnotation;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageReference;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageReferenceCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageSeries;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageStudy;
import edu.stanford.hakan.aim4api.compability.aimv3.ImagingObservation;
import edu.stanford.hakan.aim4api.compability.aimv3.ImagingObservationCharacteristic;
import edu.stanford.hakan.aim4api.compability.aimv3.ImagingObservationCharacteristicCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.ImagingObservationCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.SpatialCoordinate;
import edu.stanford.hakan.aim4api.compability.aimv3.TwoDimensionSpatialCoordinate;
import edu.stanford.hakan.aim4api.usage.AnnotationGetter;


/**
 * 
 * Class representing an AIM object and the associated features: ROIs and the Images
 * 
 * @author kurtz
 *
 */

public class BimmImageAnnotationEpad implements Comparable<BimmImageAnnotationEpad>{


	/*
	 * The AIM object
	 */
	private ImageAnnotation imageAnnotation = null;
	private String aimUID = null;

	/*
	 * The informations related to the images
	 */
	private String studyUID =null;
	private String seriesUID = null;
	
	/*
	 * The associated imagingObservations ImagingObservation -> list of imagingObservationCharacteristicCollection)
	 */
	private TreeMap<StringCouple, TreeSet<StringCouple>> imagingObservations = null;
	
	/*
	 * The associated source dicom images ImageUID -> DicomImage)
	 */
	private TreeMap<String, SourceImage> srcDicomImages = null;
	
	
	/*
	 * The associated source jpeg (from wado call) images ImageUID -> DicomImage)
	 */
	private TreeMap<String, BufferedImage> srcWadoImages = null;
	

	/*
	 * The associated buffer images ImageUID -> BufferedImageImage
	 */
	private TreeMap<String, BufferedImage> srcBufferedImages = null;

	/*
	 * The associated info seriesTag
	 */
	private String infoSeriesTag = null;

	/*
	 * The associated ROIs images ImageUID -> (Shape id -> Shape)
	 */
	private TreeMap<String, TreeMap<Integer,Shape>> ROIs = null;
	
	/*
	 * The associated buffer images with ROIS depicted ImageUID -> BufferedImageImage
	 */
	private TreeMap<String, BufferedImage> srcBufferedImagesROIs = null;
	

	public BimmImageAnnotationEpad(ImageAnnotation imgAnnotation) throws HttpException, IOException  {
		
		//The AIM object from AIM_API_v3
		imageAnnotation = imgAnnotation;
		aimUID = imgAnnotation.getUniqueIdentifier();
		
		//Load the imaging Observations 
		loadImagingObservations();
	}

	
	public String getCodeValueOfImageAnnotation(){
		return imageAnnotation.getCodeValue();
	}
	
	public String getAimUID() {
		return aimUID;
	}

	
	
	/**
	 * Load the ImagingObservations related to the AIM file
	 */
	private void loadImagingObservations() {
		
		imagingObservations = new TreeMap<StringCouple, TreeSet<StringCouple>>();
		
		//parse aimFile
		ImagingObservationCollection io= imageAnnotation.getImagingObservationCollection();
		List<ImagingObservation> imageObservationList = io.getImagingObservationList();
		
		//for each ImagingObservation
		for (int i = 0; i < imageObservationList.size(); i++) {
			ImagingObservation imageObservation = imageObservationList.get(i);
			
			String codeValue=imageObservation.getAllowedTerm().getCodeValue();
			String codeMeaning=imageObservation.getAllowedTerm().getCodeMeaning();
			
			StringCouple coupleValueMeaning = new StringCouple(codeValue,codeMeaning);
			
			//check for the existence of ImagingObservationCharacteristic
			TreeSet<StringCouple> imagingObservationCharacteristic =null;
			ImagingObservationCharacteristicCollection imageObservationCharacteristicCollection = imageObservation.getImagingObservationCharacteristicCollection();
			
			if(imageObservationCharacteristicCollection!=null){
				List<ImagingObservationCharacteristic> listObservationCharacteristic = imageObservationCharacteristicCollection.getImagingObservationCharacteristicList();
				
				if(listObservationCharacteristic!=null && listObservationCharacteristic.size()>0){
					imagingObservationCharacteristic = new TreeSet<StringCouple>();
					//for each ImagingObservationCharacteristic
					for(int j = 0; j < listObservationCharacteristic.size(); j++) {
						String codeValueCharac=listObservationCharacteristic.get(j).getAllowedTerm().getCodeValue();
						String codeMeaningCharac=listObservationCharacteristic.get(j).getAllowedTerm().getCodeMeaning();
						
						StringCouple coupleValueMeaningCharac = new StringCouple(codeValueCharac,codeMeaningCharac);
						//add an entry
						imagingObservationCharacteristic.add(coupleValueMeaningCharac);
					}
					
				}
			}
			//add the ImagingObservation
			imagingObservations.put(coupleValueMeaning, imagingObservationCharacteristic);
		}
	}

	
	/**
	 * Load the images related to the AIM file
	 * 
	 * @throws HttpException
	 * @throws IOException
	 * @throws DicomException
	 */
	public void loadImagingContent() throws HttpException, IOException{
		
		//Load the Dicom images
		//loadDicomImagesRelatedToAnAimObject();
		
		//Load the Jpeg scr images from a wado call
		loadWadoImagesRelatedToAnAimObject();
		
		//Load the image infos
		//initSeriesTagFromServer();

		//Generate the BufferedImages
		generateBufferedImages();

		//Load the ROIs
		loadROIshapes();

		//Generate the buffered Images with ROIs
		generateBufferedImageswithROIs();
		
	}
	
	
	private void generateBufferedImageswithROIs() {
		
		srcBufferedImagesROIs=new TreeMap<String, BufferedImage>();
		
		//Print ROIs on each image
		for(Entry<String, BufferedImage> entry:srcBufferedImages.entrySet()){
			String uidImage=entry.getKey();
			
			TreeMap<Integer,Shape> shapesForCurrentImage=ROIs.get(uidImage);
			if(shapesForCurrentImage!=null){

				BufferedImage img = srcBufferedImages.get(uidImage);
				
				//Create a copy of the input image
				if (img != null ){
					BufferedImage res=new BufferedImage(img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_RGB);
					res.getGraphics().drawImage(img,0,0,null);
					
					Graphics2D graph2D = (Graphics2D) res.getGraphics();
					
					//draw the schape on the color image
					for(Shape shape:shapesForCurrentImage.values()){
						ShapeTools.drawShape(shape, graph2D);
						graph2D.drawImage(res, null, 0, 0);
					}
					
					//store the result
					srcBufferedImagesROIs.put(uidImage, res);
				}
				else
				{
					System.out.println("img is null!");
				}
			}
			else
			{
				System.out.println("Shape for img " + uidImage + " is null");
			}
		}
		
		
	}

	private void loadROIshapes() {

        ROIs = new TreeMap<String, TreeMap<Integer,Shape>>();

        //--Get the ROI from the AIM (From AIM API wiki)
        List<Point> points=new ArrayList<Point>();

        //parse aimFile
        GeometricShapeCollection geometricShapeCollection = imageAnnotation.getGeometricShapeCollection();
        for (int i = 0; i < geometricShapeCollection.getGeometricShapeList().size(); i++) {
            GeometricShape geometricShape = geometricShapeCollection.getGeometricShapeList().get(i);
            //System.out.println(geometricShape.getXsiType());

            String imageRefererenceUID = null;
            Integer shapeID = geometricShape.getShapeIdentifier();

            Point [] points_Tab=new Point[geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().size()];
            
            //for each roi, we look for the points
            for (int j = 0; j < geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().size();j++) {

                //get the points
                SpatialCoordinate spatialCoordinate =
                    geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().get(j);

                if ("TwoDimensionSpatialCoordinate".equals(spatialCoordinate.getXsiType())) {
                    TwoDimensionSpatialCoordinate twoDimensionSpatialCoordinate = 
                        (TwoDimensionSpatialCoordinate) spatialCoordinate;

                    //Get the coordinate values from the ROI of the aimFIle
                    Double x=twoDimensionSpatialCoordinate.getX();
                    Double y=twoDimensionSpatialCoordinate.getY();

                    //Get the index Coordinate
                    Integer coodrIndex=geometricShape.getSpatialCoordinateCollection().getSpatialCoordinateList().get(j).getCoordinateIndex();
                    
                    //create a point
                    Point p=new Point(x.intValue(), y.intValue());
                    //add to the list of points
                    if((coodrIndex==null) ||(coodrIndex >= points_Tab.length ) ||(coodrIndex < 0)){
                        points.add(p);
                    }else{
                        points_Tab[coodrIndex]= p;
                    }
                    //save the UID of the image in which the shape is contained
                    imageRefererenceUID = twoDimensionSpatialCoordinate.getImageReferenceUID();
                }

            }
            
            if(points.isEmpty()){
                for(Point p:points_Tab){
                    points.add(p);
                }
            }
            

            //build the ROI
            Shape p=null;

            if(geometricShape.getXsiType().equals("Ellipse") || geometricShape.getXsiType().equals("Circle")){
                p=ShapeTools.transformListOfPointsIntoEllipse(points);
            }else{
                if(geometricShape.getXsiType().equals("Polyline")|| geometricShape.getXsiType().equals("MultiPoint")){
                    //p=ShapeTools.transformListOfPointsIntoPolygon(points);
                    p=ShapeTools.transformListOfPointsIntoSmoothPolygon(points);
                }else{
                    //Unknow shape
                    System.err.println("Shape not recognized");
                    System.out.println(geometricShape.getXsiType());
                }
            }

            //Save the roi
            if(ROIs.get(imageRefererenceUID)==null){
                TreeMap<Integer,Shape> listOfShapeForThisImage = new TreeMap<Integer, Shape>();
                listOfShapeForThisImage.put(shapeID, p);
                ROIs.put(imageRefererenceUID, listOfShapeForThisImage);
            }else{
                TreeMap<Integer, Shape> shapesOfImage = ROIs.get(imageRefererenceUID);
                shapesOfImage.put(shapeID, p);
            }
        }
    }


	private void loadDicomImagesRelatedToAnAimObject() throws HttpException, IOException {

		srcDicomImages =new TreeMap<String, SourceImage>();

		//parse aimFile
		ImageReferenceCollection imageReferenceCollection = imageAnnotation.getImageReferenceCollection();

		//For each kind of images (each imageReference)
		List<ImageReference> imageReferenceList = imageReferenceCollection.getImageReferenceList();
		for (int i = 0; i < imageReferenceList.size(); i++) {
			DICOMImageReference imageReference = (DICOMImageReference) imageReferenceList.get(i);

			//we get the study
			ImageStudy imageStudy = imageReference.getImageStudy();
			studyUID = imageStudy.getInstanceUID();

			//we extract the series from this study
			ImageSeries imageSeries = imageStudy.getImageSeries();
			seriesUID = imageSeries.getInstanceUID();

			ImageCollection imageCollection= imageSeries.getImageCollection();

			//We get the images
			for(Image img: imageCollection.getImageList()){
				String sopInstanceUID = img.getSopInstanceUID();

				//Get the image from disk or server
				SourceImage dicomImage = ImageTools.loadDicomImage(studyUID,seriesUID,sopInstanceUID);
				
				//adding the image to the map
				srcDicomImages.put(sopInstanceUID, dicomImage);

			}
		}
	}
	
	private void loadWadoImagesRelatedToAnAimObject() throws HttpException, IOException {

		srcWadoImages = new TreeMap<String, BufferedImage>();

		//parse aimFile
		ImageReferenceCollection imageReferenceCollection = imageAnnotation.getImageReferenceCollection();

		//For each kind of images (each imageReference)
		List<ImageReference> imageReferenceList = imageReferenceCollection.getImageReferenceList();
		
		for (int i = 0; i < imageReferenceList.size(); i++) { 
			
			DICOMImageReference imageReference = (DICOMImageReference) imageReferenceList.get(i);

			//we get the study
			ImageStudy imageStudy = imageReference.getImageStudy();
			studyUID = imageStudy.getInstanceUID();

			//we extract the series from this study
			ImageSeries imageSeries = imageStudy.getImageSeries();
			seriesUID = imageSeries.getInstanceUID();

			ImageCollection imageCollection= imageSeries.getImageCollection();

			//We get the images
			for(Image img: imageCollection.getImageList()){
				String sopInstanceUID = img.getSopInstanceUID();

				//Get the image from disk or server
				BufferedImage img_src = ImageToolsEpad.loadWadoImage(studyUID,seriesUID,sopInstanceUID);
				
				//adding the image to the map
				srcWadoImages.put(sopInstanceUID, img_src);

			}
		}
	}
	




	private void generateBufferedImages(){
		srcBufferedImages = new TreeMap<String, BufferedImage>();

		if(srcDicomImages!=null){
			for(Entry<String, SourceImage> entry : srcDicomImages.entrySet()) {
				String imageUID = entry.getKey();
				SourceImage img = entry.getValue();
	
				//First tool of enhancement 
				/*BufferedImage srcBufImg = img.getBufferedImage(0);
				BufferedImage srcBufImgEnhanced = BufferedImageUtilities.convertToMostFavorableImageType(srcBufImg);
					Tools.displayBufferedImage(srcBufImgEnhanced);
				 */
	
				//Second tool of enhancement 
				ImageEnhancer ie=new ImageEnhancer(img);
				BufferedImage enhancedBufImg = ie.enhanceImage();
				//ImageTools.displayBufferedImage(enhancedBufImg);
	
				srcBufferedImages.put(imageUID, enhancedBufImg);
	
			}
		}else{
			for(Entry<String, BufferedImage> entry : srcWadoImages.entrySet()) {
				String imageUID = entry.getKey();
				BufferedImage img = entry.getValue();
	
				srcBufferedImages.put(imageUID, img);
	
			}
			
			
		}
	}





	private void loadSeriesTagFromServer() throws HttpException, IOException {

		//--Get the AIM file from exist and epad-dev1
		HttpClient client = new HttpClient();

		String existEpadUrl = "http://epad-dev1.stanford.edu:8325/seriestag/";

		String query= "?series_iuid=" + seriesUID + "&type=text";

		String requestFile = existEpadUrl+""+query;

		GetMethod method = new GetMethod(requestFile);

		// Execute the GET method
		int statusCode = client.executeMethod(method);

		if(statusCode != -1 ) {
			try {
				//Get the result as stream
				infoSeriesTag = method.getResponseBodyAsString();

				//Close connection
				method.releaseConnection();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
		System.out.println(infoSeriesTag);


	}

	


	public void saveImagingContent(String path) {
		//changes here
		if(srcBufferedImagesROIs.firstEntry() != null)
		{
		BufferedImage res = srcBufferedImagesROIs.firstEntry().getValue();		
		//print the results
		ImageToolsEpad.saveImage(res,path+""+this.getAimUID()+".jpg");
		}
		
	}


	public TreeMap<StringCouple, TreeSet<StringCouple>> getImagingObservations() {
		return imagingObservations;
	}

	
	public void printFirstImage(){
		BufferedImage res = srcBufferedImagesROIs.firstEntry().getValue();		
		//print the results
		ImageTools.displayBufferedImage(ImageTools.resize(res, 0.8));
	}
	
	
	public String toString(){
		String res="--------------\n";
		res+="AIM UID = "+this.imageAnnotation.getUniqueIdentifier()+"\n";
		res+="------Image info\n";
		res+="Image study = "+this.studyUID+"\n";
		res+="Image series = "+this.seriesUID+"\n";
		res+="--Images = "+"\n";
		for(Entry<String, BufferedImage> entry : srcBufferedImages.entrySet()) {
			String imageUID = entry.getKey();
			res+="\t"+imageUID+"\n";
		}
		
		res+="------Imaging Annotation\n";
		int numberOfAnnotations=0;
		for(Entry<StringCouple, TreeSet<StringCouple>>  entry: imagingObservations.entrySet()) {
			res+="Image annotation = "+entry.getKey()+"\n";
			numberOfAnnotations++;
			if(entry.getValue()!=null){
				res+="--ImagingObservationCharacteristic = "+"\n";
				for(StringCouple c:entry.getValue()){
					res+="\t"+c+"\n";
					numberOfAnnotations++;
				}
			}
		}
		System.out.println("Number of annotations = "+numberOfAnnotations);
		
		return res;
		
	}
	
	public void printBimmImageAnnotation(){
		System.out.println(this.toString());
	}
	
	

	public static void main(String[] args ) throws IOException, DicomException, AimException{
		String aimID = "247167250.1"; //epad1
		String pathAimFile = "/home/kurtz/workspace/bimm/data/liver/aim_V3/AIM_247167250.1.xml";

		//--From server
		//ImageAnnotation imageAnnotation = AimTools.getAIMFileFromDatabase(aimID);

		//--From Disk
		ImageAnnotation imageAnnotation = new ImageAnnotation(AnnotationGetter.getImageAnnotationCollectionFromFile(pathAimFile,"/home/kurtz/workspace/toolsQIL/ressources/AIM_v3.xsd"));

		//Create a BIMM object
		BimmImageAnnotation bia= null;//new BimmImageAnnotation(imageAnnotation);
		//*** To-DO AIM V4 - Hakan
		bia.loadImagingContent();
		bia.printBimmImageAnnotation();
		bia.printFirstImage();

	}


	@Override
	public int compareTo(BimmImageAnnotationEpad o) {
		// TODO Auto-generated method stub
		 return this.getAimUID().compareTo(o.getAimUID());
	}


	


}
