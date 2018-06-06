package br.com.conta.bankslips.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class BankslipTest {

	@Test
	public void shouldInstantiateBankSlip() throws ParseException {
		Bankslip banckSlip = Bankslip.of(LocalDate.parse("2018-01-01"), BigDecimal.valueOf(100000), "Trillian Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getDueDate()).isEqualTo(LocalDate.parse("2018-01-01"));
		assertThat(banckSlip.getCustomer()).isEqualTo("Trillian Company");
		assertThat(banckSlip.getTotalInCents()).isEqualTo(BigDecimal.valueOf(100000));
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
	}
	
	@Test
	public void shoulMarkBankSlipPaid() throws ParseException {
		Bankslip banckSlip = Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
		
		banckSlip.pay();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PAID);
	}
	
	@Test
	public void shoulMarkBankSlipCanceled() throws ParseException {
		Bankslip banckSlip = Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
		
		banckSlip.cancel();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.CANCELED);
	}

	@Test
	public void shoulCalculateFine_ZeroFine() throws Exception {
		Bankslip banckSlip = Bankslip.of(LocalDate.now().plus(30, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Zaphod Company", SlipStatus.PENDING);
		assertThat(banckSlip.getFine()).isZero();
	}

	@Test
	public void shoulCalculateFine_ZeroFine_OnDueDate() throws Exception {
		Bankslip banckSlip = Bankslip.of(LocalDate.now(), BigDecimal.valueOf(100000), "Zaphod Company", SlipStatus.PENDING);
		assertThat(banckSlip.getFine()).isZero();
	}

    @Test
    public void shoulCalculateFine_5DaysElapse() throws Exception {
        Bankslip banckSlip = Bankslip.of(LocalDate.now().minus(5, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Zaphod Company", SlipStatus.PENDING);
        assertThat(banckSlip.getFine()).isEqualTo(BigDecimal.valueOf(2500));
    }

	@Test
	public void shoulCalculateFine_20DaysElapse() throws Exception {
		Bankslip banckSlip = Bankslip.of(LocalDate.now().minus(20, ChronoUnit.DAYS), BigDecimal.valueOf(100000), "Zaphod Company", SlipStatus.PENDING);
		assertThat(banckSlip.getFine()).isEqualTo(BigDecimal.valueOf(20000));
	}
	
	@Test
	public void shouldIssueErrorForNullDueDate() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(null, BigDecimal.valueOf(200000), "Zaphod Company", SlipStatus.PENDING))
		.isInstanceOf(NullPointerException.class)
		.hasMessage("Field due_date can not be null");
	}
	
	@Test
	public void shouldIssueErrorForNullCents() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(LocalDate.parse("2018-02-01"), null, "Zaphod Company", SlipStatus.PENDING))
		.isInstanceOf(NullPointerException.class)
		.hasMessage("Field total_in_cents can not be null");
	}
	
	@Test
	public void shouldIssueErrorForNullCustomer() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), null, SlipStatus.PENDING))
		.isInstanceOf(NullPointerException.class)
		.hasMessage("Field customer can not be null");
	}
	
	@Test
	public void shouldIssueErrorForEmptyCustomer() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(LocalDate.parse("2018-02-01"), BigDecimal.valueOf(200000), "", SlipStatus.PENDING))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Field customer can not be empty");
	}

}
