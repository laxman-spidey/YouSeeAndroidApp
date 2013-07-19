package in.yousee.yousee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sendRequest();
		//sendTestRequest();
	}

	public void sendRequest()
	{
		try
		{
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy); 
			JSONObject ob = new JSONObject();
			String result = null;
			DefaultHttpClient client = new DefaultHttpClient();
			String postURL = "http://192.168.0.102:80/yousee_test/YouseeMobile/";
			HttpPost post = new HttpPost(postURL);
			// uncomment lines when we use no proxy(at home)
			HttpResponse response = client.execute(post);
			Log.i("tag","request sent");
			HttpEntity entity = response.getEntity();
			ob.put("username", "deo");
			ob.put("pwd", "deo123");
			StringEntity se = new StringEntity(ob.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
			TextView t = (TextView) findViewById(R.id.textView2);
			t.setText("hurrrayyyy");
			
			HttpResponse responsePOST = client.execute(post);
			Log.i("tag","request sent");
			HttpEntity resEntity = responsePOST.getEntity();
		} catch (Exception e)
		{
			e.printStackTrace();
			finish();
		}

	}

	public void sendTestRequest()
	{
		URL url;
		try
		{ 

			url = new URL("http://192.168.0.102:80/yousee_test/YouseeMobile/");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();

			DefaultHttpClient client = new DefaultHttpClient();
			String postURL = "http://192.168.0.102:80/yousee_test/YouseeMobile/";
			HttpPost post = new HttpPost(postURL);

			client.execute(post);

			TextView t = (TextView) findViewById(R.id.textView1);
			t.setText("hurrrayyyy");

		} catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
