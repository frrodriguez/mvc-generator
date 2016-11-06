package Upgrade;
import java.util.ArrayList;

public class ForeignKey {
	ArrayList<CampoTabla> campos ;
	String nameTabla;
	public ForeignKey(PrimaryKey key,String nameTabla) {
		campos = key.campos;
		this.nameTabla = nameTabla;
	}
	
	
	
	@Override
	public String toString() {
		return campos.toString();
	}
}
