package hstools.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Archtype extends Node {
	public Archtype(Long id) {
		super(id);
	}
}