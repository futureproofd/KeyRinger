package to.marcus.keyRinger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import to.marcus.keyRinger.R;


public abstract class AbstractFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.fragment_main);
			
			//Load our fragment
			FragmentManager fm = getSupportFragmentManager();
			Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
			
			if (fragment == null){
				fragment = createFragment();
				fm.beginTransaction()
					.add(R.id.fragmentContainer, fragment)
					.commit();
			}
			
		} //close

		//ignore for now
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}


}
