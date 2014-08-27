package to.marcus.keyRinger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;

public class ContactJSONer {
	private Context mContext;
	private String mFilename;
	
	public ContactJSONer(Context c, String f){
		mContext = c;
		mFilename = f;
	}
	
	//save to JSON
	public void saveContacts(ArrayList<Contact> contacts) throws JSONException, IOException{
		JSONArray array = new JSONArray();
		for (Contact c : contacts)
			array.put(c.toJSON());	//define this in contact class
		
		//write to disk
		Writer writer = null;
		try{
			OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
			writer = new OutputStreamWriter(out);
			writer.write(array.toString());
		} finally {
			if (writer != null)
				writer.close();
		}
	}
	
	//load JSON file back into an arrayList of contact objects
	public ArrayList<Contact> loadContacts() throws IOException, JSONException{
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		BufferedReader reader = null;
		try{
			//open and read the file into a stringBuilder
			InputStream in = mContext.openFileInput(mFilename);
			reader = new BufferedReader(new InputStreamReader(in));
			
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null){
				jsonString.append(line);
			}
			
			//parse JSON using JSONTokener
			JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			
			//Build the array of contacts from JSONObjects (call the custom constructor)
			for (int i = 0; i < array.length(); i++){
				contacts.add(new Contact(array.getJSONObject(i)));
			}
		} catch (FileNotFoundException e){
			
		} finally{
			if (reader != null)
				reader.close();
		}
		return contacts;
	}

}
