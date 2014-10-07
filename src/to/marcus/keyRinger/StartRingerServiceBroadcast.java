package to.marcus.keyRinger;

import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartRingerServiceBroadcast extends BroadcastReceiver {
	
	public static final String TAG = "StartRingerService";

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar calendar = Calendar.getInstance();
		//get current time to help adjust scheduling times
		Long mCurrentTime = (long)calendar.getTimeInMillis();
		SaveSchedulePrefs.saveSchedule(mCurrentTime, 3, 1, context.getApplicationContext());
		//start and stop service
		Log.d(TAG, "Setting start alarm");
		ServiceController.setServiceAlarm(context.getApplicationContext(), true, false, 1);
		Log.d(TAG, "Setting stop alarm");
		ServiceController.setServiceAlarm(context.getApplicationContext(), false, false, 2);
	}
}
