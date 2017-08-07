package io.gitlab.arturbosch.grovlin.ast

/**
 * @author Artur Bosch
 */
data class Reference<N : Named>(val name: String, var source: N? = null) {
	override fun toString(): String {
		if (source == null) {
			return "Ref($name)[Unsolved]"
		} else {
			return "Ref($name)[Solved]"
		}
	}

	fun tryToResolve(candidates: List<N>): Boolean {
		val res = candidates.find { it.name == this.name }
		source = res
		return res != null
	}

	fun isResolved() = source != null
}
