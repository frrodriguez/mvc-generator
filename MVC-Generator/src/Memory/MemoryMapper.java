package Memory;

import Generator.GeneratorInstance;
import Upgrade.CampoTabla;
import Upgrade.Entidad;
import Upgrade.Principal;

public class MemoryMapper implements GeneratorInstance{

	Entidad e;
	
	@Override
	public String generate(Entidad e) {
		this.e= e;
		String toret = "" + generateFile() + "/**\n" + "* Class " + e.Name + "Mapper\n" + "*\n"
				+ "* Database interface for " + e.Name  + " entities\n" + "* \n" + "* @author fran\n" + "*/\n" + "class "
				+ e.Name  + "Mapper {\n" + "/**\n" + "* Reference to the PDO connection\n" + "* @var PDO\n" + "*/\n"
				+ "private $db;\n" 
				+ generateMapperContructor() 
				+ generateFindAll() 
				+ generateFindByKey()
				+ generateSave() 
				+ generateUpdate() 
				+ generateDelete();
		
		if(e.Name.toLowerCase().equals("usuario"))
		{
			toret += generateIsValid();
			toret += generateExists();
		}
				toret+= "}\n";

		return toret;
	}
	
	private String generateFile() {
		return "<?php\n" + "// file: model/" + e.Name + "Mapper.php\n"
				+ "require_once(__DIR__.\"/../core/PDOConnection.php\");\n"
				+ "require_once(__DIR__.\"/../model/Usuario.php\");\n" + "require_once(__DIR__.\"/../model/" + e.Name
				+ ".php\");\n";
	}

	private String generateExists() {
		String toret = "/**\n"
				   +"* Checks if a given username is already in the database\n"
				   +"* \n"
				   +"* @param string $username the username to check\n"
				   +"* @return boolean true if the username exists, false otherwise\n"
				   +"*/\n"
				  +"public function usernameExists($username) {\n"
				   +" $stmt = $this->db->prepare(\"SELECT count(username) FROM usuario where login=?\");\n"
						   +"$stmt->execute(array($username));\n"
				    
				    +"if ($stmt->fetchColumn() > 0) {   \n"
				    +"  return true;\n"
				    +"} \n"
				    +"}\n";
										return toret;
	}

	private String generateIsValid() {
		String toret = "/**\n"
				   +"* Checks if a given pair of username/password exists in the database\n"
				   +"* \n"
				   +"* @param string $username the username\n"
				   +"* @param string $passwd the password\n"
				   +"* @return boolean true the username/passwrod exists, false otherwise.\n"
						   +"*/\n"
				 +" public function isValidUser($username, $passwd) {\n"
				    +"$stmt = $this->db->prepare(\"SELECT count(login) FROM usuario where login=? and pass=?\");\n"
				    		+"$stmt->execute(array($username, $passwd));\n"
				    
				    +"if ($stmt->fetchColumn() > 0) {\n"
				     +" return true;  \n"      
				     +"}\n"
				     +" }\n";
						return toret;
	}

	private String generateDelete() {
		String QUERY = "DELETE * FROM " + e.Name;
		QUERY += " WHERE " + generateIdentifierWhere() + "";

		String toret = "/**\n" + "* Deletes a " + e.Name + " into the database\n" + "* \n" + "* @param " + e.Name + " $"
				+ e.Name + " The " + e.Name + " to be deleted\n" + "* @throws PDOException if a database error occurs\n"
				+ "* @return void\n" + "*/\n" + "public function delete(" + Principal.toUpper(e.Name) + " $" + e.Name + ") {\n"
				+ "$stmt = $this->db->prepare(\"" + QUERY + "\");\n" + "$stmt->execute(array($"
				+ generateIdentifierGetter(e.Name) + "));\n" + "}\n";
		return toret;
	}

