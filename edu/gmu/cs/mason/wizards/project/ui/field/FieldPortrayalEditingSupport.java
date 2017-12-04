package edu.gmu.cs.mason.wizards.project.ui.field;

import java.util.HashMap;

import edu.gmu.cs.mason.wizards.model.FieldInformation;
import edu.gmu.cs.mason.wizards.model.FieldInformation.PortrayalType;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class FieldPortrayalEditingSupport extends EditingSupport
{
	static
	{
		FieldPortrayalEditingSupport.fieldPortrayMap = new HashMap<String, PortrayalType[]>();
		FieldPortrayalEditingSupport.fieldPortrayals = new HashMap<String, Integer>();

		// continuous
		PortrayalType[] types = new PortrayalType[] { PortrayalType.None, PortrayalType.ContinuousPortrayal2D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("Continuous2D", types);
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.ContinuousPortrayal3D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("Continuous3D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("ContinuousPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("ContinuousPortrayal3D", 0);

		// IntGrid and DoubleGrid
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.ValueGridPortrayal2D,
				PortrayalType.HexaValueGridPortrayal2D, PortrayalType.FastValueGridPortrayal2D,
				PortrayalType.FastHexaValueGridPortrayal2D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("IntGrid2D", types);
		FieldPortrayalEditingSupport.fieldPortrayMap.put("DoubleGrid2D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("ValueGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("HexaValueGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("FastValueGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("FastHexaValueGridPortrayal2D", 0);

		types = new PortrayalType[] { PortrayalType.None, PortrayalType.ValueGridPortrayal3D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("IntGrid3D", types);
		FieldPortrayalEditingSupport.fieldPortrayMap.put("DoubleGrid3D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("ValueGridPortrayal3D", 0);

		// ObjectGrid
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.ObjectGridPortrayal2D,
				PortrayalType.HexaObjectGridPortrayal2D, PortrayalType.FastObjectGridPortrayal2D,
				PortrayalType.FastHexaObjectGridPortrayal2D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("ObjectGrid2D", types);
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.ObjectGridPortrayal3D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("ObjectGrid3D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("ObjectGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("HexaObjectGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("FastObjectGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("FastHexaObjectGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("ObjectGridPortrayal3D", 0);

		// SparseGrid
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.SparseGridPortrayal2D,
				PortrayalType.HexaSparseGridPortrayal2D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("SparseGrid2D", types);
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.SparseGridPortrayal3D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("SparseGrid3D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("SparseGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("HexaSparseGridPortrayal2D", 0);
		FieldPortrayalEditingSupport.fieldPortrayals.put("SparseGridPortrayal3D", 0);

		// DenseGrid
		types = new PortrayalType[] { PortrayalType.None, PortrayalType.DenseGridPortrayal2D };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("DenseGrid2D", types);

		FieldPortrayalEditingSupport.fieldPortrayals.put("DenseGridPortrayal2D", 0);

		types = new PortrayalType[] { PortrayalType.None };
		FieldPortrayalEditingSupport.fieldPortrayMap.put("DenseGrid3D", types);

	}

	public static HashMap<String, PortrayalType[]> fieldPortrayMap;
	public static HashMap<String, Integer> fieldPortrayals;

	private ComboBoxViewerCellEditor cellEditor = null;

	public FieldPortrayalEditingSupport(ColumnViewer viewer)
	{
		super(viewer);

		cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
		cellEditor.setLabelProvider(new LabelProvider());
		cellEditor.setContentProvider(ArrayContentProvider.getInstance());
		cellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);

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
		FieldInformation fieldInfo = (FieldInformation) element;
		return fieldInfo.getPortrayal();
	}

	/**
	 * This method only called after the table viewer lost the focus So just
	 * select the item in the drop down list won't have the affect unless
	 * another press of the mouse happen
	 */
	@Override
	protected void setValue(Object element, Object value)
	{
		FieldInformation fieldInfo = (FieldInformation) element;
		PortrayalType portrayal = (PortrayalType) value;
		fieldInfo.setPortrayal(portrayal);
		getViewer().update(element, null);
	}

	public void setProperInput(FieldInformation fieldInfo)
	{
		String key = fieldInfo.getFieldType() + fieldInfo.getDimension().toString();
		PortrayalType[] values = (PortrayalType[]) FieldPortrayalEditingSupport.fieldPortrayMap.get(key);
		cellEditor.setInput(values);

	}

}
