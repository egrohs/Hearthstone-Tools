package hcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hcs.model.Card;
import hcs.model.Card.CLASS;
import hcs.model.Entity;
import hcs.model.Mechanic;
import hcs.model.Sinergy;
import hcs.model.Tag;

public class TagBuilder {
    
    static Map<String, Mechanic> mechanics = new HashMap<String, Mechanic>();
    static List<Sinergy<Mechanic>> mechanicsSynergies = new ArrayList<Sinergy<Mechanic>>();
    static Map<String, Tag> tags = new HashMap<String, Tag>();
    static List<Sinergy<Tag>> tagsSynergies = new ArrayList<Sinergy<Tag>>();

    public TagBuilder() {
	// new SinergyFromGames();
	// new SinergyFromText();
	parseCardsText2Tags();
	calcTags();
	//printTags();
    }

    

    @Deprecated
    public static Set<Card> getMechsCards(Map<Mechanic, Integer> mechs, int manaRestante, CLASS opo) {
	Set<Card> cs = new HashSet<Card>();
	for (Mechanic mecanica : new ArrayList<Mechanic>(mechs.keySet())) {
	    for (Sinergy s : mechanicsSynergies) {
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

    

    // public static void main(String[] args) {
    // TODO charge e DD.
    // secret: when an enemy minion attacks, return it to its owner's hand
    // and it costs (2) more.
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

    public static Map<Pattern, Integer> calc(int manaRestante, CLASS opo) {
	Map<Pattern, Integer> res = new HashMap<Pattern, Integer>();
	for (Card card : CardBuilder.cards) {
	    // System.out.println(card.getText());
	    if (CLASS.contem(opo, card.getClasse()) && card.getCost() <= manaRestante) {
		for (Pattern p : pts) {
		    Matcher matcher = p.matcher(card.getText());
		    Integer i = res.get(p);
		    if (matcher.find()) {
			res.put(p, i == null ? 1 : (i + 1));
		    }
		}
	    }
	}
	return res;
    }

    /**
     * Lê arquivo de grafo tgf contendo relacionamento entre as mecanicas.
     * 
     * @param file Arquivo tgf das mecanicas.
     */
    @Deprecated
    private void readMechanics() {
	Scanner sc = null;
	try {
	    sc = new Scanner(new FileReader(new File(CardBuilder.cl.getResource("mechanics/hs.tgf").getFile())));
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
		    mechanicsSynergies.add(new Sinergy(m1, m1, 1, m1.regex + "+" + m1.regex));
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
		    mechanicsSynergies.add(new Sinergy(m1, m2, v, m1.regex + "+" + m2.regex));
		    // mechanicsSynergies.add(new Synergy(mechanics.get(s[1]),
		    // mechanics.get(s[0]), v));
		}
	    }
	} catch (FileNotFoundException e) {
	    // System.out.println("Input file " + file + " not found");
	    e.printStackTrace();
	    sc.close();
	    System.exit(1);
	} finally {
	    sc.close();
	}
    }

    private void calcTags() {
	List<List<Object>> values = null;
	try {
	    values = GoogleSheets.getDados("1WNcRrDzxyoy_TRm9v15VSGwEiRPqJhUhReq0Wh8Jp14", "TAGS!A2:C");
	} catch (GeneralSecurityException | IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	if (values == null || values.isEmpty()) {
	    System.out.println("No data found.");
	} else {
	    // System.out.println("Name, Major");
	    for (List row : values) {
		String name = (String) row.get(0);
		String regex = (String) row.get(1);
		String tts = row.size() > 2 ? (String) row.get(2) : "";
		Tag t = tags.get(name);
		if (t == null)
		    t = new Tag(name, regex, tts);
		tags.put(name, t);
		// Print columns A and C, which correspond to indices 0 and 2.
		// System.out.printf("%s, %s\n", row.get(0), row.get(2));
		//System.out.println(t);
	    }
	}

	for (Tag tag : tags.values()) {
	    for (String t : tag.getTags().split(",")) {
		Tag t1 = tags.get(tag.getName());
		Tag t2 = tags.get(t);
		if (t2 != null) {
		    tagsSynergies.add(new Sinergy<>(t1, t2, 0));
		}
	    }
	}
    }

    @Deprecated
    private static void printM2M() {
	for (Sinergy s : mechanicsSynergies) {
	    System.out.println(((Mechanic) s.getE1()).regex + "\t" + ((Mechanic) s.getE2()).regex);
	}
    }

    private void printTags() {
	for (Sinergy sinergia : tagsSynergies) {
	    System.out.println(sinergia);
	}
    }

    /**
     * Lê os textos das cartas, gerando suas Tags.
     */
    private static void parseCardsText2Tags() {
	for (Tag m : tags.values()) {
	    for (Card c : CardBuilder.cards) {
		if (Pattern.compile(m.getRegex()).matcher(c.getText()).find()) {
		    c.getTags().add(m);
		}
	    }
	}
    }

    



    @Deprecated
    private static void printQntMAffinities() {
	for (Mechanic m : mechanics.values()) {
	    int cont = 0;
	    // System.out.println(m.regex + "\t" + m.aff.size());
	    for (Card card : CardBuilder.cards) {
		if (card.getMechanics().contains(m)) {
		    cont++;
		}
	    }
	    System.out.println(m.regex + "\t" + cont);
	}
    }

    public static void main(String[] args) {
	CardBuilder.leCards();
	Map<Pattern, Integer> m = TagBuilder.calc(3, CLASS.MAGE);
	for (Pattern p : m.keySet()) {
	    System.out.println(m.get(p) + "\t" + p);
	}
    }
}