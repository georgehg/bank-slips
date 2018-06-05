package br.com.conta.bankslips.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.BankslipProjection;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.dto.BankslipPostDto;
import br.com.conta.bankslips.exceptions.BankslipNotFoundException;
import br.com.conta.bankslips.exceptions.BankslipValidationException;
import br.com.conta.bankslips.repository.BankslipRepository;


@Service
public class BankslipService {
	
	private final BankslipRepository bankslipRepo;

	public BankslipService(BankslipRepository bankslipRepo) {
		this.bankslipRepo = bankslipRepo;
	}
	
	public Bankslip createBankslip(BankslipPostDto bankslip) throws BankslipValidationException {
		return bankslipRepo.save(Bankslip.of(bankslip.getDueDate(),
									bankslip.getTotalInCents(),
									bankslip.getCustomer(),
									SlipStatus.valueOf(bankslip.getStatus())));
	}
	
	public BankslipProjection findBySerial(UUID serial) throws BankslipNotFoundException {
		return bankslipRepo.findBySerial(serial)
							.orElseThrow(() -> new BankslipNotFoundException("Banklsip not found with serial: " + serial));
	}

}
