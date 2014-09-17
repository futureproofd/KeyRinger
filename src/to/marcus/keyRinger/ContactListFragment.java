package to.marcus.keyRinger;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListFragment extends ListFragment {
	private ArrayList<Contact> mContacts;
	private static final String TAG = "ContactListFragment";
	private static final String DIALOG_DATE = "Date";
	private static final int REQUEST_CONTACT = 1;
	private long mCurrentTime;
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		getActivity().setTitle(R.string.contacts_title);
		//get our arraylist of contacts
		mContacts = ContactStorage.get(getActivity()).getContacts();
		//ready our data-set for the listview
		ContactAdapter adapter = new ContactAdapter(mContacts);
		//keep current time up to date
		Calendar calendar = Calendar.getInstance();
		mCurrentTime = (long)calendar.getTimeInMillis();
		SaveSchedulePrefs.saveSchedule(mCurrentTime, 3, 1, getActivity());
		//Log.d(TAG, "intial current time: " + mCurrentTime );
		
		setListAdapter(adapter);		
	}

	//update list changes
	@Override 
	public void onResume(){
		super.onResume();
		((ContactAdapter)getListAdapter()).notifyDataSetChanged();
	}
	
	//Save contacts
	@Override
	public void onPause(){
		super.onPause();
		ContactStorage.get(getActivity()).saveContactsToFile();
	}
	
	//add an action bar
	@Override 
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.main, menu);
	}
	
	//add context menu (delete)
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		getActivity().getMenuInflater().inflate(R.menu.contact_list_contextmenu, menu);
	}
	
	//respond to contextMenu clicks
	@Override
	public boolean onContextItemSelected(MenuItem item){
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		int position = info.position;
		ContactAdapter adapter = (ContactAdapter)getListAdapter();
		Contact contact = adapter.getItem(position);
		
		switch (item.getItemId()){
		case R.id.menu_item_delete_contact:
			ContactStorage.get(getActivity()).deleteContact(contact);
			adapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	//select an action
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.menu_item_new_contact:
				Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(i, REQUEST_CONTACT);
				return true;
			case R.id.menu_item_set_schedule:
				//code for calendar dialog 
				FragmentManager fm = getActivity().getSupportFragmentManager();
				Long millidate = SaveSchedulePrefs.getSchedule(getActivity(), 1);
				Long millidate2 = SaveSchedulePrefs.getSchedule(getActivity(), 2);
				Long currentDate = SaveSchedulePrefs.getSchedule(getActivity(), 3);
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(millidate);
				//open dialog
				ScheduleFragment dialog = ScheduleFragment.newInstance(millidate, millidate2, currentDate);
				//ScheduleFragment dialog2 = ScheduleFragment.newInstance(millidate);
				dialog.show(fm, DIALOG_DATE);
			case android.R.id.home:
				if(NavUtils.getParentActivityName(getActivity()) != null){
					NavUtils.navigateUpFromSameTask(getActivity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	//Get Contact from Database on action selection
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		//avoid back button NPE
		if (resultCode != Activity.RESULT_CANCELED){
		 if (requestCode == REQUEST_CONTACT){
			Uri contactUri = data.getData();
			//return values for:
			String[] contactFields = new String[]{
					ContactsContract.Contacts.DISPLAY_NAME,
					ContactsContract.Contacts._ID
			};
			//query
			Cursor c = getActivity().getContentResolver()
					.query(contactUri, contactFields, null, null, null);
			if (c.getCount() == 0){
				c.close();
				return;
			}
			
			//get cursor rows
			c.moveToFirst();
			String contactName = c.getString(0);
			String contactId = c.getString(1);
			
			//phone query
			Cursor cp = getActivity().getContentResolver()
					.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null);
			while (cp.moveToNext()){
				try{ 
					String number = cp.getString(cp.getColumnIndex(Phone.NUMBER));
					int type = cp.getInt(cp.getColumnIndex(Phone.TYPE));
					//instantiate a new contact to add appropriate number below
					Contact mContact = new Contact();
					
					switch (type){
						case Phone.TYPE_HOME:
							ContactStorage.get(getActivity()).addContact(mContact);
							mContact.setName(contactName);
							mContact.setNumber(number);
							break;
						case Phone.TYPE_MOBILE:
							//mContact = new Contact();
							ContactStorage.get(getActivity()).addContact(mContact);
							mContact.setName(contactName);
							mContact.setNumber(number);
							break;
						case Phone.TYPE_WORK:
							//mContact = new Contact();
							ContactStorage.get(getActivity()).addContact(mContact);
							mContact.setName(contactName);
							mContact.setNumber(number);	
							break;
						case Phone.TYPE_MAIN:
							//mContact = new Contact();
							ContactStorage.get(getActivity()).addContact(mContact);
							mContact.setName(contactName);
							mContact.setNumber(number);	
							break;
						default:
							number = "no number";
							type = 0;
					}
				}catch(Exception e){
					Log.d(TAG, " exception: " + e );
				}
			}
			cp.close();
			c.close();
		 }//contact request end
		}//cancelled end
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id){
		Contact c = ((ContactAdapter)getListAdapter()).getItem(position);
		//Start MainActivity
		Intent i = new Intent(getActivity(), ContactActivity.class);
		i.putExtra(ContactFragment.EXTRA_CONTACT_ID, c.getId());
		startActivity(i);
	}
	
	
	//adapter subclass to work with contacts
	private class ContactAdapter extends ArrayAdapter<Contact>{
		//constructor calls superclass to hook up dataset of contacts
		public ContactAdapter(ArrayList<Contact> contacts){
			super(getActivity(), 0, contacts);
		}
		
		// inflate listview
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			//if no view
			if (convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_contact, null);
			}
			//configure view
			Contact c = getItem(position);
			TextView nameTextView = (TextView)convertView.findViewById(R.id.contact_list_name);
			nameTextView.setText(c.getName());
			CheckBox isEnabledCheckBox = (CheckBox)convertView.findViewById(R.id.is_enabled);
			isEnabledCheckBox.setChecked(c.isEnabled());
			isEnabledCheckBox.setTag(position);
			isEnabledCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				//list checkbox toggle
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (Integer)buttonView.getTag();
                	Contact c = getItem(position);
                    if (isChecked) {
                        c.setEnabled(true);
                    } else {
                        c.setEnabled(false);
                    }
                }
            });
			return convertView;	
		} 
	}
	
	//list view + context menu actions
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = super.onCreateView(inflater, parent, savedInstanceState);
		//inflate custom view for empty/list
		ListView listView = (ListView)v.findViewById(android.R.id.list);
		View emptyView = inflater.inflate(R.layout.empty_list, parent, false);
		((ViewGroup)listView.getParent()).addView(emptyView);
		listView.setEmptyView(emptyView);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
			//use floating context
			registerForContextMenu(listView);
		} else {
			//use contextual action bar
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener(){
				
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked){
					//not used
				}
				@SuppressLint("NewApi")
				//display contextual action bar
				public boolean onCreateActionMode(ActionMode mode, Menu menu){
					MenuInflater inflater = mode.getMenuInflater();
					inflater.inflate(R.menu.contact_list_contextmenu, menu);
					return true;
				}
				public boolean onPrepareActionMode(ActionMode mode, Menu menu){
					return false;
					//not used
				}
				//delete multiple selections
				public boolean onActionItemClicked(ActionMode mode, MenuItem item){
					switch (item.getItemId()){
					case R.id.menu_item_delete_contact:
						ContactAdapter adapter = (ContactAdapter)getListAdapter();
						ContactStorage contactStorage = ContactStorage.get(getActivity());
						for (int i = adapter.getCount() - 1; i >= 0; i--){
							if (getListView().isItemChecked(i)){
								contactStorage.deleteContact(adapter.getItem(i));
							}
						}
						mode.finish();
						adapter.notifyDataSetChanged();
						return true;
					default:
						return false;
					}
				}
				
				public void onDestroyActionMode(ActionMode mode){
					//not used
				}
			});
		}
		return v;
	}
}
