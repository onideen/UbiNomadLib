package no.ntnu.ubinomad.lib.exceptions;

public class NoContextException extends NullPointerException {

	public NoContextException() {}
	
	public NoContextException(String message) {
		super(message);
	}

}
