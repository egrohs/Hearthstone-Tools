package hstools.components;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import hstools.model.Deck;
import hstools.model.constants.Meta;

public class MetaBuilder {
//TODO download do meta atual wild
	// https://tempostorm.com/hearthstone/meta-snapshot/wild
	// https://www.vicioussyndicate.com/wild-drr
	private LocalDate date;
	private Meta meta;
	private Map<Integer, Deck> decks = new LinkedHashMap<>();
}
