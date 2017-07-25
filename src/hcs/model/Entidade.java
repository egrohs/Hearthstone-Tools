package hcs.model;

import javafx.scene.layout.StackPane;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
public class Entidade extends StackPane {
	public String name, id;
	
	@Override
	public String toString() {
		return name;
	}
}