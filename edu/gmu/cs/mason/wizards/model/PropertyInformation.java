package edu.gmu.cs.mason.wizards.model;

import java.util.ArrayList;

import edu.gmu.cs.mason.wizards.model.PropertyInformation.DesStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.DomainStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.HiddenStatus;
import edu.gmu.cs.mason.wizards.model.PropertyInformation.NameStatus;

// See more information at Section 3.4 of Manual (Version 17)
public class PropertyInformation
{
	// So far, we allow three types
	public enum ReturnType
	{
		INT {
			public String toString() {
				return "int";
			}
		},
		DOUBLE {
			public String toString() {
				return "double";
			}
		},
		LONG {
			public String toString() {
				return "long";
			}
		},
		BOOLEAN {
			public String toString() {
				return "boolean";
			}
		},
	}
	
	public enum NameStatus
	{
		NONE,
		WITH_NAME,
	}
	
	public enum DesStatus
	{
		NONE,
		WITH_DESCRIPTION
	}
	
	public enum DomainStatus
	{
		NONE,
		SLIDER,
		MENU
	}
	
	public enum HiddenStatus
	{
		NONE,
		TRUE,
		FALSE
	}
	
	// This is the type of the variable
	private ReturnType propertyVarType;
	
	// This is the name of the variable in the code
	private String propertyVarName;
	
	// This is the name will show in the Mason's control panel
	private NameStatus nameStatus;
	private String name;
	
	// This is the tooltip over the widget
	private DesStatus desStatus;
	private String description;
	
	// Do we want to hid the property
	private HiddenStatus hiddenStatus;
	private boolean hidden;
	
	// domain of this property
	private DomainStatus domainStatus;
	
	// Options for menu domain, only useful if we use MENU Domain
	private ArrayList<String> options = new ArrayList<String>();
	
	// Max and min for slider domain
	private double max;
	private double min;
	
	public String getPropertyVarName()
	{
		return propertyVarName;
	}
	public void setPropertyVarName(String propertyVarName)
	{
		this.propertyVarName = propertyVarName;
	}
	
	public String getPropertyTypeString()
	{
		return propertyVarType.toString();
	}
	
	public ReturnType getPropertyType()
	{
		return propertyVarType;
	}
	
	public void setPropertyType(ReturnType type)
	{
		this.propertyVarType = type;
	}
	
	public void setNameStatus(NameStatus status)
	{
		this.nameStatus = status;
	}
	
	public void setDesStatus(DesStatus status)
	{
		this.desStatus = status;		
	}
	
	public void setHiddenStatus(HiddenStatus status)
	{
		this.hiddenStatus = status;	
	}
	
	public void setDomainStatus(DomainStatus status)
	{
		this.domainStatus = status;	
	}
	
	public String getHiddenStatusString()
	{
		if(hiddenStatus == HiddenStatus.TRUE)
			return "true";
		else if (hiddenStatus == HiddenStatus.FALSE)
			return "false";
		else
			return "";
	}
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	
	public void setDes(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public void setMinValue(String text)
	{
		min = Double.parseDouble(text);
	}
	
	public void setMaxValue(String text)
	{
		max = Double.parseDouble(text);
	}
	
	public void setOptionsString(String text)
	{
		String[] strs = text.split(",");
		for(int i = 0; i< strs.length;++i)
			options.add(strs[i].trim());
	}
	
	public String getDomainDescription()
	{
		if (domainStatus == DomainStatus.SLIDER)
			return "Slider (" + min + " -- " + max + ")";
		else if (domainStatus == DomainStatus.MENU)
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("Menu(");
			buffer.append(getMenuOptionsString());
			buffer.append(")");
			return buffer.toString();
		}
		else
			return "";
	}
	
	public String getMenuOptionsString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(options.get(0));
		for (int i = 1;i<options.size();++i)
			buffer.append("," + options.get(i));
		return buffer.toString();
	}
	
	public NameStatus getNameStatus()
	{
		return nameStatus;
	}
	public DesStatus getDesStatus()
	{
		return desStatus;
	}
	public HiddenStatus getHiddenStatus()
	{
		return hiddenStatus;
	}
	public DomainStatus getDomainStatus()
	{
		return domainStatus;
	}
	public Double getMinValue()
	{
		return min;
	}
	public Double getMaxValue()
	{
		return max;
	}
	
	
}
