package edu.gmu.cs.mason.wizards.project.ui.agent;

import edu.gmu.cs.mason.wizards.model.AgentInformation;
import edu.gmu.cs.mason.wizards.model.AgentInformation.SimplePortrayal;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class AgentPortrayalEditingSupport extends EditingSupport{

private ComboBoxViewerCellEditor cellEditor = null;
	
	public AgentPortrayalEditingSupport(ColumnViewer viewer) {
		super(viewer);
		

		cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		cellEditor.setLabelProvider(new LabelProvider());
		cellEditor.setContentProvider(ArrayContentProvider.getInstance());
		cellEditor.setInput(SimplePortrayal.values());
		
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
		return agentInfo.getPortrayal();
	}


	@Override
	protected void setValue(Object element, Object value) {
		AgentInformation agentInfo = (AgentInformation)element;
		SimplePortrayal portrayal = (SimplePortrayal)value;
		agentInfo.setPortrayal(portrayal);
		getViewer().update(element, null);
	}



}
