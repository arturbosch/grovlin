package io.gitlab.arturbosch.grovlin.ast.builtins

import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.Expression

/**
 * @author Artur Bosch
 */
class Print(arguments: List<Expression>) : CallExpression(null, BUILTIN_PRINT_NAME, arguments)

class PrintLn(arguments: List<Expression>) : CallExpression(null, BUILTIN_PRINTLN_NAME, arguments)

class ReadLine(arguments: List<Expression>) : CallExpression(null, BUILTIN_READLINE_NAME, arguments)

const val BUILTIN_PRINT_NAME = "print"
const val BUILTIN_PRINTLN_NAME = "println"
const val BUILTIN_READLINE_NAME = "readline"
