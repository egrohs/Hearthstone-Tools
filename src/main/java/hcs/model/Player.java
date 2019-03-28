package hcs.model;

import hcs.model.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Player extends Entity {
    private CLASS classe;
}
