package bimm.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.impl.SimpleResult;


/**
 * Servlet implementation class IndexingServlet
 */
@WebServlet("/IndexingServlet")
public class IndexingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Executing indexservlet");
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		BufferedReader reader = request.getReader();
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			while ( (line = reader.readLine()) != null)
		      jb.append(line);
		}
		catch(Exception e)
		{
			
		}
		  
		System.out.println(jb.toString());
		String[] positives = null, negatives = null;
		String selectedUID = null;
		try {
			if (jb.length() > 0) {
				JSONObject obj =  new JSONObject(jb.toString());
				if (obj.getString("positives") != null && ! obj.getString("positives").isEmpty())
					positives = obj.getString("positives").split(",");
				if (obj.getString("negatives") != null && ! obj.getString("negatives").isEmpty())
				    negatives = obj.getString("negatives").split(",");
				selectedUID = obj.getString("selectedUID");
			
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, SimpleResult> mappedData = (Map<String, SimpleResult>) request.getSession().getAttribute("uidtosimpleresultmap");
		List<String> images = runRelevanceFeedback(request, rootPath, selectedUID, positives, negatives, mappedData);
		String json = new Gson().toJson(images);
		response.setContentType("text/json");
	    PrintWriter out = response.getWriter();
	    out.write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	public List<String> runRelevanceFeedback(HttpServletRequest request, String rootPath, String selectedUID,
			String[] positives, String[] negatives, Map<String, SimpleResult> mappedData) {
		// TODO Auto-generated method stub
		ArrayList<Document> positivesDoc = new ArrayList<Document>();
		ArrayList<Document> negativesDoc = new ArrayList<Document>();

		// Iterate through the map
		if (positives != null ){
			for (String positive: positives) {
					SimpleResult rs = mappedData.get(positive);
					if (rs != null )
						positivesDoc.add(rs.getDocument());
			}
		}
		if (negatives != null) {
			for (String negative: negatives){
					SimpleResult rs = mappedData.get(negative);
					if (rs != null)
						negativesDoc.add(rs.getDocument());
			}
		}
		String imagePath = rootPath + "img" + File.separator + "imageStore" + File.separator;
		IndexThread helper = new IndexThread(imagePath 
				+ selectedUID + ".jpg", positivesDoc, negativesDoc, rootPath + "index");
		

		try {

			Thread relevanceFeedbackThread = new Thread(helper);
			relevanceFeedbackThread.start();
			relevanceFeedbackThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ImageSearchHits ish = helper.getHits();
		ArrayList<SimpleResult> simpleResults = ish.getResults();
		Set<String> hitImages = new LinkedHashSet<String>();
		Map<String, SimpleResult> uidToSimpleResultMap = new HashMap<String, SimpleResult>();
		for (SimpleResult sr : simpleResults) {
			String hitImage = sr.getDocument()
					.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER)
					.stringValue();
			
			int lastIndex = hitImage.lastIndexOf("\\");
			String imageName = hitImage.substring(lastIndex + 1);
			int fileExtensionSeparator = imageName.lastIndexOf(".");
			String uniqueId = imageName.substring(0, fileExtensionSeparator);
			hitImages.add(uniqueId.trim());
			System.out.println("File Identifier::" + uniqueId);
			// Add to Mapping
			uidToSimpleResultMap.put(uniqueId, sr);
		}
		// Update the session variable with new map
		request.getSession().setAttribute("uidtosimpleresultmap", uidToSimpleResultMap);
		// return matches;
		List<String> uniqueImages = new ArrayList<String>();
		uniqueImages.addAll(hitImages);
		return uniqueImages;

	}

}
