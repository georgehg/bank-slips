package br.com.conta.bankslips.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.BankslipDetailProjection;
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
		try {
			return bankslipRepo.save(
					Bankslip.of(LocalDate.parse(bankslip.getDueDate()),
								BigDecimal.valueOf(Long.valueOf(bankslip.getTotalInCents())),
								bankslip.getCustomer(),
								SlipStatus.valueOf(bankslip.getStatus())));
		} catch (NullPointerException | IllegalArgumentException e) {
			throw new BankslipValidationException(e.getMessage());
		}
	}

	public List<BankslipProjection> getAllBankslips() {
		return bankslipRepo.findAllBy(BankslipProjection.class);
	}

	public BankslipDetailProjection getDetailsBySerial(UUID serial) throws BankslipNotFoundException {
		return bankslipRepo.findBySerial(serial, BankslipDetailProjection.class)
				.orElseThrow(() -> new BankslipNotFoundException(serial.toString()));
	}
	
	public void payBankslip(UUID serial) throws BankslipNotFoundException {
		bankslipRepo.findBySerial(serial)
					.map(bankslip -> bankslip.pay())
					.map(bankslipRepo::save)
					.orElseThrow(() -> new BankslipNotFoundException(serial.toString()));
	}
	
	public void cancelBankslip(UUID serial) throws BankslipNotFoundException {
		bankslipRepo.findBySerial(serial)
					.map(bankslip -> bankslip.cancel())
					.map(bankslipRepo::save)
					.orElseThrow(() -> new BankslipNotFoundException(serial.toString()));
	}


}
