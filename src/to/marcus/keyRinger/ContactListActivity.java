package to.marcus.keyRinger;

import android.support.v4.app.Fragment;

public class ContactListActivity extends AbstractFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new ContactListFragment();
	}

}
