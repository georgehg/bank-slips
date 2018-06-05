package br.com.conta.bankslips.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

@Value
public class BankslipPostDto {
	
	@NotNull
	@JsonProperty("due_date")
	private Date dueDate;
	
	@NotNull
	@JsonProperty("total_in_cents")
	private BigDecimal totalInCents;
	
	@NotNull
	private String customer;
	
	@NotNull
	private String status;

}
