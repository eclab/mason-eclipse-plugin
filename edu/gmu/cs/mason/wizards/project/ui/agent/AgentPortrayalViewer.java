package edu.gmu.cs.mason.wizards.project.ui.agent;

import edu.gmu.cs.mason.wizards.model.AgentInformation;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class AgentPortrayalViewer {
	
	
	private TableViewer tableViewer;
	private AgentPortrayalEditingSupport portraySupport;
	private AgentMovableEditingSupport movableSupport;
	private AgentOrientedEditingSupport orientedSupport;
	private AgentLabelEditingSupport labelSupport;
	private AgentColorEditingSupport colorSupport;
	
	private static final int AGENT_BOUND = 100;
	private static final int COLOR_BOUND = 150;
	private static final int MOVABLE_BOUND = 60;
	private static final int ORIENTED_BOUND = 60;
	private static final int PORTRAY_BOUND = 150;
	private static final int LABEL_BOUND = 150;
	
	private static final String AGENT = "Agent class";
	private static final String COLOR = "Color";
	private static final String MOVABLE = "Movable";
	private static final String ORIENTED = "Oriented";
	private static final String PORTRAY = "SimlePortrayal";
	private static final String LABEL = "Label";
	
	
	
	
	
	
	public AgentPortrayalViewer(Group group, int style) {
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

	    
	    col = createTableViewerColumn(PORTRAY, PORTRAY_BOUND, 1);
	    portraySupport = new AgentPortrayalEditingSupport(col.getViewer());
	    col.setEditingSupport(portraySupport);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  AgentInformation agentInfo = (AgentInformation) element;
	    	  return agentInfo.getPortrayal().toString();
	      }
	    });
	    
	    col = createTableViewerColumn(COLOR, COLOR_BOUND, 2);
	    colorSupport = new AgentColorEditingSupport(col.getViewer());
	    col.setEditingSupport(colorSupport);
	    col.setLabelProvider(new ColumnLabelProvider() {
	    	@Override
			public Color getForeground(Object element) {
	    		return null;
			}

			@Override
			public Color getBackground(Object element) {

				AgentInformation agentInfo = (AgentInformation) element;
				RGB rgb = agentInfo.getColor();
				Color color = new Color(Display.getCurrent(),rgb);
				return color;
			}
			
			
			//we just need the color, nothing else, so just return null
			public String getText(Object element) {
				AgentInformation agentInfo = (AgentInformation) element;
				RGB color = agentInfo.getColor();
				return color.toString();
			}
			
			@Override
			public void update(ViewerCell cell) {
				Color color = cell.getBackground();
				super.update(cell);
				if(color!=null)
				{
					color.dispose();
				}
			}

	    });


	    
	    col = createTableViewerColumn(LABEL, LABEL_BOUND, 3);
	    labelSupport = new AgentLabelEditingSupport(col.getViewer());
	    col.setEditingSupport(labelSupport);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  AgentInformation agentInfo = (AgentInformation) element;
	    	  return agentInfo.getLabel();
	      }
	    });
	    
	    
	    col = createTableViewerColumn(MOVABLE, MOVABLE_BOUND, 4);
	    movableSupport = new AgentMovableEditingSupport(col.getViewer());
	    col.setEditingSupport(movableSupport);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  AgentInformation agentInfo = (AgentInformation) element;
	    	  return agentInfo.isMovable()?"√":"×";
	      }
	    });
	    
	    
	    
	        
	    
	    col = createTableViewerColumn(ORIENTED, ORIENTED_BOUND, 5);
	    orientedSupport = new AgentOrientedEditingSupport(col.getViewer());
	    col.setEditingSupport(orientedSupport);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  AgentInformation agentInfo = (AgentInformation) element;
	    	  return agentInfo.isOriented()?"√":"×";
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
		  
		public void addDoubleClickListener(IDoubleClickListener listener)
		{
			tableViewer.addDoubleClickListener(listener);
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

		public void disposeColors()
		{
			TableItem[] items = tableViewer.getTable().getItems();
			for(int i = 0;i<items.length;++i)
			{
				Color color = items[i].getBackground(2);
				color.dispose();
			}
		}

}
