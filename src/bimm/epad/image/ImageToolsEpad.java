package bimm.epad.image;



import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import bimm.epad.shape.ShapeToolsEpad;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.display.SourceImage;



import shape.ShapeTools;
import edu.stanford.hakan.aim3api.base.AimException;
import edu.stanford.hakan.aim3api.base.DICOMImageReference;
import edu.stanford.hakan.aim3api.base.GeometricShape;
import edu.stanford.hakan.aim3api.base.GeometricShapeCollection;
import edu.stanford.hakan.aim3api.base.ImageAnnotation;
import edu.stanford.hakan.aim3api.base.ImageCollection;
import edu.stanford.hakan.aim3api.base.ImageReference;
import edu.stanford.hakan.aim3api.base.ImageReferenceCollection;
import edu.stanford.hakan.aim3api.base.ImageSeries;
import edu.stanford.hakan.aim3api.base.ImageStudy;
import edu.stanford.hakan.aim3api.base.SpatialCoordinate;
import edu.stanford.hakan.aim3api.base.TwoDimensionSpatialCoordinate;
import edu.stanford.hakan.aim3api.usage.AnnotationGetter;
import file.FileTools;

public class ImageToolsEpad {

	//Exist and epad-dev2
	public static String serverDicomImages = "http://epad-prod1.stanford.edu:8080/resources/dicom/";
	//public static String serverWadoImages = "http://epad-prod4.stanford.edu:9080/wado/";
	public static String serverWadoImages = "http://epad-prod.stanford.edu:8080/epad/wado/";
	//public static String diskDicomImages = "/home/kurtz/workspace/bimm/data/liver/dcm_v3/";
	public static String diskDicomImages = "data/liver/dcm_v3/";
	
	
	public static SourceImage loadDicomImage(String studyUID, String seriesUID, String sopInstanceUID) throws HttpException, IOException{
		return loadDicomImageFromDisk(studyUID,seriesUID,sopInstanceUID);
		//return loadDicomImageFromServer(studyUID,seriesUID,sopInstanceUID);
	}
	
