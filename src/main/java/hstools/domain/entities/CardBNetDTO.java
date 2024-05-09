package hstools.domain.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
	private Long id;
	private US name, text;
	private Integer collectible;
	private Integer classId;
	private Integer cardTypeId;
	private Integer cardSetId;
	private Integer rarityId;
	private Boolean isZilliaxFunctionalModule;
	private Set<Integer> multiClassIds;
	private Set<Integer> keywordIds;

	@Data
	class US {
		String en_US;
	}
}