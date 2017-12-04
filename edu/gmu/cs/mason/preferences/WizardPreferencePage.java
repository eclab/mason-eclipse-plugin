package edu.gmu.cs.mason.preferences;

import edu.gmu.cs.mason.Activator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WizardPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {


	private StringFieldEditor packagePrefixEditor;
	private StringFieldEditor agentsPackageEditor;

	public WizardPreferencePage() {
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
		packagePrefixEditor = new StringFieldEditor(PreferenceConstants.PreferenceKey.PACKAGE_PREFIX, PreferenceConstants.Text.PACKAGE_PREFIX, getFieldEditorParent());
		agentsPackageEditor = new StringFieldEditor(PreferenceConstants.PreferenceKey.AGENT_PACKAGE, PreferenceConstants.Text.AGENT_PACKAGE, getFieldEditorParent());
		addField(packagePrefixEditor);
		addField(agentsPackageEditor);

	}
	
	
	
	private boolean checkPackageFields() {
		String string = this.packagePrefixEditor.getStringValue();
		IStatus status = JavaConventions.validatePackageName(string, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		// anything wrong, set the error message and return false
		if(status.getSeverity()==IStatus.ERROR){
			this.setErrorMessage(status.getMessage());
			return false;
		}
		else if(status.getSeverity()==IStatus.WARNING)
		{
			this.setErrorMessage(status.getMessage());
			return false;
		}
		
		string = this.agentsPackageEditor.getStringValue();
		
		//agent package is post fix, so it can be empty
		if(string.isEmpty())
		{
			this.setErrorMessage(null);
			return true;
		}
		status = JavaConventions.validatePackageName(string, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);

		// anything wrong, set the error message and return false
		if(status.getSeverity()==IStatus.ERROR){
			this.setErrorMessage(status.getMessage());
			return false;
		}
		else if(status.getSeverity()==IStatus.WARNING)
		{
			this.setErrorMessage(status.getMessage());
			return false;
		}
		
		// otherwise, the package name are legal, we return true
		this.setErrorMessage(null);
		return true;
	}
	
	// check if bunch of thing are validate
	@Override
	protected void checkState() {
		super.checkState();
		
		if(!checkPackageFields())
			setValid(false);
		else {
			setValid(true);
		}
	}
	
	
	

	public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (event.getProperty().equals(FieldEditor.VALUE)) {
        	checkState();
        }        
}

	
	public void init(IWorkbench workbench) {
	}

}