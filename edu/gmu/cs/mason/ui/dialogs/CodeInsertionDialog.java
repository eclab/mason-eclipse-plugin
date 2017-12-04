package edu.gmu.cs.mason.ui.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;



public class CodeInsertionDialog extends SelectionStatusDialog {

	private List<IJavaElement> insertPositions;
	private List<String> fLabels;
	private IType type;
	private static final String firstElement = "First member";
	private static final String secondElement = "Last member";
	private static final String afterElement = "After ''{0}''";
	private static final String insertionPoint = "&Insertion point:";
	private String message = "";
	private int currentPositionIndex = 0;
	private boolean enableInsertPosition = true;
	
	
	private static final int COMBO_VISIBLE_ITEM_COUNT= 30;
	
	public CodeInsertionDialog(Shell shell, IType type) throws JavaModelException {
		super(shell);
		
		
		insertPositions = new ArrayList<IJavaElement>();
		fLabels = new ArrayList<String>();

		System.out.println(type.getElementName()+type.getElementType());
		
		IJavaElement[] members = type.getChildren();

		insertPositions.add(members.length > 0 ? members[0]: null); // first
		insertPositions.add(null); // last

		fLabels.add(CodeInsertionDialog.firstElement);
		fLabels.add(CodeInsertionDialog.secondElement);

		for (int i = 0; i < members.length; i++) {
			IJavaElement curr = members[i];
			String methodLabel = JavaElementLabels.getElementLabel(curr, JavaElementLabels.M_PARAMETER_TYPES);
			//System.out.println(MessageFormat.format(afterElement, methodLabel));
			fLabels.add(MessageFormat.format(afterElement, methodLabel));
			insertPositions.add(findSibling(curr, members));
		}
		insertPositions.add(null);  // null indicate we want to insert into the last position

	}
	
	private IJavaElement findSibling(IJavaElement curr, IJavaElement[] members) throws JavaModelException {
		IJavaElement res = null;
		int methodStart = ((IMember) curr).getSourceRange().getOffset();
		for (int i = members.length-1; i >= 0; i--) {
			IMember member = (IMember) members[i];
			if (methodStart >= member.getSourceRange().getOffset()) {
				return res;
			}
			res = member;
		}
		return null;
	}


	
	protected Control createDialogArea(Composite parent){
		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData gd = null;

		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);

		Label messageLabel = createMessageArea(composite);
		if (messageLabel != null) {
			gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			gd.horizontalSpan= 2;
			messageLabel.setLayoutData(gd);
		}


		createInsertPositionCombo(composite);
		
		return composite;
	}

	
	
	
	protected Label createMessageArea(Composite composite) {
		if (getMessage() != null) {
			Label label = new Label(composite,SWT.NONE);
			label.setText(getMessage());
			label.setFont(composite.getFont());
			return label;
		}
		return null;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	protected Composite createInsertPositionCombo(Composite composite) {
		Composite selectionComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		selectionComposite.setLayout(layout);
		composite.setFocus();
		addOrderEntryChoices(selectionComposite);

		return selectionComposite;
	}

	private Composite addOrderEntryChoices(Composite buttonComposite) {
		Label enterLabel= new Label(buttonComposite, SWT.NONE);
		enterLabel.setText(CodeInsertionDialog.insertionPoint);
		if (!enableInsertPosition)
			enterLabel.setEnabled(false);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		enterLabel.setLayoutData(gd);

		final Combo enterCombo= new Combo(buttonComposite, SWT.READ_ONLY);
		enterCombo.setVisibleItemCount(COMBO_VISIBLE_ITEM_COUNT);
		if (!enableInsertPosition)
			enterCombo.setEnabled(false);
		enterCombo.setItems(fLabels.toArray(new String[fLabels.size()]));
		enterCombo.select(currentPositionIndex);
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = convertWidthInCharsToPixels(60);
		enterCombo.setLayoutData(gd);
		enterCombo.addSelectionListener(new SelectionAdapter(){


			@Override
			public void widgetSelected(SelectionEvent e) {
				int index= enterCombo.getSelectionIndex();
				setInsertPosition(index);
				System.out.println(getElementPosition().toString());
			}
		});

		return buttonComposite;
	}

	
	/*
	 * Determine where in the file to enter the newly created methods.
	 */
	public IJavaElement getElementPosition() {
		return insertPositions.get(currentPositionIndex);
	}

	public int getInsertOffset() throws JavaModelException {
		IJavaElement elementPosition= getElementPosition();
		if (elementPosition instanceof ISourceReference) {
			return ((ISourceReference) elementPosition).getSourceRange().getOffset();
		}
		return -1;
	}
	
	private void setInsertPosition(int insert) {
		currentPositionIndex = insert;
	}

	@Override
	protected void computeResult() {
		// TODO Auto-generated method stub
		
	}
}
