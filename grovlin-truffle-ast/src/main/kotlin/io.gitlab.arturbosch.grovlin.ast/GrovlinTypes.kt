package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.CompilerDirectives
import com.oracle.truffle.api.dsl.ImplicitCast
import com.oracle.truffle.api.dsl.TypeCast
import com.oracle.truffle.api.dsl.TypeCheck
import com.oracle.truffle.api.dsl.TypeSystem
import com.oracle.truffle.api.dsl.internal.DSLOptions
import java.math.BigInteger

/**
 * @author Artur Bosch
 */
@TypeSystem(value = *arrayOf(Long::class, BigInteger::class, Boolean::class,
		String::class, GrovlinFunction::class, GrovlinNull::class))
@DSLOptions(defaultGenerator = DSLOptions.DSLGenerator.FLAT)
object GrovlinTypes {

	/**
	 * Example of a manually specified type check that replaces the automatically generated type
	 * check that the Truffle DSL would generate. For [GrovlinNull], we do not need an
	 * `instanceof` check, because we know that there is only a [ singleton][GrovlinNull] instance.
	 */
	@TypeCheck(value = GrovlinNull::class)
	fun isNull(value: Any): Boolean {
		return value === GrovlinNull
	}

	/**
	 * Example of a manually specified type cast that replaces the automatically generated type cast
	 * that the Truffle DSL would generate. For [GrovlinNull], we do not need an actual cast,
	 * because we know that there is only a [singleton][GrovlinNull] instance.
	 */
	@TypeCast(value = GrovlinNull::class)
	fun asNull(value: Any): GrovlinNull {
		assert(isNull(value))
		return GrovlinNull
	}

	/**
	 * Informs the Truffle DSL that a primitive `long` value can be used in all
	 * specializations where a [BigInteger] is expected. This models the semantic of SL: It
	 * only has an arbitrary precision Number type (implemented as [BigInteger], and
	 * `long` is only used as a performance optimization to avoid the costly
	 * [BigInteger] arithmetic for values that fit into a 64-bit primitive value.
	 */
	@ImplicitCast
	@CompilerDirectives.TruffleBoundary
	fun castBigInteger(value: Long): BigInteger {
		return BigInteger.valueOf(value)
	}
}
