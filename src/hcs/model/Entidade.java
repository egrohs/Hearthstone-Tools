package hcs.model;

import javafx.scene.image.ImageView;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
public abstract class Entidade extends ImageView {
	public String name, id;
	@Override
	public String toString() {
		return name;
	}
}