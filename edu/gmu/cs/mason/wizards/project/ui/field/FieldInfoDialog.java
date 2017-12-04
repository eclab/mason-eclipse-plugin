package edu.gmu.cs.mason.wizards.project.ui.field;

import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Dimension;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Type;

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




public class FieldInfoDialog extends TitleAreaDialog {


	private static final String LENGTH = "Length";
	private static final String HEIGHT = "Height";
	private static final String WIDTH = "Width";
	private static final String DISCRETIZATION = "Discretization";
	private static final String FIELD_NAME = "Field Name";
	private static final String FIELDTYPE = "Field Type";
	private static final String FIELDDIMENSION = "Field Dimension";
	private static final String FIELDSIZE = "Field Size";
	
	private static final String DEFAULT_NAME = "field";
	private static final int DEFAULT_FIELDTYPE = 0;
	private static final int DEFAULT_DIMENSION = 0;
	private static final String DEFAULT_WIDTH = "100";
	private static final String DEFAULT_HEIGHT = "100";
	private static final String DEFAULT_LENGTH = "0";
	private static final String DEFAULT_DISCRET = "0.7";
	
	
	private static final String ADDTITLE = "Add New Field";
	private static final String ADDMESSAGE = "Enter Information for New Field";
	private static final String EDITTITLE = "Edit a Field";
	private static final String EDITMESSAGE = "Edit Information of Field";
	

	private static final int HORIZONTAL_SPACING = 4;
	private static final int VERTICAL_SPACING = 4;
	private static final int HORIZONTAL_MARGIN = 7;
	private static final int VERTICAL_MARGIN = 7;
	
	
	//widget
	private Text nameText;
	private Combo typeCombo;
	private Combo dimensionCombo;
	private Text widthText;
	private Text heightText;
	private Text lengthText;
	private Text additionalValueText;
	private ModifyListener textModifyListener;
	public FieldInformation fieldInfo;


	
	public FieldInfoDialog(Shell parentShell) {
		super(parentShell);
		
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

		this.createFieldNameGroup(container);
		this.createFieldTypeGroup(container);
		this.createFieldDimensionGroup(container);
		this.createFieldSizeGroup(container);
		this.createAdditionalGroup(container);
				
		return container;
	}

	
	
	private void createFieldNameGroup(Composite container) {		
		nameText = addLabelAndText(container, FIELD_NAME);
		nameText.setText(DEFAULT_NAME);
		nameText.addModifyListener(textModifyListener);
	}


	private void createAdditionalGroup(Composite container) {
		
		additionalValueText = addLabelAndText(container, DISCRETIZATION);
		additionalValueText.setText(DEFAULT_DISCRET);
		additionalValueText.addModifyListener(textModifyListener);
		
	}


	private void createFieldSizeGroup(Composite container) {
			
		Group group = new Group(container, SWT.NONE);
		group.setFont(container.getFont());
		group.setText(FIELDSIZE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout layout = new GridLayout(3, true);
		layout = this.configureLayout(layout);
		group.setLayout(layout);

		widthText = addLabelAndText(group, WIDTH);
		widthText.setText(DEFAULT_WIDTH);
		widthText.addModifyListener(textModifyListener);
		heightText = addLabelAndText(group, HEIGHT);
		heightText.setText(DEFAULT_HEIGHT);
		heightText.addModifyListener(textModifyListener);
		lengthText = addLabelAndText(group, LENGTH);
		lengthText.setText(DEFAULT_LENGTH);
		lengthText.setEnabled(false);
		lengthText.addModifyListener(textModifyListener);
		
	}


	private GridLayout configureLayout(GridLayout layout) {

		layout.horizontalSpacing = convertHorizontalDLUsToPixels(HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(VERTICAL_SPACING);
		layout.marginWidth = convertHorizontalDLUsToPixels(HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(VERTICAL_MARGIN);
	
		
		return layout;
	}
	
	
	private Text addLabelAndText(Composite container, String label) {
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
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
	
	
	private void createFieldDimensionGroup(Composite container) {
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		group.setFont(container.getFont());
		
		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(FIELDDIMENSION);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());
		
		dimensionCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		Dimension[] values = Dimension.values();
		for(int i = 0;i<values.length;++i)
		{
			dimensionCombo.add(values[i].toString());
			dimensionCombo.setData(values[i].toString(), values[i]);
		}
		
		// Set continuous field as default
		dimensionCombo.select(DEFAULT_DIMENSION);
		dimensionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		dimensionCombo.setFont(group.getFont());
		
		
		dimensionCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				dimensionChanged(e);
				setDialogComplete(validateData());
			}

		});
		
	}

