package edu.gmu.cs.mason.handlers.ui.dialogs;



import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import edu.gmu.cs.mason.Activator;
import edu.gmu.cs.mason.handlers.PropertyGenerationHandler.DomainApplicable;
import edu.gmu.cs.mason.ui.dialogs.CodeInsertionDialog;
import edu.gmu.cs.mason.util.CodeUtil;

public class PropertyGenerationDialog extends CodeInsertionDialog {

	
	private static final String USE_COMBOBOX = "Use ComboBox";
	private static final String USE_SLIDER = "Use slider";
	private static final String DEFINE_DOMAIN = "Define Property Domain";
	private static final String MIN_LABEL = "Min Value";
	private static final String MAX_LABEL = "Max Value";
	private static final String FIELD_TOOLTIP = "Mason extension property for the field:";
	private static final String REVISE_NAME = "Revise the name of the property";
	private static final String HIDE_PROPERTY = "Hide the property";
	private static final String CREATE_DESCRIPTION = "Create description for the property";
	private static final String TITLE = "Generate Property";
	
	private boolean hasDescription = false;
	private boolean reviseName = false;
	private boolean hideName = false;
	private boolean hasDomain = false;
	private boolean useSlider = true;
	private boolean canHaveDomain;
	private boolean canUseComboBox;
	
	
	
	private IField field;
	private String info;
	
