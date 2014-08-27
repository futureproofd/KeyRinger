package to.marcus.keyRinger;

import android.support.v4.app.Fragment;

public class ContactActivity extends AbstractFragmentActivity {
	
	@Override
	protected Fragment createFragment() {
		// pass this value into our abstract implementation while onCreate
		return new ContactFragment();
	}
}
