package hstools.ai.future;

import java.util.HashSet;
import java.util.Set;

import hstools.Constants.Operator;
import lombok.Data;

/**
 * Identifica o modelo de alvo de uma ACTION com suas restrições abaixo.
 * Toda frase terminada "." tem um target (you, your hand, this card,...).
 * @author EGrohs
 *
 */
@Data
public class Target {
	Boolean owner;// owner == active player? and both?
	Boolean random;// choose a random one from a candidades list.
	Boolean other;// other but this
	Boolean adjacent;// active player board list+-1
	Boolean damaged;// curHealth != health
	Operator operator;//list?
	Set<String> races = new HashSet<>();
	Set<String> types = new HashSet<>();
}