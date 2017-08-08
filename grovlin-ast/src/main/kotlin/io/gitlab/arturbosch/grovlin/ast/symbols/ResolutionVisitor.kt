package io.gitlab.arturbosch.grovlin.ast.symbols

import io.gitlab.arturbosch.grovlin.ast.AndExpression
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.AstNode
import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.Declaration
import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntRangeExpression
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.OrExpression
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.Position
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.RelationExpression
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnaryExpression
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.VariableDeclaration
import io.gitlab.arturbosch.grovlin.ast.XorExpression
import io.gitlab.arturbosch.grovlin.ast.visitors.TreeBaseVisitor

/**
 * @author Artur Bosch
 */
class ResolutionVisitor(val grovlinFile: GrovlinFile,
						val fileScope: FileScope) : TreeBaseVisitor() {

	// Variable declarations resolution

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		super.visit(varDeclaration, data)
		val exprPromotionType = varDeclaration.value?.promotionType
		val exprEvaluationType = varDeclaration.value?.evaluationType
		val evaluationType = exprPromotionType ?: exprEvaluationType
		if (evaluationType != null) {
			varDeclaration.type = evaluationType
		} else {
			grovlinFile.addError(SemanticError("Type of '${varDeclaration.name}' could not be inferred!",
					varDeclaration.position?.start))
		}
		varDeclaration.evaluationType = varDeclaration.type
		resolveVariableSymbolType(varDeclaration)
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: Any) {
		super.visit(propertyDeclaration, data)
		propertyDeclaration.evaluationType = propertyDeclaration.type
		resolveVariableSymbolType(propertyDeclaration)
	}

	override fun visit(parameterDeclaration: ParameterDeclaration, data: Any) {
		super.visit(parameterDeclaration, data)
		parameterDeclaration.evaluationType = parameterDeclaration.type
		resolveVariableSymbolType(parameterDeclaration)
	}

	private fun resolveVariableSymbolType(variableDeclaration: VariableDeclaration) {
		val scope = variableDeclaration.resolutionScope ?: assertScopeResolved(variableDeclaration)
		val varType = variableDeclaration.type
		val symbol = scope.resolve(varType.name)
		symbol?.type = varType
		variableDeclaration.symbol?.type = symbol?.type
		varType.symbol = symbol
	}

	// Variable reference resolution

	override fun visit(varReference: VarReference, data: Any) {
		super.visit(varReference, data)
		val scope = varReference.resolutionScope ?: assertScopeResolved(varReference)
		val referenceName = varReference.varName
		val symbol = scope.resolve(referenceName)
		varReference.symbol = symbol
		val definition = symbol?.def
		symbol?.type = definition?.type
		varReference.evaluationType = definition?.type
		checkSemanticVarReferenceCases(definition, varReference)
	}

	override fun visit(assignment: Assignment, data: Any) {
		super.visit(assignment, data)
		val scope = assignment.resolutionScope ?: assertScopeResolved(assignment)
		val symbol = scope.resolve(assignment.varName)
		assignment.symbol = symbol
		val definition = symbol?.def
		symbol?.type = definition?.type
		assignment.evaluationType = definition?.type
		checkSemanticVarReferenceCases(definition, assignment.varReference)
	}

	private fun checkSemanticVarReferenceCases(definition: Declaration?,
											   reference: VarReference) {

		val referencePositions = reference.position ?: assertPositions(reference)
		val referenceStart = referencePositions.start
		val referenceName = reference.varName
		when {
			definition == null -> grovlinFile.addError(SemanticError(
					"Declaration for '$referenceName' not found.", referenceStart))

			definition.position == null -> assertPositions(definition)

			definition.position!!.contains(referencePositions) -> grovlinFile.addError(SemanticError(
					"Reference '$referenceName' is used within its own declaration.", referenceStart))

			referenceStart.isBefore(definition.position!!.start) -> grovlinFile.addError(SemanticError(
					"Reference '$referenceName' on '$referenceStart' is used before " +
							"the declaration of '$referenceName' at '${definition.position!!.start}'.", referenceStart))
		}
	}

	private fun assertPositions(node: AstNode): Position {
		throw AssertionError("No positions for '${node.javaClass.simpleName}'.")
	}

	// Type, Object, Method resolution

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		super.visit(methodDeclaration, data)
		val scope = methodDeclaration.resolutionScope ?: assertScopeResolved(methodDeclaration)
		val returnType = methodDeclaration.type
		val symbol = scope.resolve(returnType.name)
		returnType.symbol = symbol
		methodDeclaration.evaluationType = methodDeclaration.type
	}

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		super.visit(typeDeclaration, data)
		val scope = typeDeclaration.resolutionScope ?: assertScopeResolved(typeDeclaration)
		for (extendedType in typeDeclaration.extendedTypes) {
			val symbol = scope.resolve(extendedType.name)
			extendedType.symbol = symbol
		}
		typeDeclaration.evaluationType = typeDeclaration.typeType
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: Any) {
		super.visit(objectDeclaration, data)
		val scope = objectDeclaration.resolutionScope ?: assertScopeResolved(objectDeclaration)
		val extendedObject = objectDeclaration.extendedObject
		if (extendedObject != null) {
			val symbol = scope.resolve(extendedObject.name)
			extendedObject.symbol = symbol
		}
		for (extendedType in objectDeclaration.extendedTypes) {
			val symbol = scope.resolve(extendedType.name)
			extendedType.symbol = symbol
		}
		objectDeclaration.evaluationType = objectDeclaration.objectType
	}

	// Member reference resolution

	override fun visit(thisReference: ThisReference, data: Any) {
		thisReference.symbol = thisReference.resolutionScope?.getEnclosingClass()
		thisReference.evaluationType = thisReference.symbol?.type as? Type
	}

	override fun visit(callExpression: CallExpression, data: Any) {
		super.visit(callExpression, data)
		val scopeSym = callExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(callExpression.name)
		callExpression.symbol = memberSym
	}

	override fun visit(getterAccessExpression: GetterAccessExpression, data: Any) {
		super.visit(getterAccessExpression, data)
		val scopeSym = getterAccessExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(getterAccessExpression.name)
		getterAccessExpression.symbol = memberSym
	}

	override fun visit(setterAccessExpression: SetterAccessExpression, data: Any) {
		super.visit(setterAccessExpression, data)
		val scopeSym = setterAccessExpression.scope?.symbol
		val memberSym = (scopeSym?.scope as? ClassSymbol)?.resolveMember(setterAccessExpression.name)
		setterAccessExpression.symbol = memberSym
	}

	private fun assertScopeResolved(node: AstNode): Scope =
			throw AssertionError("Scope of '${node.javaClass.simpleName}' is not resolved!")

	// Computing static expression types

	override fun visit(parenExpression: ParenExpression, data: Any) {
		super.visit(parenExpression, data)
		parenExpression.evaluationType = parenExpression.expression.evaluationType
	}

	override fun visit(typeConversion: TypeConversion, data: Any) {
		super.visit(typeConversion, data)
		typeConversion.evaluationType = typeConversion.targetType
	}

	override fun visit(objectCreation: ObjectCreation, data: Any) {
		super.visit(objectCreation, data)
		objectCreation.evaluationType = objectCreation.type
	}

	override fun visit(unaryExpression: UnaryExpression, data: Any) {
		super.visit(unaryExpression, data)
		unaryExpression.evaluationType = unaryExpression.value.evaluationType
	}

	override fun visit(intRangeExpression: IntRangeExpression, data: Any) {
		super.visit(intRangeExpression, data)
		intRangeExpression.evaluationType = IntType
	}

	// Binary expressions need promotion through lookup tables

	override fun visit(relationExpression: RelationExpression, data: Any) {
		super.visit(relationExpression, data)
		val resultType = promoteIfNecessary(relationExpression, RELATIONAL_PROMOTION_TABLE)
		if (resultType == T_BOOL_INDEX) {
			relationExpression.evaluationType = BoolType
		}
	}

	private fun promoteIfNecessary(binExpr: BinaryExpression, promotionTable: Array<IntArray>): Int {
		val resultType = getResultType(promotionTable, binExpr.left, binExpr.right)
		if (resultType == T_VOID_INDEX) {
			grovlinFile.addError(SemanticError("Incompatible types " +
					"'${binExpr.left.evaluationType}' and '${binExpr.right.evaluationType}' at ${binExpr.position}.",
					binExpr.left.position?.start))
		}
		binExpr.evaluationType = binExpr.left.promotionType ?: binExpr.left.evaluationType
		return resultType
	}

	override fun visit(sumExpression: SumExpression, data: Any) {
		super.visit(sumExpression, data)
		promoteIfNecessary(sumExpression, ARITHMETIC_PROMOTION_TABLE)
	}

	override fun visit(subtractionExpression: SubtractionExpression, data: Any) {
		super.visit(subtractionExpression, data)
		promoteIfNecessary(subtractionExpression, ARITHMETIC_PROMOTION_TABLE)
	}

	override fun visit(multiplicationExpression: MultiplicationExpression, data: Any) {
		super.visit(multiplicationExpression, data)
		promoteIfNecessary(multiplicationExpression, MUL_PROMOTION_TABLE)
	}

	override fun visit(divisionExpression: DivisionExpression, data: Any) {
		super.visit(divisionExpression, data)
		promoteIfNecessary(divisionExpression, DIVISION_PROMOTION_TABLE)
	}

	override fun visit(andExpression: AndExpression, data: Any) {
		super.visit(andExpression, data)
		val resultType = promoteIfNecessary(andExpression, BINARY_PROMOTION_TABLE)
		if (resultType == T_BOOL_INDEX) {
			andExpression.evaluationType = BoolType
		}
	}

	override fun visit(orExpression: OrExpression, data: Any) {
		super.visit(orExpression, data)
		val resultType = promoteIfNecessary(orExpression, BINARY_PROMOTION_TABLE)
		if (resultType == T_BOOL_INDEX) {
			orExpression.evaluationType = BoolType
		}
	}

	override fun visit(xorExpression: XorExpression, data: Any) {
		super.visit(xorExpression, data)
		val resultType = promoteIfNecessary(xorExpression, BINARY_PROMOTION_TABLE)
		if (resultType == T_BOOL_INDEX) {
			xorExpression.evaluationType = BoolType
		}
	}

	// Literals

	override fun visit(intLit: IntLit, data: Any) {
		intLit.evaluationType = intLit.type
	}

	override fun visit(boolLit: BoolLit, data: Any) {
		boolLit.evaluationType = boolLit.type
	}

	override fun visit(decLit: DecLit, data: Any) {
		decLit.evaluationType = decLit.type
	}

	override fun visit(stringLit: StringLit, data: Any) {
		stringLit.evaluationType = stringLit.type
	}
}
