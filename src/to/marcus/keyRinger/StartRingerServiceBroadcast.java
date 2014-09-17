package to.marcus.keyRinger;

import java.util.Calendar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartRingerServiceBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar calendar = Calendar.getInstance();
		//get current time to help adjust scheduling times
		Long mCurrentTime = (long)calendar.getTimeInMillis();
		SaveSchedulePrefs.saveSchedule(mCurrentTime, 3, 1, context.getApplicationContext());
		//start and stop service
		ServiceController.setServiceAlarm(context.getApplicationContext(), true, false);
		ServiceController.setServiceAlarm(context.getApplicationContext(), false, false);
	}
}
