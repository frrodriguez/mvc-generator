package Upgrade;

public class Foreing {
	PrimaryKey keyDestino;
	ForeignKey keyExterna;
	
	public Foreing(PrimaryKey key, ForeignKey kd){
		this.keyDestino = key;
		this.keyExterna = kd;
	}
	@Override
	public String toString() {
		return keyExterna.toString() + " -> " + keyDestino.toString();
	}
}
