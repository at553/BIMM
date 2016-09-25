package bimm.epad.test;
import edu.stanford.hakan.aim4api.base.AimException;
import edu.stanford.hakan.aim4api.base.ImageAnnotationCollection;
import edu.stanford.hakan.aim4api.usage.AnnotationGetter;
import java.util.List;

public class TestMethodCodeVal {
	
	public static void main(String[] args)
	{
		 try {
			List<ImageAnnotationCollection> res = AnnotationGetter.getImageAnnotationCollectionByImageAnnotationCodeEqual(
				       "http://epad-dev5.stanford.edu:8899/exist",
				       "gme://caCORE.caCORE/4.4/edu.northwestern.radiology.AIM",
				       "aimV4.dbxml", "epaduser", "3p4dus3r",
				       "ROI");
			
			System.out.println(res.size());
		} catch (AimException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			      
	}

}
