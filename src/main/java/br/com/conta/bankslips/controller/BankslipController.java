package br.com.conta.bankslips.controller;

import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import br.com.conta.bankslips.domain.BankslipDetailProjection;
import br.com.conta.bankslips.domain.BankslipProjection;
import br.com.conta.bankslips.exceptions.InvalidUUIDException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.conta.bankslips.dto.BankslipPostDto;
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

		UUID serial;

		try {
			serial = UUID.fromString(id);
		} catch (IllegalArgumentException e) {
			throw new InvalidUUIDException(id);
		}

		return ResponseEntity.ok(bankslipService.getDetailsBySerial(serial));
	}

	
}
