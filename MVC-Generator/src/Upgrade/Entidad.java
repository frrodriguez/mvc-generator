package Upgrade;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Generator.Generator;
import Generator.MemoryGenerator;


public class Entidad {
	
	
	public ArrayList<CampoTabla> campos;
	
	public ArrayList<Foreing> foreingKeys;
	
	public ArrayList<Entidad> relaciones;
	public ArrayList<RelacionMultiple> relacionesMultiples;
	public String Name;
	
	public Generator g = new MemoryGenerator();
	
	public Entidad(String in) throws IOException {
		campos = new ArrayList<>();
		foreingKeys = new ArrayList<>();
		relaciones = new ArrayList<>();
		relacionesMultiples = new ArrayList<>();
		Name = in.substring(0, in.indexOf("("));
		String Attributes = in.substring(in.indexOf("(")+1,in.lastIndexOf(")"));
		
		Pattern p = Pattern.compile("`(.*)`\\.`(.*)`");
		Pattern p2 = Pattern.compile("`(.*)`");
		
		Name = Name.substring(Name.indexOf("`"),Name.lastIndexOf("`")+1);
		
		Matcher m = p.matcher(Name);
		Matcher m2 = p2.matcher(Name);
		if (m.matches())
		{
			Name = m.group(2);
		}
		
		else if (m2.matches())
		{
			Name = m.group(1);
		}
		Pattern patronCampoTabla = Pattern.compile("[ ]*`(.*)`[ ]*(.*)[ ]*(NOT)?[ ]*NULL[ ]*(AUTO_INCREMENT)?[ ]*(DEFAULT)?[ ]*(NULL)?([ ]*.*[ ]*)*");
		Pattern patronPrimaryKey = Pattern.compile("[ ]*PRIMARY KEY[ ]*\\((.*)\\)[ ]*");
		Pattern patronForeignKey = Pattern.compile("[ ]*CONSTRAINT[ ]*`.*`[ ]*FOREIGN KEY[ ]*\\((.*)\\)[ ]*REFERENCES[ ]*`(.*)`\\.`(.*)`[ ]*\\((.*)\\)[ ]*.*");
		for(String att : splitIgnoringBrackets( Attributes,','))
		{
			Matcher ma = patronCampoTabla.matcher(att);
			if (ma.matches())
			{
				campos.add(new CampoTabla(ma.group(1), ma.group(2), ((ma.group(3) == null)? false : true),((ma.group(4) == null)? false : true)));
				continue;
			}
			ma = patronPrimaryKey.matcher(att);
			if (ma.matches())
			{
				for(String key : ma.group(1).split(","))
				{
					this.setKey(key.replaceAll("`", ""));
				}
				continue;
			}
			ma = patronForeignKey.matcher(att);
			if (ma.matches())
			{
				ForeignKey destKey = new ForeignKey(Principal.getEntityByName(ma.group(3)).getPrimaryKey(), this.Name);
				foreingKeys.add(new Foreing(Principal.getEntityByName(ma.group(3)).getPrimaryKey(), destKey));
				continue;
			}
		}
	}
	
	
	// TODO Revisar relaciones cuando la foreign key son dos campos
	//returns a Join sentence with the entity
	// if the entity given dont have any relations with this entity, returns null
	public String SQLSentenceFindAllEntityValuesRelationated(Entidad e)
	{
		
		for(Entidad ent : relaciones)
		{
			if (ent.equals(e))
			{
				return null;
			}
			
			
		}
		for(RelacionMultiple ent : relacionesMultiples)
		{
			if (ent.destino.equals(e))
			{
				//Relacionado directamente
				if (ent.intermedia == null)
				{
				
				}
				//Relacionado indirectamente
				else
				{
					return null;
				}
			}
		}
		return null;
	}
	
	//Return false if all of colums of this entity are foreign keys
	// Normally, if entity has no usefull colums, this means that entity its a multivalued intermediate table
	public boolean hasUsefullColums(){
		int sumForaneas = 0;
			for(Foreing f :foreingKeys)
			{
				sumForaneas += f.keyExterna.campos.size();
			}
			return !(sumForaneas == campos.size());
	}
	
	public PrimaryKey getPrimaryKey()
	{
		PrimaryKey key = new PrimaryKey(this.Name);
		for(CampoTabla ct : campos)
		{
			if (ct.getKey())
				key.addColumn(ct);
		}
		return key;
	}
	
