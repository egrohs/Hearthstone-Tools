package hstools.domain.entities;

import hstools.domain.entities.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Player extends Node {
    private CLASS classe;
}
