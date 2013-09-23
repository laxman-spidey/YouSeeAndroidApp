package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;

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
public class NetworkConnectionHandler implements Runnable
{
	// used to get System services to check network status and required
	// information
	Context context;

	// web service URL
	public static final String DOMAIN = "http://192.168.0.102:80/yousee_test/YouseeMobile/";

	DownloadWebpageTask downloadwebContent;
	HttpPost postRequest;
	Chef listener;
	public static String sessionId; 
	public static DefaultHttpClient httpclient;

	public NetworkConnectionHandler(Context context)
	{
		this.context = context;
		httpclient = new DefaultHttpClient();
	}

	/**
	 * Tells whether network is connected or not
	 * 
	 * @param Context
	 *                used to get System services
	 * 
	 * @throws CustomException
	 * @see in.yousee.yousee.model.CustomException
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
	 * @param Chef
	 *                assigned to a global variable listener. a method of
	 *                this class is called after receiving response
	 * 
	 * @throws CustomException
	 * @see in.yousee.yousee.model.CustomException
	 */
	public void sendRequest(HttpPost postRequest, Chef listener) throws CustomException
	{
		this.listener = listener;
		this.postRequest = postRequest;
		downloadwebContent = new DownloadWebpageTask();

		if (NetworkConnectionHandler.isNetworkConnected(context))
		{
			downloadwebContent.execute(postRequest);
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
	 * @param Chef
	 *                assigned to a global variable listener. a method of
	 *                this class is called after receiving response
	 * 
	 * @throws CustomException
	 * @see in.yousee.yousee.model.CustomException
	 */
	public void sendRequestInMultiThreadedMode(HttpPost postRequest, Chef listener) throws CustomException
	{
		this.listener = listener;
		this.postRequest = postRequest;
		Thread networkThread = new Thread(this);
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
			downloadwebContent = new DownloadWebpageTask();
			networkThread.start();
		}

	}

	@Override
	public void run()
	{

		Log.i("tag", "networkThread Started");

		downloadwebContent.execute(postRequest);
	}

	/**
	 * @param <HttpPost>
	 *                input
	 * 
	 * @param <void> progress
	 * 
	 * @param <Sring>
	 *                output
	 */
	private class DownloadWebpageTask extends AsyncTask<HttpPost, Void, HttpResponse>
	{

		@Override
		protected HttpResponse doInBackground(HttpPost... postRequests)
		{
			try
			{
				// Log.i("tag", "cannot retrieve : "
				// +postRequests[0].getURI().toString());
				return downloadUrl(postRequests[0]);
			}
			catch (IOException e)
			{
				Log.i("tag", "cannot retrieve");
				// HttpResponse response = new
				// BasicHttpResponse(null,
				// CustomException.IO_ERROR,
				// "IO error Occured");
				// Toast.makeText(context,
				// "IOException occured",
				// Toast.LENGTH_SHORT).show();

				e.printStackTrace();

				return null;
			}

		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(HttpResponse response)
		{
			onResponseRecieved(response);
		}

	}

	/**
	 * This method is called whenever the response is received from the
	 * server.
	 */

	public void onResponseRecieved(HttpResponse response)
	{
		int requestCode = 0;
		int resultCode = 0;

		if (response != null)
		{

			if (response.containsHeader(Chef.TAG_NETWORK_REQUEST_CODE))
			{

				Header[] headers = response.getAllHeaders();
				// Log.i("header "+headers[i].getName()+ " : ",
				// headers[i].getValue());
				for (int i = 0; i < headers.length; i++)
				{
					Log.i("tag", "header " + headers[i].getName() + " : " + headers[i].getValue());
				}

				String requestCodeString = response.getFirstHeader(Chef.TAG_NETWORK_REQUEST_CODE).getValue();
				Log.i("tag", "requestCode : " + requestCodeString);
				// String sessionId =
				// response.getFirstHeader("sessionId").getValue();
				// Log.i("tag", "sessionId : " + sessionId);
				requestCode = Integer.valueOf(requestCodeString);

				InputStream is = null;
				String contentAsString = null;
				try
				{
					is = response.getEntity().getContent();
					contentAsString = readIt(is);

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
							// TODO Auto-generated
							// catch
							// block
							e.printStackTrace();
						}
					}
				}
				// Log.i("tag", "content string : " +
				// contentAsString);
				listener.serveResponse(contentAsString, requestCode);
			}
			else
				Toast.makeText(context, "Something went wrong,", Toast.LENGTH_LONG).show();

		}
		else
		{
			Toast.makeText(context, "Something went wrong, Click on refresh.", Toast.LENGTH_LONG).show();
		}

	}

	/**
	 * This method connects to Server and downloads Response is returned
	 */
	private HttpResponse downloadUrl(HttpPost postRequest) throws IOException
	{
		InputStream is = null;

		Log.i("tag", "download Started" + readIt(postRequest.getEntity().getContent()));
		Header[] headers = postRequest.getAllHeaders();
		Log.i("tag","lenght "+headers.length);
		// headers[i].getValue());
		for (int i = 0; i < headers.length; i++)
		{
			Log.i("tag", "request " + headers[i].getName() + " : " + headers[i].getValue());
		}
		// httpclient.getCookieStore().addCookie();
		HttpResponse response = httpclient.execute(postRequest);

		return response;

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
