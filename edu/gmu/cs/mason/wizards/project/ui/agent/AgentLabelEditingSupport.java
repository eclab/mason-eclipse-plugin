package edu.gmu.cs.mason.wizards.project.ui.agent;

import edu.gmu.cs.mason.wizards.model.AgentInformation;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

public class AgentLabelEditingSupport extends EditingSupport {

	private TextCellEditor cellEditor = null;

	public AgentLabelEditingSupport(ColumnViewer viewer) {
		super(viewer);
		cellEditor = new TextCellEditor((Composite) getViewer().getControl());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {

		String label = ((AgentInformation) element).getLabel();
		if(label==null)
			return "";
		return label;
	}

	@Override
	protected void setValue(Object element, Object value) {
		String label = String.valueOf(value);
		if(label.equals(""))
			((AgentInformation) element).setLabel(null);
		else {
			((AgentInformation) element).setLabel(label);
		}
		getViewer().update(element, null);
	}
}
