package bimm.epad;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import bimm.epad.utils.UrlBuilder;

public class ClientAuthentication {
	
	public static String getID (String username, String password) throws ClientProtocolException, IOException
	{
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		String encoding = new String(Base64.encodeBase64("admin:admin".getBytes()));
		
		HttpPost identification= new HttpPost(UrlBuilder.getInstance().getSessionUrl());
		identification.setHeader("Authorization", "Basic" + encoding);
		CloseableHttpResponse response = client.execute(identification);
		HttpEntity responseEntity = response.getEntity();
		String val = EntityUtils.toString(responseEntity);
		return val;
		
	}

}
