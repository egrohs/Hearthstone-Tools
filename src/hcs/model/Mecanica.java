package hcs.model;

/**
 * Mecânicas do hearthstone.
 * 
 * @author 99689650068
 *
 */
public class Mecanica extends Entidade {
	public String regex, cathegory;
	// Map<Mechanic, Float> aff = new LinkedHashMap<Mechanic, Float>();

	public Mecanica(String id, String regex) {
		this.id = id;
		this.regex = regex;
	}

	@Override
	public String toString() {
		return this.regex;
	}
}