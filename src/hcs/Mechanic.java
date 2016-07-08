package hcs;

/**
 * Objeto de sinergia das cartas.
 * 
 * @author 99689650068
 *
 */
public class Mechanic extends Entidade {
	String id, regex, cathegory;
	// Map<Mechanic, Float> aff = new LinkedHashMap<Mechanic, Float>();

	public Mechanic(String id, String regex) {
		this.id = id;
		this.regex = regex;
	}

	@Override
	public String toString() {
		return this.regex;
	}
}