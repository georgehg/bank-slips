package br.com.conta.bankslips.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import br.com.conta.bankslips.domain.Bankslip;
import br.com.conta.bankslips.domain.SlipStatus;
import br.com.conta.bankslips.exceptions.BankslipValidationException;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BankslipRepositoryTest {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@Autowired
	private BankslipRepository repo;

	@Test
	public void shoulPersistBankslip() throws BankslipValidationException, ParseException {
		Bankslip banckSlip = repo.save(Bankslip.of(formatter.parse("2018-01-01"), BigDecimal.valueOf(100000L), "Trillian Company", SlipStatus.PENDING));
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getId()).isEqualTo(1L);
		assertThat(banckSlip.getSerial()).isNotNull();
	}

}
