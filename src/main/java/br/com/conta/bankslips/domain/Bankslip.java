package br.com.conta.bankslips.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import br.com.conta.bankslips.exceptions.BankslipValidationException;
import lombok.ToString;

@ToString
public class Bankslip {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
	
	@NotNull
	@Column(name = "due_date")
	private final Date dueDate;
	
	@Column(name = "total_in_cents")
	private final BigDecimal totalInCents;
	
	private final String customer;
	
	@Enumerated(value = EnumType.STRING)
	private SlipStatus status;
	
	protected Bankslip() {
		this(null, null, null, null);
	}

	private Bankslip(Date dueDate, BigDecimal totalInCents, String customer, SlipStatus status) {
		this.dueDate = dueDate;
		this.totalInCents = totalInCents;
		this.customer = customer;
		this.status = status;
	}
	
	public static Bankslip of(Date dueDate, BigDecimal totalInCents, String customer, SlipStatus status) throws BankslipValidationException {
		try {
			checkNotNull(dueDate, "Field due_date can not be null");
			checkNotNull(totalInCents, "Field total_in_cents can not be null");
			checkNotNull(customer, "Field customer can not be null");
			checkArgument(!customer.isEmpty(), "Field customer can not be empty");
		} catch (Exception e) {
			throw new BankslipValidationException(e.getMessage());
		}
		return new Bankslip(dueDate, totalInCents, customer, status);
	}

	public UUID getId() {
		return id;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public BigDecimal getTotalInCents() {
		return totalInCents;
	}

	public String getCustomer() {
		return customer;
	}

	public SlipStatus getStatus() {
		return status;
	}
	
	public void pay() {
		this.status = SlipStatus.PAID;
	}
	
	public void cancel() {
		this.status = SlipStatus.CANCELED;
	}
}
