package edu.gmu.cs.mason.wizards.project.ui.property;

import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Dimension;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Type;
import edu.gmu.cs.mason.wizards.model.PropertyInformation;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.DesStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.DomainStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.HiddenStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.NameStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.ReturnType;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PropertyInfoDialog extends TitleAreaDialog
{
	private static final String ADDTITLE = "Add New Property";
	private static final String ADDMESSAGE = "Enter Information for New Property";
	private static final String EDITTITLE = "Edit a Property";
	private static final String EDITMESSAGE = "Edit Information of Property";

	private static final String EMPTY_STRING = "";
	private static final String PROPERTY_VAR = "Property Variable";
	private static final String PROPERTY_TYPE = "Property Type";
	private static final String PROPERTY_NAME_LABEL = "Property Name Status";
	private static final String PROPERTY_NAME = "Property Name";
	private static final String PROPERTY_HIDDEN_LABEL = "Property Hidden Status";
	private static final String PROPERTY_DESC_LABEL = "Property Description Status";
	private static final String PROPERTY_DESC = "Property Description";
	private static final String PROPERTY_DOMAIN_LABEL = "Property Domain Status";
	private static final String PROPERTY_MENU_OPTIONS = "Menu Options";
	private static final String PROPERTY_INTERVAL_MIN = "Min Value";
	private static final String PROPERTY_INTERVAL_MAX = "Max Value";

	private static final int DEFAULT_PROPERTY_TYPE = 0;
	private static final int DEFAULT_PROPERTY_NAME_STATUS = 0;
	private static final int DEFAULT_PROPERTY_DESCRIPTION_STATUS = 0;
	private static final int DEFAULT_PROPERTY_DOMAIN_STATUS = 0;

	private static final int HORIZONTAL_SPACING = 4;
	private static final int VERTICAL_SPACING = 4;
	private static final int HORIZONTAL_MARGIN = 7;
	private static final int VERTICAL_MARGIN = 7;

	// Property value
	public PropertyInformation propertyInfo;
	public ProjectInformation projectInfo;

	// Widgets
	private Combo typeCombo;
	private Text varNameText;
	private Combo nameStatusCombo;
	private Text nameText;
	private Combo hiddenStatusCombo;
	private Combo desStatusCombo;
	private Text desText;
	private Combo domainStatusCombo;
	private Text optionsText;
	private Text minValText;
	private Text maxValText;
	private ModifyListener textModifyListener;

	public PropertyInfoDialog(Shell parentShell, ProjectInformation projectInfo)
	{
		super(parentShell);
		this.projectInfo = projectInfo;
		textModifyListener = new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				setDialogComplete(validateData());
			}
		};
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite container = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(parent);

		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setFont(parent.getFont());

		// creates groups for different aspect of the property
		createPropertyVarGroup(container);
		createPropertyTypeGroup(container);
		createPropertyNameGroup(container);
		createPropertyHiddenGroup(container);
		createPropertyDescriptionGroup(container);
		createPropertyDomainGroup(container);

		return container;
	}

	private void createPropertyVarGroup(Composite container)
	{
		varNameText = addLabelAndText(container, PROPERTY_VAR);
		varNameText.setText(EMPTY_STRING);
		varNameText.addModifyListener(textModifyListener);
	}

	private void createPropertyTypeGroup(Composite container)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(PROPERTY_TYPE);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		typeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		ReturnType[] values = ReturnType.values();
		for (int i = 0; i < values.length; ++i)
		{
			typeCombo.add(values[i].toString());
			typeCombo.setData(values[i].toString(), values[i]);
		}
		// set int as default type
		typeCombo.select(DEFAULT_PROPERTY_TYPE);
		typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		typeCombo.setFont(group.getFont());

		typeCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				propertyTypeChanged(e);
				setDialogComplete(validateData());
			}
		});
	}

	private void propertyTypeChanged(ModifyEvent e)
	{

		ReturnType type = (ReturnType) typeCombo.getData(typeCombo.getText());

		// TODO: Need to validate and enable or disable some control here
	}

	private void createPropertyNameGroup(Composite container)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(PROPERTY_NAME_LABEL);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		nameStatusCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		NameStatus[] values = NameStatus.values();
		for (int i = 0; i < values.length; ++i)
		{
			nameStatusCombo.add(values[i].toString());
			nameStatusCombo.setData(values[i].toString(), values[i]);
		}
		// set None as default type and disable nextText
		nameStatusCombo.select(DEFAULT_PROPERTY_NAME_STATUS);
		nameStatusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		nameStatusCombo.setFont(group.getFont());

		nameText = addLabelAndText(container, PROPERTY_NAME);
		nameText.setText(EMPTY_STRING);
		nameText.addModifyListener(textModifyListener);
		nameText.setEnabled(false);

		nameStatusCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				propertyNameStatusChanged(e);
				setDialogComplete(validateData());
			}
		});
	}

	private void propertyNameStatusChanged(ModifyEvent e)
	{
		NameStatus status = (NameStatus) nameStatusCombo.getData(nameStatusCombo.getText());
		// Enable the text field if status is "WITH_NAME"
		if (status == NameStatus.WITH_NAME)
			nameText.setEnabled(true);
		else
			nameText.setEnabled(false);
	}

	private void createPropertyHiddenGroup(Composite container)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(PROPERTY_HIDDEN_LABEL);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		hiddenStatusCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		HiddenStatus[] values = HiddenStatus.values();
		for (int i = 0; i < values.length; ++i)
		{
			hiddenStatusCombo.add(values[i].toString());
			hiddenStatusCombo.setData(values[i].toString(), values[i]);
		}
		// set int as default type
		hiddenStatusCombo.select(DEFAULT_PROPERTY_TYPE);
		hiddenStatusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		hiddenStatusCombo.setFont(group.getFont());

		hiddenStatusCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				propertyHiddenStatusChanged(e);
				setDialogComplete(validateData());
			}
		});
	}

	private void propertyHiddenStatusChanged(ModifyEvent e)
	{
		HiddenStatus status = (HiddenStatus) hiddenStatusCombo.getData(hiddenStatusCombo.getText());
		// TODO: Need to validate and enable or disable some control here
	}

	private void createPropertyDescriptionGroup(Composite container)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(PROPERTY_DESC_LABEL);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		desStatusCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		DesStatus[] values = DesStatus.values();
		for (int i = 0; i < values.length; ++i)
		{
			desStatusCombo.add(values[i].toString());
			desStatusCombo.setData(values[i].toString(), values[i]);
		}
		// set None as default type and disable nextText
		desStatusCombo.select(DEFAULT_PROPERTY_DESCRIPTION_STATUS);
		desStatusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		desStatusCombo.setFont(group.getFont());

		desText = addLabelAndText(container, PROPERTY_DESC);
		desText.setText(EMPTY_STRING);
		desText.addModifyListener(textModifyListener);
		desText.setEnabled(false);

		desStatusCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				propertyDescriptionStatusChanged(e);
				setDialogComplete(validateData());
			}
		});
	}

	private void propertyDescriptionStatusChanged(ModifyEvent e)
	{
		DesStatus status = (DesStatus) desStatusCombo.getData(desStatusCombo.getText());
		// Enable the text field if status is "WITH_DESCRIPTION"
		if (status == DesStatus.WITH_DESCRIPTION)
			desText.setEnabled(true);
		else
			desText.setEnabled(false);
	}

	private void createPropertyDomainGroup(Composite container)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(PROPERTY_DOMAIN_LABEL);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		domainStatusCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		DomainStatus[] values = DomainStatus.values();
		for (int i = 0; i < values.length; ++i)
		{
			domainStatusCombo.add(values[i].toString());
			domainStatusCombo.setData(values[i].toString(), values[i]);
		}
		// set None as default type and disable nextText
		domainStatusCombo.select(DEFAULT_PROPERTY_DOMAIN_STATUS);
		domainStatusCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		domainStatusCombo.setFont(group.getFont());

		optionsText = addLabelAndText(container, PROPERTY_MENU_OPTIONS);
		optionsText.setText(EMPTY_STRING);
		optionsText.addModifyListener(textModifyListener);
		optionsText.setEnabled(false);

		minValText = addLabelAndText(container, PROPERTY_INTERVAL_MIN);
		minValText.setText(EMPTY_STRING);
		minValText.addModifyListener(textModifyListener);
		minValText.setEnabled(false);

		maxValText = addLabelAndText(container, PROPERTY_INTERVAL_MAX);
		maxValText.setText(EMPTY_STRING);
		maxValText.addModifyListener(textModifyListener);
		maxValText.setEnabled(false);

		domainStatusCombo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				propertyDomainStatusChanged(e);
				setDialogComplete(validateData());
			}
		});
	}

	private void propertyDomainStatusChanged(ModifyEvent e)
	{
		DomainStatus status = (DomainStatus) domainStatusCombo.getData(domainStatusCombo.getText());
		// Enable the text field if status is "WITH_DESCRIPTION"
		if (status == DomainStatus.NONE)
		{
			optionsText.setEnabled(false);
			minValText.setEnabled(false);
			maxValText.setEnabled(false);
		}
		else if (status == DomainStatus.SLIDER)
		{
			optionsText.setEnabled(false);
			minValText.setEnabled(true);
			maxValText.setEnabled(true);
		}
		else if (status == DomainStatus.MENU)
		{
			optionsText.setEnabled(true);
			minValText.setEnabled(false);
			maxValText.setEnabled(false);
		}
	}

	private GridLayout configureLayout(GridLayout layout)
	{

		layout.horizontalSpacing = convertHorizontalDLUsToPixels(HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(VERTICAL_MARGIN);

		return layout;
	}

	private Text addLabelAndText(Composite container, String label)
	{
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		group.setFont(container.getFont());

		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(label);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());

		Text text = new Text(group, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.setFont(group.getFont());

		return text;

	}

	private boolean validateData()
	{
		// TODO: Need to verify the data
		return true;
	}

	private boolean checkFieldName()
	{
		String nameString = this.nameText.getText();
		IStatus status = JavaConventions.validateFieldName(nameString, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
		if (status.isOK())
		{
			this.setErrorMessage(null);
			return true;
		}
		else if (status.getSeverity() == IStatus.ERROR)
		{
			this.setErrorMessage(status.getMessage());
		}
		else if (status.getSeverity() == IStatus.WARNING)
		{
			this.setErrorMessage(status.getMessage());
		}
		return false;
	}

	private void setDialogComplete(boolean finished)
	{
		this.getButton(IDialogConstants.OK_ID).setEnabled(finished);
	}

	@Override
	protected void okPressed()
	{
		this.saveDataToModel();
		super.okPressed();
	}

	private void storeModelToUI()
	{
		varNameText.setText(propertyInfo.getPropertyVarName());
		ReturnType type = propertyInfo.getPropertyType();
		typeCombo.select(type.ordinal());

		NameStatus nameStatus = propertyInfo.getNameStatus();
		nameStatusCombo.select(nameStatus.ordinal());
		nameText.setText(propertyInfo.getName());

		DesStatus desStatus = propertyInfo.getDesStatus();
		desStatusCombo.select(desStatus.ordinal());
		desText.setText(propertyInfo.getDescription());

		HiddenStatus hiddenStatus = propertyInfo.getHiddenStatus();
		hiddenStatusCombo.select(hiddenStatus.ordinal());

		DomainStatus domainStatus = propertyInfo.getDomainStatus();
		domainStatusCombo.select(domainStatus.ordinal());
		if (domainStatus == DomainStatus.SLIDER)
		{
			minValText.setText(propertyInfo.getMinValue() + "");
			maxValText.setText(propertyInfo.getMaxValue() + "");
		}
		else if (domainStatus == DomainStatus.MENU)
		{
			optionsText.setText(propertyInfo.getMenuOptionsString());
		}
	}

	private void saveDataToModel()
	{
		propertyInfo.setPropertyVarName(varNameText.getText());

		ReturnType type = (ReturnType) typeCombo.getData(typeCombo.getText());
		propertyInfo.setPropertyType(type);

		NameStatus nameStatus = (NameStatus) nameStatusCombo.getData(nameStatusCombo.getText());
		propertyInfo.setNameStatus(nameStatus);
		if (nameStatus == NameStatus.WITH_NAME)
			propertyInfo.setName(nameText.getText());
		else
			propertyInfo.setName("");

		DesStatus desStatus = (DesStatus) desStatusCombo.getData(desStatusCombo.getText());
		propertyInfo.setDesStatus(desStatus);
		if (desStatus == DesStatus.WITH_DESCRIPTION)
			propertyInfo.setDes(desText.getText());
		else
			propertyInfo.setDes("");

		HiddenStatus hiddenStatus = (HiddenStatus) hiddenStatusCombo.getData(hiddenStatusCombo.getText());
		propertyInfo.setHiddenStatus(hiddenStatus);

		DomainStatus domainStatus = (DomainStatus) domainStatusCombo.getData(domainStatusCombo.getText());
		propertyInfo.setDomainStatus(domainStatus);
		if (domainStatus == DomainStatus.SLIDER)
		{
			propertyInfo.setMinValue(minValText.getText());
			propertyInfo.setMaxValue(maxValText.getText());
		}
		else if (domainStatus == DomainStatus.MENU)
		{
			propertyInfo.setOptionsString(optionsText.getText());
		}
	}

	public void clearPropertyInfo()
	{
		propertyInfo = new PropertyInformation();
	}

	public int openToAdd()
	{
		this.create();
		this.setTitle(ADDTITLE);
		this.setMessage(ADDMESSAGE);
		this.clearPropertyInfo();

		return this.open();
	}

	public int openToEdit()
	{
		this.create();
		this.setTitle(EDITTITLE);
		this.setMessage(EDITMESSAGE);
		this.storeModelToUI();

		return this.open();
	}

}
