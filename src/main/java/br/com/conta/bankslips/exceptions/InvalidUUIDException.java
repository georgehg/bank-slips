package br.com.conta.bankslips.exceptions;

public class InvalidUUIDException extends Exception {

	private static final long serialVersionUID = -6423495177145697686L;

	public InvalidUUIDException(String id) {
        super("Invalid id provided - it must be a valid UUID: " + id);
    }
}
