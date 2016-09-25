package bimm.servlets;

import java.io.File;
import java.io.IOException; 
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.httpclient.HttpException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.json.JSONArray;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import bimm.epad.cbir.CbirEpad;
import bimm.lire.LireIndexingThread;
import aim.AimTools;
import cbir.Cbir;
import edu.stanford.hakan.aim4api.base.AimException;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.impl.SimpleResult;

public class BIMMServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			// We look inside the get parameters to instantiate the CBIR with a
			// AIM uid
			String aimInputUID = req.getParameter("aimInputUID");
			System.out.println("AIM uid Query = " + aimInputUID);
			String rootPath = req.getSession().getServletContext().getRealPath("/");
			if (aimInputUID != null && !aimInputUID.equals("") && !aimInputUID.equals("null")) {
				fetchImages(aimInputUID, rootPath);
			    String imagePath = rootPath + "img" + File.separator + "imageStore" + File.separator;
			    List<String> matchedImages = runIndexing(req, imagePath, rootPath, aimInputUID);
			    String json = new Gson().toJson(matchedImages);
				resp.setContentType("text/json");
			    PrintWriter out = resp.getWriter();
			    out.write(json);
				
			}
			//req.getRequestDispatcher("/result.jsp").forward(req, resp);
		} 
		catch (Throwable e) {
			PrintWriter out = resp.getWriter();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);	  
		}
		
		return;
	}

	private List<String> runIndexing(HttpServletRequest request, String imagePath, String rootPath, String aimInputUID) {
		// TODO Auto-generated method stub
		 Thread indexer = new LireIndexingThread(imagePath, rootPath);
		   indexer.start();
			ArrayList<Document> positives = new ArrayList<Document>();
			ArrayList<Document> negatives = new ArrayList<Document>();
			
			IndexThread helper = new IndexThread(imagePath 
					+ aimInputUID + ".jpg", positives, negatives, rootPath + "index");

			try {
				indexer.join();
				Thread relFeedbackThread = new Thread(helper);
				relFeedbackThread.start();
				relFeedbackThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Call lire to process fetched images
			// helper.getHits().getResults().size();
			// Get the hits and return hits instead of matches
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
			request.getSession().setAttribute("uidtosimpleresultmap", uidToSimpleResultMap);
			List<String> uniqueImages = new ArrayList<String>();
			uniqueImages.addAll(hitImages);
			return uniqueImages;
		
	}

	private void fetchImages(String aimInputUID, String rootPath) {
		// TODO Auto-generated method stub
		CbirEpad cbi = null;
		String xmlQueryResult = null;
		try {
			cbi = new CbirEpad(aimInputUID, rootPath);
			cbi.run();
			//xmlQueryResult = cbi.generateXMLResults();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AimException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
