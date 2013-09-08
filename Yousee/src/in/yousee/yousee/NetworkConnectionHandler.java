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
	public static final String DOMAIN = "http://10.42.0.1:80/yousee_test/YouseeMobile/";

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

		}
		else
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
	private class DownloadWebpageTask extends AsyncTask<HttpPost, Void, HttpResponse>
	{

		@Override
		protected HttpResponse doInBackground(HttpPost... postRequests)
		{

			// params comes from the execute() call: params[0] is
			// the url.
			try
			{
				return downloadUrl(postRequests[0]);
			}
			catch (IOException e)
			{
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
	 * This method is called whenever the response is recieved from the
	 * server.
	 */

	public void onResponseRecieved(HttpResponse response)
	{
		String requestCodeString = response.getFirstHeader(Chef.TAG_NETWORK_REQUEST_CODE).getValue();
		int requestCode = new Integer(requestCodeString).intValue();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		listener.serveResponse(contentAsString, requestCode);

	}

	/**
	 * This method connects to Server and downloads Response is returned
	 */
	private HttpResponse downloadUrl(HttpPost postRequest) throws IOException
	{
		InputStream is = null;

		Log.i("tag", "download Started");
		HttpClient httpclient = new DefaultHttpClient();

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
