package br.com.conta.bankslips.domain;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface BankslipDetailProjection extends BankslipProjection {

	@Value("#{target.getFine()}")
	BigDecimal getFine();
	
	String getStatus();

}