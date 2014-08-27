package to.marcus.keyRinger;

import java.util.ArrayList;
import java.util.UUID;
import android.content.Context;

public class ContactStorage {
	
	private ArrayList<Contact> mContacts;
	private ContactJSONer mSerializer;
	private static ContactStorage sContactStorage;
	private Context mAppContext;
	private static final String FILENAME = "contacts.json";
	
	//constructor with context to access project resources and instantiate from JSONfile to arrayList 
	private ContactStorage(Context appContext){
		mAppContext = appContext;
		mSerializer = new ContactJSONer(mAppContext, FILENAME);
		try{
			mContacts = mSerializer.loadContacts();
		}catch (Exception e){
			mContacts = new ArrayList<Contact>();
		}
	}
	
	//get method to only return one instance from the constructor
	public static ContactStorage get(Context c){
		if (sContactStorage == null){
			sContactStorage = new ContactStorage(c.getApplicationContext());
		}
		return sContactStorage;
	}
	
	//work with our arraylist of contacts
	public ArrayList<Contact> getContacts(){
		return mContacts;
	}
	
	public Contact getContact(UUID id){
		for (Contact c: mContacts){
			if (c.getId().equals(id))
				return c;
		}
		return null;
	}
	
	//for ringer service to find matching number
	public Contact getContactNumber(String number){
		for (Contact c: mContacts){
			if (c.getNumber().replaceAll("[^0-9]", "").equals(number))
				return c;
			}
			return null;
		}

	//for our actionbar
	public void addContact(Contact c){
		mContacts.add(c);
	}
	
	//delete from contextMenu
	public void deleteContact(Contact c){
		mContacts.remove(c);
	}
	
	//note: remove log tags and add a toast for finished app
	public boolean saveContactsToFile(){
		try{
			mSerializer.saveContacts(mContacts);
			return true;
		}catch (Exception e){
			return false;
		}
	}
}
