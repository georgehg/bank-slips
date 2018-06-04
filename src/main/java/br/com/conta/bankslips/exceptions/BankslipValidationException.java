package br.com.conta.bankslips.exceptions;

public class BankslipValidationException extends Exception {
	
	private static final long serialVersionUID = 7547826004148361464L;

	public BankslipValidationException(String message) {
		super(message);
	}

}
