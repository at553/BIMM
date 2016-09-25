package bimm.servlets;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class BioportalAPIRequest extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{	
		String pathtype = (String) req.getParameter("pathtype");
		String ontoID = (String) req.getParameter("ontoID");
		String elementID = (String) req.getParameter("elementID");
		String apiKey = (String) req.getParameter("apiKey");
		
		String urlString = "http://rest.bioontology.org/bioportal/virtual/" + 
				pathtype + "/" +
				ontoID + "/" +
				elementID + "?apikey=" +
				apiKey;	
		
		System.out.println("elementID : "+elementID);
		
		String result = makeAPIRequest(urlString);
		try {
			resp.setContentType("application/xml");
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		String urlString = "http://rest.bioontology.org/bioportal/virtual/leafpath/1057/RID5972?apikey=7e3f1148-1a2d-41dd-beba-351ddb025f38";
		
		String result = makeAPIRequest(urlString);
		try {
			resp.setContentType("application/xml");
			resp.getWriter().write(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return;
	}
	
	// Generic method to make outside http request
	private String makeAPIRequest(String urlString){
		String result = "";
		try{
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;
			
			while ((line = rd.readLine()) != null){
				sb.append(line);
			}
			
			rd.close();
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