	//Widget
	private Button desCheckButton;
	private Group desGroup;
	private Button nameReviseCheckButton;
	private Group nameReviseGroup;
	private Button hideCheckButton;
	private Button domainCheckButton;
	private Group domainGroup;
	private Text desText;
	private Text nameReviseText;
	private DomainApplicable domainApplicable;
	private Group sliderGroup;
	private Button sliderButton;
	private Text minText;
	private Text maxText;
	private Button comboButton;
	private Group comboGroup;
	private Text comboText;
	private Label messageLabel;
	
	
	private String minString;
	private String maxString;
	private String desString;
	private String nameReviseString;
	private String domainString;
	private ModifyListener listener;
	
	
	
	
	public PropertyGenerationDialog(Shell shell, IType type, IField field)
			throws JavaModelException {
		super(shell, type);
		this.setTitle(TITLE);
		
		this.field = field;
		domainApplicable = getDomain(field);
		
		StringBuffer info = new StringBuffer(FIELD_TOOLTIP);
		info.append("  "+field.getElementName());
		
		setInfo(info.toString());

		
		hasDescription = false;
		reviseName = false;
		hideName = false;
		hasDomain = false;
		useSlider = true;
		
		
		listener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				canDialogComplete(validateData());
			}
		};
		
		
	}



	private DomainApplicable getDomain(IField field) {
		try {
			String signature = CodeUtil.stripSignatureToFullQualifiedName(field.getTypeSignature());
			// FIXME may we should also consider class "Integer"
			if(signature.equals("int"))
				return DomainApplicable.INTEGER;
			else if(signature.equals("double"))
				return DomainApplicable.DOUBLE;
			else if(signature.equals("long"))
				return DomainApplicable.LONG;
			
			return DomainApplicable.OTHER;
			
		} catch (Exception e) {
			Activator.log("cannot get the domain from field", e);
		}
		return DomainApplicable.OTHER;

	}

	// Override the the createDialogArea method from super class, but rearrange the controls
	protected Control createDialogArea(Composite parent) {
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData gd = null;

		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		
		Label infoLabel = new Label(composite, SWT.NONE);
		infoLabel.setText(getInfo());
		
		createInsertPositionCombo(composite);
		createHideGroup(composite);
		createDescriptionGroup(composite);
		createNameReviseGroup(composite);
		
		createDomainGroup(composite);
		
		
		messageLabel = createMessageArea(composite);
		if (messageLabel != null) {
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan= 2;
			messageLabel.setLayoutData(gd);
		}
		
		
		return parent;
	}

	
	private void createDescriptionGroup(Composite composite) {
		desCheckButton = new Button(composite, SWT.CHECK);
		desCheckButton.setSelection(hasDescription);
		desCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		desCheckButton.setText(CREATE_DESCRIPTION);
		desGroup = new Group(composite, SWT.NONE);
		desGroup.setLayout(new GridLayout(2, false));
		desGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		desGroup.setEnabled(hasDescription);
		

		desText = new Text(desGroup, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		desText.setLayoutData(gridData);
		
		desCheckButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				hasDescription = !hasDescription;
				desCheckButton.setSelection(hasDescription);
				PropertyGenerationDialog.this.desGroup.setEnabled(hasDescription);
				
				// validateData
				canDialogComplete(validateData());
					
			}
			
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		desText.addModifyListener(listener);
		
		
	}
	
	
	private void createDomainGroup(Composite composite) {
		domainCheckButton = new Button(composite, SWT.CHECK);
		domainCheckButton.setSelection(hasDomain);
		domainCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		domainCheckButton.setText(DEFINE_DOMAIN);
		domainGroup = new Group(composite, SWT.NONE);
		domainGroup.setLayout(new GridLayout(1, false));
		domainGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		domainGroup.setEnabled(hasDomain);
		
		sliderButton = new Button(domainGroup, SWT.RADIO);
		sliderButton.setText(USE_SLIDER);
		//sliderButton.setSelection(useSlider);
		sliderGroup = new Group(domainGroup, SWT.NONE);
		sliderGroup.setLayout(new GridLayout(4, false));
		sliderGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false));
		//sliderGroup.setEnabled(useSlider);
		Label minLabel = new Label(sliderGroup, SWT.NONE);
		minLabel.setText(MIN_LABEL);
		minText = new Text(sliderGroup, SWT.BORDER);
		minText.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true, false));
		Label maxLabel = new Label(sliderGroup, SWT.NONE);
		maxLabel.setText(MAX_LABEL);
		maxText = new Text(sliderGroup, SWT.BORDER);
		maxText.setLayoutData(new GridData(SWT.LEFT,SWT.CENTER,true, false));
		comboButton = new Button(domainGroup, SWT.RADIO);
		comboButton.setText(USE_COMBOBOX);
		//sliderButton.setSelection(!useSlider);
		comboGroup = new Group(domainGroup,SWT.NONE);
		comboGroup.setLayout(new GridLayout(1, false));
		comboGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, true, false));
		//sliderGroup.setEnabled(!useSlider);
		comboText = new Text(comboGroup, SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL|SWT.MULTI);
		GridData gridData = new GridData(SWT.FILL,SWT.CENTER, true, false);
		// set the height of the text control 5 times the row height
		gridData.heightHint = 5 * comboText.getLineHeight(); 
		comboText.setLayoutData(gridData);

		
		setInitialControlStatus();
		
		
		domainCheckButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				hasDomain = !hasDomain;
				domainCheckButton.setSelection(hasDomain);
				PropertyGenerationDialog.this.domainGroup.setEnabled(hasDomain);
				
				// the domainGroup enable base on hasDomain
				domainGroup.setEnabled(hasDomain);
				
				// if we can have domain, set the enable of corresponding group
				if(hasDomain)
				{
					sliderGroup.setEnabled(useSlider);
					comboGroup.setEnabled(!useSlider);
				}	
				
				canDialogComplete(validateData());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		// Only need register one listener,
        // Radio button will also issue this event when lose focus
        sliderButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				radioButtonSelected(e);
				canDialogComplete(validateData());
			}
			
			public void widgetSelected(SelectionEvent e) {
				radioButtonSelected(e);
				canDialogComplete(validateData());
			}
		});
        
        
        minText.addModifyListener(listener);
        maxText.addModifyListener(listener);
        comboText.addModifyListener(listener);
        
		
	}

	/**
	 * based on the type of the field determine the status of the control
	 * the combobox are only use for integer, the slider only use for integer, long and double
	 */
	private void setInitialControlStatus() {
		switch(domainApplicable)
		{
		case DOUBLE:
			canHaveDomain = true;
			canUseComboBox = false;
			break;
		case LONG:
			canHaveDomain = true;
			canUseComboBox = false;
			break;
		case INTEGER:
			canHaveDomain = true;
			canUseComboBox = true;
			break;
		case OTHER:
			canHaveDomain = false;
			canUseComboBox = false;
			break;
		default:
			Activator.log("This should never happen", new Exception());
		}

		// based on the type, we set the status of domainCheckButton
		domainCheckButton.setEnabled(canHaveDomain);
		// the initial status of domain group will always be false
		domainGroup.setEnabled(false);
		
		// initially we select slider
		sliderButton.setSelection(useSlider);
		// we have select slider button only if we can have domain
		sliderButton.setEnabled(canHaveDomain);
		sliderGroup.setEnabled(false);
		
		comboButton.setSelection(!useSlider);
		comboButton.setEnabled(canUseComboBox);
		comboGroup.setEnabled(false);
		
		
	}

	private void radioButtonSelected(SelectionEvent e) {
		if(sliderButton.getSelection())
		{
			useSlider = true;
			sliderGroup.setEnabled(true);
			comboGroup.setEnabled(false);
		}
		else if(comboButton.getSelection())
		{
			useSlider = false;
			sliderGroup.setEnabled(false);
			comboGroup.setEnabled(true);
		}
		
		
	}
	
	



	private void createHideGroup(Composite composite) {
		hideCheckButton = new Button(composite, SWT.CHECK);
		hideCheckButton.setSelection(hideName);
		hideCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		hideCheckButton.setText(HIDE_PROPERTY);
	
		hideCheckButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				hideName = !hideName;
				hideCheckButton.setSelection(hideName);
				//System.out.println(PropertyGenerationDialog.this.hideName);
				canDialogComplete(validateData());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
	}


	private void createNameReviseGroup(Composite composite) {
		
		nameReviseCheckButton = new Button(composite, SWT.CHECK);
		nameReviseCheckButton.setSelection(reviseName);
		nameReviseCheckButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		nameReviseCheckButton.setText(REVISE_NAME);
		nameReviseGroup = new Group(composite, SWT.NONE);
		nameReviseGroup.setLayout(new GridLayout(2, false));
		nameReviseGroup.setLayoutData(new GridData (SWT.FILL, SWT.FILL, false, false));
		nameReviseGroup.setEnabled(reviseName);
		
		
		nameReviseText = new Text(nameReviseGroup, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		nameReviseText.setLayoutData(gridData);
		
		nameReviseCheckButton.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				reviseName = !reviseName;
				nameReviseCheckButton.setSelection(reviseName);
				PropertyGenerationDialog.this.nameReviseGroup.setEnabled(reviseName);
				//System.out.println(PropertyGenerationDialog.this.reviseName);
				canDialogComplete(validateData());
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		
		nameReviseText.addModifyListener(listener);
		
	}

	public String getMinText()
	{
		return minString;
	}

	public String getMaxText()
	{
		return maxString;
	}
	
	
	public String getDesText()
	{
		if(desString!=null)
			return desString;
		return "";
	}

	public String getNameReviseText()
	{
		if(nameReviseString!=null)
			return nameReviseString;
		return "";
	}

	
	public String[] getDomainText()
	{
		if(domainString==null)
			return null;
		
		return domainString.split("\\r?\\n");

	}
	

	
	public boolean hasDescription() {
		return hasDescription;
	}

	public void setHasDescription(boolean hasDescription) {
		this.hasDescription = hasDescription;
	}

	public boolean reviseName() {
		return reviseName;
	}

	public void setReviseName(boolean reviseName) {
		this.reviseName = reviseName;
	}

	public boolean hideName() {
		return hideName;
	}

	public void setHideName(boolean hideName) {
		this.hideName = hideName;
	}

	public boolean hasDomain() {
		return hasDomain;
	}

	public void setHasDomain(boolean hasDomain) {
		this.hasDomain = hasDomain;
	}



	public boolean useSlider() {
		return useSlider;
	}



	public void setUseSlider(boolean useSlider) {
		this.useSlider = useSlider;
	}



	public String getInfo() {
		return info;
	}



	public void setInfo(String info) {
		this.info = info;
	}
	

	private boolean validateData() {
		if(hasDomain&&useSlider&&!invalidNumber())
		{
			setStatusMessage("the bound of slider is invalid");
			return false;
		}
		if(hasDomain&&!useSlider&&!invalidText())
		{
			setStatusMessage("The text of combo box cannot be empty");
			return false;
		}
		
		
		setStatusMessage(getMessage());
		
		saveData();
		
		return true;
	}
	
	



	private void saveData() {
		minString = minText.getText();
		maxString = maxText.getText();
		desString = desText.getText();
		nameReviseString = nameReviseText.getText();
		domainString = comboText.getText();
	}



	private boolean invalidText() {
		String text = comboText.getText();
		if(text==null||text.length()==0)
			return false;
		return true;
	}

	
	


	private boolean invalidNumber() {
		String minString = minText.getText();
		String maxString = maxText.getText();
		if(minString==null||maxString==null||minString.length()==0||maxString.length()==0)
			return false;
		try{
			if(domainApplicable==DomainApplicable.INTEGER)
			{
				int min = Integer.parseInt(minString);
				int max = Integer.parseInt(maxString);
				if(max<=min)
					return false;
			}
			else if(domainApplicable==DomainApplicable.DOUBLE)
			{
				double max = Double.parseDouble(maxString);
				double min = Double.parseDouble(minString);
				if(max<=min)
					return false;
			}
			else if(domainApplicable==DomainApplicable.LONG)
			{
				long max = Long.parseLong(maxString);
				long min = Long.parseLong(minString);
				if(max<=min)
					return false;
			}
		}catch(NumberFormatException e)
		{
			return false;
		}
		
		return true;
	}



	private void canDialogComplete(boolean validate) {
		getOkButton().setEnabled(validate);
	}
	
	
	private void setStatusMessage(String string) {
		messageLabel.setText(string);
	}

	
}
