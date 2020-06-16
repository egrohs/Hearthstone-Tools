package hstools.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import hstools.model.Deck.Formato;
import lombok.Data;

@Data
public class Expansion {
	private String name, shortname;
	private Formato f;
	private LocalDate release;
	private int qntCards;

	public Expansion(String name, String release, String endStd) {
		// name = name.replaceAll("\\[\\w+\\]", "");
		if (name.contains("("))
			this.shortname = name.substring(name.indexOf("(") + 1, name.indexOf(")")).toUpperCase();
		this.name = name.replaceFirst("\\s?\\(.+", "");
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM dd, yyyy");
//		String date = "16/08/2016";
//		LocalDate localDate = LocalDate.parse(date, formatter);
	}
}