package edu.gmu.cs.mason.wizards.model;


import org.eclipse.swt.graphics.RGB;





public class AgentInformation {
	
	
	public enum SimplePortrayal
	{
		None,
		OvalPortrayal2D,
		RectanglePortrayal2D
	}
	
	
	
	private String agentName;
	private FieldInformation field;
	private int agentNumber;
	private RGB color;
	private boolean movable;
	private boolean oriented;
	private String label;
	private SimplePortrayal portrayal;
	private String packageName;
	
	

	public AgentInformation()
	{

		color = new RGB(255,255,0);
		movable = false;
		oriented = false;
		portrayal = SimplePortrayal.None;
		label = null;
	}
	

	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	
	public FieldInformation getField() {
		return field;
	}

	public void setField(FieldInformation field) {
		this.field = field;
	}

	public int getAgentNumber() {
		return agentNumber;
	}

	public void setAgentNumber(int agentNumber) {
		this.agentNumber = agentNumber;
	}

	public void setAgentNumber(String numText) {
		int num = Integer.parseInt(numText);
		this.agentNumber = num;
	}
	
	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	


	public RGB getColor() {
		return color;
	}


	public void setColor(RGB color) {
		this.color = color;
	}


	public boolean isMovable() {
		return movable;
	}


	public void setMovable(boolean movable) {
		this.movable = movable;
	}


	public boolean isOriented() {
		return oriented;
	}


	public void setOriented(boolean oriented) {
		this.oriented = oriented;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public SimplePortrayal getPortrayal() {
		return portrayal;
	}


	public void setPortrayal(SimplePortrayal portrayal) {
		this.portrayal = portrayal;
	}

	
	
	
}
