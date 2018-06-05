package br.com.conta.bankslips.domain;

public interface BankslipDetailProjection {
	
	BankslipProjection getBankslip();
	
	String getFine();
	
	String getSatus();

}