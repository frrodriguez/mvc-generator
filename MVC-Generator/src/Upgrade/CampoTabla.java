package Upgrade;

public class CampoTabla {
	String name;
	
	boolean Key;
	
	boolean Obligatory;
	
	String Type;
	
	boolean AutoIncrement;
	
	public CampoTabla(String name, String type, boolean Obligatory, boolean auto)
	{
		this.name = name;
		this.Obligatory = Obligatory;
		if (type.contains("INT"))
		{
			this.Type = "int";
		}
		else 
		{
			this.Type = "string";
		}
		this.AutoIncrement = auto;
	}
	
	public boolean getKey()
	{
		return Key;
	}
	
	public void setKey(boolean key)
	{
		this.Key = key;
	}
	
	public boolean getObligatory()
	{
		return (Key)? true : Obligatory;
	}
	
	public String getType(){
		return Type;
	}
	
	public boolean getAutoIncrement()
	{
		return AutoIncrement;
	}
	
	public String getName(){
		return name;
	}
	@Override
	public String toString() {
		return name +":" +Type+ " " + ((Key)? "CLAVE": ((Obligatory)? "NOT NULL": "NULL"));
	}
}
