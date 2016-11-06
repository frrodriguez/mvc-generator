package Generator;

import Memory.MemoryController;
import Memory.MemoryMapper;
import Memory.MemoryModel;
import Upgrade.Entidad;

public class MemoryGenerator implements Generator{

	MemoryMapper mp = new MemoryMapper();
	MemoryController mc = new MemoryController();
	MemoryModel mm = new MemoryModel();
	
	@Override
	public String generateMapper(Entidad e) {
		// TODO Auto-generated method stub
		return mp.generate(e);
	}

	@Override
	public String generateClass(Entidad e) {
		// TODO Auto-generated method stub
		return mm.generate(e);
	}

	@Override
	public String generateController(Entidad e) {
		// TODO Auto-generated method stub
		return mc.generate(e);
	}

	

}
