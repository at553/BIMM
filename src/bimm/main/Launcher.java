package bimm.main;


import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;


public class Launcher {

    public static void main(String[] args){
    	
    	String path="/home/kurtz/Software/dbxml-2.5.16/install/lib/";
    	//String path="/home/bimm/dbxml-2.5.16/install/lib/";
    	
    	// set dbxml lib path
		setJavaLibPath(path);
		
		// Set up our xmldb

        try{
        	
            // list of all our webapps. We will load everything from here
            ArrayList<Handler> contextList = new ArrayList<Handler>();
        	
            // BIMM app       
            WebAppContext bimmApp = new WebAppContext();
            bimmApp.setDescriptor("webapps/bimm/WEB-INF/web.xml"); 
            bimmApp.setResourceBase("webapps/bimm");
            bimmApp.setContextPath("/bimm");
            bimmApp.setParentLoaderPriority(true);
            contextList.add(bimmApp);
            
            
            /*
                     // LINDA JSP test
            WebAppContext webApp = new WebAppContext();
            webApp.setDescriptor("webapps/jsp_test/WEB-INF/web.xml");
            webApp.setResourceBase("webapps/jsp_test");
            webApp.setContextPath("/jsp");
            webApp.setParentLoaderPriority(true);
            contextList.add(webApp);
            
            // Francisco hello world test
            WebAppContext helloApp = new WebAppContext();
            helloApp.setDescriptor("webapps/hello/WEB-INF/web.xml");
            helloApp.setResourceBase("webapps/hello");
            helloApp.setContextPath("/hola"); // url of servlet
            helloApp.setParentLoaderPriority(true);
            contextList.add(helloApp);
           */


            // Wrap our apps into a context handler collection
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(contextList.toArray(new Handler[0]));
            
            // start the server and add in our apps
            Server server = new Server(8210);
            server.setHandler(contexts);
            server.start();
            String statey = server.getState();
            System.out.println("Server is " + statey);
            server.join();

         
            /***********************
            // Set up parameters to access rufus, go as input to construct aimQuery
    		// Does this mean we need to make data / annotations folder within jetty?
            String namespace = "http://www.w3.org/2001/XMLSchema-instance";
    		String serverUrlDownload = "http://localhost:8210/annotations/xquery";
    		String serverUrlUpload = "http://localhost:8210/annotations/upload";
    		
    		// This is the official rufus berkeley database - not for bimm
    		//String serverUrlUpload = "http://rufus.stanford.edu:8100/annotations/upload";
    		//String serverUrlDownload = "http://rufus.stanford.edu:8100/annotations/xquery";
    		//String namespace = "gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM";
    		
    		// call query method to AIM file
            // we want to query based on descriptors
            aimQuery aim = new aimQuery(namespace, serverUrlUpload, serverUrlDownload);
            aim.query("liver.dbxml","RID5709");
            ***************************/
            
        }catch(Exception e){
        	e.printStackTrace();

        }

    }
    

    // Code to set the java library path for dbxml
	public static void setJavaLibPath(String classpath)  {
		System.out.println("setting java.library.path to "+classpath);
		try {
			Field field = ClassLoader.class.getDeclaredField("usr_paths");
			field.setAccessible(true);
			String[] paths = (String[])field.get(null);
			for (String path : paths) {
				if (classpath.equals(path))
					return;
			}
			String[] tmp = new String[paths.length+1];
			System.arraycopy(paths,0,tmp,0,paths.length);
			tmp[paths.length] = classpath;
			field.set(null,tmp);
			
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	

}
