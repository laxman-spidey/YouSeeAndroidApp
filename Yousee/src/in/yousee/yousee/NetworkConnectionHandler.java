package in.yousee.yousee;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkConnectionHandler implements Runnable
{
	Context context;
	String webContentResult;

	DownloadWebpageTask downloadwebContent;
	HttpPost postRequest;
	OnPostResponseRecievedListener listener;

	public NetworkConnectionHandler(Context context)
	{
		this.context = context;

	}

	public static boolean isNetworkConnected(Context context)
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
			Log.i("tag", "network connection is not available");
			return false;
		}
	}

	public void sendRequest(HttpPost postRequest, OnPostResponseRecievedListener listener)
	{
		this.listener = listener;
		this.postRequest = postRequest;
		downloadwebContent = new DownloadWebpageTask();
		downloadwebContent.execute(postRequest);

		// onResponseRecieved();

	}

	public void sendRequestInMultiThreadedMode(HttpPost postRequest, OnPostResponseRecievedListener listener)
	{
		this.listener = listener;
		this.postRequest = postRequest;
		Thread networkThread = new Thread(this);
		networkThread.start();
	}

	@Override
	public void run()
	{

		downloadwebContent = new DownloadWebpageTask();
		Log.i("tag", "networkThread Started");

		downloadwebContent.execute(postRequest);
	}

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
			webContentResult = result;
			Log.i("tag", "before notify");

			onResponseRecieved();

		}
	}

	public void onResponseRecieved()
	{

		Log.i("tag", " result length : " + webContentResult.length());

		int index = webContentResult.lastIndexOf('}');
		Log.i("tag", " index : " + index);
		String subString;
		if (index > 0)
		{
			subString = webContentResult;// .substring(0,
							// index);
			Log.i("tag", " result : " + subString);

			Map<String, String> map = new HashMap<String, String>();
			try
			{
				JSONObject jsonObject = new JSONObject(subString);
				Iterator keys = jsonObject.keys();

				while (keys.hasNext())
				{
					String key = (String) keys.next();
					map.put(key, jsonObject.getString(key));
				}
				System.out.println(map);// this map will contain
							// your
							// json stuff
			} catch (JSONException e)
			{
				e.printStackTrace();
			}

			Iterator<Map.Entry<String, String>> i = map.entrySet().iterator();
			while (i.hasNext())
			{

				String key = i.next().getKey();
				System.out.println(key + ", " + map.get(key));
			}
			listener.onPostResponseRecieved(subString);
		} else
		{
			
		}
		
		

	}

	private String downloadUrl(HttpPost postRequest) throws IOException
	{
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 1000;

		try
		{
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse response = httpclient.execute(postRequest);
			is = response.getEntity().getContent();
			String contentAsString = readIt(is, len);
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
	private String readIt(InputStream stream, int len) throws IOException
	{
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

}
