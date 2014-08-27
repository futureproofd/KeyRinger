package to.marcus.keyRinger;

import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

public class Contact {
	
	private UUID mId;
	private String mName;
	private String mNumber;
	private boolean mIsEnabled;
	
	//for json wrting
	private static final String JSON_ID = "id";
	private static final String JSON_NAME = "name";
	private static final String JSON_NUM = "number";
	private static final String JSON_ENABLED = "enabled";
	
	public Contact(){
		//get a new UUID
		mId = UUID.randomUUID();
		mIsEnabled = true;
	}
	
	public Contact(JSONObject json) throws JSONException{
		mId = UUID.fromString(json.getString(JSON_ID));
		if (json.has(JSON_NAME)){
			mName = json.getString(JSON_NAME);
		}
		mIsEnabled = json.getBoolean(JSON_ENABLED);
		mNumber = json.getString(JSON_NUM);
	}
	
	//conversion - see ContactJSONer for writing to file
	public JSONObject toJSON() throws JSONException{
		JSONObject json = new JSONObject();
		json.put(JSON_ID, mId.toString());
		json.put(JSON_NAME, mName);
		json.put(JSON_NUM, mNumber);
		json.put(JSON_ENABLED, mIsEnabled);
		return json;
	}
	
	//override object class array adapter default conversion
	@Override
	public String toString(){
		return mName;
	}

	public UUID getId(){
		return mId;
	}
	
	public String getName(){
		return mName;
	}
	
	public String getNumber(){
		return mNumber;
	}
	
	public void setName(String name){
		mName = name;
	}
	
	public void setNumber(String number){
		mNumber = number;
	}
	
	public boolean isEnabled(){
		return mIsEnabled;
	}
	
	public void setEnabled(boolean enabled){
		mIsEnabled = enabled;
	}
}
