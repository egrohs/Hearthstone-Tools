package hstools.model;

import hstools.model.Card.CLASS;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class Player extends Node {
    private CLASS classe;
}
