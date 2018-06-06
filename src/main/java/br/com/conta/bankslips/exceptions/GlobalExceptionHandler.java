package br.com.conta.bankslips.exceptions;

import java.util.AbstractMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(HttpMessageNotReadableException exception) {
		AbstractMap.SimpleEntry<String, String> response =
				new AbstractMap.SimpleEntry<>("message", "Bankslip not provided in the request body");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	
	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(InvalidUUIDException exception) {
		AbstractMap.SimpleEntry<String, String> response =
				new AbstractMap.SimpleEntry<>("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(MethodArgumentNotValidException exception) {
		AbstractMap.SimpleEntry<String, String> response = new AbstractMap.SimpleEntry<>("message",
				"Invalid bankslip provided.The possible reasons are: "
						+ "A field of the provided bankslip was null or with invalid values");
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(BankslipNotFoundException exception) {
		AbstractMap.SimpleEntry<String, String> response =
				new AbstractMap.SimpleEntry<>("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(BankslipValidationException exception) {
		AbstractMap.SimpleEntry<String, String> response =
				new AbstractMap.SimpleEntry<>("message", exception.getMessage());
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
	}

	@ExceptionHandler
	public ResponseEntity<AbstractMap.SimpleEntry<String, String>> handle(Exception exception) {
		AbstractMap.SimpleEntry<String, String> response =
				new AbstractMap.SimpleEntry<>("message", "Unable to process this request");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

}