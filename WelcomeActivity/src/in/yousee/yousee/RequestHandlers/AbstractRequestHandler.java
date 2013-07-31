package in.yousee.yousee.RequestHandlers;

import org.apache.http.client.methods.HttpPost;

public abstract class AbstractRequestHandler
{
	protected final String DOMAIN = "http://192.168.0.102:80/yousee_test/YouseeMobile/";
	protected HttpPost postRequest;

	public abstract HttpPost buildRequest();
	protected void createRequest(String fileName)
	{
		postRequest = new HttpPost(DOMAIN + fileName);
	}


}
