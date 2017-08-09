package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.ast.AndExpression
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.ElifStatement
import io.gitlab.arturbosch.grovlin.ast.Expression
import io.gitlab.arturbosch.grovlin.ast.ExpressionStatement
import io.gitlab.arturbosch.grovlin.ast.ForStatement
import io.gitlab.arturbosch.grovlin.ast.GetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.IntRangeExpression
import io.gitlab.arturbosch.grovlin.ast.IntType
import io.gitlab.arturbosch.grovlin.ast.LambdaDeclaration
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.OrExpression
import io.gitlab.arturbosch.grovlin.ast.ParameterDeclaration
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.RelationExpression
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnaryExpression
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.VoidType
import io.gitlab.arturbosch.grovlin.ast.WhileStatement
import io.gitlab.arturbosch.grovlin.ast.XorExpression

/**
 * @author Artur Bosch
 */
abstract class TreeBaseVisitor<in P> : TreeVisitor<P, Unit> {

	override fun visit(file: GrovlinFile, data: P) {
		file.block?.let { visit(it, data) }
	}

	// Declarations

	override fun visit(typeDeclaration: TypeDeclaration, data: P) {
		visit(typeDeclaration.typeType, data)
		for (extendedType in typeDeclaration.extendedTypes) {
			visit(extendedType, data)
		}
		typeDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(lambdaDeclaration: LambdaDeclaration, data: P) {
		visit(lambdaDeclaration.block, data)
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: P) {
		visit(methodDeclaration.type, data)
		methodDeclaration.parameters.forEach { visit(it, data) }
		methodDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: P) {
		visit(objectDeclaration.objectType, data)
		objectDeclaration.extendedObject?.let { visit(it, data) }
		for (extendedType in objectDeclaration.extendedTypes) {
			visit(extendedType, data)
		}
		objectDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(varDeclaration: VarDeclaration, data: P) {
		varDeclaration.value?.let { visit(it, data) }
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: P) {
		visit(propertyDeclaration.type, data)
		propertyDeclaration.value?.let { visit(it, data) }
	}

	override fun visit(parameterDeclaration: ParameterDeclaration, data: P) {
		visit(parameterDeclaration.type, data)
	}

	// Statements

	override fun visit(statement: Statement, data: P) {
		when (statement) {
			is ExpressionStatement -> visit(statement, data)
			is TypeDeclaration -> visit(statement, data)
			is ObjectDeclaration -> visit(statement, data)
			is MethodDeclaration -> visit(statement, data)
			is VarDeclaration -> visit(statement, data)
			is PropertyDeclaration -> visit(statement, data)
			is ParameterDeclaration -> visit(statement, data)
			is LambdaDeclaration -> visit(statement, data)
			is IfStatement -> visit(statement, data)
			is ElifStatement -> visit(statement, data)
			is ForStatement -> visit(statement, data)
			is WhileStatement -> visit(statement, data)
			is Assignment -> visit(statement, data)
			is BlockStatement -> visit(statement, data)
			is ReturnStatement -> visit(statement, data)
			else -> throw UnsupportedOperationException("Missing visit method for ${statement.javaClass.simpleName}")
		}
	}

	override fun visit(expressionStatement: ExpressionStatement, data: P) {
		visit(expressionStatement.expression, data)
	}

	override fun visit(blockStatement: BlockStatement, data: P) {
		for (statement in blockStatement.statements) {
			visit(statement, data)
		}
	}

	override fun visit(ifStatement: IfStatement, data: P) {
		visit(ifStatement.condition, data)
		visit(ifStatement.thenStatement, data)
		for (elif in ifStatement.elifs) {
			visit(elif, data)
		}
		ifStatement.elseStatement?.let { visit(it, data) }
	}

	override fun visit(elifStatement: ElifStatement, data: P) {
		visit(elifStatement.condition, data)
		visit(elifStatement.thenStatement, data)
	}

	override fun visit(forStatement: ForStatement, data: P) {
		visit(forStatement.expression, data)
		visit(forStatement.varDeclaration, data)
		visit(forStatement.block, data)
	}

	override fun visit(whileStatement: WhileStatement, data: P) {
		visit(whileStatement.condition, data)
		visit(whileStatement.thenStatement, data)
	}

	override fun visit(assignment: Assignment, data: P) {
		visit(assignment.value, data)
	}

	override fun visit(returnStatement: ReturnStatement, data: P) {
		visit(returnStatement.expression, data)
	}

	// Expressions

	override fun visit(expression: Expression, data: P) {
		when (expression) {
			is ParenExpression -> visit(expression, data)
			is CallExpression -> visit(expression, data)
			is GetterAccessExpression -> visit(expression, data)
			is SetterAccessExpression -> visit(expression, data)
			is ThisReference -> visit(expression, data)
			is TypeConversion -> visit(expression, data)
			is VarReference -> visit(expression, data)
			is ObjectCreation -> visit(expression, data)
			is IntRangeExpression -> visit(expression, data)
			is BinaryExpression -> visit(expression, data)
			is UnaryExpression -> visit(expression, data)
			is StringLit -> visit(expression, data)
			is IntLit -> visit(expression, data)
			is DecLit -> visit(expression, data)
			is BoolLit -> visit(expression, data)
		}
	}

	override fun visit(parenExpression: ParenExpression, data: P) {
		visit(parenExpression.expression, data)
	}

	override fun visit(callExpression: CallExpression, data: P) {
		callExpression.scope?.let { visit(it, data) }
		callExpression.arguments.forEach { visit(it, data) }
	}

	override fun visit(getterAccessExpression: GetterAccessExpression, data: P) {
		getterAccessExpression.scope?.let { visit(it, data) }
	}

	override fun visit(setterAccessExpression: SetterAccessExpression, data: P) {
		setterAccessExpression.scope?.let { visit(it, data) }
		visit(setterAccessExpression.expression, data)
	}

	override fun visit(thisReference: ThisReference, data: P) {}

	override fun visit(typeConversion: TypeConversion, data: P) {
		visit(typeConversion.targetType, data)
		visit(typeConversion.value, data)
	}

	override fun visit(varReference: VarReference, data: P) {}

	override fun visit(objectCreation: ObjectCreation, data: P) {
		visit(objectCreation.type, data)
	}

	override fun visit(binaryExpression: BinaryExpression, data: P) {
		when (binaryExpression) {
			is RelationExpression -> visit(binaryExpression, data)
			is SumExpression -> visit(binaryExpression, data)
			is SubtractionExpression -> visit(binaryExpression, data)
			is MultiplicationExpression -> visit(binaryExpression, data)
			is DivisionExpression -> visit(binaryExpression, data)
			is AndExpression -> visit(binaryExpression, data)
			is OrExpression -> visit(binaryExpression, data)
			is XorExpression -> visit(binaryExpression, data)
		}
	}

	@Suppress("NOTHING_TO_INLINE")
	private inline fun visitInternal(binaryExpression: BinaryExpression, data: P) {
		visit(binaryExpression.left, data)
		visit(binaryExpression.right, data)
	}

	override fun visit(relationExpression: RelationExpression, data: P) {
		visitInternal(relationExpression, data)
	}

	override fun visit(sumExpression: SumExpression, data: P) {
		visitInternal(sumExpression, data)
	}

	override fun visit(subtractionExpression: SubtractionExpression, data: P) {
		visitInternal(subtractionExpression, data)
	}

	override fun visit(multiplicationExpression: MultiplicationExpression, data: P) {
		visitInternal(multiplicationExpression, data)
	}

	override fun visit(divisionExpression: DivisionExpression, data: P) {
		visitInternal(divisionExpression, data)
	}

	override fun visit(andExpression: AndExpression, data: P) {
		visitInternal(andExpression, data)
	}

	override fun visit(orExpression: OrExpression, data: P) {
		visitInternal(orExpression, data)
	}

	override fun visit(xorExpression: XorExpression, data: P) {
		visitInternal(xorExpression, data)
	}

	override fun visit(unaryExpression: UnaryExpression, data: P) {
		visit(unaryExpression.value, data)
	}

	override fun visit(intRangeExpression: IntRangeExpression, data: P) {
		visit(intRangeExpression.start, data)
		visit(intRangeExpression.endExclusive, data)
	}

	// Literale

	override fun visit(intLit: IntLit, data: P) {}

	override fun visit(boolLit: BoolLit, data: P) {}

	override fun visit(decLit: DecLit, data: P) {}

	override fun visit(stringLit: StringLit, data: P) {}

	// Types

	override fun visit(type: Type, data: P) {
		when (type) {
			is ObjectOrTypeType -> visit(type, data)
			is PrimitiveType -> visit(type, data)
			is UnknownType -> visit(type, data)
			is VoidType -> visit(type, data)
		}
	}

	override fun visit(objectOrTypeType: ObjectOrTypeType, data: P) {}

	override fun visit(primitiveType: PrimitiveType, data: P) {
		when (primitiveType) {
			is BoolType -> visit(primitiveType, data)
			is IntType -> visit(primitiveType, data)
			is DecimalType -> visit(primitiveType, data)
		}
	}

	override fun visit(boolType: BoolType, data: P) {}

	override fun visit(intType: IntType, data: P) {}

	override fun visit(decType: DecimalType, data: P) {}

	override fun visit(unknownType: UnknownType, data: P) {}

	override fun visit(voidType: VoidType, data: P) {}

}
