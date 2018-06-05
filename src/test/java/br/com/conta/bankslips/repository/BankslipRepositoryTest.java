package br.com.conta.bankslips.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import br.com.conta.bankslips.domain.BankslipDetailProjection;
import br.com.conta.bankslips.domain.BankslipProjection;
import org.junit.Before;
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

	@Autowired
	private BankslipRepository repo;

	@Before
	public void setup() {
		repo.deleteAll();
	}

	@Test
	public void shoulPersistBankslip() throws BankslipValidationException, ParseException {
		Bankslip banckSlip = repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING));

		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getId()).isNotNull();
		assertThat(banckSlip.getSerial()).isNotNull();
	}

	@Test
	public void shouldReturnBankslipList() throws BankslipValidationException, ParseException {
		repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING));
		repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Ford Prefect Company", SlipStatus.PENDING));

		List<BankslipProjection> projections = repo.findAllBy(BankslipProjection.class);
		assertThat(projections).isNotEmpty();
		assertThat(projections).hasSize(2);
	}

	@Test
	public void shouldReturnBankslipProjection() throws BankslipValidationException, ParseException {
		Bankslip banckSlip = repo.save(Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Ford Prefect Company", SlipStatus.PENDING));

		BankslipProjection projection = repo.findBySerial(banckSlip.getSerial(), BankslipProjection.class).get();

		assertThat(projection).isNotNull();
		assertThat(projection.getSerial()).isEqualTo(banckSlip.getSerial().toString());
		assertThat(projection.getDueDate()).isEqualTo(banckSlip.getDueDate());
		assertThat(projection.getTotalInCents()).isEqualByComparingTo(banckSlip.getTotalInCents());
		assertThat(projection.getCustomer()).isEqualTo(banckSlip.getCustomer());
	}

	@Test
	public void shouldReturnBankslipDetailProjection() throws BankslipValidationException, ParseException {
		Bankslip banckSlip = repo.save(Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING));

		BankslipDetailProjection projection = repo.findBySerial(banckSlip.getSerial(), BankslipDetailProjection.class).get();

		assertThat(projection).isNotNull();
		assertThat(projection.getSerial()).isEqualTo(banckSlip.getSerial().toString());
		assertThat(projection.getDueDate()).isEqualTo(banckSlip.getDueDate());
		assertThat(projection.getTotalInCents()).isEqualByComparingTo(banckSlip.getTotalInCents());
		assertThat(projection.getCustomer()).isEqualTo(banckSlip.getCustomer());
		assertThat(projection.getFine()).isEqualTo(banckSlip.getFine());
		assertThat(projection.getStatus()).isEqualTo(banckSlip.getStatus().name());
	}

}
