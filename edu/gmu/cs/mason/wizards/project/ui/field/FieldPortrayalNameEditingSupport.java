package edu.gmu.cs.mason.wizards.project.ui.field;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import edu.gmu.cs.mason.wizards.model.FieldInformation;

public class FieldPortrayalNameEditingSupport extends FieldPortrayalEditingSupport
{

	private TextCellEditor cellEditor = null;

	public FieldPortrayalNameEditingSupport(ColumnViewer viewer)
	{
		super(viewer);
		cellEditor = new TextCellEditor((Composite) getViewer().getControl());
	}

	@Override
	protected CellEditor getCellEditor(Object element)
	{
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element)
	{
		return true;
	}

	@Override
	protected Object getValue(Object element)
	{

		String portrayalName = ((FieldInformation) element).getPortrayalName();
		if (portrayalName == null || portrayalName.isEmpty()) // if its null or
																 // empty, we
																 // show nothing
			return "";
		return portrayalName;
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		String portrayalName = String.valueOf(value);
		if (portrayalName.equals(""))
			((FieldInformation) element).setPortrayalName(null);
		else
		{
			((FieldInformation) element).setPortrayalName(portrayalName);
		}
		getViewer().update(element, null);
	}

}
