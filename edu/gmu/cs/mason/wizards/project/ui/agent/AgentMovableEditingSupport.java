package edu.gmu.cs.mason.wizards.project.ui.agent;

import edu.gmu.cs.mason.wizards.model.AgentInformation;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class AgentMovableEditingSupport extends EditingSupport{

	private CheckboxCellEditor cellEditor = null;
	
	public AgentMovableEditingSupport(ColumnViewer viewer) {
		super(viewer);
		

		cellEditor = new CheckboxCellEditor((Composite) getViewer().getControl(), SWT.CHECK|SWT.READ_ONLY);

		
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
		AgentInformation agentInfo = (AgentInformation)element;
	    return agentInfo.isMovable();
	}


	@Override
	protected void setValue(Object element, Object value) {
		
		AgentInformation agentInfo = (AgentInformation)element;
		agentInfo.setMovable((Boolean)value);
		getViewer().update(element, null);
	}



}
