package Memory;

import Generator.GeneratorInstance;
import Upgrade.Entidad;
import Upgrade.Principal;
import Upgrade.RelacionMultiple;

public class MemoryModel implements GeneratorInstance{

	Entidad e;
	@Override
	public String generate(Entidad e) {
		this.e = e;
		String toret = "<?php\n" + "// file: model/" + e.getName() + ".php\n"
				+ "require_once(__DIR__.\"/../core/ValidationException.php\");\n" 
				+ "/**\n" 
				+ " * Class " + Principal.toUpper(e.getName()) + "\n"
				+ "* \n" + "* @author fran \n" 
				+ "*/\n" + "class " + Principal.toUpper(e.getName()) + " {\n"
				+ generarAtributos() 
				+ " /**\n" 
				+ " * The constructor\n" 
				+ " * \n" 
				+ generateParams() 
				+ "*/  \n"
				+ generarConstructor() 
				+ generateGetSet() 
				+ generateCheckers() 
				+ "}";
		return toret;
	}

	private String generateParams() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			String introducir = "* @param " + (e.campos.get(i)).getType()+ " $" + (e.campos.get(i)).getName() + " The " + (e.campos.get(i)).getName()
					+ " of the " + e.Name + "\n";
			toret += introducir;

		}
		for (Entidad rel : e.relaciones) {
			String introducir = "* @param " + rel + " $" + rel.Name.toLowerCase() + " The " + rel.Name.toLowerCase() + " of the " + e.Name
					+ "\n";
			toret += introducir;
		}
		for (RelacionMultiple rel : e.relacionesMultiples) {

			String introducir = "* @param mixed $" + rel.destino.Name.toLowerCase() + "List The list of " + rel.destino.Name.toLowerCase() + " of the "
					+ e.Name + "\n";
			toret += introducir;
		}
		return toret;
	}

	private String generateCheckers() {
		String create = "/**\n" + "* Checks if the current instance is valid\n"
				+ "* for being updated in the database.\n" + "* \n"
				+ "* @throws ValidationException if the instance is\n" + "* not valid\n" + "* \n" + "* @return void\n"
				+ "*/\n" + "public function checkIsValidForCreate() {\n" + "$errors = array();\n";

		String update = "/**\n" + "* Checks if the current instance is valid\n"
				+ "* for being updated in the database.\n" + "* \n"
				+ "* @throws ValidationException if the instance is\n" + "* not valid\n" + "* \n" + "* @return void\n"
				+ "*/\n" + "public function checkIsValidForUpdate() {\n" + "$errors = array();\n";

		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getObligatory()) {
				create += "if (strlen(trim($this->" + e.campos.get(i).getName() + ")) == 0 ) {\n" + "$errors[\"" + e.campos.get(i).getName()+ "\"] = \""
						+ e.campos.get(i).getName() + " is mandatory\";\n" + "}\n";
				if (e.campos.get(i).getKey()) {
					update += "if (!isset($this->" + e.campos.get(i).getName() + ")) {\n" + "$errors[\"" + e.campos.get(i).getName() + "\"] = \"" + e.campos.get(i).getName()
							+ " is mandatory\";\n" + "}\n";
				}
			}
		}
		create += "if (sizeof($errors) > 0){\n" + "throw new ValidationException($errors, i18n(\"" + e.Name
				+ " is not valid\"));\n" + "}\n}\n";
		update += "try{\n" + "$this->checkIsValidForCreate();\n" + "}catch(ValidationException $ex) {\n"
				+ "foreach ($ex->getErrors() as $key=>$error) {\n" + "$errors[$key] = $error;\n" + "}\n" + "}\n"
				+ "if (sizeof($errors) > 0) {\n" + " throw new ValidationException($errors, i18n(\"" + e.Name
				+ " is not valid\"));\n" + "}\n}";
		return create + update;
	}

	private String generateGetSet() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			String setter = "/**\n" + "* Sets the " + e.campos.get(i).getName() + " of this " + e.Name + "\n" + "* \n"
					+ "* @param " + (e.campos.get(i).getType()).replace("*", "") + " $" + e.campos.get(i).getName() + " the "
					+ e.campos.get(i).getName() + " of this " + e.Name + "\n" + "* @return void\n" + "*/\n"
					+ "public function set" + Principal.toUpper(e.campos.get(i).getName()) + "(" + (e.campos.get(i).getType()).replace("*", "").replaceAll("string", "")
					+ " $" + e.campos.get(i).getName() + ") {\n" + " $this->" + e.campos.get(i).getName() + " = $"
					+ e.campos.get(i).getName() + ";\n" + "}\n\n";
			String getter = "/**\n" + " * Gets the " + e.campos.get(i).getName() + " of this " + e.Name + "\n" + "* \n"
					+ "* @return " + (e.campos.get(i).getType()).replace("*", "") + " The " + e.campos.get(i).getName() + " of this "
					+ e.Name + "\n" + "*/\n" + "public function get" + Principal.toUpper(e.campos.get(i).getName()) + "() {\n"
					+ "return $this->" + e.campos.get(i).getName() + ";\n" + "}\n\n";
			toret += getter + setter;
		}
		for (Entidad rel : e.relaciones) {
			String setter = "/**\n" + "* Sets the " + Principal.toLower(rel.getName()) + " of this " + e.Name + "\n" + "* \n" + "* @param "
					+ rel + " $" + Principal.toLower(rel.getName()) + " the " + Principal.toLower(rel.getName()) + " of this " + e.Name + "\n"
					+ "* @return void\n" + "*/\n" + "public function set" + Principal.toUpper(rel.getName()) + "(" + Principal.toUpper(rel.getName()) + " $" + Principal.toLower(rel.getName())
					+ ") {\n" + " $this->" + Principal.toLower(rel.getName()) + " = $" + Principal.toLower(rel.getName()) + ";\n" + "}\n\n";
			String getter = "/**\n" + " * Gets the " + Principal.toLower(rel.getName()) + " of this " + e.Name + "\n" + "* \n" + "* @return "
					+ rel + " The " + Principal.toLower(rel.getName()) + " of this " + e.Name + "\n" + "*/\n" 
					+ "public function get" + Principal.toUpper(rel.getName())
					+ "() {\n" + "return $this->" + Principal.toLower(rel.getName()) + ";\n" + "}\n\n";
			toret += getter + setter;
		}
		for (RelacionMultiple rel : e.relacionesMultiples) {
			String add = "";
			if (rel.intermedia!= null)
			{
				add = rel.intermedia.getName();
			}
			String setter = "/**\n" + "* Sets the list of " + Principal.toLower(rel.destino.getName()) + " of this " + e.Name + "\n" + "* \n"
					+ "* @param mixed $" + Principal.toLower(rel.destino.getName()) + "List The list of " + Principal.toLower(rel.destino.getName()) + " of this " + e.Name + "\n"
					+ "* @return void\n" + "*/\n" 
					+ "public function set" + Principal.toUpper(rel.destino.getName()) + add+"List(array $" + Principal.toLower(rel.destino.getName())
					+ "List) {\n" + " $this->" + Principal.toLower(rel.destino.getName()) + "List = $" + Principal.toLower(rel.destino.getName()) + "List;\n" + "}\n\n";
			String getter = "/**\n" + " * Gets the list of " + Principal.toLower(rel.destino.getName()) + " of this " + e.Name + "\n" + "* \n"
					+ "* @return mixed The list of " + Principal.toLower(rel.destino.getName()) + " of this " + e.Name + "\n" + "*/\n"
					+ "public function get" + Principal.toUpper(rel.destino.getName()) + add+"List() {\n" + "return $this->" + Principal.toLower(rel.destino.getName()) + "List;\n"
					+ "}\n\n";
			toret += getter + setter;
		}
		return toret;
	}

	private String generarConstructor() {
		String introducir = "public function __construct(";
		String asignaciones = "";
		for (int i = 0; i < e.campos.size(); i++) {
			introducir += "$" + e.campos.get(i).getName() + "=NULL, ";
			asignaciones += "$this->" + e.campos.get(i).getName() + "=$" + e.campos.get(i).getName() + ";\n";

		}
		for (Entidad rel : e.relaciones) {
			introducir += "$" + Principal.toLower(rel) + "=NULL, ";
			asignaciones += "$this->" + Principal.toLower(rel) + "=$" + Principal.toLower(rel) + ";\n";
		}
		for (RelacionMultiple rel : e.relacionesMultiples) {

			String add = "";
			if (rel.intermedia!= null)
			{
				add = rel.intermedia.getName();
			}
			introducir += "$" + Principal.toLower(rel.destino) +add+ "List=NULL, ";
			asignaciones += "$this->" + Principal.toLower(rel.destino) +add+ "=$" + Principal.toLower(rel.destino) + add+"List;\n";
		}
		introducir = introducir.substring(0, introducir.length() - 2) + "){\n";
		asignaciones += "}\n";
		return introducir + asignaciones;
	}

	private String generarAtributos() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			String introducir = 
					"/** The " + e.campos.get(i).getName() + " of this " + e.Name + "\n" 
					+ "* @var " + (e.campos.get(i).getType()).replace("*", "") + "\n"
					+ "*/\n" + "private $" +  e.campos.get(i).getName()+ ";\n";
			toret += introducir;

		}
		for (Entidad rel : e.relaciones) {
			String introducir = 
					"/** The " + Principal.toLower(rel) + " of this " + e.Name 
					+ "\n" + "* @var " + rel + "\n" 
					+ "*/\n"
					+ "private $" + Principal.toLower(rel) + ";\n";
			toret += introducir;
		}
		for (RelacionMultiple rel : e.relacionesMultiples) {

			String add = "";
			if (rel.intermedia!= null)
			{
				add = rel.intermedia.getName();
			}
			String introducir = 
					"/** The list of " + Principal.toLower(rel.destino) + add+" of this " + e.Name 
					+ "\n" + "* @var mixed\n"
					+ "*/\n" 
					+ "private $" + Principal.toLower(rel.destino) + add+"List;\n";
			toret += introducir;
		}
		return toret;
		
	}
	
	

}