	private String generateIdentifierWhere() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getKey()) {
				toret +=  e.campos.get(i).getName() + " = ? AND ";
			}
		}
		return toret.substring(0, toret.length() - 4);
	}

	private String generateIdentifierGetter(String name) {
		String toret = "";
		for (int i = 0; i <  e.campos.size(); i++) {
			if (e.campos.get(i).getKey()) {
				toret += "$" + name + "->get" + Principal.toUpper(e.campos.get(i).getName()) + "(), ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}

	private String generateUpdate() {
		boolean ret = true;
		for (CampoTabla val : e.campos) {
			if (!val.getKey()) {
				ret = false;
			}
		}
		if (ret)
			return "";

		String QUERY = "UPDATE " + e.Name + " set " + generateAttribsNoKeyToUpdate() + " WHERE "
				+ generateIdentifierWhere() + "; ";
		String toret = "/**\n" + "* Updates a " + e.Name + " in the database\n" + "* \n" + "* @param " + e.Name + " $"
				+ e.Name + " The " + e.Name + " to be updated\n" + "* @throws PDOException if a database error occurs\n"
				+ "* @return void\n" + "*/     \n" + "public function update(" + Principal.toUpper(e.Name) + " $" + e.Name + ") {\n"
				+ "$stmt = $this->db->prepare(\"" + QUERY + "\");\n" + "$stmt->execute(array("
				+ generateAttribsGettersToUpdate(e.Name) + ", " + generateKeyParameters() + "));  \n" + "}\n";

		return toret;
	}

	private String generateAttribsGettersToUpdate(String name) {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			if (!e.campos.get(i).getKey()) {
				toret += "$" + name + "->get" + Principal.toUpper(e.campos.get(i).getName()) + "(), ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}

	private String generateAttribsNoKeyToUpdate() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			if (!e.campos.get(i).getKey()) {
				toret += "$" +  e.campos.get(i).getName() + "=?, ";
			}
		}
		return toret;
	}
	
	private String generateKeyParameters() {
		String toret = "";
		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getKey()) {
				toret += "$" + e.campos.get(i).getName() + ", ";
			}
		}
		return toret.substring(0, toret.length() - 2);
	}
	
	private String generateSave() {
		String QUERY = "INTSERT INTO " + Principal.toUpper(e.Name) + " (" + e.generateAllAttribsInsert() + ") values("
				+ e.generateAllAttribsParamsInsert() + "); ";

		String toret = "/**\n" + "* Saves a "+e.Name+" into the database\n" + "* \n"
				+ "* @param "+Principal.toUpper(e.Name)+" $"+e.Name+" The "+e.Name+" to be saved\n" + "* @throws PDOException if a database error occurs\n"
				+ ((e.hasAutoIncrement()) ? "* @return int The new " + e.Name + " autogenerated value for autoincrement\n"
						: "")
				+ "*/\n" + "public function save(" + Principal.toUpper(e.Name) + " $" + e.Name + ") {\n" + "$stmt = $this->db->prepare(\""
				+ QUERY + "\");\n" + "$stmt->execute(array(" + e.generateAllAttribsGetter(e.Name) + "));\n"
				+ ((e.hasAutoIncrement()) ? "return $this->db->lastInsertId();\n" : "") + "}\n";

		return toret;
	}


	private String generateMapperContructor() {
		return " public function __construct() {\n" + "$this->db = PDOConnection::getInstance();\n" + "}\n";
	}


	private String generateFindAll() {
		String SELECT = "SELECT * FROM " + Principal.toUpper(e.Name);

		String toret = "/**\n" + "* Retrieves all " + e.Name + "\n" + "*\n"
				+ "* @throws PDOException if a database error occurs\n" 
				+ "* @return mixed Array of " + Principal.toUpper(e.Name)
				+ " instances\n" 
				+ "*/  \n" 
				+ " public function findAll() {   \n" 
				+ "$stmt = $this->db->query(\""+ SELECT + "\");    \n" 
				+ "$" + e.Name + "_db = $stmt->fetchAll(PDO::FETCH_ASSOC);\n" 
				+ "$" + e.Name + "List = array();\n" 
				+ "foreach ($" + e.Name + "_db as $" + e.Name + "Item) {\n" 
				+ "$" + e.Name + "Obj = "+ e.generateNewStatementExternal(e.Name+"Item") + ";\n";
				// TODO añadir objetos y clases externos
		toret += "array_push($" + e.Name + "List, $" + e.Name + "Obj);\n" 
				+ "}   \n" 
				+ "return $"+e.Name+"List;\n" 
				+ "}\n";
		for (Entidad rel : e.relaciones)
		{
			SELECT = "SELECT * FROM " + Principal.toUpper(e.Name) +" WHERE ("+rel.generateIdentifierWhere()+")";
			
			toret += "/**\n" + "* Retrieves all " + e.Name + " with "+Principal.toUpper(e.Name)+" key\n" 
					+ "*\n"
					+ "* @throws PDOException if a database error occurs\n" 
					+ "* @return mixed Array of " + Principal.toUpper(e.Name)
					+ " instances\n" 
					+ "*/  \n" 
					+ " public function findAllWith"+Principal.toUpper(rel.Name)+"Key("+rel.generateKeyParameters()+") {   \n" 
					+ "$stmt = $this->db->prepare(\""+ SELECT + "\");\n" 
					+ "$stmt->execute(array(" + rel.generateKeyParameters() + "));\n" 
					+ "$" + e.Name + "_db = $stmt->fetchAll(PDO::FETCH_ASSOC);\n" 
					+ "$" + e.Name + "List = array();\n" 
					+ "foreach ($" + e.Name + "_db as $" + e.Name + "Item) {\n" 
					+ "$" + e.Name + "Obj = "+ e.generateNewStatementExternal(e.Name+"Item") + ";\n";
					// TODO añadir objetos y clases externos
			toret += "array_push($" + e.Name + "List, $" + e.Name + "Obj);\n" 
					+ "}   \n" 
					+ "return $"+e.Name+"List;\n" 
					+ "}\n";
		}
		
		return toret;
	}

	

	private String generateFindByKey() {
		// TODO Auto-generated method stub
		return "";
	}
	
	
}
