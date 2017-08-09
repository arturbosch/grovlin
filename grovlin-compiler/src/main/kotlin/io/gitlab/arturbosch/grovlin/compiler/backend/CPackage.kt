package io.gitlab.arturbosch.grovlin.compiler.backend

/**
 * @author Artur Bosch
 */
data class CPackage(val main: CUnit, val cus: List<CUnit>) {
	fun all() = cus + main
}
