package Generator;

import Upgrade.Entidad;

public interface Generator {
	public String generateMapper(Entidad e);
	
	public String generateClass(Entidad e);
	
	public String generateController(Entidad e);

}
