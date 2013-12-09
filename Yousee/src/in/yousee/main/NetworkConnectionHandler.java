package in.yousee.main;

import in.yousee.main.model.CustomException;
import in.yousee.main.model.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * NetworkConnectionHandler.java Purpose: Connects to server, receives response
 * and passes it to class which implements class Chef
 * 
 * @author Laxman
 * @version 1.0 15/08/2013
 */
public class NetworkConnectionHandler extends AsyncTask<HttpPost, Void, ResponseBody>
{
	// used to get System services to check network status and required
	// information
	Context context;

	// web service URL 
	public static final String DOMAIN = "http://192.168.0.3/yousee_test/YouseeMobile/";   
	//public static final String DOMAIN = "http://yousee.in/YouseeMobile/";
	// DownloadWebpageTask downloadwebContent;
	HttpPost postRequest;
	Middleware listener;
	public static String sessionId; 
	public static final DefaultHttpClient httpclient = new DefaultHttpClient();

	public static boolean isExecuting = false;
	public String toastString = null;

	public NetworkConnectionHandler(Context context)
	{
		this.context = context;

	}

	public NetworkConnectionHandler(Context context, Middleware listener)
	{
		this.listener = listener;
		this.context = context;

	} 

	/**
	 * Tells whether network is connected or not
	 * 
	 * @param Context
	 *                used to get System services
	 * 
	 * @throws CustomException 
	 * @see in.yousee.main.model.CustomException
	 * 
	 * @return network Connection status
	 */
	public static boolean isNetworkConnected(Context context) throws CustomException
	{
		Log.i("tag", "is network connected");
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		Log.i("tag", "network information is recieved");
		if (networkInfo != null && networkInfo.isConnected())
		{
			Log.i("tag", "network connection is available");
			return true;

		}
		else
		{
			Log.i("tag", "throwing exception");
			throw new CustomException(CustomException.ERROR_NETWORK_NOT_FOUND);
		}

	}

	/**
	 * Checks network status and creates a AsyncTask object starts its
	 * execution
	 * 
	 * @param HttpPostRequest
	 *                Post request object
	 * 
	 * @param Middleware
	 *                assigned to a global variable listener. a method of
	 *                this class is called after receiving response
	 * 
	 * @throws CustomException
	 * @see in.yousee.main.model.CustomException
	 */
	public ResponseBody sendRequest(HttpPost postRequest)
	{
		this.listener = listener;
		this.postRequest = postRequest;
		// downloadwebContent = new DownloadWebpageTask();

		try
		{

			return downloadUrl(postRequest);
		}
		catch (IOException e)
		{
			Log.i("tag", "cannot retrieve");
			e.printStackTrace();
			return null;
		}

		// onResponseRecieved();

	}

	/**
	 * This method does the same job as sendRequest() but executed in a new
	 * Thread
	 * 
	 * @param HttpPostRequest
	 *                Post request object
	 * 
	 * @param Middleware
	 *                assigned to a global variable listener. a method of
	 *                this class is called after receiving response
	 * 
	 * @throws CustomException
	 * @see in.yousee.main.model.CustomException
	 */
	public void sendRequestInMultiThreadedMode(HttpPost postRequest, Middleware listener) throws CustomException
	{
		this.listener = listener;
		this.postRequest = postRequest;
		// Thread networkThread = new Thread(this);
		try
		{
			Log.i("tag", "fksdjklfjskdhfkjshd");
			Log.i("tag", readIt(postRequest.getEntity().getContent()));
		}
		catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (NetworkConnectionHandler.isNetworkConnected(context))
		{
			Log.i("tag", "before Started");
			// downloadwebContent = new DownloadWebpageTask();
			// networkThread.start();
		}

	}

	public void run()
	{

		Log.i("tag", "networkThread Started");

		// downloadwebContent.execute(postRequest);
	}

	/**
	 * @param <HttpPost>
	 *                input
	 * 
	 * @param <void> progress
	 * 
	 * @param <HttpResponse>
	 *                output
	 */

	@Override
	protected ResponseBody doInBackground(HttpPost... postRequests)
	{
		isExecuting = true;
		return sendRequest(postRequests[0]);

	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(ResponseBody responseBody)
	{
		isExecuting = false;
		if (toastString != null)
		{
			Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
		}
		if (responseBody != null)
			listener.serveResponse(responseBody.getResponseString(), responseBody.getRequestCode());
	}

	/**
	 * This method is called whenever the response is received from the
	 * server.
	 */

	public ResponseBody onResponseRecieved(HttpResponse response)
	{
		int requestCode = 0;
		int resultCode = 0;

		if (response != null)
		{

			if (response.containsHeader(Middleware.TAG_NETWORK_REQUEST_CODE))
			{

				ResponseBody responseBody = new ResponseBody();
				Header[] headers = response.getAllHeaders();
				for (int i = 0; i < headers.length; i++)
				{
					Log.i("tag", "header " + headers[i].getName() + " : " + headers[i].getValue());
				}

				String requestCodeString = response.getFirstHeader(Middleware.TAG_NETWORK_REQUEST_CODE).getValue();
				Log.i("tag", "requestCode : " + requestCodeString);
				requestCode = Integer.valueOf(requestCodeString);
				responseBody.setRequestCode(requestCode);

				InputStream is = null;
				String contentAsString = null;
				try
				{
					is = response.getEntity().getContent();
					contentAsString = readIt(is);
					responseBody.setResponseString(contentAsString);

				}
				catch (IllegalStateException e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				finally
				{
					if (is != null)
					{
						try
						{
							is.close();
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}

					}
				}

				Log.i("tag", "content string : " + contentAsString);
				return responseBody;

			}
			else
				toastString = "Something went wrong, Please Report the issue to the developer.";

		}
		else
		{
			toastString = "Something went wrong, Please report the issue to the developer.";

		}
		return null;
	}

	/**
	 * This method connects to Server and downloads Response is returned
	 */
	private ResponseBody downloadUrl(HttpPost postRequest) throws IOException
	{
		InputStream is = null;

		Log.d("tag", "download Started" + readIt(postRequest.getEntity().getContent()));
		Header[] headers = postRequest.getAllHeaders();
		Log.i("tag", "lenght " + headers.length);
		// headers[i].getValue());
		for (int i = 0; i < headers.length; i++)
		{
			Log.i("tag", "request " + headers[i].getName() + " : " + headers[i].getValue());
		}
		Log.i("tag", readIt(postRequest.getEntity().getContent()));
		// httpclient.getCookieStore().addCookie();
		HttpResponse response = httpclient.execute(postRequest);

		return onResponseRecieved(response);

		// Makes sure that the InputStream is closed after the
		// app is
		// finished using it.

	}

	// Reads an InputStream and converts it to a String.
	private String readIt(InputStream stream) throws IOException
	{

		InputStreamReader is = new InputStreamReader(stream);
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(is);
		String read = br.readLine();

		while (read != null)
		{
			// System.out.println(read);
			sb.append(read);
			read = br.readLine();

		}

		return sb.toString();
	}

}
