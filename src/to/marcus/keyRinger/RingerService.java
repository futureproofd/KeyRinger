package to.marcus.keyRinger;

import java.util.Calendar;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class RingerService extends Service {
	
	private static final String TAG = "RingerService";
	private TelephonyManager mTelephonyManager;
	private PhoneStateListener mPhoneStateListener;
	private AudioManager myRingerManager;
	private Contact mContact;
	private String number;
	private Context mContext;
	private Long mCurrentTime;
	

	@Override
	public IBinder onBind(Intent intent) {
		//stub
		return null;
	}
	
	//setup telephony controls
	//get Contact array
	 @Override
	   public void onCreate(){
		   mContext = (Context)this;

	       mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	       Log.d(TAG, "Started Service");
	       
	       mPhoneStateListener = new PhoneStateListener(){
	           // state change 
	           @Override
	           public void onCallStateChanged(int state, String incomingNumber){
	        	 //set ringer stuff
	        	   myRingerManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
	        	   int ringerMode = myRingerManager.getRingerMode();
	               if (state == 1){ 
	            	   try{
	            		   Log.d(TAG, " number from incoming call: " + incomingNumber);
							mContact = ContactStorage.get(mContext).getContactNumber(incomingNumber);
							number = mContact.getNumber();
							Log.d(TAG, " number from contact object: " +number);
							if(mContact.isEnabled()){
								switch(ringerMode){
									case 1:
										myRingerManager.setRingerMode(2);
									case 0:
										myRingerManager.setRingerMode(2);
									}
							}
						}catch(Exception e){
							Log.d(TAG, " exception: " + e + " " + number);
							e.printStackTrace();
						}
	               }else{
	            	   //revert to silence
	                   if (state == 0){
							myRingerManager.setRingerMode(0);
	                   };
	               }
	           }   
	       };
	      super.onCreate();
	   }
	
	//register listener
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if (intent == null || intent.getAction() == null){
			Log.d(TAG, "resending intent because it was null");
			return START_REDELIVER_INTENT;
			
		}
		
		boolean isStop = (intent.getAction().equals("stop")) ? true : false;
		//create calendar to get time elements
		Calendar calendar = Calendar.getInstance();
		//get current time to help adjust scheduling times
		mCurrentTime = (long)calendar.getTimeInMillis();
		Log.d(TAG, "current time update: " + mCurrentTime);
		SaveSchedulePrefs.saveSchedule(mCurrentTime, 3, 1, getApplicationContext());
		
	//KitKat
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
			if(isStop){
				Log.d(TAG, " stopping service 4.4: " + startId + " " + intent);
				mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
				ServiceController.setServiceAlarm(this, false, false, 2);
				return START_STICKY;
			}else{
				Log.d(TAG, " starting service 4.4: " + startId + " " + intent);
				mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
				ServiceController.setServiceAlarm(this, true, false, 1);
				return START_STICKY;
			}
	//pre-KitKat
		}else{
			//stop
			if (isStop){
				//stopSelfResult(startId);
				mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
				Log.d(TAG, " stopping service from intent action: " + startId + " " + intent + " " + isStop);
				return START_STICKY;	
			//start
			}else{
				mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
				Log.d(TAG, " starting service: " + startId + " " + intent + " " + isStop);
				return START_STICKY;
			}
		}
	}
	
	//shut down
	@Override
	public void onDestroy(){
		Log.d(TAG, "Shutting down the service"); //shouldn't occur regularly
		mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		super.onDestroy();
	}

}
