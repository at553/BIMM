package bimm.servlets;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import bimm.epad.ClientAuthentication;
import edu.stanford.hakan.aim4api.base.AimException; 
import edu.stanford.hakan.aim4api.base.ImageAnnotationCollection;
import edu.stanford.hakan.aim4api.compability.aimv3.ImageAnnotation;
import edu.stanford.hakan.aim4api.usage.AnnotationGetter;

public class ImageAnnotationUIDServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	{
		try {
			response.getWriter().print(getAimFiles());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AimException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getAimFiles() throws AimException, ClientProtocolException, IOException
	{
		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String sessionID = ClientAuthentication.getID("admin", "admin");
		HttpGet httpget = new HttpGet(
				"http://epad-prod.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin");
		httpget.setHeader("Cookie", "JSESSIONID=" + sessionID);
		HttpResponse response = httpClient.execute(httpget);
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String val = EntityUtils.toString(responseEntity);
				System.out.println(val);
				AnnotationGetter anno = new AnnotationGetter();
				List<ImageAnnotationCollection> images = anno.getImageAnnotationCollectionsFromString(val, "");
				List<String> uidList = new ArrayList<String>();
				for ( ImageAnnotationCollection image: images)
				{
					uidList.add(image.getUniqueIdentifier().getRoot());
					
				}
				return new Gson().toJson(uidList);
			}
		}
		//http://epad-dev2.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin
		return null;
		
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	{
		doGet(request, response);
	}
	
	
	
}
