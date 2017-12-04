package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import java.util.ArrayList;

import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.preferences.PreferenceConstants;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.project.ui.MasonProjectWizard;
import edu.gmu.cs.mason.wizards.project.ui.field.FieldInfoDialog;
import edu.gmu.cs.mason.wizards.project.ui.field.FieldInfoViewer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class SimulationClassPage extends MasonWizardPage {

	private static final String SIMSTATE_CLASS = "SimState Class";
	private static final String PACKAGE = "Package";
	// Here is some fixed String that will be used to display in the dialog
	private static final String TITLE = "New MASON Class";
	private static final String DESCRIPTION = "Define Simluation Fields";
	

	private static final String REMOVE_BUTTON = "Remove";
	private static final String EDIT_BUTTON = "Edit";
	private static final String ADD_BUTTON = "Add";
	
	// Our project model
	private ProjectInformation projectInfo;
	
	// Our local variable
	private boolean firstVisit = true;
	private FieldInformation selectedFieldInfo;
	private String projectName = "Default";
	public ArrayList<FieldInformation> fieldInfoList;
	private ModifyListener modifyListener;
	
	//widgets
	private Button addButton;
	private Button removeButton;
	private Button editButton;
	private FieldInfoViewer fieldViewer;
	private Text classNameText;
	private Text packageText;
	private FieldInfoDialog dialog;
	
	
	public SimulationClassPage(String pageName, ProjectInformation projectInfo) {
		super(pageName);

		this.setTitle(TITLE);
		this.setDescription(DESCRIPTION);
		
		// Initialize fields
		this.projectInfo = projectInfo;
		this.dialog = new FieldInfoDialog(getShell());
		fieldInfoList = new ArrayList<FieldInformation>();
		
		this.modifyListener = new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				boolean validate = validatePage();
				setPageComplete(validate);
			}
		};
	}

	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
        initializeDialogUnits(parent);
		
        
        // Set layout and font
        container.setLayout(new GridLayout(1,false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setFont(parent.getFont());

        // Create control for group
  	    createPackageAndNameGroup(container);
	    createFieldViewGroup(container);

	    // Required to avoid an error in the system
	    setControl(container);
	    setPageComplete(false);
	    
	    
		// we have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonProjectWizard.PAGE_WIDTH, MasonProjectWizard.PAGE_HEIGHT);
		getShell().setSize( size );
	}

	
	
	private void createFieldViewGroup(Composite container) {
		
		// Configure the group layout
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2,false));
		group.setText("Field type");
		group.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		group.setFont(container.getFont());
		
		fieldViewer = new FieldInfoViewer(group, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.BORDER);
		
		// Configure the fieldViewer with given data and layout
		// Register it with the listener
		fieldViewer.setInput(fieldInfoList);
		fieldViewer.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true));
		fieldViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				selectdItemChanged(event);
			}

		});
		
		
		// Create the buttons on the side
		this.getButtonBox(group);
	}
	

	// Set a new field information and enable edit and remove button
	private void selectdItemChanged(SelectionChangedEvent event) {
		if(!event.getSelection().isEmpty())
		{
			IStructuredSelection selection = (IStructuredSelection)fieldViewer.getSelection();
			this.selectedFieldInfo = (FieldInformation)selection.getFirstElement();
			this.editButton.setEnabled(true);
			this.removeButton.setEnabled(true);
		}
		else {
			this.selectedFieldInfo = null;
			this.editButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
		
	}
	
	
	private void getButtonBox(Group group) {
		
		Composite buttonBox = new Composite(group, SWT.NONE);
		buttonBox.setLayout(new GridLayout(1,false));
		buttonBox.setFont(group.getFont());
		
		// set the layout of the add, edit and remove button
		buttonBox.setLayoutData(new GridData(GridData.FILL,GridData.BEGINNING,false,false));
		
		
		addButton = new Button(buttonBox, SWT.PUSH);
		addButton.setText(ADD_BUTTON);
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,true));
		addButton.setEnabled(true);
		addButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				addButtonPressed(e);
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				addButtonPressed(e);
			}
		});
		
		
		
		editButton = new Button(buttonBox, SWT.PUSH);
		editButton.setText(EDIT_BUTTON);
		editButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,true));
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				editButtonPressed(e);
			}
			

			public void widgetDefaultSelected(SelectionEvent e) {
				editButtonPressed(e);
			}
		});
		
		
		removeButton = new Button(buttonBox, SWT.PUSH);
		removeButton.setText(REMOVE_BUTTON);
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,true));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				removeButtonPressed(e);
			}

			
			public void widgetDefaultSelected(SelectionEvent e) {
				removeButtonPressed(e);
			}
		});
			
	}
	
	
	// If remove button was pressed, we first delete the field from our list.
	// Then we refresh our tableviewer to reflect the change.
	// We check the validation at last
	private void removeButtonPressed(SelectionEvent e) {
		fieldInfoList.remove(this.selectedFieldInfo);
		fieldViewer.refresh();
		setPageComplete(validatePage());
	}
	
	

	// Before the edit button was pressed, an entry must be select to determine which field to change
	// Then we load the data of that field into dialog for change.
	// We check the validation at last
	private void editButtonPressed(SelectionEvent e) {
		dialog.fieldInfo = this.selectedFieldInfo;
		int code = dialog.openToEdit();
		if(code==Window.OK)
		{
			fieldViewer.refresh();
		}
		setPageComplete(validatePage());
	}
	
	// Open the field dialog and show the default value of each control
	// We check the validation after the input of the information about field
	private void addButtonPressed(SelectionEvent e) {
		int code = dialog.openToAdd();
		if(code==Window.OK)
		{
			fieldInfoList.add(this.dialog.fieldInfo);
			fieldViewer.refresh();
		}
		setPageComplete(validatePage());
	}
	

	
	

	private void createPackageAndNameGroup(Composite container) {
	
		packageText = addStringFieldEditor(container,PACKAGE);
		packageText.addModifyListener(this.modifyListener);
		
		classNameText = addStringFieldEditor(container, SIMSTATE_CLASS);
		classNameText.addModifyListener(this.modifyListener);
	
	}

	
	
	protected boolean validatePage() {
		
		if(!this.checkSimClassName())
			return false;
		if(!this.checkPackageName())
			return false;
		if(!this.checkField())
			return false;
		
		saveDataToModel();
		return true;
	}
	


	
	// Check if two field share the same instance name
	private boolean checkField() {
		//may come up with some faster compare way
		for(int i = 0;i<fieldInfoList.size();++i)
		{
			for(int j = 0;j<fieldInfoList.size();++j)
			{
				if(i!=j)
				{
					String firstName = ((FieldInformation)fieldInfoList.get(i)).getFieldName();
					String secondName = ((FieldInformation)fieldInfoList.get(j)).getFieldName();
					if(firstName.equals(secondName))
					{
						setErrorMessage(MasonWizardConstants.Message.FIELD_NAME_ERROR);
						return false;
					}
				}
			}
		}
		setErrorMessage(null);
		return true;
	}

	private boolean checkPackageName() {
		String nameString = this.packageText.getText();
		IStatus status = JavaConventions.validatePackageName(nameString, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		if(status.isOK())
		{
			this.setErrorMessage(null);
			return true;
		}
		else if(status.getSeverity()==IStatus.ERROR){
			this.setErrorMessage(status.getMessage());
		}
		else if(status.getSeverity()==IStatus.WARNING)
		{
			this.setErrorMessage(status.getMessage());
		}
		return false;
	}

	private boolean checkSimClassName() {
		String nameString = this.classNameText.getText();
		IStatus status = JavaConventions.validateJavaTypeName(nameString, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		if(status.isOK())
		{
			this.setErrorMessage(null);
			return true;
		}
		else if(status.getSeverity()==IStatus.ERROR){
			this.setErrorMessage(status.getMessage());
		}
		else if(status.getSeverity()==IStatus.WARNING)
		{
			this.setErrorMessage(status.getMessage());
		}
		return false;
	}
	
	
	
	
	@Override
	public void setVisible(boolean visible) {
		if(firstVisit)
		{
			this.projectName = ((ProjectInfoPage)this.getPreviousPage()).getProjectName();
			// Make the first letter uppercase
			char[] characters = projectName.toCharArray();
			characters[0] = Character.toUpperCase(characters[0]);
			this.classNameText.setText(new String(characters));

			// the package name should be lower case
			IPreferenceStore preference = Activator.getDefault().getPreferenceStore();
			String packagePrefix = preference.getString(PreferenceConstants.PreferenceKey.PACKAGE_PREFIX);
			String defaultPackageName = projectName.toLowerCase();
			this.packageText.setText(packagePrefix+"."+defaultPackageName);
			
			firstVisit = false;
		}
		else
		{
			this.classNameText.setText(this.projectInfo.simStateClassName);
			this.packageText.setText(this.projectInfo.packageName);
			
		}
		
		
		this.classNameText.setFocus();
		super.setVisible(visible);	
	}
	
	

	// All the field is validate, so we just store it.
	protected void saveDataToModel()
	{
		this.projectInfo.packageName = this.packageText.getText();
		this.projectInfo.simStateClassName = this.classNameText.getText();
		this.projectInfo.fieldInfoList = this.fieldInfoList;
	}
	
	
	
	
}
