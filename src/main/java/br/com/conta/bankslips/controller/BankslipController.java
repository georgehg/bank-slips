package br.com.conta.bankslips.controller;

import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conta.bankslips.domain.BankslipDetailProjection;
import br.com.conta.bankslips.domain.BankslipProjection;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.exceptions.InvalidUUIDException;
import br.com.conta.bankslips.service.BankslipService;

@RestController
@RequestMapping("/bankslips")
public class BankslipController {
	
	private final BankslipService bankslipService;

	public BankslipController(BankslipService bankslipService) {
		this.bankslipService = bankslipService;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createBankslip(@RequestBody(required = true) @Valid BankslipPostDto bankslip)
			throws Exception {

		String newBankslipSerial = bankslipService.createBankslip(bankslip).getSerial().toString();
		
		return ResponseEntity.created(URI.create("localhost:8080/rest/bankslips/" + newBankslipSerial))
							.body(new AbstractMap.SimpleEntry<>("message", "Bankslip created"));
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BankslipProjection>> createBankslip() {
		return ResponseEntity.ok(bankslipService.getAllBankslips());
	}

	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankslipDetailProjection> getDetailsBySerial(@PathVariable(value = "id") String id) throws Exception {
		UUID serial = validateSerial(id);
		return ResponseEntity.ok(bankslipService.getDetailsBySerial(serial));
	}
	
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> payOrCancelBankSlip(@PathVariable(value = "id") String id,
													@RequestBody(required = true) Map<String, String> status) throws Exception {
		UUID serial = validateSerial(id);
		
		switch(SlipStatus.valueOf(status.get("status"))) {
			case PAID:
				bankslipService.payBankslip(serial);
				return ResponseEntity.ok(new AbstractMap.SimpleEntry<>("message", "Bankslip paid"));
			case CANCELED:
				bankslipService.cancelBankslip(serial);
				return ResponseEntity.ok(new AbstractMap.SimpleEntry<>("message", "Bankslip canceled"));
			default:
				return ResponseEntity.badRequest()
									.body(new AbstractMap.SimpleEntry<>("message", "Invalid Status to update Bankslip: " + status.get("status")));
		}
		
	}
	
	private UUID validateSerial(String value) throws InvalidUUIDException {
		try {
			return UUID.fromString(value);
		} catch (IllegalArgumentException e) {
			throw new InvalidUUIDException(value);
		}
	}

	
}
