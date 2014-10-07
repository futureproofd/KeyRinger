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
	public static void setServiceAlarm(Context context, boolean isOn, boolean userControlled, int reqCode){
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Long mTime = SaveSchedulePrefs.getSchedule(context, 1);
		Long mTime2 = SaveSchedulePrefs.getSchedule(context, 2);
		Long mCurrentTime = SaveSchedulePrefs.getSchedule(context, 3);

		//version 4.4
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
			Log.d(TAG, "entering KitKat logic");
			//auto-repeating alarm
			if (!userControlled){
				Intent i = new Intent(context, RingerService.class);
				if(isOn){
					i.setAction("start");
					//cancel current alarm
					PendingIntent piStart = PendingIntent.getService(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
					Log.d(TAG, "cancelling old START AUTO pending intent with reqCode: " + reqCode);
					alarmManager.cancel(piStart);
					if (mTime > mCurrentTime){
						Log.d(TAG, "mTime is Greater: AUTO kitkat Start alarm loop " + mTime);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime, piStart);
					}else{
						Log.d(TAG, "mTime is Less: AUTO kitkat future Start alarm loop "  + (mTime + (24*60*60*1000)));
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), piStart);
					}
					Log.d(TAG, "START TIME day increase "  + (mTime + (24*60*60*1000)));
					SaveSchedulePrefs.saveSchedule(mTime + (24*60*60*1000), 1, 1, context.getApplicationContext());
				}else{
					i.setAction("stop");
					//cancel current alarm
					PendingIntent piStop = PendingIntent.getService(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
					Log.d(TAG, "cancelling old STOP AUTO pending intent w reqCode: "+ reqCode);
					alarmManager.cancel(piStop);
					if (mTime2 > mCurrentTime){
						Log.d(TAG, "Mtime is Greater: AUTO kitkat Stop alarm loop " + mTime2);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2, piStop);
					}else{
						Log.d(TAG, "mTime is less: AUTO kitkat future Stop alarm loop "  + (mTime2 + (24*60*60*1000)));
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2 + (24*60*60*1000), piStop);
					}
					Log.d(TAG, "STOP TIME day increase "  + (mTime2 + (24*60*60*1000)));
					SaveSchedulePrefs.saveSchedule(mTime2 + (24*60*60*1000), 2, 2, context.getApplicationContext());
				}
		//if user set alarm
			}else{
				Intent i = new Intent(context, RingerService.class);
				//cancel current alarm
				if(isOn){
					i.setAction("start");
					PendingIntent piStart = PendingIntent.getService(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.cancel(piStart);
					Log.d(TAG, "cancelling USER SET pending intent for Start w reqCode: " + reqCode);
					if (mTime > mCurrentTime){
						Log.d(TAG, "USER changed kitkat Start alarm loop " + mTime);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime, piStart);
					}else{
						Log.d(TAG, "USER changed kitkat future Start alarm loop "  + (mTime + (24*60*60*1000)));
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), piStart);
					}
				}else{
					i.setAction("stop");
					PendingIntent piStop = PendingIntent.getService(context, reqCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
					alarmManager.cancel(piStop);
					Log.d(TAG, "cancelling USER SET pending intent for Stop w reqCode: " + reqCode);
					if (mTime2 > mCurrentTime){
						Log.d(TAG, "USER changed kitkat Stop alarm loop " + mTime2);
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2, piStop);
					}else{
						Log.d(TAG, "USER changed kitkat future Stop alarm loop "  + (mTime2 + (24*60*60*1000)));
						alarmManager.setExact(AlarmManager.RTC_WAKEUP, mTime2 + (24*60*60*1000), piStop);
					}
				}
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
