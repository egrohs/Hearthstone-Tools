package hstools.domain.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(of = "name")
@Data
//TODO usar um flattening?
public class Mechanic {
	private String name;
}