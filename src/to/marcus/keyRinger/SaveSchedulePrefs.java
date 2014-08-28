package to.marcus.keyRinger;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SaveSchedulePrefs {
	private static String SCHEDULE = "com.marcus.vipringer.Schedule";
	private static String TIME = "time";
	private static String TIME2 = "time2";
	private static String TIMECURRENT = "timeCurrent";
	private static final String TAG = "SaveSchedulePrefs";
	
	public static boolean saveSchedule(Long schedule, int option, Context context){
		SharedPreferences settings = context.getSharedPreferences(SCHEDULE, 0);
		Editor editor = settings.edit();
		//start time or end time
		if (option == 1){
			editor.putLong(TIME, schedule);
			return editor.commit();
		}else if (option == 2){
			editor.putLong(TIME2, schedule);
			return editor.commit();
		}else{
			editor.putLong(TIMECURRENT, schedule);
			return editor.commit();
		}
	}
	
	public static Long getSchedule(Context context, int option){
		SharedPreferences savedSchedule = context.getSharedPreferences(SCHEDULE, 0);
		//get shared prefs value or default, future time if no schedule has been set
		Long offset = savedSchedule.getLong(TIMECURRENT, 0);
		offset = offset + (24*60*60*1000);
		Long offset2 = offset + 86400000;
		Log.d(TAG," default set time: " + offset + " " + offset2);
		
		if (option == 1){
			return savedSchedule.getLong(TIME, offset);
		}else if (option == 2){
			return savedSchedule.getLong(TIME2, offset + 86400000); //24h+
		}else{
			return savedSchedule.getLong(TIMECURRENT, 1043901293);
		} 
	}
}
