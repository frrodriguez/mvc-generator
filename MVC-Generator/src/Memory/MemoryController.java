package Memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import Generator.GeneratorInstance;
import Upgrade.Entidad;
import Upgrade.Principal;
import Upgrade.RelacionMultiple;

public class MemoryController implements GeneratorInstance{

	
    Entidad e;
	@Override
	public String generate(Entidad e) {
		this.e = e;
		String toret = "<?php\n" + "// file: controller/" + Principal.toUpper(e.Name) + "Controller.php\n"
				+ "require_once(__DIR__.\"/../model/" + Principal.toUpper(e.Name) + ".php\");\n" 
				+ "require_once(__DIR__.\"/../model/" + Principal.toUpper(e.Name) + "Mapper.php\");\n";
		ArrayList<String> yaAnadidas = new ArrayList<>();
				for (Entidad rel : e.relaciones)
				{
					if (!yaAnadidas.contains(rel.getName()))
					{
						toret +=  "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.Name) + ".php\");\n" 
								+ "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.Name) + "Mapper.php\");\n";
						yaAnadidas.add(rel.getName());
					}
				}
				for (RelacionMultiple rel : e.relacionesMultiples)
				{
					if (!yaAnadidas.contains(rel.destino.getName()))
					{
						toret +=  "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.destino) + ".php\");\n" 
								+ "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.destino) + "Mapper.php\");\n";
						yaAnadidas.add(rel.destino.getName());
					}
					if (rel.intermedia != null)
					if (!yaAnadidas.contains(rel.intermedia.getName()))
					{
						toret +=  "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.intermedia.getName()) + ".php\");\n" 
								+ "require_once(__DIR__.\"/../model/" + Principal.toUpper(rel.intermedia.getName()) + "Mapper.php\");\n";
						yaAnadidas.add(rel.intermedia.getName());
					}
					
				}
				if (!yaAnadidas.contains("usuario"))
				{
					toret +=  "require_once(__DIR__.\"/../model/Usuario.php\");\n";
				}
				
				toret += "require_once(__DIR__.\"/../core/ViewManager.php\");\n"
				+ "require_once(__DIR__.\"/../controller/BaseController.php\");\n" + "/**\n" + " * Class " + Principal.toUpper(e.Name)
				+ "Controller\n" + "* \n" + "* Controller to make a CRUDL of " + Principal.toUpper(e.Name) + " entities\n" + "* \n"
				+ "* @author fran \n" + "*/\n" 
				+ "class " + Principal.toUpper(e.Name) + "Controller extends BaseController {\n";
				toret +=  "/**\n"
				+ "* Reference to the " + Principal.toUpper(e.Name) + "Mapper to interact\n" 
				+ "* with the database\n" 
				+"* \n" + "* @var "+ Principal.toUpper(e.Name) + "Mapper\n" 
				+ "*/\n" + "private $" + Principal.toLower(e.Name) + "Mapper; \n" ;
		for (String rel : yaAnadidas)
		{
				toret +=  "/**\n"
				+ "* Reference to the " + Principal.toUpper(rel) + "Mapper to interact\n" 
				+ "* with the database\n" 
				+"* \n" + "* @var "+ Principal.toUpper(rel) + "Mapper\n" 
				+ "*/\n" + "private $" + Principal.toLower(rel) + "Mapper; \n" ;
		}
		
				toret +=  " /**\n" + " * The constructor\n"
				+ "*/  \n" + "public function __construct() { \n" + "parent::__construct();\n"
				+ "$this->" + Principal.toLower(e.Name) + "Mapper = new " + Principal.toUpper(e.Name) + "Mapper(); \n";
				for (String rel : yaAnadidas)
				{
					toret += "$this->" + Principal.toLower(rel) + "Mapper = new " + Principal.toUpper(rel) + "Mapper(); \n";
				}
				toret +=  "}\n" 
				+ generateIndexController()
				+ generateViewController() 
				+ generateAddController() 
				+ generateEditController()
				+ generateDeleteController() ;
				if(e.Name.toLowerCase().equals("usuario"))
				{
					toret += 
							generateLogin(Principal.entidades.get(0).getName())
					+ generateLogout() ;
				}
				toret += "}";
		return toret;
	}
	

