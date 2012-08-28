package main;

public class MenuSlider {
	
	public String name;
	public int minval;
	public int maxval;
	public int addfac;
	private int value;
	public String option;
	public boolean bool;
	
	public MenuSlider(String name, int minval, int maxval, int value, int addfac, String option, boolean bool) {
		this.name = name;
		this.minval = minval;
		this.maxval = maxval;
		this.value = value;
		this.addfac = addfac;
		this.option = option;
		this.bool = bool;
	}
	
	public MenuSlider(String name, String option, boolean bool) {
		this(name, 0, 100, !bool?(int)(Options.getAsFloat(option)*100):(Options.getAsBoolean(option)?100:0), 10, option, bool);
	}
	
	public MenuSlider(String name, int minval, int maxval, int addfac, String option, boolean bool) {
		this(name, minval, maxval, !bool?(int)(Options.getAsFloat(option)*100):(Options.getAsBoolean(option)?maxval:minval), addfac, option, bool);
	}
	
	public void add() {
		value += addfac;
		if (value > maxval) value = maxval;
		if (value < minval) value = minval;
		
		if (option != "") {
			if (bool) {
				Options.set(option, (value==maxval)+"");
			} else {
				Options.set(option, (value/100f)+"");
			}
		}
	}
	
	public void subtract() {
		value -= addfac;
		if (value > maxval) value = maxval;
		if (value < minval) value = minval;
		
		if (option != "") {
			if (bool) {
				Options.set(option, (value==maxval)+"");
			} else {
				Options.set(option, (value/100f)+"");
			}
		}
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public String getMenuValue() {
		return name+": "+(value)+"%";
	}
	
	public String getMenuBoolean() {
		return name+": "+((value==maxval)?"Yes":"No");
	}
	
}
