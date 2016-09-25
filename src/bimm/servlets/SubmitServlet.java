package bimm.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bimm.tools.Query;

public class SubmitServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
			 doPost(req, resp);
		}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
		{
		/*
		resp.setContentType("application/json");
		resp.setStatus(HttpServletResponse.SC_OK);
		*/

		try{
			Query.setUpXmlManager();
			String ridQuery = req.getParameter("allRelatedRIDgroups");
			System.out.println("ridQuery = "+ridQuery);
			//String xmlQueryResult = Query.setUpXmlManager();
			String xmlQueryResult = Query.crossProductQueries(ridQuery);
			//System.out.println("xmlQueryResult ="+xmlQueryResult);
			
			xmlQueryResult = xmlQueryResult.replaceAll("<","\\<");
			xmlQueryResult = xmlQueryResult.replaceAll(">","\\>;");
			req.setAttribute("xmlResult", xmlQueryResult);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		req.getRequestDispatcher("/result.jsp").forward(req, resp);
        return;
		}
}
