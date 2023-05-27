package hstools.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Player extends Node {
	// TODO Necessario? Classe do deck que esta sendo jogado.
	private String classe;
}