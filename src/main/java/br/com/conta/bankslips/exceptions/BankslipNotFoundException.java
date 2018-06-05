package br.com.conta.bankslips.exceptions;

public class BankslipNotFoundException extends Exception {
	
	private static final long serialVersionUID = 1405584683105513611L;

	public BankslipNotFoundException(String message) {
		super(message);
	}

}