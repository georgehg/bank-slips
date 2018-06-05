package br.com.conta.bankslips.controller;

import java.net.URI;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.exceptions.BankslipValidationException;
import br.com.conta.bankslips.service.BankslipService;

@RestController
@RequestMapping("/bankslips")
public class BankslipController {
	
	private final BankslipService bankslipService;

	public BankslipController(BankslipService bankslipService) {
		this.bankslipService = bankslipService;
	}
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BankslipPostDto> createBankslip(@RequestBody(required = true) @Valid BankslipPostDto bankslip)
			throws BankslipValidationException {
		UUID newBankslipSerial = bankslipService.createBankslip(bankslip).getSerial();
		
		return ResponseEntity.created(URI.create("localhost:8080/rest/bankslips/" + String.valueOf(newBankslipSerial))).build();
	}
	
}
