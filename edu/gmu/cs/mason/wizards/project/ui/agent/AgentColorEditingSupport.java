package edu.gmu.cs.mason.wizards.project.ui.agent;


import edu.gmu.cs.mason.wizards.model.AgentInformation;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;


public class AgentColorEditingSupport extends EditingSupport{

	private ColorCellEditor cellEditor = null;
	
	public AgentColorEditingSupport(ColumnViewer viewer) {
		super(viewer);
		

		cellEditor = new ColorCellEditor((Composite) getViewer().getControl());

		
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
		return agentInfo.getColor();
	}


	@Override
	protected void setValue(Object element, Object value) {

		AgentInformation agentInfo = (AgentInformation)element;
		agentInfo.setColor((RGB)value);
		TableViewer viewer = (TableViewer)getViewer();

		//viewer.getTable().
		viewer.update(element, null);
	}



}

