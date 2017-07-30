package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.ExecutionContext
import com.oracle.truffle.api.TruffleLanguage
import java.io.BufferedReader
import java.io.PrintWriter
import java.util.HashMap


/**
 * @author Artur Bosch
 */
class GrovlinContext(env: TruffleLanguage.Env,
					 bufferedReader: BufferedReader,
					 out: PrintWriter,
					 functionRegistry: FunctionRegistry = DefaultFunctionRegistry()) : ExecutionContext(),
		FunctionRegistry by functionRegistry

interface FunctionRegistry {
	fun lookup(globalName: String, createIfNotExist: Boolean): GrovlinFunction
}

class DefaultFunctionRegistry : FunctionRegistry {

	private val functions = HashMap<String, GrovlinFunction>()

	override fun lookup(globalName: String, createIfNotExist: Boolean): GrovlinFunction {
		functions.putIfAbsent(globalName, GrovlinFunction(globalName))
		return functions[globalName]!!
	}
}
