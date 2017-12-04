package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import java.io.File;
import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.preferences.PreferenceConstants;
import edu.gmu.cs.mason.util.MasonDirectoryVerifier;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.project.ui.MasonProjectWizard;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.*;


public class ProjectInfoPage extends WizardNewProjectCreationPage {

	
	// Here is some fixed String that will be used to display in the dialog
	private static final String TITLE = "New MASON Project";
	private static final String DESCRIPTION = "Create a MASON Project";
	private static final String ONE_FOLDER = "Use project folder as root for sources and class files";
	private static final String TWO_FOLDER = "Create separate folders for sources and class files";
	private static final String PROJECT_LAYOUT = "Project Layout";
	private static final String MASON_DIRECTORY = "Mason Directory:";
	private static final String BROWSE_LABEL = "Browse";

	
	// Those value are used to configure the spacing of the dialog layout
	private static final int HORIZONTAL_SPACING = 4;
	private static final int VERTICAL_SPACING = 4;
	private static final int HORIZONTAL_MARGIN = 7;
	private static final int VERTICAL_MARGIN = 7;

	
		
	// Our project model
	private ProjectInformation projectInfo;
	
	// Do we want a single folder or not
	public boolean singleFolder;


	// widgets
	private Button oneFolderButton; 
	private Button twoFolderButton;
	private Text directoryText;
	private Button browseButton;
	
	private DirectoryDialog dialog;
	private ModifyListener modifyListener;

		
	public ProjectInfoPage(String pageName, ProjectInformation projectInfo) {
		super(pageName);
		this.setTitle(TITLE);
		this.setDescription(DESCRIPTION);
		
		
		// Initialize our model
		singleFolder = true;
		this.projectInfo = projectInfo;
		
		
		// we check any modification
		modifyListener = new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}
		};
		
	}

	
	// add control to the panel
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		Composite container = (Composite) this.getControl();

		// Create our control
		createMasonPathGroup(container);
		createLayoutGroup(container);
		
		
		// we have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonProjectWizard.PAGE_WIDTH, MasonProjectWizard.PAGE_HEIGHT);
		getShell().setSize( size );
	}

	
	/**
	 * This function create the mason path control,
	 * and grab the mason directory path from the preference and fill out the blank with it
	 * @param container The container of the control
	 */
	private void createMasonPathGroup(Composite container) {
		
		Group group = new Group(container, SWT.NONE);
		
		// Configure our group
		group.setFont(container.getFont());
		group.setText(MASON_DIRECTORY);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout layout = new GridLayout(3,false);
		layout = this.configureLayout(layout);
		group.setLayout(layout);
		
		// location label
		Label label = new Label(group, SWT.NONE);
		label.setText(MASON_DIRECTORY);

		// project location entry field
		directoryText = new Text(group, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		directoryText.setLayoutData(data);
		
		IPreferenceStore preference = Activator.getDefault().getPreferenceStore();
		String preferencePath= preference.getString(PreferenceConstants.PreferenceKey.DIRECTORY);
		directoryText.setText(preferencePath);
		directoryText.addModifyListener(modifyListener);
		
		
		
		// browse button
		browseButton = new Button(group, SWT.PUSH);
		browseButton.setText(BROWSE_LABEL);
		browseButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				browseButtonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				browseButtonPressed();
			}
		});
		
	}

	
	private void browseButtonPressed() {
		if(dialog==null)
			dialog = new DirectoryDialog(getShell(), SWT.SHEET);
		if(directoryText.getText()!=null&&new File(directoryText.getText()).isDirectory())
		{
			dialog.setFilterPath(directoryText.getText());
		}
		else
			dialog.setFilterPath(null);

		String selectedDirectory = dialog.open();
		if(selectedDirectory!=null)
			this.directoryText.setText(selectedDirectory);
		else {
			this.directoryText.setText("");
		}
	}
	
	
	
	private void createLayoutGroup(Composite container) {
		Group group = new Group(container, SWT.NONE);
		
		// Configure our group
		group.setFont(container.getFont());
		group.setText(PROJECT_LAYOUT);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout layout = new GridLayout(1,false);
		layout = this.configureLayout(layout);
		group.setLayout(layout);
		
		
		// Configure our buttons
        oneFolderButton = new Button(group,SWT.RADIO);
        oneFolderButton.setText(ONE_FOLDER);
        oneFolderButton.setSelection(true);
        
        twoFolderButton = new Button(group, SWT.RADIO);
        twoFolderButton.setText(TWO_FOLDER);
        twoFolderButton.setSelection(false);
        
        
        // Only need register one listener,
        // Radio button will also issue this event when lose focus
        oneFolderButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				radioButtonSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				radioButtonSelected(e);
			}
		});
        
	}

	
	

	private void radioButtonSelected(SelectionEvent e) {
		if(this.oneFolderButton!=null)
			this.singleFolder = oneFolderButton.getSelection();
	}


	
	@Override
	public void setVisible(boolean visible) {

		super.setVisible(visible);
		if(visible)
		{
			validatePage();
		}
	}
	
	
	private void saveDataToModel()
	{
		this.projectInfo.projectLocation = null;
		if(!this.useDefaults())	
			this.projectInfo.projectLocation = this.getLocationURI();
		this.projectInfo.projectName = this.getProjectName();
		this.projectInfo.singleFolder = this.singleFolder;
		this.projectInfo.masonPath = directoryText.getText();
	}
	
	@Override
	protected boolean validatePage() {
		if(super.validatePage())
		{
			//check if the mason directory is what we want
			MasonDirectoryVerifier verifier = MasonDirectoryVerifier.getInstance();
			if(!verifier.isMasonDirectory(directoryText.getText()))
			{
				setErrorMessage(MasonWizardConstants.Message.MASON_DIRECTORY_ERROR);
				return false;
			}
			else
			{
				setErrorMessage(null);
				saveDataToModel();
				return true;
			}
		}
		return false;
	}
	
	
	private GridLayout configureLayout(GridLayout layout) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(VERTICAL_MARGIN);

		return layout;
	}
	
	
}
