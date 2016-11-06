package Upgrade;
import java.util.ArrayList;

public class PrimaryKey {
	ArrayList<CampoTabla> campos ;
	String nombreTabla;
	public PrimaryKey(String nombreTabla) {
		campos = new ArrayList<>();
		this.nombreTabla = nombreTabla;
	}
	
	public void addColumn(CampoTabla ct)
	{
		campos.add(ct);
	}
	
	@Override
	public String toString() {
		return campos.toString();
	}
	
}
