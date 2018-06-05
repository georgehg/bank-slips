package br.com.conta.bankslips.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
public class BankslipPostDto {

	@NotNull
	@JsonProperty("due_date")
	private String dueDate;

	@NotNull
	@JsonProperty("total_in_cents")
	private String totalInCents;

	@NotNull
	private String customer;

	@NotNull
	private String status;

	protected BankslipPostDto() {
		this(null, null, null, null);
	}
}
