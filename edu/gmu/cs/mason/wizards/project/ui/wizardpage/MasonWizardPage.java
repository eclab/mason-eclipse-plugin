package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;



/**
 * This is the parent class of all the project wizard page
 * New subclass could sub this class 
 * @author nosyndicate
 *
 */


public abstract class MasonWizardPage extends WizardPage {

	private static final int HORIZONTAL_SPACING = 4;
	private static final int VERTICAL_SPACING = 4;
	private static final int HORIZONTAL_MARGIN = 7;
	private static final int VERTICAL_MARGIN = 7;

	protected MasonWizardPage(String pageName) {
		super(pageName);
	}

	
	// we have to save the data to model if the validatePage method returns true
	protected abstract void saveDataToModel();
	protected abstract boolean validatePage();
	
	
	
	//every time we set the page visible, we validate the fields
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible)
		{
			setPageComplete(validatePage());
		}
	}
	
	
	protected GridLayout configureLayout(GridLayout layout) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(VERTICAL_MARGIN);

		return layout;
	}

	protected Text addStringFieldEditor(Composite container, String label) {
		Composite group = new Composite(container, SWT.NONE);
		
		// it turns out this is the only method I can make it align
		// use the 5 columns with equal width
		group.setLayout(new GridLayout(5, true));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		group.setFont(container.getFont());


		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(label);
		nameLabel.setFont(group.getFont());

		Text text = new Text(group, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 4;
		text.setLayoutData(gridData);
		text.setFont(group.getFont());

		return text;

	}

}
