package to.marcus.keyRinger;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ServiceController {
	
	public static final String STOP_SWITCH = "stop";
	public static final String TAG = "com.marcus.vipringer.ServiceController";
	
	//set alarm to trigger service
	public static void setServiceAlarm(Context context, boolean isOn){
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Long mTime = SaveSchedulePrefs.getSchedule(context, 1);
		Long mTime2 = SaveSchedulePrefs.getSchedule(context, 2);
		Long mCurrentTime = SaveSchedulePrefs.getSchedule(context, 3);

		if(isOn){
			Intent i = new Intent(context, RingerService.class);
			i.setAction("start");
			PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
				if (mTime > mCurrentTime){
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime, AlarmManager.INTERVAL_DAY, pi);
				}else{
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, mTime + (24*60*60*1000), AlarmManager.INTERVAL_DAY, pi);
				}
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
