package br.com.conta.bankslips.exceptions;

public class InvalidUUIDException extends Exception {

    public InvalidUUIDException(String id) {
        super("Invalid id provided - it must be a valid UUID: " + id);
    }
}
