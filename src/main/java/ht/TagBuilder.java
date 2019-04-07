package ht;

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

import ht.model.Card;
import ht.model.SynergyEdge;
import ht.model.Tag;

public class TagBuilder {
	static Map<String, Tag> tags = new HashMap<String, Tag>();
	List<SynergyEdge<Tag>> tagsSynergies = new ArrayList<SynergyEdge<Tag>>();
	private ClassLoader cl = this.getClass().getClassLoader();
	ScriptEngineManager mgr = new ScriptEngineManager();
	ScriptEngine engine = mgr.getEngineByName("JavaScript");

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

	public List<SynergyEdge<Tag>> getTagsSynergies() {
		return tagsSynergies;
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

	public static void main(String[] args) {
		new TagBuilder();
	}
}