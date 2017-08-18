package io.gitlab.arturbosch.grovlin.compiler.java.inmemory;

/**
 * @author Artur Bosch
 */
public class GrovlinRuntimeError extends RuntimeException {

	public GrovlinRuntimeError(String message) {
		super(message);
	}
}
