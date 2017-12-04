package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.project.ui.MasonProjectWizard;
import edu.gmu.cs.mason.wizards.project.ui.agent.AgentPortrayalViewer;
import edu.gmu.cs.mason.wizards.project.ui.field.FieldPortrayalViewer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
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

public class GUIClassPage extends MasonWizardPage {

	private static final String GUI_BUTTON = "This is a simulation with GUI";
	private static final String GUI_CLASS = "GUI Class";

	private static final String FIELDS_PORTRAY = "Fields portrayal";
	private static final String AGENTS_PORTRAY = "Agents portrayal";
	private static final String DISPLAY_SETTING = "Display setting";
	private static final String TWODBUTTON = "Display the simulation in 2D";
	private static final String THREEDBUTTON = "Display the simulation in 3D";

	private static final String WIDTH = "Width";
	private static final String DEFAULT_WIDTH = "600";
	private static final String HEIGHT = "Height";
	private static final String DEFAULT_HEIGHT = "600";
	private static final String TITLE = "New MASON Visualization";
	private static final String DESCRIPTION = "Define GUI";

	private ProjectInformation projectInfo;

	public boolean withGUI;
	private boolean firstVisit = true;

	// widget
	private Button GUIButton;
	private Button _2dButton;
	private Button _3dButton;
	private Text widthText;
	private Text heightText;
	private Text GUIText;
	private FieldPortrayalViewer fieldPortrayViewer;
	private AgentPortrayalViewer agentPortrayViewer;
	private ModifyListener textModifyListener;
	private boolean _2dVisualization = true;

	public GUIClassPage(String pageName, ProjectInformation projectInfo) {
		super(pageName);
		this.projectInfo = projectInfo;

		withGUI = true;

		textModifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setPageComplete(validatePage());
			}

		};

		this.setTitle(TITLE);
		this.setDescription(DESCRIPTION);
	}


	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		initializeDialogUnits(parent);

		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		container.setFont(parent.getFont());

		createGUIGroup(container);
		createDisplayGroup(container);
		createFieldPortrayalGroup(container);
		//createAgentPortrayalGroup(container);

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
		
		
		// we have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonProjectWizard.PAGE_WIDTH, MasonProjectWizard.PAGE_HEIGHT);
		getShell().setSize( size );
	}

	private void createGUIGroup(Composite container) {

		GUIButton = new Button(container, SWT.CHECK);
		GUIButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		GUIButton.setText(GUI_BUTTON);
		GUIButton.setSelection(true);

		GUIButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				GUIButtonSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				GUIButtonSelected(e);
			}
		});

		GUIText = addStringFieldEditor(container, GUI_CLASS);

		GUIText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				boolean validate = validatePage();
				setPageComplete(validate);
			}
		});
	}

	private void GUIButtonSelected(SelectionEvent e) {
		if (this.GUIButton != null) {
			this.withGUI = GUIButton.getSelection();
		}

		//if we don't need GUI, we may need to disable all the item in this wizard page
		if (withGUI) {
			
			//disable GUIText
			this.GUIText.setEnabled(true);
			this._2dButton.setEnabled(true);
			this._3dButton.setEnabled(true);
			this.widthText.setEnabled(true);
			this.heightText.setEnabled(true);
		} else {
			this.GUIText.setEnabled(false);
			this._2dButton.setEnabled(false);
			this._3dButton.setEnabled(false);
			this.widthText.setEnabled(false);
			this.heightText.setEnabled(false);
		}
		
		saveDataToModel();

	}

	private void modifyClassName(ModifyEvent e) {
		if (this.withGUI) {
			String GUIName = this.GUIText.getText();
			if (GUIName.endsWith("WithUI"))
				this.GUIText.setText(this.projectInfo.simStateClassName+ "WithUI");
		}
	}

	private void createDisplayGroup(Composite container) {
		Group group = new Group(container, SWT.NONE);

		
		// Configure our group
		group.setFont(container.getFont());
		group.setText(DISPLAY_SETTING);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout layout = new GridLayout(1, false);
		group.setLayout(layout);

		Composite buttonGroup = new Composite(group, SWT.NONE);
		buttonGroup.setFont(group.getFont());
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout buttonGroupLayout = new GridLayout(2, true);
		// buttonGroupLayout = this.configureLayout(buttonGroupLayout);
		buttonGroup.setLayout(buttonGroupLayout);

		// Configure our buttons
		_2dButton = new Button(buttonGroup, SWT.RADIO);
		_2dButton.setText(TWODBUTTON);
		// _2dButton.setLayoutData(new
		// GridData(SWT.FILL,SWT.CENTER,true,false));
		_2dButton.setSelection(true);

		_3dButton = new Button(buttonGroup, SWT.RADIO);
		_3dButton.setText(THREEDBUTTON);
		// _3dButton.setLayoutData(new
		// GridData(SWT.FILL,SWT.CENTER,true,false));
		_3dButton.setSelection(false);

		// Only need register one listener,
		// Radio button will also issue this event when lose focus
		_2dButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				radioButtonSelected(e);
			}

			public void widgetSelected(SelectionEvent e) {
				radioButtonSelected(e);
			}
		});

		Composite textGroup = new Composite(group, SWT.NONE);
		textGroup.setFont(group.getFont());
		textGroup
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout textGroupLayout = new GridLayout(2, true);
		textGroup.setLayout(textGroupLayout);

		widthText = addStringFieldEditor(textGroup, WIDTH);
		widthText.setText(DEFAULT_WIDTH);
		widthText.addModifyListener(textModifyListener);
		heightText = addStringFieldEditor(textGroup, HEIGHT);
		heightText.setText(DEFAULT_HEIGHT);
		heightText.addModifyListener(textModifyListener);

	}

	private void radioButtonSelected(SelectionEvent e) {
		if (this._2dButton != null)
			this._2dVisualization = _2dButton.getSelection();

	}

	private void createFieldPortrayalGroup(Composite container) {
		Group group = new Group(container, SWT.NONE);

		// Configure our group
		group.setFont(container.getFont());
		group.setText(FIELDS_PORTRAY);

		GridLayout layout = new GridLayout(1, false);
		// layout = this.configureLayout(layout);
		group.setLayout(layout);

		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		fieldPortrayViewer = new FieldPortrayalViewer(group, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		fieldPortrayViewer.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL, true, true));

	}

