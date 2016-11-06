package Upgrade;

public class RelacionMultiple {
	public Entidad destino;
	public Entidad intermedia;
	public RelacionMultiple(Entidad d, Entidad inter)
	{
		destino = d;
		intermedia = inter;
	}
	
	@Override
	public String toString() {
		return  ((intermedia != null) ? intermedia.Name +"->" : "")+ destino.Name;
	}
}
