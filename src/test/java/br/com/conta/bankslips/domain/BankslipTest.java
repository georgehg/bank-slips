package br.com.conta.bankslips.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import br.com.conta.bankslips.exceptions.BankslipValidationException;

public class BankslipTest {
	
	private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	@Test
	public void shouldInstantiateBankSlip() throws ParseException, BankslipValidationException {
		Bankslip banckSlip = Bankslip.of(formatter.parse("2018-01-01"), BigDecimal.valueOf(100000L), "Trillian Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getDueDate()).isEqualTo(formatter.parse("2018-01-01"));
		assertThat(banckSlip.getCustomer()).isEqualTo("Trillian Company");
		assertThat(banckSlip.getTotalInCents()).isEqualTo(BigDecimal.valueOf(100000L));
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
	}
	
	@Test
	public void shoulMarkBankSlipPaid() throws ParseException, BankslipValidationException {
		Bankslip banckSlip = Bankslip.of(formatter.parse("2018-02-01"), BigDecimal.valueOf(200000L), "Zaphod Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
		
		banckSlip.pay();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PAID);
	}
	
	@Test
	public void shoulMarkBankSlipCanceled() throws ParseException, BankslipValidationException {
		Bankslip banckSlip = Bankslip.of(formatter.parse("2018-02-01"), BigDecimal.valueOf(200000L), "Zaphod Company", SlipStatus.PENDING);
		
		assertThat(banckSlip).isNotNull();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.PENDING);
		
		banckSlip.cancel();
		assertThat(banckSlip.getStatus()).isEqualTo(SlipStatus.CANCELED);
	}
	
	@Test
	public void shouldIssueErrorForNullDueDate() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(null, BigDecimal.valueOf(200000L), "Zaphod Company", SlipStatus.PENDING))
		.isInstanceOf(BankslipValidationException.class)
		.hasMessage("Field due_date can not be null");
	}
	
	@Test
	public void shouldIssueErrorForNullCents() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(formatter.parse("2018-02-01"), null, "Zaphod Company", SlipStatus.PENDING))
		.isInstanceOf(BankslipValidationException.class)
		.hasMessage("Field total_in_cents can not be null");
	}
	
	@Test
	public void shouldIssueErrorForNullCustomer() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(formatter.parse("2018-02-01"), BigDecimal.valueOf(200000L), null, SlipStatus.PENDING))
		.isInstanceOf(BankslipValidationException.class)
		.hasMessage("Field customer can not be null");
	}
	
	@Test
	public void shouldIssueErrorForEmptyCustomer() throws ParseException {
		assertThatThrownBy(() ->
			Bankslip.of(formatter.parse("2018-02-01"), BigDecimal.valueOf(200000L), "", SlipStatus.PENDING))
		.isInstanceOf(BankslipValidationException.class)
		.hasMessage("Field customer can not be empty");
	}
	
	

}
