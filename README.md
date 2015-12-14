YappySDK
========

SDK to integrate SMS apps with Yappy https://www.yappy.im 

##### Please note, Yappy used to be called EndlessJabber but due to a trademark issue, the name was changed. Most of the code still references EndlessJabber  
<br/>

Yappy is an android app that syncs a user's SMS/MMS database with a cloud service and allows the users to view and reply to the messages from https://www.yappy.im or the various tools provided by the service (XMPP, chrome extension, firefox addon-on, etc..)

Purpose of Library
----

In android devices running 4.4 and up, the app cannot touch the SMS repository, breaking a few features such as:

  - Marking messages as read (when a user reads a message via the website, the phone should mark the messages as read so the SMS   Notification/LED turn off)
  - When sending MMS messages, the MMS can not be saved to the local repository, so the user will never see the MMS message they sent
  - Deleting a conversation when it is deleted online
  - Deleting individual messages (SMS/MMS) when they are deleted online

This library allows Yappy to integrate with apps running on the user's system which have access to the default repository (are set as the default SMS system app). Yappy will then proxy commands for things such as marking messages as read to the app so it may perform the action on the SMS repository.

Usage
----

The SDK is meant to be utilised by apps which have access to the SMS repository. By generating the .jar and including as a library in your app, Yappy will automatically broadcast out messages which your app will receive and can act upon. 

Setup
----

Setup is straightforward . Build the .jar, include it as a library in your app, and add the following to your manifest file:

```xml
        <receiver
            android:name="com.mariussoft.endlessjabber.sdk.EndlessJabberReceiver"
            android:enabled="true" >
            <intent-filter>
                <action android:name="com.mariussoft.endlessjabber.action.extend" >
                </action>
            </intent-filter>
        </receiver>
        <service android:name="com.mariussoft.endlessjabber.sdk.EndlessJabberWakefulService"></service>
```

Implementation
----
First thing you need to do is to inherit from the class IEndlessJabberImplemenation. This class and its methods will be called by the SDK

```java
public class MySampleImplementation implements IEndlessJabberImplementation {

	public MySampleImplementation() {

	}

	/**
	 * Update all messages in the SMS/MMS repositories as read where the time <= the provided time as well as where the threadID matches the provided ID
	 * @param context	The context to utilize
	 * @param time		The 'time' in the SMS database to use when marking messages as read 
	 * 					(NOTE* for MMS divide by 1000 as MMS repository stores times as 1/1000th of the SMS)
	 * @param threadID	The ID of the thread in the SMS/MMS repositories to update
	 */
	@Override
	public void UpdateReadMessages(Context context, long time, int threadID) {

	}

	/**
	 * Delete any messages/conversations in the SMS/MMS repositories where the threadID matches the provided ID
	 * @param context	The context to utilize
	 * @param threadID	The ID of the thread in the SMS/MMS repositories to delete
	 */
	@Override
	public void DeleteThread(Context context, int threadID) {

	}

	/**
	 * Delete the message with the given messageID from the SMS repository
	 * @param context	The context to utilize
	 * @param threadID	The ID of the message in the SMS repository to delete
	 */
	public void DeleteSMSMessage(Context context,int messageID)	{
	
	}
	
	/**
	 * Delete the message with the given messageID from the MMS repository
	 * @param context	The context to utilize
	 * @param threadID	The ID of the message in the MMS repository to delete
	 */
	void DeleteMMSMessage(Context context,int messageID){
	
	}
	
	/**
	 * Method gets called when EndlessJabber is requested to send an MMS message via the web app
	 * @param context		The context to utilize
	 * @param recipients	List of recipients to send message to
	 * @param parts			The message parts to send along with message
	 * @param subject		The MMS subject of the message
	 * @param save			Whether or not to save the MMS to the MMS repository
	 * @param send			If true, send the message along with saving it to the MMS repository
	 * 						NOTE: On KitKat save will be true only when enabled in the SDK & your app is the default messaging app on the system
	 * 
	 * If both save & send are false, this call is only for informational purposes (e.g. modify notifications, update UI, etc...)
	 */
	@Override
	public void SendMMS(Context context, String[] recipients, MMSPart[] parts, String subject, boolean save, boolean send) {

	}

	/**
	 * Method gets called when EndlessJabber is requested to send an SMS message via the web app
	 * @param context		The context to utilize
	 * @param recipients	List of recipients to send message to
	 * @param message		The message to send
	 * @param send			If true, send the message along with saving it to the SMS repository, otherwise this is only for informational purposes (e.g. modify notifications, update UI, etc...)
	 */
	@Override
	public void SendSMS(Context context, String[] recipients, String message, boolean send) {

	}
}
```

Finally, you must tell the SDK that your app is ready to integrate. This should be called once either when your app is first installed, after a user checks a settings to enable integrate, and every time after integration options change.

```java
//IMPORTANT: This should be called once to finalize integration (can be done at app startup or when user sets option to enable integration)
			try {
				EndlessJabberInterface.EnableIntegration(getContext(), MySampleImplementation.class, true, true, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
```



Final Notes
----

To test the SDK integration, make sure Yappy is installed, start your app, then go to the settings tab in Yappy. Your apps name should be displayed as the SMS integration app if everything works. Any actions your perform on https://www.yappy.im/web/ which require the SDK integration will be called and proxied to your app.


## License

    Copyright 2014 Marius Dornean (MariusSoft LLC)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