//	private void createAgentPortrayalGroup(Composite container) {
//		Group group = new Group(container, SWT.NONE);
//
//		// Configure our group
//		group.setFont(container.getFont());
//		group.setText(AGENTS_PORTRAY);
//		group.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		GridLayout layout = new GridLayout(1, false);
//		// layout = this.configureLayout(layout);
//		group.setLayout(layout);
//
//		agentPortrayViewer = new AgentPortrayalViewer(group, SWT.MULTI
//				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
//
//		agentPortrayViewer.setLayoutData(new GridData(GridData.FILL,
//				GridData.FILL, true, true));
//	}
	
	

	@Override
	protected void saveDataToModel() {
		this.projectInfo.displayHeight = Integer.parseInt(this.heightText.getText());
		this.projectInfo.displayWidth = Integer.parseInt(this.widthText.getText());
		this.projectInfo.withGUI = this.withGUI;
		this.projectInfo.GUIClassName = this.GUIText.getText();

	}

	@Override
	protected boolean validatePage() {
		if (!this.checkDisplay())
			return false;
		if (!this.checkGUIClassName())
			return false;
		saveDataToModel();
		return true;
	}

	private boolean checkGUIClassName() {
		if (!this.withGUI)
			return true;
		String nameString = this.GUIText.getText();
		IStatus status = JavaConventions.validateJavaTypeName(nameString,
				JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		if (status.isOK()) {
			this.setErrorMessage(null);
			return true;
		} else if (status.getSeverity() == IStatus.ERROR) {
			this.setErrorMessage(status.getMessage());
		} else if (status.getSeverity() == IStatus.WARNING) {
			this.setErrorMessage(status.getMessage());
		}
		return false;
	}

	private boolean checkDisplay() {
		try {
			Integer.parseInt(this.widthText.getText());
			Integer.parseInt(this.heightText.getText());
		} catch (NumberFormatException e) {
			setErrorMessage(MasonWizardConstants.Message.DISPLAY_ERROR);
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			fieldPortrayViewer.setInput(projectInfo.fieldInfoList);
			//agentPortrayViewer.setInput(projectInfo.agentInfoList);
		}
		
		if(firstVisit)
		{
			// Set GUI class name
			if(withGUI)
			{
				this.GUIText.setText(this.projectInfo.simStateClassName+"WithUI");
				this.GUIText.setEnabled(true);
			}
			else{
				this.GUIText.setText("");
				this.GUIText.setEnabled(false);
			}
			firstVisit = false;
		}
		else {
			if(this.projectInfo.withGUI)
			{
				this.GUIText.setText(this.projectInfo.GUIClassName);
				this.GUIText.setEnabled(true);
				this.GUIButton.setSelection(true);
			}
			else {
				this.GUIText.setText("");
				this.GUIText.setEnabled(false);
				this.GUIButton.setSelection(false);
			}
		}
		
		
		super.setVisible(visible);
	}

	public void disposeResource() {
		//this.agentPortrayViewer.disposeColors();
	}
	
	

}