	private String generateLogin(String Redirect) {
		String toret = "/**\n"
   +"* Action to login\n"
   +"* \n"
   +"* Logins a user checking its creedentials agains\n"
   +"* the database   \n"
   +"* \n"
   +"* When called via GET, it shows the login form\n"
   +"* When called via POST, it tries to login\n"
   +"* \n"
   +"* The expected HTTP parameters are:\n"
   +"* <ul>\n"
   +"* <li>login: The username (via HTTP POST)</li>\n"
   +"* <li>passwd: The password (via HTTP POST)</li>   \n"   
   +"* </ul>\n"
   +"*\n"
   +"* The views are:\n"
   +"* <ul>\n"
   +"* <li>posts/login: If this action is reached via HTTP GET (via include)</li>\n"
   +"* <li>posts/index: If login succeds (via redirect)</li>   \n"
   +"* <li>users/login: If validation fails (via include). Includes these view variables:</li>\n"
   +"* <ul>   \n"
   +"*  <li>errors: Array including validation errors</li>   \n"
   +"* </ul>  \n" 
   +"* </ul>\n"
   +"* \n"
   +"* @return void\n"
  +"*/\n"
  +"public function login() {\n"
    +"if (isset($_POST[\"username\"])){ // reaching via HTTP Post...\n"
    +" //process login form    \n"
     +" if ($this->usuarioMapper->isValidUser($_POST[\"username\"],$_POST[\"passwd\"])) {\n"
	
     +"$_SESSION[\"currentuser\"]=$_POST[\"username\"];\n"
	
     +"// send user to the restricted area (HTTP 302 code)\n"
	+"$this->view->redirect(\""+Redirect+"\", \"index\");\n"
	
	+" }else{\n"
	+"$errors = array();\n"
	+"$errors[\"general\"] = \"Username is not valid\";\n"
	+"$this->view->setVariable(\"errors\", $errors);\n"
	+"   }\n"
	+"  }       \n"
    
    +"// render the view (/view/usuario/login.php)\n"
    +"$this->view->render(\"usuario\", \"login\");\n"    
    +"}\n";
		return toret;
	}

	private String generateLogout() {
		String toret = "/**\n"
   +"* Action to logout\n"
   +"* \n"
   +"* This action should be called via GET\n"
  +" * \n"
   +"* No HTTP parameters are needed.\n"
   +"*\n"
   +"* The views are:\n"
  +" * <ul>\n"
   +"* <li>users/login (via redirect)</li>\n"
  +" * </ul>\n"
   +"*\n"
   +"* @return void\n"
   +"*/\n"
  +"public function logout() {\n"
    +"session_destroy();\n"
    
   +" // perform a redirection. More or less: \n"
   +" // header(\"Location: index.php?controller=usuario&action=login\")\n"
		   +" // die();\n"
   +" $this->view->redirect(\"usuario\", \"login\");\n"
   
   +" }\n";
		return toret;
	}

