package a;

import java.util.ArrayList;
import java.util.List;

public class Mechanic {
	String id, name, regex, cathegory;
	// int[] sinergy;
	List<Mechanic> aff = new ArrayList<Mechanic>();

	public Mechanic(String id, String regex) {
		this.id = id;
		this.regex = regex;
	}

	// public void setSinergy() {
	// // this.sinergy = sinergy;
	// for (int i : sinergy) {
	// this.aff.add(Sinergy.getMechanic(i));
	// }
	// }

	public boolean eval(Card c) {
		return true;
	}
}
