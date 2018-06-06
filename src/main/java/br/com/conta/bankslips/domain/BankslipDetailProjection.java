package br.com.conta.bankslips.domain;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public interface BankslipDetailProjection extends BankslipProjection {

	@Value("#{target.getFine()}")
	@JsonFormat(shape = Shape.STRING)
	BigDecimal getFine();
	
	String getStatus();

}