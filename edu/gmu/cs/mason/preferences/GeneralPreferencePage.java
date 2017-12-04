package edu.gmu.cs.mason.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.util.MasonDirectoryVerifier;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class GeneralPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
		
	private StringFieldEditor directoryFieldEditor;

	public GeneralPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(PreferenceConstants.Text.PREFERENCE_DESCRIPTION);
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		directoryFieldEditor = new DirectoryFieldEditor(PreferenceConstants.PreferenceKey.DIRECTORY, 
				PreferenceConstants.Text.MASON_DIRECTORY, getFieldEditorParent());
		addField(directoryFieldEditor);

	}
	
	// check in the current directory, if we have the desire class file
	@Override
	protected void checkState() {
		super.checkState();
		
		String dir = directoryFieldEditor.getStringValue();
		MasonDirectoryVerifier verifier = MasonDirectoryVerifier.getInstance();
		if(dir!=null&&verifier.isMasonDirectory(dir))
		{
			setErrorMessage(null);
			setValid(true);
		}else {
            setErrorMessage(PreferenceConstants.Message.DIRECTORY_ERROR_MESSAGE);
            setValid(false);
		}
	}
	
	
	public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (event.getProperty().equals(FieldEditor.VALUE)) {
        	checkState();
        }        
}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}