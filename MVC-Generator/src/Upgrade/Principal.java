package Upgrade;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Principal {
	public static ArrayList<Entidad> entidades;
	public static void main(String[] args) throws IOException {
		BufferedReader fr = new BufferedReader (new FileReader(new File("SQL_ABP_COmpleto.sql")));
		String linea = "";
		
		String SQL = "";
		while((linea = fr.readLine()) != null)
		{
			SQL += linea;
		}
		
		fr.close();
		ArrayList<String> Creates = getCreateTableSentences(SQL);

		entidades = new ArrayList<>();
		for(String CreateSentence : Creates)
		{
			entidades.add(new Entidad(CreateSentence));
		}
		
		establecerRelacionesEntidades();
		
		for (Entidad e : entidades)
		{
			BufferedWriter fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/model/" + e.Name+"Mapper.php")));
			fw.write(e.generateMapper());
			fw.close();
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/model/"+e.Name+".php")));
			fw.write(e.generateClass());
			fw.close();
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/controller/"+e.Name+"Controller.php")));
			fw.write(e.generateController());
			fw.close();
		}
		
		String messagesFile = "<?php\n"
				  +"//file: /view/messages/messages_es.php\n"
				  +"$i18n_messages = \n"
				  +"array(\n";
		for (Entidad e : entidades)
		{
		String[] lineasTraduccion = searchIt8(e.generateController() + e.generateClass() + e.generateMapper());
		for (String lin : lineasTraduccion)
		{
			messagesFile += lin+", \n";
		}
		}
		messagesFile += ")\n?>\n";
		
		BufferedWriter fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/messages/messages_es.php")));
		fw.write(messagesFile);
		fw.close();
		
		for (Entidad e : entidades)
		{
			if (!e.hasUsefullColums())
				continue;
			File f = new File("ContenidoGenerado/view/"+e.getName());
			f.mkdir();
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/"+e.Name+"/add.php")));
			fw.write(e.generateViewAdd());
			fw.close();
			
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/"+e.Name+"/edit.php")));
			fw.write(e.generateViewEdit());
			fw.close();
			
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/"+e.Name+"/index.php")));
			fw.write(e.generateViewIndex());
			fw.close();
			
			fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/"+e.Name+"/view.php")));
			fw.write(e.generateViewView());
			fw.close();
		}
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/layouts/default.php")));
		fw.write(generateDefaultPHP(entidades));
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/layouts/welcome.php")));
		fr = new BufferedReader(new FileReader(new File("Source/welcome.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/layouts/language_select_element.php")));
		fr = new BufferedReader(new FileReader(new File("Source/language_select_element.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/view/usuario/login.php")));
		fr = new BufferedReader(new FileReader(new File("Source/login.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/controller/BaseController.php")));
		fr = new BufferedReader(new FileReader(new File("Source/BaseController.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/core/I18n.php")));
		fr = new BufferedReader(new FileReader(new File("Source/I18n.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/core/PDOConnection.php")));
		fr = new BufferedReader(new FileReader(new File("Source/PDOConnection.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/core/ValidationException.php")));
		fr = new BufferedReader(new FileReader(new File("Source/ValidationException.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/core/ViewManager.php")));
		fr = new BufferedReader(new FileReader(new File("Source/ViewManager.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/controller/LanguageController.php")));
		fr = new BufferedReader(new FileReader(new File("Source/LanguageController.php")));
		while((linea = fr.readLine() )!= null)
			fw.write(linea+"\n");
		fr.close();
		fw.close();
		
		fw = new BufferedWriter (new FileWriter(new File("ContenidoGenerado/index.php")));
		fw.write(generateIndexPHP(entidades));
		fw.close();
		
		System.out.println("");
	}
	
	private static String generateIndexPHP(ArrayList<Entidad> tablas) {
		String toret = "<?php\n"
+"// file: index.php\n"

+"/**\n"
 +"* Default controller if any controller is passed in the URL\n"
 +"*/\n"
 +"define(\"DEFAULT_CONTROLLER\", \""+tablas.get(0).Name+"\");\n"

 +"/**\n"
 +"* Default action if any action is passed in the URL\n"
 +"*/\n"
+"define(\"DEFAULT_ACTION\", \"index\");\n"

+"/**\n"
+" * Main router (single entry-point for all requests)\n"
 +"* of the MVC implementation.\n"
+" * \n"
+" * This router will create an instance of the corresponding\n"
+"* controller, based on the \"controller\" parameter and call\n"
+"* the corresponding method, based on the \"action\" parameter.\n"
+"* \n"
 +"* The rest of GET or POST parameters should be handled by\n"
+" * the controller itself.\n"
+" * \n"
+" * Parameters:\n"
+" * <ul>\n"
+" * <li>controller: The controller name (via HTTP GET)\n"
 +"* <li>action: The name inside the controller (via HTTP GET)\n"
+" * </ul>\n"
+" * \n"
 +"* @return void\n"
+" * \n"
 +"* @author fran\n"
+"*/\n"
+"function run() {\n"
  +"// invoke action!\n"
 +"try {\n"
  +"  if (!isset($_GET[\"controller\"])) {\n"
  +"   $_GET[\"controller\"] = DEFAULT_CONTROLLER; \n"
  +"  }\n"
    
   +" if (!isset($_GET[\"action\"])) {\n"
   +"   $_GET[\"action\"] = DEFAULT_ACTION;\n"
   +" }\n"
    
   +" // Here is where the \"magic\" occurs.\n"
   +" // URLs like: index.php?controller=posts&action=add\n"
   +" // will provoke a call to: new PostsController()->add()\n"
    
   +" // Instantiate the corresponding controller\n"
   +" $controller = loadController($_GET[\"controller\"]);\n"
    
   +" // Call the corresponding action\n"
   +" $actionName = $_GET[\"action\"];\n"
   +" $controller->$actionName(); \n"
   +" } catch(Exception $ex) {\n"
   +" //uniform treatment of exceptions\n"
  +"  die(\"An exception occured!!!!!\".$ex->getMessage());   \n"
  +" }\n"
  +"}\n"
 
+"/**\n"
 +"* Load the required controller file and create the controller instance\n"
+" * \n"
+" * @param string $controllerName The controller name found in the URL\n"
+" * @return Object A Controller instance\n"
+" */\n"
+"function loadController($controllerName) {  \n"
 +" $controllerClassName = getControllerClassName($controllerName);\n"
  
 +" require_once(__DIR__.\"/controller/\".$controllerClassName.\".php\");  \n"
 +" return new $controllerClassName();\n"
 +"}\n"
 
+"/**\n"
 +"* Obtain the class name for a controller name in the URL\n"
+" * \n"
 +"* For example $controllerName = \"users\" will return \"UsersController\"\n"
		 +"* \n"
		 +"* @param $controllerName The name of the controller found in the URL\n"
 +"* @return string The controller class name\n"
 +"*/\n"
+"function getControllerClassName($controllerName) {\n"
+"  return strToUpper(substr($controllerName, 0, 1)).substr($controllerName, 1).\"Controller\";\n"
+"}\n"
 
+"//run!\n"
+"run();\n"
 
+"?>\n";
		
		
		return toret;
	}
	private static String generateDefaultPHP(ArrayList<Entidad> tablas) {
		String toret = "<?php\n"
 +"//file: view/layouts/default.php\n"
 
 +"require_once(__DIR__.\"/../../core/ViewManager.php\");\n"
 +"$view = ViewManager::getInstance();\n"
 +"$currentuser = $view->getVariable(\"currentusername\");\n"
 
 +"?><!DOCTYPE html>\n"
+"<html>\n"
 +" <head>\n"
   +" <title><?= $view->getVariable(\"title\", \"no title\") ?></title>\n"
   +"<meta charset=\"utf-8\">\n"
   +"<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\">\n"
   +"<?= $view->getFragment(\"css\") ?>\n"
   +"<?= $view->getFragment(\"javascript\") ?>\n"
   +"</head>\n"
  +"<body>    \n"
    +"<!-- header -->\n"
    +"<header>\n"
     +" <h1>Blog</h1>\n"
     +" <nav id=\"menu\" style=\"background-color:grey\">\n"
     +"<ul>\n";
		for (Entidad e : tablas) {
			if (!e.Name.contains("relacion"))
			toret += "<li><a href=\"index.php?controller="+e.Name+"&amp;action=index\">"+toUpper(e.Name)+"</a></li>\n";
		}
	
			toret += "<?php if (isset($currentuser)): ?>    \n"  
	 +" <li><?= sprintf(i18n(\"Hello %s\"), $currentuser) ?>\n"
	 +" <a 	href=\"index.php?controller=usuario&amp;action=logout\">(Logout)</a>	\n"
			 +" </li>\n"
	
	+"<?php else: ?>\n"
	 +" <li><a href=\"index.php?controller=usuario&amp;action=login\"><?= i18n(\"Login\") ?></a></li>\n"
			 +" <?php endif ?>\n"
	 +"</ul>\n"
     +" </nav>\n"
   +" </header>\n"
    
   +" <main>\n"
    +"  <div id=\"flash\">\n"
    +"<?= $view->popFlash() ?>\n"
      +"</div>\n"
	 
      +"<?= $view->getFragment(ViewManager::DEFAULT_FRAGMENT) ?>    \n"
  +"  </main>\n"
   +" <footer>\n"
      +"<?php\n"
     +" include(__DIR__.\"/language_select_element.php\");\n"
     +"  ?>\n"
    +"</footer>\n"
    
  +"</body>\n"
+"</html>\n";
		
	 return toret;
	 }
	
	private static String[] searchIt8(String string) {
		ArrayList<String> toret = new ArrayList<>();
		while(string.contains("i18n"))
		{
			string = string.substring(string.indexOf("i18n")+5);
			toret.add(string.substring(0, string.indexOf(")")) + " => \"\"");
		}
		String list2[] = new String[toret.size()];
		return toret.toArray(list2);
	}

	//Foreach table, creates the relations with the others tables creates
	private static void establecerRelacionesEntidades() {
		for(Entidad e : entidades)
		{
			for (Foreing f : e.foreingKeys)
			{
				e.relaciones.add(getEntityByName(f.keyDestino.nombreTabla));
				if (e.hasUsefullColums()){
					// TODO antes de añadir a viceversa, comprobar unique
					getEntityByName(f.keyDestino.nombreTabla).relacionesMultiples.add(new RelacionMultiple(e, null));
				}
			}
		}
		for(Entidad e : entidades)
		{
			for(Entidad e2 : entidades)
			{
				if (!e2.equals(e))
				{
					if (e.getOtherEntityRelacionated(e2.Name) != null)
					{
						e2.relacionesMultiples.add(new RelacionMultiple( getEntityByName(e.getOtherEntityRelacionated(e2.Name)),e));
					}
				}
			}
		}
		
	}
	
	//Retuns a Entity instance with given object
	public static Entidad getEntityByName(String tabla) {
		for(Entidad e : entidades)
		{
			if (e.Name.equals(tabla))
				return e;
		}
		return null;
	}
	
	//Returns a list of CREATE TABLE sentences of the given SQL Content
	private static ArrayList<String> getCreateTableSentences(String SQL) {
		ArrayList<String> toret = new ArrayList<>();
		StringBuilder sb = new StringBuilder(SQL);
		int actualIndex = 0;
		while(sb.indexOf("CREATE TABLE", actualIndex) != -1)
		{
			StringBuilder aux = new StringBuilder();
			aux.append(sb.substring(sb.indexOf("CREATE TABLE",actualIndex), sb.indexOf(";",sb.indexOf("CREATE TABLE",actualIndex))));
			actualIndex = sb.indexOf(";",sb.indexOf("CREATE TABLE",actualIndex));
			toret.add(aux.toString());
			
		}
		return toret;
	}
	public static String toLower(String val) {
		return val.substring(0, 1).toLowerCase() + val.substring(1);
	}
	public static String toLower(Entidad val) {
		return toLower(val.Name);
	}

	public static String toUpper(Entidad val) {
		return toUpper(val.Name);
	}
	public static String toUpper(String val) {
		return val.substring(0, 1).toUpperCase() + val.substring(1);
	}
}
