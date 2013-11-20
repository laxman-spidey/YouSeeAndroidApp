/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.yousee.main;

import org.json.JSONException;
import org.json.JSONObject;

import in.yousee.main.model.ProxyOpportunityItem;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService
{
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;
	Bundle extras;
	private static int notificationCount = 10;
	private Context context;

	public GcmIntentService()
	{
		super("GcmIntentService");

	}

	public static final String TAG = "GCM Demo";

	@Override
	protected void onHandleIntent(Intent intent)
	{
		Toast.makeText(getApplicationContext(), "gcm data recieved", Toast.LENGTH_LONG).show();
		extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you
		// received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty())
		{ // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is
			 * likely that GCM will be extended in the future with
			 * new message types, just ignore any message types
			 * you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
			{
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
			{
				sendNotification("Deleted messages on server: " + extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
			{
				// This loop represents the service doing some
				// work.
				for (int i = 0; i < 5; i++)
				{
					Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
					}
				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
				// Post notification of received message.

				sendNotificationTest("Received: " + extras.toString());
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
		// Release the wake lock provided by the
		// WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private ProxyOpportunityItem extractData(Bundle extras)
	{
		String idS = extras.getString("id");
		int id = new Integer(idS);
		String title = extras.getString("title");
		String description = extras.getString("description");
		String type = extras.getString("type");
		ProxyOpportunityItem proxyItem = new ProxyOpportunityItem(id, title, type, null, description);
		return proxyItem;

	}

	private static SharedPreferences getNotificationCountSharedPreferences(Context context)
	{
		return context.getSharedPreferences("NOTIFICATION_COUNT", MODE_PRIVATE);
	}

	private final static String TAG_NOTIFICATION_COUNT = "notificationCount";

	public static void resetNotificationCount(Context context)
	{
		SharedPreferences pref = getNotificationCountSharedPreferences(context);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(TAG_NOTIFICATION_COUNT, 0);
		editor.commit();
	}

	private void setNotificationCount(int count)
	{
		SharedPreferences pref = getNotificationCountSharedPreferences(getApplicationContext());
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(TAG_NOTIFICATION_COUNT, count);
		editor.commit();
	}

	private int getNotificationCount()
	{
		SharedPreferences pref = getNotificationCountSharedPreferences(getApplicationContext());
		return pref.getInt(TAG_NOTIFICATION_COUNT, 0);
	}

	private int incrementNotificationCount()
	{
		int currentCount = getNotificationCount();
		setNotificationCount(++currentCount);
		return notificationCount;
	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(String msg)
	{
		String notificationHeader = "New Opportunity.";
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		int notifCount = getNotificationCount();
		PendingIntent contentIntent;
		Intent resultIntent;
		if (notifCount == 0)
		{
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, IndividualOpportunityItemActivity.class), 0);
			resultIntent = new Intent(this, IndividualOpportunityItemActivity.class);
			notificationHeader = "1 New Opportunity.";

		} else
		{
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
			resultIntent = new Intent(this, MainActivity.class);
			notificationHeader = notifCount + " New Opportunities.";
		}
		incrementNotificationCount();
		Log.i("tag", msg);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(resultIntent);

		ProxyOpportunityItem proxyItem = extractData(extras);
		// resultIntent.putExtra("result", proxyItem.toJsonString());
		resultIntent.putExtra("result", proxyItem.toJsonString());
		Log.i("tag", "checking extra: " + resultIntent.getStringExtra("result"));
		String title = proxyItem.getTitle();
		int resourceId = proxyItem.getResourceOfCatagoryType();

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(notificationHeader).setStyle(new NotificationCompat.BigTextStyle().bigText(title)).setContentText(title);
		mBuilder.setAutoCancel(true);

		Bitmap icon = BitmapFactory.decodeResource(getResources(), resourceId);
		mBuilder.setLargeIcon(icon);

		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setNumber(notifCount);
		Notification notification = mBuilder.build();
		// notification.defaults = Notification.DEFAULT_ALL;
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	TaskStackBuilder stackBuilder;

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	private void sendCommonNotification(String msg)
	{
		String notificationHeader = "There are new Opportunities. Check them out!";
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		Intent resultIntent = new Intent(this, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addNextIntent(resultIntent);

		Log.i("tag", msg);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(notificationHeader).setStyle(new NotificationCompat.BigTextStyle().bigText(notificationHeader));
		mBuilder.setAutoCancel(true);

		Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

		mBuilder.setLargeIcon(icon);

		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		Notification notification = mBuilder.build();
		notification.number = notificationCount++;
		// notification.defaults = Notification.DEFAULT_ALL;
		mNotificationManager.notify(NOTIFICATION_ID, notification);
	}

	public void sendNotificationTest(String msg)
	{

		String notificationHeader = "There are new Opportunities. Check them out!";
		// Creates an explicit intent for an Activity in your app
		
		ProxyOpportunityItem proxyItem = extractData(extras);
		PendingIntent contentIntent;
		Intent resultIntent;
		Bitmap icon;
		incrementNotificationCount();
		int notifCount = getNotificationCount();
		if (notifCount == 1)
		{ 
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, IndividualOpportunityItemActivity.class), 0);
			resultIntent = new Intent(this, IndividualOpportunityItemActivity.class);
			notificationHeader = "1 New Opportunity.";
			icon = BitmapFactory.decodeResource(this.getResources(), proxyItem.getResourceOfCatagoryType());
		} else
		{
			contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
			resultIntent = new Intent(this, MainActivity.class);
			notificationHeader = notifCount + " New Opportunities.";
			icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// The stack builder object will contain an artificial back
		// stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads
		// out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent
		// itself)
		//stackBuilder.addParentStack(NotificationTestActivity.class);
		// Adds the Intent that starts the Activity to the top of the
		// stack
		String string = proxyItem.toJsonString();
		resultIntent.putExtra("result", string);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(notificationHeader).setContentText(proxyItem.getTitle());
		
		mBuilder.setLargeIcon(icon);

		mBuilder.setAutoCancel(true);
		
		mBuilder.setNumber(notifCount);
		// mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setContentIntent(contentIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(123456, mBuilder.build());
	}

}
