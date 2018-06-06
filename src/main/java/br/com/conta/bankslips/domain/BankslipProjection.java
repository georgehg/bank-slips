package br.com.conta.bankslips.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

public interface BankslipProjection {
	
	@JsonProperty("id")
	String getSerial();
	
	@JsonProperty("due_date")
	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	LocalDate getDueDate();
	
	@Value("#{target.getTotalInCents()}")
	@JsonProperty("total_in_cents")
	@JsonFormat(shape = Shape.STRING)
	BigDecimal getTotalInCents();
	
	String getCustomer();

}