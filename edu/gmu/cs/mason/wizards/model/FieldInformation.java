package edu.gmu.cs.mason.wizards.model;



public class FieldInformation {
	public enum Dimension
	{
		twoD{
			public String toString()
			{
				return "2D";
			}
		},
		threeD{
			public String toString()
			{
				return "3D";
			}
		}
	};
	
	public enum Type 
	{
		Continuous,
		IntGrid,
		DoubleGrid,
		ObjectGrid,
		SparseGrid,
		DenseGrid
	}
	
	
	public enum PortrayalType
	{
		None,
		ContinuousPortrayal2D,
		ContinuousPortrayal3D,
		ValueGridPortrayal2D,
		HexaValueGridPortrayal2D,
		FastValueGridPortrayal2D,
		FastHexaValueGridPortrayal2D,
		ValueGridPortrayal3D,
		ObjectGridPortrayal2D,
		HexaObjectGridPortrayal2D,
		FastObjectGridPortrayal2D,
		FastHexaObjectGridPortrayal2D,
		ObjectGridPortrayal3D,
		SparseGridPortrayal2D,
		HexaSparseGridPortrayal2D,
		SparseGridPortrayal3D,
		DenseGridPortrayal2D,
	}
	
	
	private String fieldName;
	private String portrayalName;
	

	private Type fieldType;
	private Dimension dimension;
	private PortrayalType portrayal = PortrayalType.None;
	private double width;
	private double height;
	private double length;
	private double discretization;
	private Object initialValueObject;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	
	public String getPortrayalName() {
		return portrayalName;
	}

	public void setPortrayalName(String portrayalName) {
		this.portrayalName = portrayalName;
	}
	
	public void setFieldInformation(FieldInformation info)
	{
		this.setDimension(info.getDimension());
		this.setFieldType(info.getFieldType());
		this.setWidth(info.getWidth());
		this.setHeight(info.getHeight());
		this.setLength(info.getLength());
		this.setDiscretization(info.getDiscretization());
		this.setInitialValueObject(info.getInitialValueObject());
	}
	
	
	public Type getFieldType() {
		return fieldType;
	}
	
	public void setFieldType(Type fieldType) {
		this.fieldType = fieldType;
	}
	
	
	public Dimension getDimension() {
		return dimension;
	}
	
	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
	
	public double getWidth() {
		return width;
	}
	
	public void setWidth(double width) {
		this.width = width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public double getDiscretization() {
		return discretization;
	}
	
	public void setDiscretization(double discretization) {
		this.discretization = discretization;
	}
	
	public Object getInitialValueObject() {
		return initialValueObject;
	}
	
	public void setInitialValueObject(Object initialValueObject) {
		this.initialValueObject = initialValueObject;
	}

	public String getHeightStr() {
		if(this.fieldType==Type.Continuous)
		{
			return height+"";
		}
		else {
			return ((int)height)+"";
		}
	}

	public String getWidthStr() {
		if(this.fieldType==Type.Continuous)
		{
			return width+"";
		}
		else {
			return ((int)width)+"";
		}
	}

	public String getLengthStr() {
		if(this.fieldType==Type.Continuous)
		{
			return length+"";
		}
		else {
			return ((int)length)+"";
		}
	}
	
	public PortrayalType getPortrayal() {
		return portrayal;
	}

	public void setPortrayal(PortrayalType portrayal) {
		this.portrayal = portrayal;
	}

	@Override
	public String toString() {
		return this.getFieldName() + "   --   " + this.getFieldType().toString()+this.getDimension().toString();
	}
	
	
}
