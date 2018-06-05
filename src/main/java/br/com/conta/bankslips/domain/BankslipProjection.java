package br.com.conta.bankslips.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface BankslipProjection {
	
	@JsonProperty("id")
	String getSerial();
	
	@JsonProperty("due_date")
	Date getDueDate();
	
	@JsonProperty("total_in_cents")
	String getTotalInCents();
	
	String getCustomer();

}