	//Splits a given string by char. This ignore the char if apears bettewn () -> CHAR SPLITS '(' CHAR IGNORED HERE')' CHAR SPLITS
	private ArrayList<String> splitIgnoringBrackets(String attributes, char string) {
		ArrayList<String> toret = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		boolean open = false;
		
		for(int i = 0; i < attributes.length();i++)
		{
			if(open)
			{
				if (attributes.charAt(i) == ')')
					open = false;
				
			}
			else
			{
				if (attributes.charAt(i) == '(')
					open = true;
				
				if (attributes.charAt(i) == string)
				{
					toret.add(sb.toString());
					sb.setLength(0);
					continue;
				}
			}
			sb.append(attributes.charAt(i));
		}
		toret.add(sb.toString());
		return toret;
	}

	private CampoTabla getCampoTabla(String nombre) {
		for(CampoTabla c : campos)
		{
			if (c.getName().equals(nombre))
				return c;
		}
		return null;
	}

	private void setKey(String key) {
		for(CampoTabla c : campos)
		{
			if (c.name.equals(key))
				c.setKey(true);
		}
	}
	
	//returns [tablename].[column1], [tablename].[column2], .. , [tablename].[column],  
	public String getColumsWithNameTable(boolean ignoreForeaneas)
	{
		StringBuilder sb= new StringBuilder();
		
		for(CampoTabla ct : campos)
		{
			boolean esForanea = false;
			for(Foreing f :foreingKeys)
			{
				for(CampoTabla ctf : f.keyExterna.campos)
					if (ct.equals(ctf))
						esForanea = true;
			}
			
			if (ignoreForeaneas)
			{
				if(!esForanea)
					sb.append(this.Name+"."+ct.name+", ");
			}
			else
				sb.append(this.Name+"."+ct.name+", ");
		}
		sb.setLength(sb.length()-2);
		return sb.toString();
	}
	
