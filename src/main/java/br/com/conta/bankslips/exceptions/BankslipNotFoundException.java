package br.com.conta.bankslips.exceptions;

public class BankslipNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1405584683105513611L;

	public BankslipNotFoundException(String value) {
		super("Bankslip not found with the specified id: " + value);
	}

}