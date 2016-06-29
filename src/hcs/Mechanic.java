package hcs;

import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de sinergia das cartas.
 * 
 * @author 99689650068
 *
 */
public class Mechanic {
	String id, name, regex, cathegory;
	List<Mechanic> aff = new ArrayList<Mechanic>();

	public Mechanic(String id, String regex) {
		this.id = id;
		this.regex = regex;
	}

	public boolean eval(Card c) {
		return true;
	}

	@Override
	public String toString() {
		return this.regex;
	}
}