	//If one table has 2 foreing keys, normally one foreign key table links with the other foreign key table
	public String getOtherEntityRelacionated(String oneTable)
	{
		for (int i = 0; i < foreingKeys.size(); i++)
		{
			if (foreingKeys.get(i).keyDestino.nombreTabla.equals(oneTable))
			{
				if (i == 0)
				{
					if (foreingKeys.size() == 1)
						return null;
					else
						return foreingKeys.get(i+1).keyDestino.nombreTabla;
				}
				else
				{
					return foreingKeys.get(i-1).keyDestino.nombreTabla;
				}
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		return this.Name + " " + campos.toString() + " " + foreingKeys.toString();
	}


	public String getName()
	{
		return this.Name;
	}
	
	public String generateMapper() {
		return g.generateMapper(this);
	}


	public String generateClass() {
		return g.generateClass(this);
	}


	public String generateController() {
		return g.generateController(this);
	}
	
	public String generateEditForController() {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			if (this.campos.get(i).getKey()) {
				toret += "$_REQUEST[\"" + this.campos.get(i).getName() + "\"], ";
			}
		}
		return toret.substring(0, toret.length() - 2);

	}
	
	public String generateIdentifierOther(Entidad eOne, String varName) {
		ArrayList<CampoTabla> atributesOne =eOne.campos;
		String toret = "";
		
		for(int i = 0; i < this.campos.size(); i++)
		{
			if (this.campos.get(i).getKey())
			{
				if (!atributesOne.contains(this.campos.get(i).getName()))
				{
					toret += "$"+varName+"[\""+this.campos.get(i).getName()+"\"], ";
				}
			}
		}
		
		//if (toret.length() < 2)
		//	return "";
		
		return toret.substring(0, toret.length()-2);
	}

	
	public String generateIndexForController() {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			if (this.campos.get(i).getKey()) {
				toret += "$_GET[\"" + this.campos.get(i).getName() + "\"], ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}
	
	public  String generateAllAttribsGetter(String name) {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			toret += "$" + name + "->get" + Principal.toUpper(this.campos.get(i).getName()) + "(), ";
		}
		return toret.substring(0, toret.length() - 2);
	}

	public String generateAllAttribsParamsInsert() {
		String toret = "";
		for (int i = 0; i <  this.campos.size(); i++) {
			if (!this.campos.get(i).getAutoIncrement())
				toret += "?, ";
		}
		return toret.substring(0, toret.length() - 2);
	}

	public String generateAllAttribsInsert() {
		String toret = "";
		for (int i = 0; i <  this.campos.size(); i++) {
			if (!this.campos.get(i).getAutoIncrement())
				toret += this.campos.get(i) + ", ";
		}
		return toret.substring(0, toret.length() - 2);
	}
	public String generateNewStatementExternal(String string) {
		String toret = "new " + Principal.toUpper(this.Name) + " (";
		for (int i = 0; i < this.campos.size(); i++) {
			toret += "$" + string + "[\"" + this.campos.get(i).getName() + "\"], ";
		}
		return toret.substring(0, toret.length() - 2) + ")";
	}
	public  String generateKeyParameters() {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			if (this.campos.get(i).getKey()) {
				toret += "$" + this.campos.get(i).getName() + ", ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}
	
	public boolean hasAutoIncrement() {
		for (CampoTabla ct : campos)
		{
			if (ct.AutoIncrement)
				return true;
		}
		return false;
	}


	public String generateIdentifierWhere() {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			if (this.campos.get(i).getKey()) {
				toret += this.campos.get(i).getName() + " = ? AND ";
			}
		}
		return toret.substring(0, toret.length() - 4);
	}
	

	public String generateIdentifierGetter(String name2) {
		String toret = "";
		for (int i = 0; i < this.campos.size(); i++) {
			if (campos.get(i).getKey()) {
				toret += "$" + name2 + "->get" + Principal.toUpper(this.campos.get(i).getName()) + "(), ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}
	
	public String generateViewAdd()
	{
		String toret = "<?php \n"
		 +"//file: view/"+this.Name+"/add.php\n"
		 +"require_once(__DIR__.\"/../../core/ViewManager.php\");\n"
		 +"$view = ViewManager::getInstance();\n"
		 
		 +"$"+this.Name+" = $view->getVariable(\""+this.Name+"\");\n";
		for (RelacionMultiple rel : this.relacionesMultiples)
		{
			toret += "$"+rel.destino+"List = $view->getVariable(\""+rel.destino.getName()+"List\");\n";
		}
		 toret +=  "$currentuser = $view->getVariable(\"currentuserthis.Name\");\n"
			 		+ "$errors = $view->getVariable(\"errors\");\n"
		 
		+"$view->setVariable(\"title\", \"Create "+this.Name+"\");\n"
		 
		+"?><h1><?= i18n(\"Create "+this.Name+"\")?></h1>\n";
		return toret;
	}
	public String generateViewEdit()
	{
		String toret = "<?php \n"
				 +"//file: view/"+this.Name+"/edit.php\n"
				 +"require_once(__DIR__.\"/../../core/ViewManager.php\");\n"
				 +"$view = ViewManager::getInstance();\n"
				 
				 +"$"+this.Name+" = $view->getVariable(\""+this.Name+"\");\n";
				for (RelacionMultiple rel  : this.relacionesMultiples)
				{
					toret += "$"+rel.destino.getName()+"List = $view->getVariable(\""+rel.destino.getName()+"List\");\n";
				}
				 toret +=  "$currentuser = $view->getVariable(\"currentuserthis.Name\");\n"
					 		+ "$errors = $view->getVariable(\"errors\");\n"
				 
				+"$view->setVariable(\"title\", \"Edit "+this.Name+"\");\n"
				 
				+"?><h1><?= i18n(\"Modify "+this.Name+"\")?></h1>\n";
				return toret;
	}
	public String generateViewIndex()
	{
		String toret = "<?php \n"
				 +"//file: view/"+this.Name+"/index.php\n"
				 +"require_once(__DIR__.\"/../../core/ViewManager.php\");\n"
				 +"$view = ViewManager::getInstance();\n"
				 
				 +"$"+this.Name+"List = $view->getVariable(\""+this.Name+"List\");\n";
				 toret +=  "$currentuser = $view->getVariable(\"currentuserthis.Name\");\n"
				 		+ "$errors = $view->getVariable(\"errors\");\n"
				 
				+"$view->setVariable(\"title\", \"Edit "+this.Name+"\");\n"
				 
				+"?><h1><?= i18n(\"Create "+this.Name+"\")?></h1>\n";
				return toret;
	}
	public String generateViewView()
	{
		String toret = "<?php \n"
				 +"//file: view/"+this.Name+"/view.php\n"
				 +"require_once(__DIR__.\"/../../core/ViewManager.php\");\n"
				 +"$view = ViewManager::getInstance();\n"
				 
				 +"$"+this.Name+" = $view->getVariable(\""+this.Name+"\");\n";
				for (RelacionMultiple rel : this.relacionesMultiples)
				{
					toret += "$"+rel.destino.getName()+"List = $view->getVariable(\""+rel.destino.getName()+"List\");\n";
				}
				 toret +=  "$currentuser = $view->getVariable(\"currentuserthis.Name\");\n"
					 		+ "$errors = $view->getVariable(\"errors\");\n"
				 
				+"$view->setVariable(\"title\", \"Edit "+this.Name+"\");\n"
				 
				+"?><h1><?= i18n(\"Create "+this.Name+"\")?></h1>\n";
				return toret;
	}

}
