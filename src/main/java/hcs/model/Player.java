package hcs.model;

import hcs.model.Carta.CLASS;
import lombok.Data;

@Data
public class Player extends Entidade {
    private CLASS classe;
}
