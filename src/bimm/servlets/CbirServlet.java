package bimm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bimm.epad.cbir.CbirEpad;
import aim.AimTools;
import cbir.Cbir;


public class CbirServlet extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
		 doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws ServletException, IOException
	{
	
	try{
		//We look inside the get parameters to instantiate the CBIR with a AIM uid
		String aimInputUID = req.getParameter("aimInputUID");
		System.out.println("AIM uid Query = "+aimInputUID);
		
		if(aimInputUID!=null && !aimInputUID.equals("") && !aimInputUID.equals("null")){
			CbirEpad cbi = new CbirEpad(aimInputUID, req.getSession().getServletContext().getInitParameter("img.upload"));
			cbi.run();
			String xmlQueryResult=cbi.generateXMLResults();
			
			req.setAttribute("xmlResult", xmlQueryResult);
		}
		
		//We also get all the possible AIm uid to build the list in resultCBIR.jsp
		String xmlListOfAIMUID= AimTools.getALlAimUIDFromDatabase();
		req.setAttribute("xmlListOfAIMUID", xmlListOfAIMUID);

	} catch(Exception e) {
		e.printStackTrace();
	}
	req.getRequestDispatcher("/resultCBIR.jsp").forward(req, resp);
    return;
	}
	
	
}
