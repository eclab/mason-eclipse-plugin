package edu.gmu.cs.mason.wizards.project.ui.agent;

import edu.gmu.cs.mason.wizards.model.AgentInformation;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;

public class AgentInfoViewer {
	
	private static final int PACKAGE_BOUND = 130;
	private static final int AGENT_BOUND = 100;

	
	private static final String AGENT = "Agent class";
	private static final String PACKAGE = "Packge of agent file";

	private TableViewer tableViewer;
	
	
	public AgentInfoViewer(Group group, int style) {
		tableViewer = new TableViewer(group, style);
		
		createColumns(group, tableViewer);	
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		
	}
	

	
	private void createColumns(Composite parent, TableViewer viewer) {

		TableViewerColumn col = createTableViewerColumn(AGENT, AGENT_BOUND, 0);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        AgentInformation agentInfo = (AgentInformation) element;
	        return agentInfo.getAgentName();
	      }
	    });

	    

	    col = createTableViewerColumn(PACKAGE, PACKAGE_BOUND, 1);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  AgentInformation agentInfo = (AgentInformation) element;
	    	  return agentInfo.getPackageName();
	      }
	    });

	  }

	  private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(bound);
	    column.setResizable(true);
	    column.setMoveable(true);
	    return viewerColumn;
	  }
	
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.addSelectionChangedListener(listener);
	}
	  
	  
	public ISelection getSelection()
	{
		return tableViewer.getSelection();
	}
	
	public void setInput(Object input)
	{
		tableViewer.setInput(input);
	}
	
	
	public void setLayoutData(Object layoutData)
	{
		tableViewer.getTable().setLayoutData(layoutData);
	}
	
		
	public void refresh()
	{
		tableViewer.refresh();
	}
}
