package twig;


import java.io.File;

import javax.swing.GrayFilter;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;


public class Principal {

	public static void main(String[] args) {
		 JtwigTemplate template  = JtwigTemplate.fileTemplate(new File("Template/Controller.twig"));
		 JtwigModel model = JtwigModel.newModel();
		 model.with("name", "Petter");

		 template.render(model, System.out);

	}

}