	private boolean validateData() {
		Type type = (Type) this.typeCombo.getData(this.typeCombo.getText());
		Dimension dimension = (Dimension) this.dimensionCombo.getData(this.dimensionCombo.getText());
		if(!this.checkFieldName())
			return false;
		if(!this.checkSize(type,dimension))
			return false;
		if(!this.checkDiscretization(type,dimension))
			return false;
		
		return true;
	}
	
	
	private boolean checkFieldName() {
		String nameString = this.nameText.getText();
		IStatus status = JavaConventions.validateFieldName(nameString, JavaCore.VERSION_1_3, JavaCore.VERSION_1_3);
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


	private boolean checkDiscretization(Type type, Dimension dimension) {
		if(type!=Type.Continuous)
		{
			setErrorMessage(null);
			return true;
		}
		try{
			if(type==Type.Continuous)
			{
				Double.parseDouble(this.additionalValueText.getText());
			}
		}
		catch(NumberFormatException exception)
		{
			setErrorMessage("Discretization must be a number");
			return false;
		}
		setErrorMessage(null);
		return true;
	}


	private boolean checkSize(Type type, Dimension dimension) {
		try{
			if(type==Type.Continuous)
			{
				Double.parseDouble(this.widthText.getText());
				Double.parseDouble(this.heightText.getText());
				if(dimension==Dimension.threeD)
					Double.parseDouble(this.lengthText.getText());
			}
			else
			{
				Integer.parseInt(this.widthText.getText());
				Integer.parseInt(this.heightText.getText());
				if(dimension==Dimension.threeD)
					Integer.parseInt(this.lengthText.getText());
			}
		}
		catch(NumberFormatException e)
		{
			setErrorMessage("The field size is invalid");
			return false;
		}
			
		setErrorMessage(null);
		return true;
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
	
	
	
	private void dimensionChanged(ModifyEvent e) {
		//2D field
		if(dimensionCombo.getData(dimensionCombo.getText())==Dimension.twoD)
		{
			this.lengthText.setEnabled(false);
		}
		//3d field
		else if(dimensionCombo.getData(dimensionCombo.getText())==Dimension.threeD)
		{
			this.lengthText.setEnabled(true);
		}
				
	}
	
	
	private void storeModelToUi() {
		this.nameText.setText(this.fieldInfo.getFieldName());
		
		Type type = this.fieldInfo.getFieldType();
		this.typeCombo.select(type.ordinal());
		
		Dimension dimension = this.fieldInfo.getDimension();
		this.dimensionCombo.select(dimension.ordinal());
		
		

		this.widthText.setText(this.fieldInfo.getWidthStr());
		this.heightText.setText(this.fieldInfo.getHeightStr());
		this.lengthText.setText(this.fieldInfo.getLengthStr());

		
		this.additionalValueText.setText(this.fieldInfo.getDiscretization()+"");
	}


	private void saveDataToModel() {
		this.fieldInfo.setFieldName(this.nameText.getText());
		
		Type type = (Type) this.typeCombo.getData(this.typeCombo.getText());
		this.fieldInfo.setFieldType(type);
		
		Dimension dimension = (Dimension) this.dimensionCombo.getData(this.dimensionCombo.getText());
		this.fieldInfo.setDimension(dimension);
		
		double width = 0;
		try{
			if(type==Type.Continuous)
			{
				width = Double.parseDouble(this.widthText.getText());
			}
			else {
				width = Integer.parseInt(this.widthText.getText());
			}
		}catch(NumberFormatException exception)
		{
			System.err.println("Saving wrong value");
			width = 0;
		}
		this.fieldInfo.setWidth(width);
		
		double height = 0;
		try{
			if(type==Type.Continuous)
			{
				height = Double.parseDouble(this.heightText.getText());
			}
			else {
				height = Integer.parseInt(this.heightText.getText());
			}
		}catch(NumberFormatException exception)
		{
			System.err.println("Saving wrong value");
			height = 0;
		}
		this.fieldInfo.setHeight(height);
		
		double length = 0;
		try{
			if(type==Type.Continuous)
			{
				length = Double.parseDouble(this.lengthText.getText());
			}
			else {
				length = Integer.parseInt(this.lengthText.getText());
			}
		}catch(NumberFormatException exception)
		{
			System.err.println("Saving wrong value");
			length = 0;
		}
		this.fieldInfo.setLength(length);
		
		double discretization = 0;
		try{
			discretization = Double.parseDouble(this.additionalValueText.getText());
		}catch(NumberFormatException exception){
			System.err.println("Saving wrong value");
			discretization = 0;
		}
		this.fieldInfo.setDiscretization(discretization);
		
		//for now, we are not using it
		//this.fieldInfo.setInitialValueObject(initialValueObject);
	}


	private void createFieldTypeGroup(Composite container) {
		Composite group = new Composite(container, SWT.NONE);
		group.setLayout(new GridLayout(2,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
		group.setFont(container.getFont());
		
		Label nameLabel = new Label(group, SWT.NONE);
		nameLabel.setText(FIELDTYPE);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		nameLabel.setFont(group.getFont());
		
		typeCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		Type[] values = Type.values();
		for(int i = 0;i<values.length;++i)
		{
			typeCombo.add(values[i].toString());
			typeCombo.setData(values[i].toString(), values[i]);
		}
		//set continuous grid as default
		typeCombo.select(DEFAULT_FIELDTYPE);
		typeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		typeCombo.setFont(group.getFont());
		

		typeCombo.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				fieldTypeChanged(e);
				setDialogComplete(validateData());
			}
		});
		
	}

	private void fieldTypeChanged(ModifyEvent e) {

		Type type = (Type)typeCombo.getData(typeCombo.getText());
		if(type==Type.Continuous)
		{
			this.additionalValueText.setEnabled(true);
		}
		else {
			this.additionalValueText.setEnabled(false);
		}
	}
	

	public void clearFieldInfo()
	{
		fieldInfo = new FieldInformation();
	}
	

	public int openToAdd() 
	{
		this.create();
		this.setTitle(ADDTITLE);
		this.setMessage(ADDMESSAGE);
		this.clearFieldInfo();
		
		return this.open();
	}
	
	public int openToEdit()
	{
		this.create();
		this.setTitle(EDITTITLE);
		this.setMessage(EDITMESSAGE);
		this.storeModelToUi();

		return this.open();
	}

	
}
