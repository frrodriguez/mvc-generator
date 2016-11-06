<?php
// file: controller/::TABLENAME.LOWER::Controller.php
::MODEL.REQUIREMENTS::
::MODELMAPPER.REQUIREMENTS::
require_once(__DIR__."/../core/ViewManager.php");
require_once(__DIR__."/../controller/BaseController.php");
/**
 * Class actividadController
* 
* Controller to make a CRUDL of ::TABLENAME.LOWER:: entities
* 
* @author fran 
*/
class ::TABLENAME.UPPER::Controller extends BaseController {
/**
* Reference to the ::TABLENAME.LOWER::Mapper to interact
* with the database
* 
* @var ::TABLENAME.LOWER::Mapper
*/
private $::TABLENAME.LOWER::Mapper; 

::FOR.RELACIONESMULTIPLES.VARIABLEMAPPER::

 /**
 * The constructor
*/  
public function __construct() { 
parent::__construct();
$this->::TABLENAME.LOWER::Mapper = new ::TABLENAME.UPPER::Mapper(); 
::FOR.RELACIONESMULTIPLES.CREATEMAPPER::
}
/**
* Action to list Activities
* 
* Loads all the posts from the database.
* No HTTP parameters are needed.
* 
* The views are:
* <ul>
* <li>actividad/index (via include)</li>   
* </ul>
*/
public function index() {
// obtain the data from the database
$actividadList = $this->actividadMapper->findAll();    
// put the array containing Post object to the view
$this->view->setVariable("actividadList", $actividadList);
// render the view (/view/actividad/index.php)
$this->view->render("actividad", "index");
}
/**
* Action to view a given Activity
 * 
* This action should only be called via GET
* 
* The expected HTTP parameters are:
* <ul>
* <li>id: Id of the post (via HTTP GET)</li>   
* </ul>
* 
* The views are:
* <ul>
* <li>activities/view: If post is successfully loaded (via include).  Includes these view variables:</li>
* <ul>
*  <li>activity: The current Post retrieved</li>
* </ul>
* </ul>
* 
* @throws Exception If no such activity of the given id is found
* @return void
* 
*/
public function view(){
if (!isset($GET["actividadId"])) {
throw new Exception(i18n("actividadId is mandatory"));
}
// find the actividad object in the database
$actividad = $this->actividadMapper->findById($_GET["actividadId"]);
if ($actividad == NULL) {
  throw new Exception(i18n("No such actividad with given key"));
 }
$sesionActividadList = array();
array_push($sesionActividadList, $this->sesionActividadMapper->findAllWithactividadkey($actividad->getActividadId()));
// put the sesionActividadList of asociated objects to the view
$this->view->setVariable("sesionActividadList", $sesionActividadList);
// put the actividad object to the view
$this->view->setVariable("actividad", $actividad);
// render the view (/view/actividad/view.php)
$this->view->render("actividad", "view");
}
/**
* Action to add a new actividad
* 
* When called via GET, it shows the add form
* When called via POST, it adds the actividad to the
* database
* 
* The views are:
* <ul>
* <li>actividad/add: If this action is reached via HTTP GET (via include)</li>   
* <li>actividad/index: If actividad was successfully added (via redirect)</li>
* <li>actividad/add: If validation fails (via include). Includes these view variables:</li>
* <ul>
*  <li>actividad: The current actividad instance, empty or 
*  being added (but not validated)</li>
*  <li>errors: Array including per-field validation errors</li>
* </ul>
* </ul>
* @throws Exception if no user is in session
* @return void
*/
public function add() {
if (!isset($this->currentUser)) {
 throw new Exception(i18n("Not in session. Adding actividad requires login"));
}
$actividad = new actividad();
if (isset($_POST["submit"])) { // reaching via HTTP Post...
// populate the actividad object with data form the form
$actividad->setTitulo($_POST["titulo"]); 
$actividad->setDescripcion($_POST["descripcion"]); 
$actividad->setImagen($_POST["imagen"]); 
$actividad->setVideo($_POST["video"]); 
$actividad->setLimitePlazas($_POST["limitePlazas"]); 
try {
// validate actividad object
$actividad->checkIsValidForCreate(); // if it fails, ValidationException
// save the Post object into the database
$this->actividadMapper->save($actividad);
// POST-REDIRECT-GET
// Everything OK, we will redirect the user to the list of actividad
// We want to see a message after redirection, so we establish
// a "flash" message (which is simply a Session variable) to be
// get in the view after redirection.
$this->view->setFlash(sprintf(i18n("actividad successfully added.")));
// perform the redirection. More or less: 
// header("Location: index.php?controller=actividad&action=index")
// die();
$this->view->redirect("actividad", "index");
 }catch(ValidationException $ex) {     
// Get the errors array inside the exepction...
$errors = $ex->getErrors();	
// And put it to the view as "errors" variable
$this->view->setVariable("errors", $errors);
  }
}
// Put the actividad object visible to the view
$this->view->setVariable("actividad", $actividad); 
 // render the view (/view/actividad/add.php)
$this->view->render("actividad", "add");
}
/**
* Action to edit a actividad
* 
* When called via GET, it shows an edit form
* including the current data of the actividad.
* When called via POST, it modifies the actividad in the
* database.
* The views are:
* <ul>
* <li>actividad/edit: If this action is reached via HTTP GET (via include)</li>
* <li>actividad/index: If actividad was successfully edited (via redirect)</li>
* <li>actividad/edit: If validation fails (via include). Includes these view variables:</li>
* <ul>
*  <li>actividad: The current actividad instance, empty or being added (but not validated)</li>
*  <li>errors: Array including per-field validation errors</li> 
* </ul>
* </ul>
* @throws Exception if no key was provided
* @return void
*/  
public function edit() {
if (!isset($_REQUEST["actividadId"])) {
 throw new Exception(i18n("A actividad actividadId is mandatory"));
 }
if (!isset($_REQUEST["titulo"])) {
 throw new Exception(i18n("A actividad titulo is mandatory"));
 }
if (!isset($_REQUEST["descripcion"])) {
 throw new Exception(i18n("A actividad descripcion is mandatory"));
 }
if (!isset($_REQUEST["imagen"])) {
 throw new Exception(i18n("A actividad imagen is mandatory"));
 }
if (!isset($_REQUEST["video"])) {
 throw new Exception(i18n("A actividad video is mandatory"));
 }
if (!isset($_REQUEST["limitePlazas"])) {
 throw new Exception(i18n("A actividad limitePlazas is mandatory"));
 }
if (!isset($this->currentUser)) {
  throw new Exception(i18n("Not in session. Editing actividad requires login"));
}
// Get the actividad object from the database
$actividad = $this->actividadMapper->findById($_REQUEST["actividadId"]);
// Does the actividad exist?
if ($actividad == NULL) {
  throw new Exception(i18n("No such actividad with given key"));
}
if (isset($_POST["submit"])) { // reaching via HTTP Post...  
// populate the actividad object with data form the form
$actividad->setActividadId($_POST["actividadId"]); 
$actividad->setTitulo($_POST["titulo"]); 
$actividad->setDescripcion($_POST["descripcion"]); 
$actividad->setImagen($_POST["imagen"]); 
$actividad->setVideo($_POST["video"]); 
$actividad->setLimitePlazas($_POST["limitePlazas"]); 
try {
// validate actividad object
$actividad->checkIsValidForUpdate(); // if it fails, ValidationException
// update the Post object in the database
$this->actividadMapper->update($actividad);
// POST-REDIRECT-GET
// Everything OK, we will redirect the user to the list of actividad
// We want to see a message after redirection, so we establish
// a "flash" message (which is simply a Session variable) to be
// get in the view after redirection.
$this->view->setFlash(sprintf(i18n("actividad  successfully updated.")));
// perform the redirection. More or less: 
// header("Location: index.php?controller=actividad&action=index")
// die();
$this->view->redirect("actividad", "index");	
}catch(ValidationException $ex) {
// Get the errors array inside the exepction...
$errors = $ex->getErrors();
// And put it to the view as "errors" variable
$this->view->setVariable("errors", $errors);
}
}
// Put the Post object visible to the view
$this->view->setVariable("actividad", $actividad);
// render the view (/view/actividad/add.php)
$this->view->render("actividad", "edit");   
}
/**
* Action to delete a actividad
* 
* This action should only be called via HTTP POST
* The views are:
* <ul>
* <li>activities/index: If activity was successfully deleted (via redirect)</li>
* </ul>
* @throws Exception if no key was provided
* @return void
*/  
public function delete() {
if (!isset($_POST["actividadId"])) {
 throw new Exception(i18n("A actividad actividadId is mandatory"));
 }
if (!isset($_POST["titulo"])) {
 throw new Exception(i18n("A actividad titulo is mandatory"));
 }
if (!isset($_POST["descripcion"])) {
 throw new Exception(i18n("A actividad descripcion is mandatory"));
 }
if (!isset($_POST["imagen"])) {
 throw new Exception(i18n("A actividad imagen is mandatory"));
 }
if (!isset($_POST["video"])) {
 throw new Exception(i18n("A actividad video is mandatory"));
 }
if (!isset($_POST["limitePlazas"])) {
 throw new Exception(i18n("A actividad limitePlazas is mandatory"));
 }
if (!isset($this->currentUser)) {
  throw new Exception(i18n("Not in session. Deleting actividad requires login"));
}
// Get the actividad object from the database
$actividad = $this->actividadMapper->findById($_REQUEST["actividadId"]);
// Does the actividad exist?
if ($actividad == NULL) {
  throw new Exception(i18n("No such actividad with given key"));
}
// delete the actividad object in the database
$this->actividadMapper->update($actividad);
// POST-REDIRECT-GET
// Everything OK, we will redirect the user to the list of posts
// We want to see a message after redirection, so we establish
// a "flash" message (which is simply a Session variable) to be
// get in the view after redirection.
$this->view->setFlash(sprintf(i18n("actividad  successfully deleted..")));
// perform the redirection. More or less: 
// header("Location: index.php?controller=actividad&action=index")
// die();
$this->view->redirect("actividad", "index");	
}
}