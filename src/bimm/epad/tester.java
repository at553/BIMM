package bimm.epad;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import aim.AimTools;
import bimm.epad.cbir.CbirEpad;
import cbir.Cbir;
import edu.stanford.hakan.aim3api.base.AimException;

public class tester {
	public static void main(String[] args) throws Exception {
		/*CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		String sessionID = ClientAuthentication.getID("admin", "admin");
		HttpGet httpget = new HttpGet(
				"http://epad-dev5.stanford.edu:8080/epad/aimresource/?annotationUID=all&user=admin");
		httpget.setHeader("Cookie", "JSESSIONID=" + sessionID);
		HttpResponse response = httpClient.execute(httpget);
		System.out.println(response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
				String val = EntityUtils.toString(responseEntity);
				System.out.println(val);
			}
		}*/
		testCbir();
	}
	
	public static void testCbir() throws AimException, TransformerException, ParserConfigurationException, edu.stanford.hakan.aim4api.base.AimException
	{
		String aimInputUID = "341733037.560862";
		CbirEpad cbi = null;
		try {
			cbi = new CbirEpad(aimInputUID, "C:\\Users\\avina_000\\bimmImages\\");
			
			
				cbi.run();
			/* catch (edu.stanford.hakan.aim4api.base.AimException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			cbi.generateXMLResults();
			
			System.out.println(cbi.generateXMLResults());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}

}
