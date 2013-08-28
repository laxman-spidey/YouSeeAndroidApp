package in.yousee.yousee;

import in.yousee.yousee.model.CustomException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

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

	public NetworkConnectionHandler(Context context)
	{
		this.context = context;

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

		} else
		{
			throw new CustomException(CustomException.NETWORK_NOT_FOUND);
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
	 *                this class is called after recieving response
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
	 *                this class is called after recieving response
	 * 
	 * @throws CustomException
	 * @see in.yousee.yousee.model.CustomException
	 */
	public void sendRequestInMultiThreadedMode(HttpPost postRequest, Chef listener) throws CustomException
	{
		this.listener = listener;
		this.postRequest = postRequest;
		Thread networkThread = new Thread(this);
		if (NetworkConnectionHandler.isNetworkConnected(context))
		{
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
	private class DownloadWebpageTask extends AsyncTask<HttpPost, Void, String>
	{

		@Override
		protected String doInBackground(HttpPost... postRequests)
		{

			// params comes from the execute() call: params[0] is
			// the url.
			try
			{
				return downloadUrl(postRequests[0]);
			} catch (IOException e)
			{
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result)
		{
			onResponseRecieved(result);
		}
	}

	/**
	 * This method is called whenever the response is recieved from the
	 * server.
	 */

	public void onResponseRecieved(String webContentResult)
	{

		listener.serveResponse(webContentResult);

	}

	/**
	 * This method connects to Server and downloads Response String is
	 * extracted from Body of response
	 */
	private String downloadUrl(HttpPost postRequest) throws IOException
	{
		InputStream is = null;

		try
		{
			Log.i("tag", "download Started");
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse response = httpclient.execute(postRequest);
			is = response.getEntity().getContent();
			String contentAsString = readIt(is);
			Log.i("tag", "download completed");
			return contentAsString;

			// Makes sure that the InputStream is closed after the
			// app is
			// finished using it.
		} finally
		{
			if (is != null)
			{
				is.close();
			}
		}

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
