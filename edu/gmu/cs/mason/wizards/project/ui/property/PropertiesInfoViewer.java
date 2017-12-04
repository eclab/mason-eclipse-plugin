package edu.gmu.cs.mason.wizards.project.ui.property;

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

import edu.gmu.cs.mason.wizards.model.PropertyInformation;

public class PropertiesInfoViewer {
	
	private static final int PROPERTY_COLUMN_WIDTH = 100;
	private static final int PROPERTY_COLUMN_NARROWER_WIDTH = 70;
	private static final int PROPERTY_COLUMN_WIDER_WIDTH = 70;

	
	private static final String PROPERTY_VAR = "Property";
	private static final String PROPERTY_TYPE = "Type";
	private static final String PROPERTY_HIDDEN_STATUS = "Hide Property";
	private static final String PROPERTY_NAME_STATUS = "Property Name";
	private static final String PROPERTY_DESCRIPTION_STATUS = "Property Description";
	private static final String PROPERTY_DOMAIN_STATUS = "Property Domain";

	private TableViewer tableViewer;
	
	
	public PropertiesInfoViewer(Group group, int style) {
		tableViewer = new TableViewer(group, style);
		
		createColumns(group, tableViewer);	
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
	}
	

	/**
	 * Create columns for the viewer, each column correspond to a piece of information about generating
	 * the properties.
	 */
	private void createColumns(Composite parent, TableViewer viewer) {
		int columnIndex = 0;
		TableViewerColumn col = createTableViewerColumn(PROPERTY_VAR, PROPERTY_COLUMN_WIDTH, columnIndex++);
		
		// The name of the property variables
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        PropertyInformation propertyInfo = (PropertyInformation) element;
	        return propertyInfo.getPropertyVarName();
	      }
	    });

	    // The type of the property variables
	    col = createTableViewerColumn(PROPERTY_TYPE, PROPERTY_COLUMN_WIDTH, columnIndex++);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PropertyInformation propertyInfo = (PropertyInformation) element;
	    	  return propertyInfo.getPropertyTypeString();
	      }
	    });
	    
	    // The name status of the property variables
	    col = createTableViewerColumn(PROPERTY_NAME_STATUS, PROPERTY_COLUMN_NARROWER_WIDTH, columnIndex++);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PropertyInformation propertyInfo = (PropertyInformation) element;
	    	  return propertyInfo.getName();
	      }
	    });
	    
	    // The hidden status of the property variables
	    col = createTableViewerColumn(PROPERTY_HIDDEN_STATUS, PROPERTY_COLUMN_NARROWER_WIDTH, columnIndex++);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PropertyInformation propertyInfo = (PropertyInformation) element;
	    	  return propertyInfo.getHiddenStatusString();
	      }
	    });
	    
	    // The description of the property variables
	    col = createTableViewerColumn(PROPERTY_DESCRIPTION_STATUS, PROPERTY_COLUMN_WIDTH, columnIndex++);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PropertyInformation propertyInfo = (PropertyInformation) element;
	    	  return propertyInfo.getDescription();
	      }
	    });
	    
	    // The domain of the property variables
	    col = createTableViewerColumn(PROPERTY_DOMAIN_STATUS, PROPERTY_COLUMN_WIDER_WIDTH, columnIndex++);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PropertyInformation propertyInfo = (PropertyInformation) element;
	    	  return propertyInfo.getDomainDescription();
	      }
	    });

	  }

	  private TableViewerColumn createTableViewerColumn(String title, int width, int colNumber) {
	    final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
	    final TableColumn column = viewerColumn.getColumn();
	    column.setText(title);
	    column.setWidth(width);
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
