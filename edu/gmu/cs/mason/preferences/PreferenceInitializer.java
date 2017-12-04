package edu.gmu.cs.mason.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import edu.gmu.cs.mason.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		// These settings will show up when the  preference page
		// is shown for the first time.
		store.setDefault(PreferenceConstants.PreferenceKey.DIRECTORY,
				PreferenceConstants.PreferenceValue.DEFAULT_DIR);
		store.setDefault(PreferenceConstants.PreferenceKey.PACKAGE_PREFIX,
				PreferenceConstants.PreferenceValue.DEFAULT_PACKAGE_PREFIX);
		store.setDefault(PreferenceConstants.PreferenceKey.AGENT_PACKAGE,
				PreferenceConstants.PreferenceValue.DEFAULT_AGENT_PACKAGE);

	}	
	
	
}