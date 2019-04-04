package ht;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import ht.model.Card;
import ht.model.Card.CLASS;
import ht.model.Mechanic;
import ht.model.Sinergy;
import ht.model.Tag;

public class TagBuilder {
	Map<String, Mechanic> mechanics = new HashMap<String, Mechanic>();
	List<Sinergy<Mechanic>> mechanicsSynergies = new ArrayList<Sinergy<Mechanic>>();
	static Map<String, Tag> tags = new HashMap<String, Tag>();
	Set<Sinergy<Tag>> tagsSynergies = new HashSet<Sinergy<Tag>>();
	private ClassLoader cl = this.getClass().getClassLoader();

	public TagBuilder() {
		// loadTags();
		importTags();
		importTagSinergies();
		// calcSinergies();
		// printTags();
	}

	public static Map<String, Tag> getTags() {
		return tags;
	}

	public Set<Sinergy<Tag>> getTagsSynergies() {
		return tagsSynergies;
	}

	@Deprecated
	public Set<Card> getMechsCards(Map<Mechanic, Integer> mechs, int manaRestante, CLASS opo) {
		Set<Card> cs = new HashSet<Card>();
		for (Mechanic mecanica : new ArrayList<Mechanic>(mechs.keySet())) {
			for (Sinergy<Mechanic> s : mechanicsSynergies) {
				if (s.getE1() == mecanica) {
					mechs.put(mecanica, 0);
				}
				if (s.getE2() == mecanica) {
					mechs.put(mecanica, 0);
				}
			}
		}
		for (Mechanic mecanica : mechs.keySet()) {
			for (Card c1 : CardBuilder.cards) {
				if (c1.getMechanics().contains(mecanica) && CLASS.contem(opo, c1.getClasse())
						&& c1.getCost() <= manaRestante) {
					cs.add(c1);
				}
			}
		}
		return cs;
	}

	@Deprecated
	static Pattern[] pts = new Pattern[] {
			Pattern.compile("return (a|an|all) (enemy )?minion(s)? to ((its|their) owner's|your opponent's) hand"),
			Pattern.compile("silence (a|all) (enemy )?(minion(s)?|[race])(\\.|\\, |\\s)?(with [ability])?"),
			// contem random antes de "into"?
			// secret: after your opponent plays a minion, transform it into
			// a 1/1
			// sheep. (somente esse caso de it)
			Pattern.compile("transform (a|all|another random) (enemy )?(minion(s)? )?into"),
			// control a secret? random control?
			Pattern.compile("(take|gain) control of"), Pattern.compile(
					"destroy (a|an|all|\\d) (random )?(damaged |frozen |legendary )?(enemy |other )?minion(s)?(\\s|\\.|\\,)(with (taunt|\\d+ or less attack|an attack of \\d+ or more))?") };
	// }

	/**
	 * Lê arquivo de grafo tgf contendo relacionamento entre as mecanicas.
	 * 
	 * @param file Arquivo tgf das mecanicas.
	 */
	@Deprecated
	private void readMechanics() {
		Scanner sc = null;
		try {
			sc = new Scanner(new FileReader(new File(cl.getResource("mechanics/hs.tgf").getFile())));
			boolean nodes = true;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if ("#".equals(line)) {
					nodes = false;
					continue;
				}
				if (nodes) {
					String id = line.substring(0, line.indexOf(" "));
					String regex = line.substring(line.indexOf(" ") + 1);
					// cria nodo
					// ns.put(s[0], new Mechanic());
					Mechanic m1 = new Mechanic(id, regex);
					mechanics.put(id, new Mechanic(id, regex));
					// auto sinergia
					mechanicsSynergies.add(new Sinergy<Mechanic>(m1, m1, 1, m1.getRegex() + "+" + m1.getRegex()));
				} else {
					String[] s = line.split(" ");
					Float v = 0f;
					try {
						v = Float.parseFloat(s[2]);
					} catch (Exception e) {
					}
					// TODO cria vinculo bidirecional?
					Mechanic m1 = mechanics.get(s[0]);
					Mechanic m2 = mechanics.get(s[1]);
					mechanicsSynergies.add(new Sinergy<Mechanic>(m1, m2, v, m1.getRegex() + "+" + m2.getRegex()));
					// mechanicsSynergies.add(new Synergy(mechanics.get(s[1]),
					// mechanics.get(s[0]), v));
				}
			}
		} catch (FileNotFoundException e) {
			// System.out.println("Input file " + file + " not found");
			e.printStackTrace();
			System.exit(1);
		} finally {
			sc.close();
		}
	}

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

	/** Import tags from google spreadsheet. */
	private void importTags() {
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
				tagsSynergies.add(new Sinergy<Tag>(t1, t2, label, weight));
			}
			// Every tag sinergies with itself.
			tagsSynergies.add(new Sinergy<Tag>(t1, t1, label, weight));
		}
	}

	@Deprecated
	private void printM2M() {
		for (Sinergy<Mechanic> s : mechanicsSynergies) {
			System.out.println(((Mechanic) s.getE1()).getRegex() + "\t" + ((Mechanic) s.getE2()).getRegex());
		}
	}

	/**
	 * Generate all cards Tags.
	 */
	void buildCardTags() {
		try {
			for (Card c : CardBuilder.cards) {
				for (Tag tag : tags.values()) {
					String expr = c.replaceVars(tag.getExpr());
					if ((expr.equals("") || (boolean) engine.eval(expr) == true)
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

	ScriptEngineManager mgr = new ScriptEngineManager();
	ScriptEngine engine = mgr.getEngineByName("JavaScript");
//return (boolean) engine.eval(foo);

	@Deprecated
	private void printQntMAffinities() {
		for (Mechanic m : mechanics.values()) {
			int cont = 0;
			// System.out.println(m.getRegex() + "\t" + m.aff.size());
			for (Card card : CardBuilder.cards) {
				if (card.getMechanics().contains(m)) {
					cont++;
				}
			}
			System.out.println(m.getRegex() + "\t" + cont);
		}
	}

	public static void main(String[] args) {
		new TagBuilder();
	}
}