package io.gitlab.arturbosch.grovlin.ast.builtins

import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.Expression

/**
 * @author Artur Bosch
 */
class Print(argument: Expression) : CallExpression(null, BUILTIN_PRINT_NAME, listOf(argument))

class PrintLn(argument: Expression) : CallExpression(null, BUILTIN_PRINTLN_NAME, listOf(argument))

class ReadLine : CallExpression(null, BUILTIN_READLINE_NAME)

class RandomNumber(argument: Expression) : CallExpression(null, BUILTIN_RAND_NAME, listOf(argument))

class ToString(scope: Expression) : CallExpression(scope, BUILTIN_TO_STRING_NAME)

const val BUILTIN_PRINT_NAME = "print"
const val BUILTIN_PRINTLN_NAME = "println"
const val BUILTIN_READLINE_NAME = "readline"
const val BUILTIN_RAND_NAME = "rand"
const val BUILTIN_TO_STRING_NAME = "toString"
