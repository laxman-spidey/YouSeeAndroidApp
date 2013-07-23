package in.yousee.yousee;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class MainActivity extends SherlockActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		// requestWindowFeature(Window.FEATURE_PROGRESS);
		// setSupportProgressBarVisibility(true);
		// setSupportProgressBarIndeterminate(true);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// sendRequest();
		// sendTestRequest();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getSupportMenuInflater().inflate(R.menu.main, menu);
		// menu.add(R.id.group1, R.id.item1, 2, "fsajdkfjsdk");
		return true;
	}

	private boolean filterMenuVisibility = false;

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		switch (item.getItemId())
			{
			case R.id.action_filter:
				filterMenuVisibility = !(filterMenuVisibility);
				// showFilterMenu(filterMenuVisibility);

				break;

			default:
				break;
			}
		return true;
	}

	public void showFilterMenu(boolean visibility)
	{
		RelativeLayout lay = (RelativeLayout) findViewById(R.id.filterMenuLayout);
		if (visibility)
			lay.setVisibility(View.VISIBLE);
		else
			lay.setVisibility(View.INVISIBLE);
	}

	public void sendRequest()
	{

	}

	ArrayList<String> groupItem = new ArrayList<String>();
	ArrayList<Object> childItem = new ArrayList<Object>();

	public void setGroupData()
	{

		
		groupItem.add("Area");
		groupItem.add("Domain");
		groupItem.add("City");
		groupItem.add("Activity Type");
	}

}
