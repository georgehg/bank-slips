package br.com.conta.bankslips.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.BankslipProjection;

public interface BankslipRepository extends JpaRepository<Bankslip, Long> {
	
	Optional<BankslipProjection> findBySerial(UUID serial);

}
