package edu.gmu.cs.mason.wizards.project.ui.field;

import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Dimension;
import edu.gmu.cs.mason.wizards.model.FieldInformation.Type;

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

public class FieldInfoViewer {

	private static final String NAME = "Field name";
	private static final String FIELD = "Field type";
	private static final String DIMENSION = "Dimension";
	private static final String WIDTH = "Width";
	private static final String HEIGHT = "Height";
	private static final String LENGTH = "Length";
	private static final String DISCRET = "Discretization";

	
	
	private static final int NAME_BOUND = 100;
	private static final int FIELD_BOUND = 130;
	private static final int DIMENSION_BOUND = 90;
	private static final int WIDTH_BOUND = 70;
	private static final int HEIGHT_BOUND = 70;
	private static final int LENGTH_BOUND = 70;
	private static final int DISCRET_BOUND = 70;

	
	private TableViewer tableViewer;
	
	
	public FieldInfoViewer(Group group, int style) {
		tableViewer = new TableViewer(group, style);
		
		createColumns(group, tableViewer);	
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		
		
	}
	

	
	private void createColumns(Composite parent, TableViewer viewer) {

		// 1st column is for the field instance name
		TableViewerColumn col = createTableViewerColumn(NAME, NAME_BOUND, 0);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        FieldInformation fieldInfo = (FieldInformation) element;
	        return fieldInfo.getFieldName();
	      }
	    });
		
		
	    // 2nd column is for the field type
	    col = createTableViewerColumn(FIELD, FIELD_BOUND, 1);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        FieldInformation fieldInfo = (FieldInformation) element;
	        return fieldInfo.getFieldType().toString();
	      }
	    });

	    // 3rd column is for the dimension
	    col = createTableViewerColumn(DIMENSION, DIMENSION_BOUND, 2);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FieldInformation fieldInfo = (FieldInformation) element;
		      return fieldInfo.getDimension().toString();
	      }
	    });
	    
	    
	    // 4th column is the width
	    col = createTableViewerColumn(WIDTH, WIDTH_BOUND, 3);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FieldInformation fieldInfo = (FieldInformation) element;
		      return fieldInfo.getWidth()+"";
	      }
	    });

	    // 5th column is the height
	    col = createTableViewerColumn(HEIGHT, HEIGHT_BOUND, 4);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FieldInformation fieldInfo = (FieldInformation) element;
		      return fieldInfo.getHeight()+"";
	      }
	    });
	    
	    
	    // 6th column is the length
	    col = createTableViewerColumn(LENGTH, LENGTH_BOUND, 5);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FieldInformation fieldInfo = (FieldInformation) element;
		      if(fieldInfo.getDimension()==Dimension.twoD)
		    	  return "NULL";
		      else if(fieldInfo.getDimension()==Dimension.threeD)
		    	  return fieldInfo.getLength()+"";
		      return "NULL";
	      }
	    });
	    
	    // 7th column is the discretization
	    col = createTableViewerColumn(DISCRET, DISCRET_BOUND, 6);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  FieldInformation fieldInfo = (FieldInformation) element;
		      if(fieldInfo.getFieldType()==Type.Continuous)
		    	  return fieldInfo.getDiscretization()+"";
		      else
		    	  return "NULL";
	      }
	    });

	  }

	
	private TableViewerColumn createTableViewerColumn(String title, int bound, int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
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
