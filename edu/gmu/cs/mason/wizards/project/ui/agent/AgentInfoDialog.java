package edu.gmu.cs.mason.wizards.project.ui.agent;


import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.preferences.PreferenceConstants;
import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AgentInfoDialog extends TitleAreaDialog {

	private static final String ADDTITLE = "Add New Agent";
	private static final String ADDMESSAGE = "Enter Information for New Agent";
	private static final String EDITTITLE = "Edit a Agent";
	private static final String EDITMESSAGE = "Edit Information of Agent";
	private static final String DEFAULT_NAME = "Agent";
	
	
	//widget
	private Text agentPackageText;
	private Text nameText;
	
	private ModifyListener textModifyListener;
	
	
	// Fields value
	public AgentInformation agentInfo;
	public ProjectInformation projectInfo;
	
	
	public AgentInfoDialog(Shell parentShell, ProjectInformation projectInfo) {
		super(parentShell);
		this.projectInfo = projectInfo;
		
		
		
		textModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setDialogComplete(validateData());
			}
		};
	}

	
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = (Composite) super.createDialogArea(parent);
		
        initializeDialogUnits(parent);
		
        container.setLayout(new GridLayout(1,false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		container.setFont(parent.getFont());


		this.createAgentPackAgentAndNameGroup(container);
		
		
		return container;
	}
	






	private void createAgentPackAgentAndNameGroup(Composite container) {
		Composite group = new Composite(container, SWT.NONE);
		
        group.setLayout(new GridLayout(1,true));
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setFont(container.getFont());
		

		// get the prefix of the agent package
		IPreferenceStore preference = Activator.getDefault().getPreferenceStore();
		String agentPostFix = preference.getString(PreferenceConstants.PreferenceKey.AGENT_PACKAGE);
		String packageString = projectInfo.packageName;
		if(!agentPostFix.isEmpty())
			packageString += ("."+agentPostFix);
		agentPackageText = addLabelAndText(group, "Agent Package");		
		agentPackageText.setText(packageString);
		agentPackageText.addModifyListener(textModifyListener);
		
		nameText = addLabelAndText(group, "Agent Class");
		nameText.setText(DEFAULT_NAME);
		nameText.addModifyListener(textModifyListener);
				
	}


	public void clearFieldInfo()
	{
		agentInfo = new AgentInformation();
	}
	
	
	@Override
	protected void okPressed()
	{
		this.saveDataToModel();
		super.okPressed();
	}
	
	
	
	private void saveDataToModel() {
		this.agentInfo.setAgentName(this.nameText.getText());
		this.agentInfo.setPackageName(this.agentPackageText.getText());
	}

	public void storeModelToUi() {
		nameText.setText(agentInfo.getAgentName());
		agentPackageText.setText(agentInfo.getPackageName());
	}
	

	private void setDialogComplete(boolean finished)
	{
		this.getButton(IDialogConstants.OK_ID).setEnabled(finished);
	}
	
	
	public int openToAdd() {
		this.create();
		this.setTitle(ADDTITLE);
		this.setMessage(ADDMESSAGE);
		this.clearFieldInfo();
		
		return this.open();
	}




	public int openToEdit() {
		this.create();
		this.setTitle(EDITTITLE);
		this.setMessage(EDITMESSAGE);
		this.storeModelToUi();

		return this.open();
	}


	private boolean validateData() {
		if(!this.checkAgentName())
			return false;
		if(!this.checkPackageName())
			return false;
		return true;
	}
	

	private boolean checkAgentName() {
		String nameString = this.nameText.getText();
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


	private boolean checkPackageName() {
		String nameString = this.agentPackageText.getText();
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

	
	private Text addLabelAndText(Composite container, String label) {
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
