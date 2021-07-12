package com.oracle.jcclassic.samples.wallet;

public class Disciplina {
	byte cod_disciplina;
	public byte nota;
	byte zi, luna, an;
	byte cod_concurs;
	byte punctaj;
	
	public Disciplina() {
		this.cod_disciplina=0;
		this.nota=0;
		this.zi=0;
		this.luna=0;
		this.an=0;
		this.cod_concurs=0;
		this.punctaj=0;
	}
	
	public Disciplina(byte cod_disc,byte nota,byte zi, byte luna, byte an,byte concurs,byte punctaj) {
		this.cod_disciplina=cod_disc;
		this.nota=nota;
		this.zi=zi;
		this.luna=luna;
		this.an=an;
		this.cod_concurs=concurs;
		this.punctaj=punctaj;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		//return super.equals(obj);
		if (obj == this) {
	         return true;
	      }
	      if (!(obj instanceof Disciplina)) {
	         return false;
	      }
	      Disciplina disc = (Disciplina) obj;
	      return disc.cod_disciplina==this.cod_disciplina;
	}

	
}
