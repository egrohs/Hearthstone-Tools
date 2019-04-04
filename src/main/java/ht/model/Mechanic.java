package ht.model;

import lombok.Data;

/**
 * Mecânicas do hearthstone.
 * 
 * @author 99689650068
 *
 */
@Deprecated//(since = "Use Tag class")
@Data
public class Mechanic extends Entity {
    private String regex, cathegory;
    // Map<Mechanic, Float> aff = new LinkedHashMap<Mechanic, Float>();

    public Mechanic(String cod, String regex) {
	this.cod = cod;
	this.regex = regex;
    }

    @Override
    public String toString() {
	return this.regex;
    }

    public enum MECANICAS {

    }
}