package hcs.model;

import hcs.model.Carta.CLASS;
import lombok.Data;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
@Data
public class Entidade /*extends StackPane*/ {
	protected String name, id;
	
	@Override
	public String toString() {
		return name;
	}
}