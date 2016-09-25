package bimm.epad.utils;

public class UrlBuilder {
	private static UrlBuilder urlBuilderInstance = new UrlBuilder();
	private static String baseUrl = "http://epad-dev5.stanford.edu:8080/epad/";
	private UrlBuilder(){
	
	}
	
	public static UrlBuilder getInstance()
	{
		return urlBuilderInstance;
	}
	
	public String getSessionUrl()
	{
		return baseUrl + "session/";
	}
	
	public String getAimByUID(String uniqueIdentifier)
	{
		return baseUrl + "aimresource/?annotationUID=" + uniqueIdentifier + "&user=guest";
				//http://epad-dev2.stanford.edu:8080/epad/aimresource/?annotationUID=<uniqueIdentifier>&user=admin
		
	}
}
