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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetworkConnectionHandler
{
	Context context;
	String webContentResult;

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

	public void createConnection()
	{

	}

	public void sendRequest()
	{
		Log.i("tag", "send request started");
		DownloadWebpageTask downloadwebContent = new DownloadWebpageTask();
		String postURL = "http://192.168.0.3:80/yousee_test/YouseeMobile/";
		downloadwebContent.execute(postURL);

		Log.i("tag", "response returned");
		// Log.i("tag", " result : "+webContentResult);

	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... urls)
		{

			// params comes from the execute() call: params[0] is
			// the url.
			try
			{
				return downloadUrl(urls[0]);
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
			onResponseRecieved();

		}
	}

	public void onResponseRecieved()
	{

		Log.i("tag", " result length : " + webContentResult.length());

		int index = webContentResult.lastIndexOf(';');
		Log.i("tag", " index : " + index);
		String subString = webContentResult.substring(0, index);
		Log.i("tag", " result : " + subString);
		if (index < 0)
		{
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
		}
		else
		{
			
		}

	}

	private String downloadUrl(String myurl) throws IOException
	{
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 500;

		try
		{
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);

			// Starts the query
			conn.connect();
			// long contentLength =
			// Long.parseLong(conn.getHeaderField("Content-Length"));
			int response = conn.getResponseCode();
			is = conn.getInputStream();
			String res = conn.getResponseMessage();

			// Log.i("tag", "The response is: " + res);
			// Log.i("tag", "The content length is: " +
			// contentLength);
			// Toast.makeText(context, "The content length is: " +
			// contentLength, Toast.LENGTH_LONG).show();

			// Convert the InputStream into a string
			String contentAsString = readIt(is, len);
			conn.disconnect();
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
	public String readIt(InputStream stream, int len) throws IOException
	{
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

}
