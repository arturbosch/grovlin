package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINTLN_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_PRINT_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_RAND_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_READLINE_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.BUILTIN_TO_STRING_NAME
import io.gitlab.arturbosch.grovlin.ast.builtins.StringType

/**
 * @author Artur Bosch
 */
class FileScope(fileName: String) : BaseScope() {

	init {
		define(BuiltinTypeSymbol(IntType.name, IntType))
		define(BuiltinTypeSymbol(BoolType.name, BoolType))
		define(BuiltinTypeSymbol(VoidType.name, VoidType))
		define(BuiltinTypeSymbol(DecimalType.name, DecimalType))
		define(BuiltinTypeSymbol(StringType.name, StringType))
		define(BuiltinTypeSymbol(BUILTIN_RAND_NAME, IntType))
		define(BuiltinTypeSymbol(BUILTIN_PRINTLN_NAME, VoidType))
		define(BuiltinTypeSymbol(BUILTIN_PRINT_NAME, VoidType))
		define(BuiltinTypeSymbol(BUILTIN_READLINE_NAME, StringType))
		define(BuiltinTypeSymbol(BUILTIN_TO_STRING_NAME, StringType))
	}

	override val name: String = "<file:$fileName>"
}
