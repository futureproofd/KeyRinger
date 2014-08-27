package to.marcus.keyRinger;

import java.util.UUID;
import to.marcus.keyRinger.R;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;


public class ContactFragment extends Fragment {
	public static final String EXTRA_CONTACT_ID = "com.marcus.vipringer.contact_id";
	public static final String TAG = "ContactFragment";
	
	private Contact mContact;
	private TextView mContactTitle;
	private TextView mContactNumber;
	private CheckBox mContactEnabled;
	//private String number;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//direct intent retrieval - consider fragment arguments?
		UUID contactId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_CONTACT_ID);
		mContact = ContactStorage.get(getActivity()).getContact(contactId);
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		if (menu != null){
			menu.findItem(R.id.menu_item_new_contact).setVisible(false);
			menu.findItem(R.id.menu_item_set_schedule).setVisible(false);
		}
		inflater.inflate(R.menu.contact_fragment_menu, menu);
	}
	
	//delete a contact
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.contact_delete:
				ContactStorage.get(getActivity()).deleteContact(mContact);
				getActivity().finish();
				return true;
			//case android.R.id.home:
			default:
				return false;
		}
	}
	@TargetApi(11)
	@Override 
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_contacts, parent, false);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			if (NavUtils.getParentActivityName(getActivity()) != null){
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
			}
		}
		
		mContactTitle = (TextView)v.findViewById(R.id.contact_name);
		mContactTitle.setText(mContact.getName());
		
		mContactNumber = (TextView)v.findViewById(R.id.phone_number);
		mContactNumber.setText(mContact.getNumber());
		
		mContactEnabled = (CheckBox)v.findViewById(R.id.is_enabled);
		mContactEnabled.setChecked(mContact.isEnabled());
		
		//set checkbox listener
		mContactEnabled.setOnCheckedChangeListener(new OnCheckedChangeListener() { 
			
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
					mContact.setEnabled(isChecked);
				}
			});

		return v;
	}

}
