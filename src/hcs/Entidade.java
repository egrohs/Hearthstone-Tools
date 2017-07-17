package hcs;

/**
 * Entidade abstrada, pode ser carta, mecanica, etc.
 * 
 * @author 99689650068
 *
 */
public abstract class Entidade {
	protected String name;
	@Override
	public String toString() {
		return name;
	}
}