package hstools.domain.entities;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = { "id" })
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class CardBNetDTO {
	private Long id; // temp id used by neo4j at runtime, must never be set by app
	@NotNull
	@JsonProperty("nameEn_US")
	private String nameEn_US;
	private List<String> text, name;
	private Integer collectible;
	private Integer classId;
	private Integer cardTypeId;
	private Integer cardSetId;
	private Integer rarityId;
	private Boolean isZilliaxFunctionalModule;
	private Set<Integer> multiClassIds;
	private Set<Integer> keywordIds;
}