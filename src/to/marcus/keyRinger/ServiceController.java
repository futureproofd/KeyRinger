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
		boolean mUserTouched = SaveSchedulePrefs.getScheduleUserTouched(context);
		
		//version logic here
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
			Log.d(TAG, "entering KitKat logic");
			if (!mUserTouched){
				//cancel current alarm
				Intent i = new Intent(context, RingerService.class);
				PendingIntent pi = PendingIntent.getService(context, SaveSchedulePrefs.getStartReqCode(context), i, PendingIntent.FLAG_UPDATE_CURRENT);
				alarmManager.cancel(pi);
				//set future time +24h
				mTime = mTime + (24*60*60*1000);
				mTime2 = mTime2 + (24*60*60*1000);
				SaveSchedulePrefs.saveSchedule(mTime, 1, 1, context.getApplicationContext());
				SaveSchedulePrefs.saveSchedule(mTime2, 2, 2, context.getApplicationContext());
				//set new alarm
				alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime, pi);
			/*
				add 24 hrs to shared prefs
				alarmset = true
				

			 */
		//if user touched
			}else{
				//cancel current alarm
				//userSet = true
			}
	// pre KitKat
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
