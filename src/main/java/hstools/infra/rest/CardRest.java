package hstools.infra.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hstools.domain.components.CardComponent;
import hstools.domain.components.SynergyBuilder;
import hstools.domain.dtos.GraphJson;
import hstools.domain.entities.Card;

@RequestMapping(value = "/api/v1")
////@Transactional(rollbackOn = { Exception.class })
@Validated
@RestController
/**
 * External services for card quering.
 */
//@Api(description = "External services for card quering.")
public class CardRest {
	@Autowired
	private CardComponent cardComp;
	@Autowired
	private SynergyBuilder synComp;

	@GetMapping("/cards")
	@ResponseBody
//	@ApiOperation(value = "Get all collectible cards", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "All cards returned"),
//			@ApiResponse(code = 500, message = "Out of service???"), })
	public ResponseEntity<List<Card>> getCards() {
		List<Card> cards = cardComp.buildCards();
		cardComp.importTags();
		cardComp.buildAllCardTags();
		synComp.loadTagSinergies();
		return ResponseEntity.ok(cards);
	}

	@GetMapping("/graph")
	@ResponseBody
	public ResponseEntity<GraphJson> cardSyns(String idsORname) {
		// TODO Auto-generated method stub
//		List<Card> syns = synComp.sinergias(cardComp.getCard(idsORname), false);
//		GraphJson g = new GraphJson(syns);
		return null;//ResponseEntity.ok(g);
	}
}