package hstools.domain.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RapidApiInfo {
	private String patch; // 26.2.0.174258",
	private List<String> classes; // Enum TODO enum nao seria dinamico
	// @jsonproperty(".shortname")
	private List<String> sets; // Expansion
	private List<String> standard; // Expansion
	private List<String> wild; // Expansion
	private List<String> types; // minion, spell
	private List<String> factions; // alliance, horde, neutral
	private List<String> qualities; // rarity
	private List<String> races;
}