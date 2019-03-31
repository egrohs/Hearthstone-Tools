TODOS
- Which type, location and file format should contains tags sinergies? (must be graph readable, and easy to modify)
- This file should contain card race, cost and type condition formulas.
- How should be calculation output, a json webservice to be consumed?
- Which UI tech for deckbuilder? Angular?
- How avoid false positive sinegies?
- Regular expressions should avoid  .* ou .+ (too generic).
- Hearthstone json api new version automatic download (at project root, not inside the jar).

	// "your (other )?minions", AoE effect
	// Aoe dmg? adjacent?
	// all minions cost (1) more.
	// your minions cost (1).
	// force an enemy minion to deal its damage to the (minions next to it).
	// this minion's attack is always equal to its health.
	// swap the attack and health of all minions.
	// "at the start of your turn, swap this minion with a random one in your hand.", return hand? hand into the battlefield?
	// your cards and powers that restore health now deal damage instead.
	// at the start of your turn, restore this minion to full health.
	// also damages the minions next to whomever he attacks.
	// deal damage to each minion equal to its attack. AoE dmg?
	// restore all characters to full health.
	// whenever you summon a minion, give it (+1/+1) and this loses 1 durability.//detectar o +1/+1!

DONE
loop
draw				costs (1) less for each other card in your hand.		whenever you draw a card,
then, it dies. horribly.	deathrattle:
deal .. damage randomly SPLIT between ALL	whenever this minion takes damage,		whenever this minion survives damage,
while you have a mech.		MECH			at the end of your turn, give another friendly mech +2/+2.	if you have a mech,		give your other mechs +2 attack.
??hero power costs 0?? 		you can use your hero power any number of times.	battlecry: replace your starting hero power with a better one.		your hero power can target minions.
spell damage +5			CHEAP? DMG SPELLS
COMBO INIMIGO, NAO ROLA! change an enemy minion's attack to 1.	destroy a random enemy minion with 2 or less attack.
murlocs				murloc
COMBO INIMIGO, NAO ROLA! set|change ...getAttack()		destroy a minion with 3 or less attack.
opponent draws|each player draws			costs (1) less for each card in your opponent's hand.
dragon type			dragon
overload			whenever you play a card with overload, gain +1/+1.
SUMMON				give your other minions +1/+1.
GIVE WEAPON			WEAPON
COMBO INIMIGO, NAO ROLA! +ATTACK				destroy a minion with an attack of 5 or more.
LOW COST CARD			gain +2/+2 for each card played earlier this turn.

ONESHOTHABILITIES (stealth, divine shield,charge)			at the end of each turn, summon all friendly minions that died this turn.	dies	
Kel'Thuzadat the end of each turn, summon all friendly minions that died this turn.	Reincarnatedestroy a minion, then return it to life with full health.
BAD BATTLECRY			RESSURECT|RESUMMON		summon a random friendly minion that died this game.
give ... stealth		good perma hability minion
summon beast, snakes, hounds, hyenas...		whenever a friendly beast dies, gain +2/+1.		costs (1) less for each minion that died this turn.
deathrattle:			battlecry: destroy the minions on either side of this minion
GOOD NON-BATTLE CRY MINION	when a friendly minion dies, put 2 copies of it into your hand.
destroy your opponent's weapon		equip a random weapon for each player.
HIGHCOSTMINION(NON-BATTLECRY)	swap this minion with a random one in your hand.	when a friendly minion dies, summon a random minion with the same cost.
HIGH ATTACK WEAPON		gain attack equal to the attack of your weapon.		costs (1) less per attack of your weapon.
taunt				give your taunt minions +2/+2.
GOOD MINION SPELLS		when an enemy casts a spell on a minion, summon a 1/3 as the new target.
HIGH COST CARD			draw a card and deal damage equal to its cost.
