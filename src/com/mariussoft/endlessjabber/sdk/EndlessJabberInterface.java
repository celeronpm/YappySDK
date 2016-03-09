package com.mariussoft.endlessjabber.sdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class EndlessJabberInterface {
	
	/**
	 * The intent to broadcast to
	 */
	static final String EndlessJabber_INTENT = "com.mariussoft.endlessjabber.action.extendResponse";
	
	static final String Pref_Store ="EndlessJabberSDK";
	static final String Pref_SendSMS = "SendSMS";
	static final String Pref_SendMMS = "SendMMS";
	static final String Pref_DontDelete = "DontDelete";
	static final String Pref_SupportsMarkAsReadSignal = "SupportsMarkAsReadSignal";
	static final String Pref_InterfaceClass = "InterfaceClass";
	

	/** Checks whether or not EndlessJabber is installed */
	public static boolean IsInstalled(Context ctx) {

		PackageManager pm = ctx.getPackageManager();
		try {
			pm.getPackageInfo("com.mariussoft.endlessjabber", PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/** Opens up GooglePlay and redirects it to the EndlessJabber app */
	public static void OpenGooglePlayLink(Context ctx, String referralCode) {

		SharedPreferences.Editor editor = ctx.getSharedPreferences(Pref_Store, Context.MODE_PRIVATE).edit();
		editor.putString("Referral", referralCode);
		editor.commit();

		final String appPackageName = "com.mariussoft.endlessjabber";
		try {
			ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName + "&referrer=utm_source%3D" + referralCode)));
		} catch (android.content.ActivityNotFoundException anfe) {
			ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName + "&referrer=utm_source%3D" + referralCode)));
		}
	}

	/** Launches EndlessJabber */
	public static void LaunchEndlessJabber(Context ctx) {
		Intent intent = ctx.getPackageManager().getLaunchIntentForPackage("com.mariussoft.endlessjabber");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	/**
	 * Enables integration by specifying which class to call
	 * 
	 * @param context
	 *            The context to use
	 * @param concreteImplementation
	 *            The concrete implementation to call on events
	 * @param sendSMS
	 *            If true, your app will be responsible for sending SMS messages
	 *            as well as persisting them to the SMS repository
	 * @param sendMMS
	 *            If true, your app will be responsible for sending & persisting
	 *            MMS messages to the MMS repository
	 * @param dontDeleteMessages
	 *            If true, your app will be responsible for deleting conversations and messages when signaled
	 * @param supportsMarkAsReadSignal
	 * 			 Set this to true to prevent Yappy from automatically tracking when messages have been read. Whenever a conversation thread has been read,
	 *           you MUST call the appropriate method to notify Yappy of a thread read.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static void EnableIntegration(Context context, Class concreteImplementation, boolean sendSMS, boolean sendMMS, boolean dontDeleteMessages, boolean supportsMarkAsReadSignal) throws Exception {
		Class<?> clazz = concreteImplementation;
		if (!IEndlessJabberImplementation.class.isAssignableFrom(clazz)) {
			throw new Exception("Class must implement IEndlessJabberImplementation");
		}

		SharedPreferences.Editor editor = context.getSharedPreferences(Pref_Store, Context.MODE_PRIVATE).edit();
		editor.putString("InterfaceClass", concreteImplementation.getName());
		editor.putBoolean(Pref_SendSMS, sendSMS);
		editor.putBoolean(Pref_SendMMS, sendMMS);
		editor.putBoolean(Pref_DontDelete, dontDeleteMessages);
		editor.putBoolean(Pref_SupportsMarkAsReadSignal, supportsMarkAsReadSignal);
		editor.commit();

		SendInfoToEndlessJabber(context);
	}

	/** Disables integration */
	@SuppressWarnings("rawtypes")
	public static void DisableIntegration(Context context, Class nameOfImplementation) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Pref_Store, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();

		SendInfoToEndlessJabber(context);
	}

	/**
	 * Refresh integration info with EndlessJabber
	 * 
	 * @param ctx
	 *            Context to use
	 */
	static void SendInfoToEndlessJabber(Context context) {
		
		SharedPreferences prefs = context.getSharedPreferences(Pref_Store, Context.MODE_PRIVATE);

		Intent i = new Intent();
		i.setAction(EndlessJabber_INTENT);		
		i.putExtra("Action", "UpdateInfo");
		i.putExtra("PackageName", context.getPackageName());
		
		i.putExtra("Enabled", prefs.contains(Pref_InterfaceClass));
		i.putExtra("Referral", prefs.getString("Referral", ""));

		if (prefs.contains("InterfaceClass")) {
			i.putExtra(Pref_SendSMS, prefs.getBoolean(Pref_SendSMS, false));
			i.putExtra(Pref_SendMMS, prefs.getBoolean(Pref_SendMMS, false));
 			i.putExtra(Pref_DontDelete, prefs.getBoolean(Pref_DontDelete, false));
 			i.putExtra(Pref_SupportsMarkAsReadSignal, prefs.getBoolean(Pref_SupportsMarkAsReadSignal, false));
		}
		
		//Yappy uses this to track SDK version and capabilities
		i.putExtra("SDKVersion", 1);

		context.sendBroadcast(i);
	}
	
	/**
	 * If SupportsMarkAsReadSignal was set to true, allows you to notify Yappy when a conversation thread was read
	 * 
	 * @param context 
	 * 			The context to use
	 * @param messageID 
	 * 			The thread ID of the conversation in the stock SMS/MMS database
	 */
	public static void MarkAsRead(Context context, int threadID) {
		SharedPreferences prefs = context.getSharedPreferences(Pref_Store, Context.MODE_PRIVATE);

		if (!prefs.contains("InterfaceClass")) {

			throw new RuntimeException("The SDK has not been properly set up, please call EnableIntegration first");
		}

		if (!prefs.contains(Pref_SupportsMarkAsReadSignal) || !prefs.getBoolean(Pref_SupportsMarkAsReadSignal, false)) {
			throw new RuntimeException("SupportsMarkAsReadSignal was not specified during setup.");
		}
				
		Intent i = new Intent();
		i.setAction(EndlessJabber_INTENT);		
		i.putExtra("Action", "MarkAsRead");
		i.putExtra("PackageName", context.getPackageName());
		
		i.putExtra("ThreadID", threadID);
		context.sendBroadcast(i);
	}
}
