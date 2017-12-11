package edu.gmu.cs.mason.wizards.project.ui.wizardpage;

import java.util.ArrayList;

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

import edu.gmu.cs.mason.util.MasonDirectoryVerifier;
import edu.gmu.cs.mason.wizards.MasonWizardConstants;
import edu.gmu.cs.mason.wizards.model.ProjectInformation;
import edu.gmu.cs.mason.wizards.model.PropertyInformation;
import edu.gmu.cs.mason.wizards.project.ui.MasonProjectWizard;
import edu.gmu.cs.mason.wizards.project.ui.property.PropertiesInfoViewer;
import edu.gmu.cs.mason.wizards.project.ui.property.PropertyInfoDialog;

public class PropertiesPage extends MasonWizardPage
{

	private static final String TITLE = "Simulation Properties";
	private static final String DESCRIPTION = "Define Properties";

	// Model to store the collect information
	private ProjectInformation projectInfo;
	private PropertyInformation selectedPropertyInfo;
	public ArrayList<PropertyInformation> propertiesInfoList;

	private static final String REMOVE_BUTTON = "Remove";
	private static final String EDIT_BUTTON = "Edit";
	private static final String ADD_BUTTON = "Add";

	// Widgets
	private PropertyInfoDialog dialog;
	private PropertiesInfoViewer propertiesViewer;
	private Button addButton;
	private Button removeButton;
	private Button editButton;

	public PropertiesPage(String pageName, ProjectInformation projectInfo)
	{
		super(pageName);

		setTitle(TITLE);
		setDescription(DESCRIPTION);
		this.projectInfo = projectInfo;
		this.dialog = new PropertyInfoDialog(getShell(), this.projectInfo);
		propertiesInfoList = new ArrayList<PropertyInformation>();
	}

	public void createControl(Composite parent)
	{
		Composite container = new Composite(parent, SWT.NONE);

		initializeDialogUnits(parent);

		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setFont(parent.getFont());

		createPropertiesViewGroup(container);

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

		// We have too many control, so let's resize the wizard
		Point size = getShell().computeSize(MasonProjectWizard.PAGE_WIDTH, MasonProjectWizard.PAGE_HEIGHT);
		getShell().setSize(size);
	}

	private void createPropertiesViewGroup(Composite container)
	{
		Group group = new Group(container, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setText("Properties");
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setFont(container.getFont());

		propertiesViewer = new PropertiesInfoViewer(group,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		propertiesViewer.setInput(propertiesInfoList);
		propertiesViewer.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		propertiesViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				selectdItemChanged(event);
			}
		});
		this.createButtonBox(group);
	}

	private void selectdItemChanged(SelectionChangedEvent event)
	{
		if (!event.getSelection().isEmpty())
		{
			IStructuredSelection selection = (IStructuredSelection) propertiesViewer.getSelection();
			this.selectedPropertyInfo = (PropertyInformation) selection.getFirstElement();
			this.editButton.setEnabled(true);
			this.removeButton.setEnabled(true);
		}
		else
		{
			this.selectedPropertyInfo = null;
			this.editButton.setEnabled(false);
			this.removeButton.setEnabled(false);
		}
	}

	/**
	 * Add the buttons on the right side of the viewer for editing the items in
	 * the viewer.
	 */
	private void createButtonBox(Group group)
	{
		Composite buttonBox = new Composite(group, SWT.NONE);
		buttonBox.setLayout(new GridLayout(1, false));
		buttonBox.setFont(group.getFont());
		buttonBox.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, false, false));

		// Add item button
		addButton = new Button(buttonBox, SWT.PUSH);
		addButton.setText(ADD_BUTTON);
		addButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		addButton.setEnabled(true);
		addButton.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				addButtonPressed(e);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				addButtonPressed(e);
			}
		});

		// Edit button
		editButton = new Button(buttonBox, SWT.PUSH);
		editButton.setText(EDIT_BUTTON);
		editButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		editButton.setEnabled(false);
		editButton.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editButtonPressed(e);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				editButtonPressed(e);
			}
		});

		// Remove button
		removeButton = new Button(buttonBox, SWT.PUSH);
		removeButton.setText(REMOVE_BUTTON);
		removeButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		removeButton.setEnabled(false);
		removeButton.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				removeButtonPressed(e);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				removeButtonPressed(e);
			}
		});
	}

	private void addButtonPressed(SelectionEvent e)
	{
		int code = dialog.openToAdd();
		if (code == Window.OK)
		{
			PropertyInformation propertyInfo = dialog.propertyInfo;
			propertiesInfoList.add(propertyInfo);
			propertiesViewer.refresh();
		}
		setPageComplete(validatePage());
	}

	private void editButtonPressed(SelectionEvent e)
	{
		dialog.propertyInfo = selectedPropertyInfo;
		int code = dialog.openToEdit();
		if (code == Window.OK)
		{
			propertiesViewer.refresh();
		}
		setPageComplete(validatePage());
	}

	private void removeButtonPressed(SelectionEvent e)
	{
		propertiesInfoList.remove(this.selectedPropertyInfo);
		propertiesViewer.refresh();
		setPageComplete(validatePage());
	}

	@Override
	protected void saveDataToModel()
	{
		this.projectInfo.propertiesInfoList = this.propertiesInfoList;
	}

	@Override
	protected boolean validatePage()
	{
		// FIXME add verification
		saveDataToModel();
		return true;
	}

}
