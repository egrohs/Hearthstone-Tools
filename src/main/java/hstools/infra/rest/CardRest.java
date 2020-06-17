package hstools.infra.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import hstools.domain.components.CardService;
import hstools.domain.entities.Card;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RequestMapping(value = "/api/v1")
//@Transactional(rollbackOn = { Exception.class })
@Validated
@RestController
/**
 * External services for card quering.
 */
@Api(description = "External services for card quering.")
public class CardRest {
	@Autowired
	private CardService cs;

	@GetMapping("/cards")
	@ResponseBody
	@ApiOperation(value = "Get all collectible cards", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "All cards returned"),
			@ApiResponse(code = 500, message = "Out of service???"), })
	public ResponseEntity<List<Card>> getCards() {
		return ResponseEntity.ok(cs.getCards());
	}
}