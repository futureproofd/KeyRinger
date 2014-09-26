package to.marcus.keyRinger;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveSchedulePrefs {
	private static String SCHEDULE = "com.marcus.vipringer.Schedule";
	private static String TIME = "time";
	private static String TIME2 = "time2";
	private static String TIMECURRENT = "timeCurrent";
	private static String USER_TOUCHED = "userTouched ";
	//also hold our alarm data
	private static String ALARM_START_ID = "AlarmStartId";
	private static String ALARM_STOP_ID = "AlarmStopId";
//	private static final String TAG = "SaveSchedulePrefs";
	
	//save set time (1 for start, 2 stop)
	public static boolean saveSchedule(Long schedule, int option, int requestCode, Context context){
		SharedPreferences settings = context.getSharedPreferences(SCHEDULE, 0);
		Editor editor = settings.edit();
		//start time or end time
		if (option == 1){
			editor.putLong(TIME, schedule);
			editor.putInt(ALARM_START_ID, requestCode);
			return editor.commit();
		}else if (option == 2){
			editor.putLong(TIME2, schedule);
			editor.putInt(ALARM_STOP_ID, requestCode);
			return editor.commit();
		}else{
			editor.putLong(TIMECURRENT, schedule);
			return editor.commit();
		}
	}
	
	//toggle user set alarm 
	//dont really need this anymore....
	public static boolean saveScheduleUserTouched(Boolean userTouched, int option, Context context){
		SharedPreferences settings = context.getSharedPreferences(SCHEDULE, 0);
		Editor editor = settings.edit();
			editor.putBoolean(USER_TOUCHED, userTouched);
			return editor.commit();
		}

	public static Long getSchedule(Context context, int option){
		SharedPreferences savedSchedule = context.getSharedPreferences(SCHEDULE, 0);
		//get shared prefs value or default, future time if no schedule has been set
		Long offset = savedSchedule.getLong(TIMECURRENT, 0);
		offset = offset + (24*60*60*1000);
		
		//Log.d(TAG," default set time: " + offset + " " + offset2);
		
		if (option == 1){
			return savedSchedule.getLong(TIME, offset);
		}else if (option == 2){
			return savedSchedule.getLong(TIME2, offset + 86400000); //24h+
		}else{
			return savedSchedule.getLong(TIMECURRENT, 1043901293);
		} 
	}
	
	public static boolean getScheduleUserTouched(Context context){
		SharedPreferences savedSchedule = context.getSharedPreferences(SCHEDULE, 0);
			return savedSchedule.getBoolean(USER_TOUCHED, false);
		
	}
	
	public static int getStartReqCode(Context context){
		SharedPreferences savedSchedule = context.getSharedPreferences(SCHEDULE, 0);
			return savedSchedule.getInt(ALARM_START_ID, 0);
	}
	
	public static int getStopReqCode(Context context){
		SharedPreferences savedSchedule = context.getSharedPreferences(SCHEDULE, 0);
			return savedSchedule.getInt(ALARM_STOP_ID, 0);
	}
}
