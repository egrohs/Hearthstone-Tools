package hstools.components;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hstools.GoogleSheets;
import hstools.model.Card;
import hstools.model.SynergyEdge;
import hstools.model.Tag;
import hstools.model.Card.CLASS;
import lombok.Getter;

@Component
public class TagBuilder {
	@Autowired
	private CardService cb;
	@Getter
	private Map<String, Tag> tags = new HashMap<String, Tag>();
	private List<SynergyEdge<Tag>> tagsSynergies = new ArrayList<SynergyEdge<Tag>>();
	private ClassLoader cl = this.getClass().getClassLoader();
	private ScriptEngineManager mgr = new ScriptEngineManager();
	private ScriptEngine engine = mgr.getEngineByName("JavaScript");

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
			tags.put(tag, new Tag(tag, regex, expr, description));
		}
		sc.close();
		System.out.println(tags.size() + " tags loaded.");
	}

	// TODO armazenar localmente as tags evitando buscar se mesma versao ou sem
	// internet.
	/** Import tags from google spreadsheet. */
	public void importTags() {
		List<List<Object>> values = null;
		try {
			values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "TAGS!A2:C");
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}

		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			for (List<Object> row : values) {
				String name = (String) row.get(0);
				String regex = row.size() > 1 ? (String) row.get(1) : "";
				String expr = row.size() > 2 ? (String) row.get(2) : "";
				String desc = row.size() > 3 ? (String) row.get(3) : "";
				Tag t = tags.get(name);
				if (t == null)
					tags.put(name, new Tag(name, regex, expr, desc));

			}
		}
		System.out.println(tags.size() + " tags imported.");
	}

	/**
	 * Import all card tags form google sheet
	 */
	private void importTagSinergies() {
		List<List<Object>> values = null;
		try {
			values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "TAG_EDGES!A2:D");
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}

		for (List<Object> row : values) {
			String source = (String) row.get(0);
			String taget = row.size() > 1 ? (String) row.get(1) : "";
			String label = row.size() > 2 ? (String) row.get(2) : "";
			Float weight = row.size() > 3 ? (Float) row.get(3) : 0.0f;
			Tag t1 = tags.get(source);
			Tag t2 = tags.get(taget);
			if (t2 != null) {
				tagsSynergies.add(new SynergyEdge<Tag>(t1, t2, label, weight));
			}
		}
		for (Tag t1 : tags.values()) {
			if (t1.getRegex() != null && !"".equals(t1.getRegex())) {
				// Almost every tag synergies with itself.
				tagsSynergies.add(new SynergyEdge<Tag>(t1, t1, t1.getName(), 0.0f));
			}
		}
		System.out.println(tagsSynergies.size() + " tags synergies imported.");
	}

	public void importTags2() {
		List<List<Object>> values = null;
		try {
			values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "CARD_FUNC!A2:B");
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
		}

		if (values == null || values.isEmpty()) {
			System.out.println("No data found.");
		} else {
			for (List<Object> row : values) {
				String name = (String) row.get(0);
				String regex = row.size() > 1 ? (String) row.get(1) : "";
				Tag t = tags.get(name);
				if (t == null)
					tags.put(name, new Tag(name, regex));

			}
		}
		System.out.println(tags.size() + " tags2 imported.");
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
	}
	
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