package hstools.domain.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import hstools.domain.entities.Card;
import hstools.domain.entities.Card.CLASS;
import hstools.domain.entities.SynergyEdge;
import hstools.domain.entities.Tag;
import lombok.Getter;

/**
 * Load tags and synergies from google sheets. Should it be on scrap or
 * datascience service?
 * 
 * @author EGrohs
 *
 */
@Component("Tags")
@DependsOn(value = { "Cards", "Scrap" })
public class TagBuilder {
	@Autowired
	private CardService cb;
	@Autowired
	private ScrapService ss;
	@Getter
	private Map<String, Tag> tags = new HashMap<String, Tag>();
	private List<SynergyEdge<Tag>> tagsSynergies = new ArrayList<SynergyEdge<Tag>>();
	private ClassLoader cl = this.getClass().getClassLoader();
	private ScriptEngineManager mgr = new ScriptEngineManager();
	private ScriptEngine engine = mgr.getEngineByName("JavaScript");

	// public TagBuilder() {
	@PostConstruct
	public void init() {
		tags = ss.importTags();
		buildCardTags();
	}

	@Deprecated
	private void loadTags() {
		Scanner sc = null;
		try {
			sc = new Scanner(new File(cl.getResource("tags/hstags.csv").getFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// skip header row
		sc.nextLine();
		Long id = 0L;
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			String tag = "", regex = "", expr = "", description = "";
			try {
				tag = (line[0]);
				regex = (line[1]);
				expr = (line[2]);
				description = line[3];
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
			}
			tags.put(tag, new Tag(id, tag, regex, expr, description));
			id++;
		}
		sc.close();
		System.out.println(tags.size() + " tags loaded.");
	}

	/**
	 * Generate all cards Tags.
	 */
	public void buildCardTags() {
		try {
			for (Card c : cb.getCards()) {
				for (Tag tag : tags.values()) {
					String expr = c.replaceVars(tag.getExpr());
					if ((expr == null || expr.equals("") || (boolean) engine.eval(expr) == true)
							&& Pattern.compile(tag.getRegex()).matcher(c.getText()).find()) {
						c.getTags().add(tag);
					}
				}
			}
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(tags.keySet().size() + " tags created.");
	}

	// Depends on previous tagsSynergies calculated
	public List<SynergyEdge<Card>> sinergias(Card c1, boolean everyCard) {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		for (SynergyEdge<Tag> ts : tagsSynergies) {
			Tag tag = null;
			Tag tag1 = (Tag) ts.getE1();
			Tag tag2 = (Tag) ts.getE2();
			if (c1.getTags().contains(tag1)) {
				tag = tag2;
			} else if (c1.getTags().contains(tag2)) {
				tag = tag1;
			}
			if (tag != null) {
				for (Card c2 : cb.getCards()) {
					if ((everyCard || c2.getClasse() == CLASS.NEUTRAL || c1.getClasse() == c2.getClasse())
							&& c2.getTags().contains(tag)) {
						SynergyEdge<Card> cs = new SynergyEdge<Card>(c1, c2,
								c2.getText() + "\t" + tag1.getRegex() + " + " + tag2.getRegex(), ts.getWeight());
						cardsSynergies.add(cs);

						System.out.println(cs);
					}
				}
			}
		}
		return cardsSynergies;
	}

	/**
	 * Generate all synergies for card c1
	 * 
	 * @param everyCard if true, generate all synergies, class independ.
	 */
	public List<SynergyEdge<Card>> generateCardSynergies(Card c1, boolean everyCard) {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		if (c1 != null && !c1.isCalculada()) {
			cardsSynergies.addAll(sinergias(c1, everyCard));
			// Collections.sort(Sinergias.cardsSynergies);
			c1.setCalculada(true);
		}
		return cardsSynergies;
	}

	/**
	 * Generate all cards synergies.
	 */
	private void generateCardsSynergies() {
		List<SynergyEdge<Card>> cardsSynergies = new ArrayList<SynergyEdge<Card>>();
		System.out.println("generateCardsSynergies...");
		long ini = System.currentTimeMillis();
		for (Card c1 : cb.getCards()) {
			cardsSynergies.addAll(generateCardSynergies(c1, false));
		}
		// imprimSins();

		// Collections.sort(Sinergias.cardsSynergies);
		System.out.println(cardsSynergies.size() + " sinergies calculated from parsed card texts in "
				+ (System.currentTimeMillis() - ini) / 60000 + " minutes.");
	}
}