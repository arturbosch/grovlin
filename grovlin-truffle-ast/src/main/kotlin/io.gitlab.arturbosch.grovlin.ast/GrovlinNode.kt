package io.gitlab.arturbosch.grovlin.ast

import com.oracle.truffle.api.dsl.TypeSystemReference
import com.oracle.truffle.api.frame.FrameDescriptor
import com.oracle.truffle.api.frame.VirtualFrame
import com.oracle.truffle.api.interop.ForeignAccess
import com.oracle.truffle.api.interop.TruffleObject
import com.oracle.truffle.api.nodes.Node
import com.oracle.truffle.api.nodes.NodeInfo
import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.profiles.BranchProfile
import com.oracle.truffle.api.source.SourceSection


/**
 * @author Artur Bosch
 */
@NodeInfo(language = "Grovlin language")
abstract class GrovlinNode : Node()

@TypeSystemReference(value = GrovlinTypes::class)
@NodeInfo(description = "The abstract base node for all expressions")
abstract class GrovlinExpression : GrovlinStatement() {
	abstract fun executeGeneric(frame: VirtualFrame): Any

	override fun executeVoid(frame: VirtualFrame) {
		executeGeneric(frame)
	}
}

abstract class GrovlinStatement : GrovlinNode() {
	abstract fun executeVoid(frame: VirtualFrame)
}

open class GrovlinFunctionDeclaration(@Child private val body: GrovlinStatement) : GrovlinExpression() {

	/**
	 * Profiling information, collected by the interpreter, capturing whether the function had an
	 * [explicit return statement][GrovlinReturn]. This allows the compiler to generate better
	 * code.
	 */
	private val exceptionTaken = BranchProfile.create()
	private val nullTaken = BranchProfile.create()


	override fun executeGeneric(frame: VirtualFrame): Any {
		try {
			/* Execute the function body. */
			body.executeVoid(frame)

		} catch (ex: GrovlinReturnError) {
			/*
             * In the interpreter, record profiling information that the function has an explicit
             * return.
             */
			exceptionTaken.enter()
			/* The exception transports the actual return value. */
			return ex.result
		}

		/*
         * In the interpreter, record profiling information that the function ends without an
         * explicit return.
         */
		nullTaken.enter()
		/* Return the default null value. */
		return GrovlinNull
	}
}

@NodeInfo(shortName = "return", description = "The node implementing a return statement")
class GrovlinReturn(@Node.Child private val valueNode: GrovlinExpression?) : GrovlinStatement() {

	override fun executeVoid(frame: VirtualFrame) {
		val result: Any
		if (valueNode != null) {
			result = valueNode.executeGeneric(frame)
		} else {
			/*
             * Return statement that was not followed by an expression, so return the SL null value.
             */
			result = GrovlinNull
		}
		throw GrovlinReturnError(result)
	}
}

@NodeInfo(language = "Grovlin language")
class GrovlinFile constructor(
		sourceSection: SourceSection,
		frameDescriptor: FrameDescriptor,
		@Node.Child private val program: GrovlinProgram) :
		RootNode(GrovlinLanguage::class.java, sourceSection, frameDescriptor) {

	override fun execute(frame: VirtualFrame): Any {
		assert(GrovlinLanguage.findContext() != null)
		return program.executeGeneric(frame)
	}
}

class GrovlinProgram(bodyStatement: GrovlinBodyStatement) : GrovlinFunctionDeclaration(bodyStatement)

class GrovlinBodyStatement(private val statements: List<GrovlinStatement>) : GrovlinStatement() {

	override fun executeVoid(frame: VirtualFrame) {
		throw UnsupportedOperationException("not implemented")
	}
}

//callTarget: RootCallTarget
open class GrovlinFunction(globalName: String) : TruffleObject {
	override fun getForeignAccess(): ForeignAccess {
		TODO()
	}
}
