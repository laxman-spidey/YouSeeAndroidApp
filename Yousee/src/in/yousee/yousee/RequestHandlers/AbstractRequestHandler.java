package in.yousee.yousee.RequestHandlers;

import in.yousee.yousee.NetworkConnectionHandler;

import org.apache.http.client.methods.HttpPost;

public abstract class AbstractRequestHandler
{

	protected HttpPost postRequest;

	public abstract HttpPost buildRequest();

	protected void createRequest(String fileName)
	{
		postRequest = new HttpPost(NetworkConnectionHandler.DOMAIN + fileName);
	}

}
