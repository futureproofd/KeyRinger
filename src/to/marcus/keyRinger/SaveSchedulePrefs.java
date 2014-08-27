package to.marcus.keyRinger;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveSchedulePrefs {
	private static String SCHEDULE = "com.marcus.vipringer.Schedule";
	private static String TIME = "time";
	private static String TIME2 = "time2";
	private static String TIMECURRENT = "timeCurrent";
	
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
		if (option == 1){
			return savedSchedule.getLong(TIME, 1023901293);
		}else if (option == 2){
			return savedSchedule.getLong(TIME2, 1023901293);
		}else{
			return savedSchedule.getLong(TIMECURRENT, 1043901293);
		} 
	}
}
