package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.parser.Error

/**
 * @author Artur Bosch
 */
open class GrovlinError(message: String) : RuntimeException()

class InvalidGrovlinFile(errors: List<Error>) :
		GrovlinError("Could not parse the grovlin file:\n ${errors.joinToString("\n", prefix = "\t- ")}")

class ProgramDeclarationMissing : GrovlinError("No program declaration was found in this file!")

class GrovlinReturnError(val result: Any) : Throwable()
