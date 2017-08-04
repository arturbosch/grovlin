package io.gitlab.arturbosch.grovlin.ast.builtins

import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType

/**
 * @author Artur Bosch
 */
abstract class BuiltinObjectType(name: String) : ObjectOrTypeType(name)

object StringType : BuiltinObjectType("String")
