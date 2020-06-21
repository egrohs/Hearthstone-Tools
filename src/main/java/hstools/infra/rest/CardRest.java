package hstools.infra.rest;

import org.springframework.beans.factory.annotation.Autowired;

import hstools.domain.components.CardService;

//@RequestMapping(value = "/api/v1")
////@Transactional(rollbackOn = { Exception.class })
//@Validated
//@RestController
/**
 * External services for card quering.
 */
//@Api(description = "External services for card quering.")
public class CardRest {
	@Autowired
	private CardService cs;

//	@GetMapping("/cards")
//	@ResponseBody
//	@ApiOperation(value = "Get all collectible cards", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "All cards returned"),
//			@ApiResponse(code = 500, message = "Out of service???"), })
//	public ResponseEntity<List<Card>> getCards() {
//		return ResponseEntity.ok(cs.getCards());
//	}
}