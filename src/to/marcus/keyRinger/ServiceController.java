package to.marcus.keyRinger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceController {
	
	public static final String STOP_SWITCH = "stop";
	public static final String TAG = "com.marcus.KeyRinger.ServiceController";
	public static final String ID = "id";
	
	//set alarm to trigger service
	@SuppressLint("NewApi")
	public static void setServiceAlarm(Context context, boolean isOn, boolean userControlled){
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Long mTime = SaveSchedulePrefs.getSchedule(context, 1);
		Long mTime2 = SaveSchedulePrefs.getSchedule(context, 2);
		Long mCurrentTime = SaveSchedulePrefs.getSchedule(context, 3);
		//boolean mUserTouched = SaveSchedulePrefs.getScheduleUserTouched(context);
		
		//version 4.4
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
			Log.d(TAG, "entering KitKat logic");
			//repeating alarm
			if (!userControlled){
				Intent i = new Intent(context, RingerService.class);
				if(isOn){
					i.setAction("start");
					//cancel current alarm
					PendingIntent piStart = PendingIntent.getService(context, SaveSchedulePrefs.getStartReqCode(context), i, PendingIntent.FLAG_UPDATE_CURRENT);
					Log.d(TAG, "cancelling old START non-user pending intent with reqCode: " + SaveSchedulePrefs.getStartReqCode(context));
					alarmManager.cancel(piStart);
					if (mTime > mCurrentTime){
						Log.d(TAG, "non-user kitkat Start alarm loop " + mTime);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime, piStart);
					}else{
						Log.d(TAG, "non-user kitkat future Start alarm loop "  + mTime);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), piStart);
					}
					SaveSchedulePrefs.saveSchedule(mTime + (24*60*60*1000), 1, 1, context.getApplicationContext());
				}else{
					i.setAction("stop");
					//cancel current alarm
					PendingIntent piStop = PendingIntent.getService(context, SaveSchedulePrefs.getStopReqCode(context), i, PendingIntent.FLAG_UPDATE_CURRENT);
					Log.d(TAG, "cancelling old STOP non-user pending intent w reqCode: "+ SaveSchedulePrefs.getStopReqCode(context));
					alarmManager.cancel(piStop);
					if (mTime2 > mCurrentTime){
						Log.d(TAG, "non-user kitkat Stop alarm loop " + mTime2);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2, piStop);
					}else{
						Log.d(TAG, "non-user kitkat future Stop alarm loop "  + mTime2);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2 + (24*60*60*1000), piStop);
					}		
					SaveSchedulePrefs.saveSchedule(mTime2 + (24*60*60*1000), 2, 2, context.getApplicationContext());
				}
				
		//if user set alarm
			}else{
				Intent i = new Intent(context, RingerService.class);
				//cancel current alarm
				if(isOn){
					i.setAction("start");
					PendingIntent piStart = PendingIntent.getService(context, SaveSchedulePrefs.getStartReqCode(context), i, PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.cancel(piStart);
					Log.d(TAG, "cancelling user set pending intent for Start w reqCode: " + SaveSchedulePrefs.getStartReqCode(context));
					if (mTime > mCurrentTime){
						Log.d(TAG, "user changed kitkat Start alarm loop " + piStart);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime, piStart);
					}else{
						Log.d(TAG, "user changed kitkat future Start alarm loop "  + piStart);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), piStart);
					}
				}else{
					i.setAction("stop");
					PendingIntent piStop = PendingIntent.getService(context, SaveSchedulePrefs.getStopReqCode(context), i, PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.cancel(piStop);
					Log.d(TAG, "cancelling user set pending intent for Stop w reqCode: " + SaveSchedulePrefs.getStopReqCode(context));
					if (mTime2 > mCurrentTime){
						Log.d(TAG, "user changed kitkat Stop alarm loop " + piStop);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2, piStop);
					}else{
						Log.d(TAG, "user changed kitkat future Stop alarm loop "  + piStop);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2 + (24*60*60*1000), piStop);
					}
				}
				//reset user touched 
			//	mUserTouched = false;
			//	SaveSchedulePrefs.saveScheduleUserTouched(mUserTouched, 4, context);
			}
	//pre-KitKat
		}else{
			//start
			if(isOn){
				Intent i = new Intent(context, RingerService.class);
				i.setAction("start");
				PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
				if (mTime > mCurrentTime){
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime, AlarmManager.INTERVAL_DAY, pi);
				}else{
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), AlarmManager.INTERVAL_DAY, pi);
				}
			//stop
			}else{
				Intent i2 = new Intent(context, RingerService.class);
				i2.setAction("stop");
				PendingIntent pi2 = PendingIntent.getService(context, 0, i2, 0); //set cancel flag
				if (mTime2 > mCurrentTime){
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime2, AlarmManager.INTERVAL_DAY, pi2);
				}else{
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime2 + (24*60*60*1000), AlarmManager.INTERVAL_DAY, pi2);
				}
			}
		}
	}
}