	public static BufferedImage loadWadoImage(String studyUID, String seriesUID, String sopInstanceUID) throws HttpException, IOException{
		return loadWadoImageFromServer(studyUID,seriesUID,sopInstanceUID);
	}
	
	
	public static SourceImage loadDicomImageFromDisk(String studyUID, String seriesUID, String sopInstanceUID){
		
		String requestFile = studyUID+"/"+seriesUID+"/"+sopInstanceUID;
		String request = diskDicomImages+requestFile.replace('.', '_')+".dcm";

		//create the image
		SourceImage dicomImage=null;
		
		try {
			dicomImage = new SourceImage(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dicomImage;
	}
	
	public static SourceImage loadDicomImageFromServer(String studyUID, String seriesUID, String sopInstanceUID) throws HttpException, IOException{
		
		//create the image
		SourceImage dicomImage=null;

		//--Get the Dicom file from the server
		HttpClient client = new HttpClient();

		//build the request to the server
		String requestFile = studyUID+"/"+seriesUID+"/"+sopInstanceUID;
		String request = serverDicomImages+requestFile.replace('.', '_')+".dcm";

		System.out.println("Request -- "+request);
		
		GetMethod method = new GetMethod(request);

		// Execute the GET method
		int statusCode = client.executeMethod(method);

		if(statusCode != -1 ) {
			try {
				//Get the result as stream
				InputStream res = method.getResponseBodyAsStream();

				//create the image
				dicomImage = new SourceImage(new DicomInputStream(res));

				//Close connection
				method.releaseConnection();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
				
		return dicomImage;
	}
	
	
	public static BufferedImage loadWadoImageFromServer(String studyUID, String seriesUID, String sopInstanceUID) throws HttpException, IOException{
		
		//create the image
		BufferedImage srcImage=null;

		//--Get the Dicom file from the server
		HttpClient client = new HttpClient();

		//build the request to the server
		String requestFile = "?requestType=WADO" + "&studyUID="+ studyUID + "&seriesUID="+ seriesUID+ "&objectUID=" + sopInstanceUID;
		String request = serverWadoImages+requestFile.replace('_', '.');

		GetMethod method = new GetMethod(request);

		// Execute the GET method
		int statusCode = client.executeMethod(method);

		if(statusCode != -1 ) {
			try {
				//Get the result as stream
				InputStream res = method.getResponseBodyAsStream();

				//create the image
				srcImage = ImageIO.read(res);

				//Close connection
				method.releaseConnection();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
				
		return srcImage;
	}
	
	
	public static SourceImage loadWadoDicomImageFromServer(String studyUID, String seriesUID, String sopInstanceUID) throws HttpException, IOException{
		
		//create the image
		SourceImage dicomImage=null;

		//--Get the Dicom file from the server
		HttpClient client = new HttpClient();

		//build the request to the server
		String requestFile = "?requestType=WADO" + "&studyUID="+ studyUID + "&seriesUID="+ seriesUID+ "&objectUID=" + sopInstanceUID+"&contentType=application/dicom";
		String request = serverWadoImages+requestFile.replace('_', '.');
		
		System.out.println(request);
		GetMethod method = new GetMethod(request);

		// Execute the GET method
		int statusCode = client.executeMethod(method);

		if(statusCode != -1 ) {
			try {
				//Get the result as stream
				InputStream res = method.getResponseBodyAsStream();

				//create the image
				dicomImage = new SourceImage(new DicomInputStream(res));

				//Close connection
				method.releaseConnection();
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
		}
				
		return dicomImage;
	}
	
	
	
	public static void displayBufferedImage(BufferedImage img){
		ImageIcon icon = new ImageIcon();
		icon.setImage(img);

		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(new JLabel(icon));
		frame.pack();
		frame.setVisible(true);
	}
	

	public static BufferedImage resize(BufferedImage img, double ratio) {  
		int w = img.getWidth();  
		int h = img.getHeight(); 

		int newW = (int) (w * ratio);
		int newH = (int) (h * ratio);

		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
		Graphics2D g = dimg.createGraphics();  
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
		g.dispose();  
		return dimg;  
	}  
	
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {  
		int w = img.getWidth();  
		int h = img.getHeight();  
		BufferedImage dimg = new BufferedImage(newW, newH, img.getType());  
		Graphics2D g = dimg.createGraphics();  
		//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);  
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);  
		g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);  
		g.dispose();  
		return dimg;  
	}  

	public static BufferedImage cropImage(BufferedImage src, int x1,int y1,int x2, int y2) {
	      BufferedImage dest = src.getSubimage(x1, y1, x2-x1, y2-y1);
	      return dest; 
	   }
	
	
	/** 
	 * Saves a BufferedImage to the given file, pathname must not have any 
	 * periods "." in it except for the one before the format, i.e. C:/images/fooimage.png 
	 * @param img 
	 * @param saveFile 
	 */  
	public static void saveImage(BufferedImage img, String ref) {  
		try {  
			String format = (ref.endsWith(".png")) ? "png" : "jpg"; 
			ImageIO.write(img, format, new File(ref));  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}  


	public static BufferedImage convertImageToColorImage(BufferedImage img) {  

		BufferedImage bufImgColor=new BufferedImage(img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_RGB);
		bufImgColor.getGraphics().drawImage(img,0,0,null);

		return bufImgColor;
	}  


	
	
	
	

	/**
	 * 
	 * Download the dicom images from the server
	 * 
	 * @deprecated 
	 * 
	 * @param imageAnnotation
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static TreeMap<String,SourceImage> getDicomImagesRelatedToAnAimObjectFromServer(ImageAnnotation imageAnnotation) throws HttpException, IOException{

		//Storing the dicoms result
		TreeMap<String,SourceImage> mapDicomImages = new TreeMap<String, SourceImage>();;

		//parse aimFile
		ImageReferenceCollection imageReferenceCollection = imageAnnotation.getImageReferenceCollection();

		String imageStudyUID = null;
		String imageSeriesUID = null;

		List<ImageReference> imageReferenceList = imageReferenceCollection.getImageReferenceList();
		for (int i = 0; i < imageReferenceList.size(); i++) {
			DICOMImageReference imageReference = (DICOMImageReference) imageReferenceList.get(i);
			ImageStudy imageStudy = imageReference.getImageStudy();
			imageStudyUID = imageStudy.getInstanceUID();

			ImageSeries imageSeries = imageStudy.getImageSeries();
			imageSeriesUID = imageSeries.getInstanceUID();

			ImageCollection imageCollection= imageSeries.getImageCollection();
			for(edu.stanford.hakan.aim3api.base.Image img: imageCollection.getImageList()){

				String sopInstanceUID = img.getSopInstanceUID();

				//--Get the Dicom file from the ePAD-dev1 server
				HttpClient client = new HttpClient();

				//build the request to the server
				String epadUrl = "http://epad-dev1.stanford.edu:8325/resources/dicom/";
				String requestFile = imageStudyUID+"/"+imageSeriesUID+"/"+sopInstanceUID;
				String request = epadUrl+requestFile.replace('.', '_')+".dcm";

				GetMethod method = new GetMethod(request);

				// Execute the GET method
				int statusCode = client.executeMethod(method);

				if(statusCode != -1 ) {
					try {
						//Get the result as stream
						InputStream res = method.getResponseBodyAsStream();

						//create the image
						SourceImage dicomImage = new SourceImage(new DicomInputStream(res));

						//adding the image to the map
						mapDicomImages.put(sopInstanceUID, dicomImage);

						//Close connection
						method.releaseConnection();
					}
					catch( Exception e ) {
						e.printStackTrace();
					}
				}



			}
		}

		return mapDicomImages;
	}



	
	/**
	 * Draw the ROI contained in a AIM Annotation object on a BufferedImage
	 * 
	 * @deprecated 
	 * 
	 * @param imageAnnotation
	 * @param inputImage
	 * @return
	 */
	public static BufferedImage drawROIFromAimObjectOnBufferedImage(ImageAnnotation imageAnnotation,BufferedImage inputImage) {

		//Create a copy of the input image
		BufferedImage res=new BufferedImage(inputImage.getWidth(),inputImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		res.getGraphics().drawImage(inputImage,0,0,null);

		//--Get the ROI from the AIM (From AIM API wiki)
		List<Point> points=new ArrayList<Point>();

		//parse aimFile
		GeometricShapeCollection geometricShapeCollection = imageAnnotation.getGeometricShapeCollection();
		for (int i = 0; i < geometricShapeCollection.getGeometricShapeList().size(); i++) {
			GeometricShape geometricShape = geometricShapeCollection.getGeometricShapeList().get(i);
			System.out.println(geometricShape.getXsiType());

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

					//create a point
					Point p=new Point(x.intValue(), y.intValue());
					//add to the list of points
					points.add(p);

				}
			}

			//build the ROI
			Shape p=null;

			if(geometricShape.getXsiType().equals("Ellipse") || geometricShape.getXsiType().equals("Circle")){
				p=ShapeTools.transformListOfPointsIntoEllipse(points);
			}else{
				if(geometricShape.getXsiType().equals("Polyline")){
					//p=ShapeTools.transformListOfPointsIntoPolygon(points);
					p=ShapeTools.transformListOfPointsIntoSmoothPolygon(points);
				}else{
					//Unknow shape
					System.err.println("Shape not recognized");
				}
			}

			if(p!=null){
				//draw the ROI on the bufferedImage
				Graphics2D graph2D = (Graphics2D) res.getGraphics();
				ShapeTools.drawShape(p, graph2D);
				graph2D.drawImage(res, null, 0, 0);
			}
		}




		return res;

	}


	
	/**
	 * get the ROI contained in a AIM Annotation object
	 * 
	 * @param imageAnnotation
	 * @param inputImage
	 * @return
	 */
	public static List<Point> getROIFromAimObject(ImageAnnotation imageAnnotation) {

		
		//--Get the ROI from the AIM (From AIM API wiki)
		List<Point> points=new ArrayList<Point>();

		//parse aimFile
		GeometricShapeCollection geometricShapeCollection = imageAnnotation.getGeometricShapeCollection();
		for (int i = 0; i < geometricShapeCollection.getGeometricShapeList().size(); i++) {
			GeometricShape geometricShape = geometricShapeCollection.getGeometricShapeList().get(i);
			System.out.println(geometricShape.getXsiType());

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

					//create a point
					Point p=new Point(x.intValue(), y.intValue());
					//add to the list of points
					points.add(p);

				}
			}

		}




		return points;

	}
	
	public static void main(String[] args) throws Exception {

    	File in = new File("/home/kurtz/Bureau/1_2_276_0_7230010_3_1_4_2550647646_5644_1350494561_491-234.png");
    	BufferedImage source = ImageIO.read(in);

    	//int color = source.getRGB(0, 0);

    	Image image = makeColorTransparent(source, Color.BLACK);
    	BufferedImage transparent = imageToBufferedImage(image);
    	
    	Image image2 = makeColorSemiTransparent(transparent, Color.WHITE);
    	BufferedImage transparent2 = imageToBufferedImage(image2);

    	File out = new File("/home/kurtz/Bureau/1_2_276_0_7230010_3_1_4_2550647646_5644_1350494561_491-234-1.png");
    	ImageIO.write(transparent2, "PNG", out);

    }

    private static BufferedImage imageToBufferedImage(Image image) {

    	BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g2 = bufferedImage.createGraphics();
    	g2.drawImage(image, 0, 0, null);
    	g2.dispose();

    	return bufferedImage;

    }

    public static Image makeColorTransparent(BufferedImage im, final Color color) {
    	ImageFilter filter = new RGBImageFilter() {

    		// the color we are looking for... Alpha bits are set to opaque
    		public int markerRGB = color.getRGB() | 0xFF000000;

    		public final int filterRGB(int x, int y, int rgb) {
    			if ((rgb | 0xFF000000) == markerRGB) {
    				// Mark the alpha bits as zero - transparent
    				return 0x00FFFFFF & rgb;
    			} else {
    				// nothing to do
    				return rgb;
    			}
    		}
    	};
    	

    	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
    	return Toolkit.getDefaultToolkit().createImage(ip);
    }
	
    public static Image makeColorSemiTransparent(BufferedImage im, final Color color) {
    	ImageFilter filter = new RGBImageFilter() {

    		// the color we are looking for... Alpha bits are set to opaque
    		public int markerRGB = color.getRGB() | 0xFF000000;

    		public final int filterRGB(int x, int y, int rgb) {
    			if ((rgb | 0xFF000000) == markerRGB) {
    				// Mark the alpha bits as zero - transparent
    				
    				int r = 255;
    				int g = 255;
    				int b = 0;
    				int a = 80;
    				
    				int col = (a << 24) | (r << 16) | (g << 8) | b;	
    				
    				return col;
    			} else {
    				// nothing to do
    				return rgb;
    			}
    		}
    	};
    	

    	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
    	return Toolkit.getDefaultToolkit().createImage(ip);
    }
	
	
	
	public static void main2(String[] args ) throws IOException, DicomException, AimException{
		
		File dir_aim = new File("/home/kurtz/Documents/Backup_vintia/Documents/Travail_Postdoc/chris/bones_tumor/RSNA/full_2");
		File dir = new File("/home/kurtz/Documents/Backup_vintia/Documents/Travail_Postdoc/chris/bones_tumor/RSNA/full_dcm2");


		// This filter only returns directories
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.getName().endsWith(".xml");
			}
		};

		File[] files = dir_aim.listFiles(fileFilter);
		if (files == null) {
			System.out.println("Directory does not exist or is not a Directory");
		} else {
			for (int i=0; i< files.length;i++){              // Get filename of file or directory
				File file = files[i];
				System.out.println(file.getName());

				String aimUID = FileTools.stripExtension(file.getName());
				System.out.println("aimUID ="+aimUID);

				// Get position of last '.'.
				int pos = aimUID.lastIndexOf("_");
				// Otherwise return the string, up to the dot.
				aimUID= aimUID.substring(pos+1, aimUID.length());
				System.out.println("new aimUID ="+aimUID);


				String pathAimFile = file.getPath();

				ImageAnnotation imageAnnotation = null;
				imageAnnotation = AnnotationGetter.getImageAnnotationFromFile(pathAimFile,"/home/kurtz/workspace/toolsQIL/ressources/AIM_v3.xsd");

				//parse aimFile
				ImageReferenceCollection imageReferenceCollection = imageAnnotation.getImageReferenceCollection();

				//For each kind of images (each imageReference)
				String studyUID = null;
				String seriesUID =null;
				String sopInstanceUID=null;;

				List<ImageReference> imageReferenceList = imageReferenceCollection.getImageReferenceList();
				for (int j = 0; j < imageReferenceList.size(); j++) {
					DICOMImageReference imageReference = (DICOMImageReference) imageReferenceList.get(j);

					//we get the study
					ImageStudy imageStudy = imageReference.getImageStudy();
					studyUID = imageStudy.getInstanceUID();

					//we extract the series from this study
					ImageSeries imageSeries = imageStudy.getImageSeries();
					seriesUID = imageSeries.getInstanceUID();

					ImageCollection imageCollection= imageSeries.getImageCollection();


					//We get the images
					for(edu.stanford.hakan.aim3api.base.Image img: imageCollection.getImageList()){
						sopInstanceUID = img.getSopInstanceUID();
					}
				}
				System.out.println(studyUID+" "+seriesUID+ " "+sopInstanceUID);

				String pathToSave= dir.getAbsolutePath()+"/"+aimUID+".png";
				
				List<Point> roi= ImageToolsEpad.getROIFromAimObject(imageAnnotation);
				List<Point> bb=ShapeToolsEpad.getBoundingBox(roi);
				
				//get the image
				BufferedImage dcm=loadWadoImageFromServer(studyUID, seriesUID, sopInstanceUID);
			
				if(dcm!=null){	
					BufferedImage enhancedBufImgColorWithROI = ImageToolsEpad.drawROIFromAimObjectOnBufferedImage(imageAnnotation, dcm);
					
					int borderCrop=15;
					
					ImageToolsEpad.saveImage(
							ImageToolsEpad.resize(ImageToolsEpad.cropImage(enhancedBufImgColorWithROI, bb.get(0).x - borderCrop, bb.get(0).y -borderCrop,bb.get(1).x +borderCrop,bb.get(1).y +borderCrop), 256, 256),
							pathToSave);
					
					
					//ImageTools.saveImage(ImageTools.resize(enhancedBufImgColorWithROI, 0.6),pathToSave);
				}else{
					System.out.println("pb =====@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
					System.out.println("pb ====="+aimUID);
				}
				
				
			}
		}
		
		
		
	}
	
	
		

}
