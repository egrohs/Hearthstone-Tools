package hstools.ai.future;

import javax.annotation.Nullable;

public class Effect {
	@Nullable
	String condicao;// if/IN (mark), If you've dealt 10 damage with your Hero Power this game <=
					// desdobrar
					// em um novo comando. Normalmente no inicio da frase separado por ,
	String trigger;// now, battlecry:(when summoned), at eot, whenever...
	Mechanic action;// deal, discover...
	@Nullable
	What what;// 10 damage, +4 attack, 2/2 murloc, 2 damage for each... TOKEN, BONUS, ... Pode
				// ser vazio ex. "Destroy all other minions." ???
	Target target;// pode estar oculto/implicito. you, your board, opponent hand...
	String until;// once, eot...
}
