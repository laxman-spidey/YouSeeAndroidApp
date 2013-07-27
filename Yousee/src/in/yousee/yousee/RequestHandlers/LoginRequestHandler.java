package in.yousee.yousee.RequestHandlers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class LoginRequestHandler extends AbstractRequestHandler
{

	private String username;
	private String password;
	private final String FILE_NAME = "login.php";

	public LoginRequestHandler(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public HttpPost buildRequest()
	{
		super.createRequest(FILE_NAME);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("username", username));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		try
		{
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		return postRequest;

		// TODO Auto-generated method stub

	}

}
