package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import java.util.ArrayList;
import java.util.HashMap;

import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.project.ui.MasonProjectWizard;
import edu.gmu.cs.mason.wizards.project.ui.agent.AgentInfoDialog;
import edu.gmu.cs.mason.wizards.project.ui.agent.AgentInfoViewer;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;


public class AgentsPage extends MasonWizardPage{
	
	private static final String TITLE = "New MASON Agents";
	private static final String DESCRIPTION = "Define Agents";
	
	
	
	private ProjectInformation projectInfo;
	private AgentInformation selectedAgentInfo;
	public ArrayList<AgentInformation> agentInfoList;
	
	
	
	//widget
	private AgentInfoDialog dialog;
	private Button addButton;
	private Button removeButton;
	private Button editButton;
	private AgentInfoViewer agentViewer;
	
	
	
	
	
	public AgentsPage(String pageName, ProjectInformation projectInfo)
	{
		super(pageName);

		this.setTitle(TITLE);
		this.setDescription(DESCRIPTION);
		this.projectInfo = projectInfo;
		this.dialog = new AgentInfoDialog(getShell(), this.projectInfo);
		agentInfoList = new ArrayList<AgentInformation>();
		
		
	}
	
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
        initializeDialogUnits(parent);
		
        container.setLayout(new GridLayout(1,false));
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		container.setFont(parent.getFont());

	    createAgentViewGroup(container);
	    
	    // Required to avoid an error in the system
	    setControl(container);

	    setPageComplete(false);
	    
	    
		// we have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonProjectWizard.PAGE_WIDTH, MasonProjectWizard.PAGE_HEIGHT);
		getShell().setSize( size );
	}

	private void createAgentViewGroup(Composite container) {
		
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2,false));
		group.setText("Agents");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setFont(container.getFont());
		
		agentViewer = new AgentInfoViewer(group, SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL|SWT.FULL_SELECTION|SWT.BORDER);
		
	
		agentViewer.setInput(agentInfoList);
		agentViewer.setLayoutData(new GridData(GridData.FILL,GridData.FILL,true,true));
		agentViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				selectdItemChanged(event);
			}

		});
		
		this.getButtonBox(group);
		
	}
	
	
	
	private void getButtonBox(Group group) {
		
		Composite buttonBox = new Composite(group, SWT.NONE);
		buttonBox.setLayout(new GridLayout(1,false));
		buttonBox.setFont(group.getFont());
		
		
		buttonBox.setLayoutData(new GridData(GridData.FILL,GridData.BEGINNING,false,false));
		
		
		addButton = new Button(buttonBox, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,false));
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
		editButton.setText("Edit");
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
		removeButton.setText("Remove");
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

	private void selectdItemChanged(SelectionChangedEvent event) {
		if(!event.getSelection().isEmpty())
		{
			//System.out.println("selection changed");
			IStructuredSelection selection = (IStructuredSelection)agentViewer.getSelection();
			this.selectedAgentInfo = (AgentInformation)selection.getFirstElement();
			this.editButton.setEnabled(true);
			this.removeButton.setEnabled(true);
		}
		else {
			//System.out.println("empty selection");
			this.selectedAgentInfo = null;
			this.editButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
	}
	
	private void addButtonPressed(SelectionEvent e) {
		int code = dialog.openToAdd();
		if(code==Window.OK)
		{
			AgentInformation agentInfo = this.dialog.agentInfo;
			agentInfoList.add(agentInfo);
			agentViewer.refresh();
		}
		setPageComplete(validatePage());
	}
	
	private void editButtonPressed(SelectionEvent e) {
		dialog.agentInfo = this.selectedAgentInfo;
		int code = dialog.openToEdit();
		if(code==Window.OK)
		{
			agentViewer.refresh();
		}
		setPageComplete(validatePage());
	}
	

	private void removeButtonPressed(SelectionEvent e) {
		
		agentInfoList.remove(this.selectedAgentInfo);
		agentViewer.refresh();
		setPageComplete(validatePage());
	}
	
	
	protected boolean validatePage() {
		if(!this.checkAgents())
			return false;
		
		saveDataToModel();
		return true;
	}

	
	private boolean checkAgents() {

		HashMap<String, Integer> agentNameMap = new HashMap<String, Integer>();
		
		// go through all the agents check if they are valid
		for(AgentInformation a:this.agentInfoList)
		{
			// Agent cannot have the same name with simstate class
			if(projectInfo.simStateClassName.equals(a.getAgentName()))
			{
				setErrorMessage(MasonWizardConstants.Message.AGENT_NAME_ERROR);
				return false;
			}
			
			// if other agents have the same name, that's illegal
			if(agentNameMap.containsKey(a.getAgentName()))
			{
				setErrorMessage(MasonWizardConstants.Message.AGENT_NAME_ERROR);
				return false;
			}
			else {
				agentNameMap.put(a.getAgentName(), 1);
			}
			
		}
		
		this.setErrorMessage(null);
		return true;
	}
	

	
	//if we delete a field in the previous page, we should update the result here
	@Override
	public void setVisible(boolean visible) {
		ArrayList<FieldInformation> fieldList = projectInfo.fieldInfoList;
		ArrayList<AgentInformation> agentList = this.agentInfoList;
		for(int i = 0;i<agentList.size();++i)
		{
			AgentInformation agentInfo = agentList.get(i);
			boolean inList = false;
			for(int j = 0;j<fieldList.size();++j)
			{
				if(agentInfo.getField()!=null&&agentInfo.getField().equals(fieldList.get(j)))
				{
					inList = true;
					break;
				}
			}
			if(!inList)
			{
				agentInfo.setField(null);
			}
		}
		agentViewer.refresh();		
		super.setVisible(visible);
	};
	
	
	protected void saveDataToModel()
	{
		this.projectInfo.agentInfoList = this.agentInfoList;
	}


	
	
}
