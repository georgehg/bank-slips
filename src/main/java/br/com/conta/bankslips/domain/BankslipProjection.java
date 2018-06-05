package br.com.conta.bankslips.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface BankslipProjection {
	
	@JsonProperty("id")
	String getSerial();
	
	@JsonProperty("due_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate getDueDate();
	
	@JsonProperty("total_in_cents")
	BigDecimal getTotalInCents();
	
	String getCustomer();

}