package hcs;

import java.util.HashSet;
import java.util.Set;

/**
 * Objeto de sinergia das cartas.
 * 
 * @author 99689650068
 *
 */
public class Mechanic {
	String id, name, regex, cathegory;
	Set<Mechanic> aff = new HashSet<Mechanic>();

	public Mechanic(String id, String regex) {
		this.id = id;
		this.regex = regex;
	}

	// TODO usar ou nao esse eval para testar expressoes como HIGH ATTACK?
	public boolean eval(Card c) {
		return true;
	}

	@Override
	public String toString() {
		return this.regex;
	}
}