package br.com.conta.bankslips.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(of = "serial")
@Entity
@Table(name = "bank_slip")
public class Bankslip {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	private final UUID serial;
	
	@Column(name = "due_date")
	private final LocalDate dueDate;
	
	@Column(name = "total_in_cents")
	private final BigDecimal totalInCents;
	
	private final String customer;
	
	@Enumerated(value = EnumType.STRING)
	private SlipStatus status;
	
	protected Bankslip() {
		this(null, null, null, null);
	}

	private Bankslip(LocalDate dueDate, BigDecimal totalInCents, String customer, SlipStatus status) {
		this.serial = UUID.randomUUID();
		this.dueDate = dueDate;
		this.totalInCents = totalInCents;
		this.customer = customer;
		this.status = status;
	}
	
	public static Bankslip of(LocalDate dueDate, BigDecimal totalInCents, String customer, SlipStatus status) {
		checkNotNull(dueDate, "Field due_date can not be null");
		checkNotNull(totalInCents, "Field total_in_cents can not be null");
		checkNotNull(customer, "Field customer can not be null");
		checkArgument(!customer.isEmpty(), "Field customer can not be empty");
		return new Bankslip(dueDate, totalInCents, customer, status);
	}

	public Long getId() {
		return id;
	}
	
	public UUID getSerial() {
		return serial;
	}

	public LocalDate getDueDate() {
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
	
	public BigDecimal getFine() {
		if (dueDate.isBefore(LocalDate.now())) {
			Long daysDiff = dueDate.until(LocalDate.now(), ChronoUnit.DAYS);
			return Fine.from(daysDiff.intValue()).calculateFine(totalInCents);
		}
		return BigDecimal.ZERO;
	}
}
