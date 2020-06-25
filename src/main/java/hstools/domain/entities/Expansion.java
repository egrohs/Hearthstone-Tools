package hstools.domain.entities;

import java.time.LocalDate;

import hstools.Constants.Format;
import lombok.Data;

@Data
public class Expansion extends Node {
	private String shortname;
	private Format format;
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