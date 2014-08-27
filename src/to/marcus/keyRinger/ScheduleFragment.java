package to.marcus.keyRinger;

import java.util.Calendar;
import to.marcus.keyRinger.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class ScheduleFragment extends DialogFragment {
	
	public static final String EXTRA_TIME = "com.marcus.vipringer.time";
	public static final String EXTRA_TIME2 = "com.marcus.vipringer.time2";
	public static final String EXTRA_CURRENTTIME = "com.marcus.vipringer.currentTime";
	private Long mTime;
	private Long mTime2;
	private Long mCurrentTime;
	
	//fragment args for holding our date (sharedPreferences)
	public static ScheduleFragment newInstance(Long date, Long date2, Long date3){ 
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_TIME, date);
		args.putSerializable(EXTRA_TIME2, date2);
		args.putSerializable(EXTRA_CURRENTTIME, date3);
		
		ScheduleFragment fragment = new ScheduleFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	//inflate timepicker view and save selection
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		
		//to hold sharedPrefs through fragment argument
		mTime = (Long)getArguments().getSerializable(EXTRA_TIME);
		mTime2 = (Long)getArguments().getSerializable(EXTRA_TIME2);
		
		//create calendar to get time elements
		Calendar calendar = Calendar.getInstance();
		
		//get current time to help adjust scheduling times
		mCurrentTime = (long)calendar.getTimeInMillis();
		SaveSchedulePrefs.saveSchedule(mCurrentTime, 3, getActivity());
		
		//set both start and stop times
		calendar.setTimeInMillis(mTime);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		
		calendar.setTimeInMillis(mTime2);
		int hour2 = calendar.get(Calendar.HOUR_OF_DAY);
		int minute2 = calendar.get(Calendar.MINUTE);
		
		//get view
		View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);
		
		//get timepicker object of the currently inflated view and set date from shared prefs
		TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_picker);
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
		
		TimePicker timePicker2 = (TimePicker)v.findViewById(R.id.dialog_time_picker2);
			timePicker2.setCurrentHour(hour2);
			timePicker2.setCurrentMinute(minute2);
		
		TextView startTime = (TextView)v.findViewById(R.id.start_schedule);
			startTime.setText("Start");
		
		TextView endTime = (TextView)v.findViewById(R.id.end_schedule);
			endTime.setText("Stop");
	
		//on timechange events
		//Start time listener
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
			
			@Override
			public void onTimeChanged(TimePicker view, int hour, int minute){
				//translate time into a date object
				int hr = view.getCurrentHour(); 
				int min = view.getCurrentMinute();
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, hr);
				calendar.set(Calendar.MINUTE, min);
				Long t2 = calendar.getTimeInMillis();
				
				//save to shared prefs
				Activity myActivity = (Activity)(view.getContext());		
				SaveSchedulePrefs.saveSchedule(t2, 1, myActivity);
				//update args to preserve value on rotation
				getArguments().putSerializable(EXTRA_TIME, t2);
			}
		});
		//End time listener
		timePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
			
			@Override
			public void onTimeChanged(TimePicker view, int hour, int minute){
				//translate time into a date object
				int hr = view.getCurrentHour(); 
				int min = view.getCurrentMinute();
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, hr);
				calendar.set(Calendar.MINUTE, min);
				Long t2 = calendar.getTimeInMillis();
				
				//save to shared prefs
				Activity myActivity = (Activity)(view.getContext());		
				SaveSchedulePrefs.saveSchedule(t2, 2, myActivity);
				//update args to preserve value on rotation
				getArguments().putSerializable(EXTRA_TIME2, t2);
			}
			
		});
		
		return new AlertDialog.Builder(getActivity())
			.setView(v)
			.setTitle(R.string.set_schedule_title)
			.setPositiveButton(android.R.string.ok, null)
			.create();
	}
	
	@Override
	public void onStop(){
		//update service alarms
		super.onStop();
		ServiceController.setServiceAlarm(getActivity(), true);
		//turn off
		ServiceController.setServiceAlarm(getActivity(), false);
	}
	

}
