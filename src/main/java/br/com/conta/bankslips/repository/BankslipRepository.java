package br.com.conta.bankslips.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.conta.bankslips.domain.Bankslip;

public interface BankslipRepository extends JpaRepository<Bankslip, Long> {

	<T> List<T> findAllBy(Class<T> projection);
	
	Optional<Bankslip> findBySerial(UUID serial);
	
	<T> Optional<T> findBySerial(UUID serial, Class<T> projection);

}