	private String generateDeleteController() {
		String toret = "/**\n" + "* Action to delete a " + e.Name + "\n" + "* \n"
				+ "* This action should only be called via HTTP POST\n" + "* The views are:\n" + "* <ul>\n"
				+ "* <li>activities/index: If activity was successfully deleted (via redirect)</li>\n" + "* </ul>\n"
				+ "* @throws Exception if no key was provided\n" + "* @return void\n" + "*/  \n"
				+ "public function delete() {\n";
		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getKey() || e.campos.get(i).getObligatory()) {
				toret += "if (!isset($_POST[\"" + e.campos.get(i).getName() + "\"])) {\n" + " throw new Exception(i18n(\"A " + e.Name + " "
						+ e.campos.get(i).getName() + " is mandatory\"));\n" + " }\n";
			}
		}
		toret += "if (!isset($this->currentUser)) {\n" + "  throw new Exception(i18n(\"Not in session. Deleting " + e.Name
				+ " requires login\"));\n" + "}\n"

				+ "// Get the " + Principal.toUpper(e.Name) + " object from the database\n" + "$" + e.Name + " = $this->" + e.Name
				+ "Mapper->findById(" + e.generateEditForController() + ");\n"

				+ "// Does the " + e.Name + " exist?\n" + "if ($" + e.Name + " == NULL) {\n"
				+ "  throw new Exception(i18n(\"No such " + e.Name + " with given key\"));\n" + "}\n"

				+ "// delete the " + Principal.toUpper(e.Name) + " object in the database\n" + "$this->" + e.Name + "Mapper->update($" + e.Name
				+ ");\n"

				+ "// POST-REDIRECT-GET\n" + "// Everything OK, we will redirect the user to the list of posts\n"
				+ "// We want to see a message after redirection, so we establish\n"
				+ "// a \"flash\" message (which is simply a Session variable) to be\n"
				+ "// get in the view after redirection.\n" + "$this->view->setFlash(sprintf(i18n(\"" + Principal.toUpper(e.Name)
				+ "  successfully deleted..\")));\n"

				+ "// perform the redirection. More or less: \n" + "// header(\"Location: index.php?controller=" + e.Name
				+ "&action=index\")\n" + "// die();\n" + "$this->view->redirect(\"" + e.Name + "\", \"index\");	\n"
				+ "}\n";
		return toret;
	}

	private String generateEditController() {
		String toret = "/**\n" + "* Action to edit a " + e.Name + "\n" + "* \n"
				+ "* When called via GET, it shows an edit form\n" + "* including the current data of the " + Principal.toUpper(e.Name)
				+ ".\n" + "* When called via POST, it modifies the " + e.Name + " in the\n" + "* database.\n"
				+ "* The views are:\n" + "* <ul>\n" + "* <li>" + e.Name
				+ "/edit: If this action is reached via HTTP GET (via include)</li>\n" + "* <li>" + e.Name + "/index: If "
				+ e.Name + " was successfully edited (via redirect)</li>\n" + "* <li>" + e.Name
				+ "/edit: If validation fails (via include). Includes these view variables:</li>\n" + "* <ul>\n"
				+ "*  <li>" + e.Name + ": The current " + Principal.toUpper(e.Name)
				+ " instance, empty or being added (but not validated)</li>\n"
				+ "*  <li>errors: Array including per-field validation errors</li> \n" + "* </ul>\n" + "* </ul>\n"
				+ "* @throws Exception if no key was provided\n" + "* @return void\n" + "*/  \n"
				+ "public function edit() {\n";
		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getKey() || e.campos.get(i).getObligatory()) {
				toret += "if (!isset($_REQUEST[\"" + e.campos.get(i).getName() + "\"])) {\n" + " throw new Exception(i18n(\"A " + e.Name + " "
						+ e.campos.get(i).getName() + " is mandatory\"));\n" + " }\n";
			}
		}
		toret += "if (!isset($this->currentUser)) {\n" + "  throw new Exception(i18n(\"Not in session. Editing " + e.Name
				+ " requires login\"));\n" + "}\n"

				+ "// Get the " + Principal.toUpper(e.Name) + " object from the database\n" + "$" + e.Name + " = $this->" + e.Name
				+ "Mapper->findById(" + e.generateEditForController() + ");\n"

				+ "// Does the " + e.Name + " exist?\n" + "if ($" + e.Name + " == NULL) {\n"
				+ "  throw new Exception(i18n(\"No such " + e.Name + " with given key\"));\n" + "}\n"

				+ "if (isset($_POST[\"submit\"])) { // reaching via HTTP Post...  \n"

				+ "// populate the " + Principal.toUpper(e.Name) + " object with data form the form\n";

		for (int i = 0; i < e.campos.size(); i++) {
			toret += "$" + e.Name + "->set" + Principal.toUpper(e.campos.get(i).getName()) + "($_POST[\"" + e.campos.get(i).getName() + "\"]); \n";
		}

		toret += "try {\n" + "// validate " + Principal.toUpper(e.Name) + " object\n" + "$" + e.Name
				+ "->checkIsValidForUpdate(); // if it fails, ValidationException\n"

				+ "// update the Post object in the database\n" + "$this->" + e.Name + "Mapper->update($" + e.Name + ");\n"

				+ "// POST-REDIRECT-GET\n" + "// Everything OK, we will redirect the user to the list of " + e.Name + "\n"
				+ "// We want to see a message after redirection, so we establish\n"
				+ "// a \"flash\" message (which is simply a Session variable) to be\n"
				+ "// get in the view after redirection.\n" + "$this->view->setFlash(sprintf(i18n(\"" + Principal.toUpper(e.Name)
				+ "  successfully updated.\")));\n"

				+ "// perform the redirection. More or less: \n" + "// header(\"Location: index.php?controller=" + e.Name
				+ "&action=index\")\n" + "// die();\n" + "$this->view->redirect(\"" + e.Name + "\", \"index\");	\n"

				+ "}catch(ValidationException $ex) {\n" + "// Get the errors array inside the exepction...\n"
				+ "$errors = $ex->getErrors();\n" + "// And put it to the view as \"errors\" variable\n"
				+ "$this->view->setVariable(\"errors\", $errors);\n" + "}\n" + "}\n"

				+ "// Put the Post object visible to the view\n" + "$this->view->setVariable(\"" + e.Name + "\", $" + e.Name
				+ ");\n"

				+ "// render the view (/view/" + e.Name + "/add.php)\n" + "$this->view->render(\"" + e.Name
				+ "\", \"edit\");   \n" + "}\n";
		return toret;
	}

	

	private String generateAddController() {
		String toret = "/**\n" + "* Action to add a new " + e.Name + "\n" + "* \n"
				+ "* When called via GET, it shows the add form\n" + "* When called via POST, it adds the " + e.Name
				+ " to the\n" + "* database\n" + "* \n" + "* The views are:\n" + "* <ul>\n" + "* <li>" + e.Name
				+ "/add: If this action is reached via HTTP GET (via include)</li>   \n" + "* <li>" + e.Name
				+ "/index: If " + e.Name + " was successfully added (via redirect)</li>\n" + "* <li>" + e.Name
				+ "/add: If validation fails (via include). Includes these view variables:</li>\n" + "* <ul>\n"
				+ "*  <li>" + e.Name + ": The current " + Principal.toUpper(e.Name) + " instance, empty or \n"
				+ "*  being added (but not validated)</li>\n"
				+ "*  <li>errors: Array including per-field validation errors</li>\n" + "* </ul>\n" + "* </ul>\n"
				+ "* @throws Exception if no user is in session\n" + "* @return void\n" + "*/\n"
				+ "public function add() {\n" + "if (!isset($this->currentUser)) {\n"
				+ " throw new Exception(i18n(\"Not in session. Adding " + e.Name + " requires login\"));\n" + "}\n"

				+ "$" + e.Name + " = new " + Principal.toUpper(e.Name) + "();\n"

				+ "if (isset($_POST[\"submit\"])) { // reaching via HTTP Post...\n"

				+ "// populate the " + Principal.toUpper(e.Name) + " object with data form the form\n";

		for (int i = 0; i < e.campos.size(); i++) {
			if (!e.campos.get(i).getAutoIncrement())
				toret += "$" + e.Name + "->set" + Principal.toUpper(e.campos.get(i).getName()) + "($_POST[\"" + e.campos.get(i).getName() + "\"]); \n";
		}

		toret += "try {\n" + "// validate " + Principal.toUpper(e.Name) + " object\n" + "$" + e.Name
				+ "->checkIsValidForCreate(); // if it fails, ValidationException\n"

				+ "// save the Post object into the database\n" + "$this->" + e.Name + "Mapper->save($" + e.Name + ");\n"

				+ "// POST-REDIRECT-GET\n" + "// Everything OK, we will redirect the user to the list of " + e.Name + "\n"
				+ "// We want to see a message after redirection, so we establish\n"
				+ "// a \"flash\" message (which is simply a Session variable) to be\n"
				+ "// get in the view after redirection.\n" + "$this->view->setFlash(sprintf(i18n(\"" + Principal.toUpper(e.Name)
				+ " successfully added.\")));\n"

				+ "// perform the redirection. More or less: \n" + "// header(\"Location: index.php?controller=" + e.Name
				+ "&action=index\")\n" + "// die();\n" + "$this->view->redirect(\"" + e.Name + "\", \"index\");\n"

				+ " }catch(ValidationException $ex) {     \n" + "// Get the errors array inside the exepction...\n"
				+ "$errors = $ex->getErrors();	\n" + "// And put it to the view as \"errors\" variable\n"
				+ "$this->view->setVariable(\"errors\", $errors);\n" + "  }\n" + "}\n"

				+ "// Put the " + Principal.toUpper(e.Name) + " object visible to the view\n" + "$this->view->setVariable(\"" + e.Name
				+ "\", $" + e.Name + "); \n"

				+ " // render the view (/view/" + e.Name + "/add.php)\n" + "$this->view->render(\"" + e.Name
				+ "\", \"add\");\n"

				+ "}\n";
		return toret;
	}

	private String generateViewController() {
		String toret = "/**\n" + "* Action to view a given Activity\n" + " * \n"
				+ "* This action should only be called via GET\n" + "* \n" + "* The expected HTTP parameters are:\n"
				+ "* <ul>\n" + "* <li>id: Id of the post (via HTTP GET)</li>   \n" + "* </ul>\n" + "* \n"
				+ "* The views are:\n" + "* <ul>\n"
				+ "* <li>activities/view: If post is successfully loaded (via include).  Includes these view variables:</li>\n"
				+ "* <ul>\n" + "*  <li>activity: The current Post retrieved</li>\n" + "* </ul>\n" + "* </ul>\n" + "* \n"
				+ "* @throws Exception If no such activity of the given id is found\n" + "* @return void\n" + "* \n"
				+ "*/\n" + "public function view(){\n";
		for (int i = 0; i < e.campos.size(); i++) {
			if (e.campos.get(i).getKey()) {
				toret += "if (!isset($GET[\"" + e.campos.get(i).getName() + "\"])) {\n" + "throw new Exception(i18n(\"" + e.campos.get(i).getName()
						+ " is mandatory\"));\n" + "}\n";
			}

		}
		toret += "// find the " + Principal.toUpper(e.Name) + " object in the database\n" 
				+ "$" + e.Name + " = $this->" + e.Name+ "Mapper->findById(" + e.generateIndexForController() + ");\n";
	
		toret += "if ($" + e.Name + " == NULL) {\n"
				+ "  throw new Exception(i18n(\"No such " + Principal.toUpper(e.Name) + " with given key\"));\n" 
				+ " }\n" ;
				for (Entidad rel :e.relaciones)
				{
					toret +=  "$" + e.Name + "->set"+Principal.toUpper(e.Name)+"($this->" + rel.getName()+ "Mapper->findById(" + rel.generateIdentifierGetter(e.Name) + "));\n";
				}
				for (RelacionMultiple rel :e.relacionesMultiples)
				{
					toret += "$" + rel.destino.getName() + "List = array();\n";
					toret += "array_push($" + rel.destino.getName() + "List, $this->" + rel.destino.getName()+ "Mapper->findAllWith"+Principal.toUpper(e.Name)+"key(" + e.generateIdentifierGetter(e.Name)+"));\n";
					toret +=  "// put the " + Principal.toUpper(e.Name)+ "List of asociated objects to the view\n" 
							+ "$this->view->setVariable(\"" + rel.destino.getName() + "List\", $" + rel.destino.getName() + "List);\n";
					
					if (rel.intermedia != null)
					{
					toret += "$" + rel.intermedia.getName() + "List = array();\n";
					toret += "foreach($this->" + rel.destino.getName()+ "Mapper->findAll() as $"+rel.destino.getName()+"Item) {\n";
					toret += "array_push($" + rel.intermedia.getName() + "List, $this->" + rel.intermedia.getName()+ "Mapper->findById(" + rel.destino.generateIdentifierOther(e,rel.destino.getName()+"Item")+"));\n";
					toret += "}\n";
					toret +=  "// put the " + rel.intermedia.getName()+ "List of related objects to the view\n" 
							+ "$this->view->setVariable(\"" + rel.intermedia.getName() + "List\", $" + rel.intermedia.getName() + "List);\n";
					}
					
				}
				toret +=  "// put the " + Principal.toUpper(e.Name)+ " object to the view\n" 
				+ "$this->view->setVariable(\"" + e.Name + "\", $" + e.Name + ");\n"
				+ "// render the view (/view/" + e.Name + "/view.php)\n" 
				+ "$this->view->render(\"" + e.Name+ "\", \"view\");\n" 
				+ "}\n";
		return toret;
	}

	
	

	private String generateIndexController() {
		String toret = "/**\n" + "* Action to list Activities\n" + "* \n" + "* Loads all the posts from the database.\n"
				+ "* No HTTP parameters are needed.\n" + "* \n" + "* The views are:\n" + "* <ul>\n" + "* <li>" + e.Name
				+ "/index (via include)</li>   \n" + "* </ul>\n" + "*/\n" + "public function index() {\n"

				+ "// obtain the data from the database\n" + "$" + Principal.toUpper(e.Name) + "List = $this->" + Principal.toLower(e.Name)
				+ "Mapper->findAll();    \n"

				+ "// put the array containing Post object to the view\n" + "$this->view->setVariable(\"" + e.Name
				+ "List\", $" + e.Name + "List);\n"

				+ "// render the view (/view/" + e.Name + "/index.php)\n" + "$this->view->render(\"" + e.Name
				+ "\", \"index\");\n" + "}\n";
		return toret;
	}

}
