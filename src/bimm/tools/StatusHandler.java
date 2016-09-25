package bimm.tools;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * @author amsnyder
 */
public class StatusHandler extends AbstractHandler{


    private long startTime;

    // This must be the constructor
    public StatusHandler(){
    	// We just record the start time when we make it
        startTime = System.currentTimeMillis();
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws IOException, ServletException {
        httpResponse.setContentType("text/html");
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);

        long upTime = System.currentTimeMillis() - startTime;
        long upTimeSec = upTime/1000;
       

      //THIS IS WHAT I IDEALLY WANT TO DO, JUST FORWARD AFTER SETTING VALUES ON THE REQUEST
     /*
        try{
    	  httpRequest.getRequestDispatcher("/jsp/stat.jsp").forward(httpRequest, httpResponse);
      } catch (Exception e) {
    	  e.printStackTrace();
      }
     */
      
      //THIS IS AN ALTERNATE ALSO ACCEPTABLE METHOD, BUT NEED TO FIGURE OUT HOW TO SET UP SESSIONMANAGER
      //HttpSession session = httpRequest.getSession(true);
      //session.setAttribute("upTime",upTimeSec);
        
      //THIS WORKS, BUT WE CAN'T SET ATTRIBUTES ON THE REQUEST THIS WAY  
      httpResponse.sendRedirect("/jsp/status.jsp");
      return;

       //THIS IS ONLY IF WE CAN'T USE JSPS
        /*
        PrintWriter out = httpResponse.getWriter();

        out.print("<!doctype html>");
        out.print("<html>");
        out.print("<head>");
        //ToDo: replace the localhost with ip address.
        //out.print("<link type=\"text/css\" rel=\"stylesheet\" href=\"resources/proxy.css\">");
        out.print("</head>");
        out.print("<body>");
        out.print("<h3 class=\"update\">BIMM Server uptime: "+upTimeSec+" sec</h3>");
        out.print("<br />");
        out.print("</body>");
        out.print("</html>");
        */
    }
}
