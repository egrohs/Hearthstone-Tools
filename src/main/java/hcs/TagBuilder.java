package hcs;

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

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Mechanic;
import hcs.model.Sinergy;
import hcs.model.Tag;

public class TagBuilder {
	Map<String, Mechanic> mechanics = new HashMap<String, Mechanic>();
	List<Sinergy<Mechanic>> mechanicsSynergies = new ArrayList<Sinergy<Mechanic>>();
	static Map<String, Tag> tags = new HashMap<String, Tag>();
	List<Sinergy<Tag>> tagsSynergies = new ArrayList<Sinergy<Tag>>();
	private ClassLoader cl = this.getClass().getClassLoader();

	public TagBuilder() {
		loadTags();
		calcSinergies();
		// importTags();
		// printTags();
	}

	public static Map<String, Tag> getTags() {
		return tags;
	}
	
	public List<Sinergy<Tag>> getTagsSynergies() {
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
		while (sc.hasNextLine()) {
			String[] line = sc.nextLine().split("\t");
			String tag = "", regex = "", type = "", cost = "", attack = "", hp = "", sinergies = "", description = "";
			try {
				tag = (line[0]);
				regex = (line[1]);
				type = (line[2]);
				cost = (line[3]);
				attack = line[4];
				hp = line[5];
				sinergies = line[6];
				description = line[7];
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
			}
			tags.put(tag, new Tag(tag, regex, type, cost, attack, hp, sinergies, description));
		}
		sc.close();
		System.out.println(tags.size() + " tags loaded.");
	}

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
				String regex = (String) row.get(1);
				String tts = row.size() > 2 ? (String) row.get(2) : "";
				Tag t = tags.get(name);
//				if (t == null)
//					t = new Tag(name, regex, tts);
//				tags.put(name, t);

			}
		}
		System.out.println(tags.size() + " tags imported.");
	}

	private void calcSinergies() {
		for (Tag tag : tags.values()) {
			for (String s : tag.getSinergies().split(",")) {
				Tag t1 = tags.get(tag.getName());
				Tag t2 = tags.get(s);
				if (t2 != null) {
					tagsSynergies.add(new Sinergy<Tag>(t1, t2, 0));
				}
			}
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
		for (Card c : CardBuilder.cards) {
			// System.out.println(c);
			for (Tag tag : tags.values()) {
				if (eval("\"" + c.getType() + "\"" + tag.getType()) && eval(c.getCost() + tag.getCost())
						&& eval(c.getAttack() + tag.getAttack()) && eval(c.getHealth() + tag.getHp())
						&& (c.getRace().contains(tag.getName())
								|| Pattern.compile(tag.getRegex()).matcher(c.getText()).find())) {
					c.getTags().add(tag);
				}
			}
		}
	}

	ScriptEngineManager mgr = new ScriptEngineManager();
	ScriptEngine engine = mgr.getEngineByName("JavaScript");

	private boolean eval(String foo) {
		// System.out.println(foo);
		if (foo.contains("=") || foo.contains("<") || foo.contains(">")) {
			try {
				// System.out.println(engine.eval(foo));
				return (boolean) engine.eval(foo);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				// System.exit(0);
			}
		}
		return true;
	}

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