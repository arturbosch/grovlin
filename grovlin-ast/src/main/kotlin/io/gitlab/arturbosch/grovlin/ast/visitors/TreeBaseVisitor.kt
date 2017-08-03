package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.BinaryExpression
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.BoolLit
import io.gitlab.arturbosch.grovlin.ast.BoolType
import io.gitlab.arturbosch.grovlin.ast.CallExpression
import io.gitlab.arturbosch.grovlin.ast.DecLit
import io.gitlab.arturbosch.grovlin.ast.DecimalType
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
import io.gitlab.arturbosch.grovlin.ast.ObjectCreation
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.ParenExpression
import io.gitlab.arturbosch.grovlin.ast.PrimitiveType
import io.gitlab.arturbosch.grovlin.ast.Print
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.SetterAccessExpression
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.StringLit
import io.gitlab.arturbosch.grovlin.ast.ThisReference
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeConversion
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.UnaryExpression
import io.gitlab.arturbosch.grovlin.ast.UnknownType
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarReference
import io.gitlab.arturbosch.grovlin.ast.WhileStatement

/**
 * @author Artur Bosch
 */
abstract class TreeBaseVisitor : TreeVisitor<Any, Unit> {

	override fun visit(file: GrovlinFile, data: Any) {
		file.block?.let { visit(it, data) }
	}

	override fun visit(program: Program, data: Any) {
		program.block?.let { visit(it, data) }
	}

	// Declarations

	override fun visit(typeDeclaration: TypeDeclaration, data: Any) {
		visit(typeDeclaration.type, data)
		for (extendedType in typeDeclaration.extendedTypes) {
			visit(extendedType, data)
		}
		typeDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(lambdaDeclaration: LambdaDeclaration, data: Any) {
		visit(lambdaDeclaration.block, data)
	}

	override fun visit(methodDeclaration: MethodDeclaration, data: Any) {
		methodDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(objectDeclaration: ObjectDeclaration, data: Any) {
		visit(objectDeclaration.type, data)
		objectDeclaration.extendedObject?.let { visit(it, data) }
		for (extendedType in objectDeclaration.extendedTypes) {
			visit(extendedType, data)
		}
		objectDeclaration.block?.let { visit(it, data) }
	}

	override fun visit(varDeclaration: VarDeclaration, data: Any) {
		visit(varDeclaration.value, data)
	}

	override fun visit(propertyDeclaration: PropertyDeclaration, data: Any) {
		visit(propertyDeclaration.type, data)
		propertyDeclaration.value?.let { visit(it, data) }
	}

	// Statements

	override fun visit(statement: Statement, data: Any) {
		when (statement) {
			is Program -> visit(statement, data)
			is TypeDeclaration -> visit(statement, data)
			is ObjectDeclaration -> visit(statement, data)
			is MethodDeclaration -> visit(statement, data)
			is VarDeclaration -> visit(statement, data)
			is LambdaDeclaration -> visit(statement, data)
			is IfStatement -> visit(statement, data)
			is ElifStatement -> visit(statement, data)
			is Assignment -> visit(statement, data)
			is Print -> visit(statement, data)
			is BlockStatement -> visit(statement, data)
			is ExpressionStatement -> visit(statement, data)
		}
	}

	override fun visit(expressionStatement: ExpressionStatement, data: Any) {
		visit(expressionStatement.expression, data)
	}

	override fun visit(blockStatement: BlockStatement, data: Any) {
		for (statement in blockStatement.statements) {
			visit(statement, data)
		}
	}

	override fun visit(ifStatement: IfStatement, data: Any) {
		visit(ifStatement.condition, data)
		visit(ifStatement.thenStatement, data)
		for (elif in ifStatement.elifs) {
			visit(elif, data)
		}
		ifStatement.elseStatement?.let { visit(it, data) }
	}

	override fun visit(elifStatement: ElifStatement, data: Any) {
		visit(elifStatement.condition, data)
		visit(elifStatement.thenStatement, data)
	}

	override fun visit(forStatement: ForStatement, data: Any) {
		visit(forStatement.expression, data)
		visit(forStatement.block, data)
	}

	override fun visit(whileStatement: WhileStatement, data: Any) {
		visit(whileStatement.condition, data)
		visit(whileStatement.thenStatement, data)
	}

	override fun visit(assignment: Assignment, data: Any) {
		visit(assignment.value, data)
	}

	override fun visit(print: Print, data: Any) {
		visit(print.value, data)
	}

	override fun visit(returnStatement: ReturnStatement, data: Any) {
		visit(returnStatement.expression, data)
	}

	// Expressions

	override fun visit(expression: Expression, data: Any) {
		when (expression) {
			is ParenExpression -> visit(expression, data)
			is CallExpression -> visit(expression, data)
			is GetterAccessExpression -> visit(expression, data)
			is SetterAccessExpression -> visit(expression, data)
			is ThisReference -> visit(expression, data)
			is TypeConversion -> visit(expression, data)
			is VarReference -> visit(expression, data)
			is ObjectCreation -> visit(expression, data)
			is BinaryExpression -> visit(expression, data)
			is UnaryExpression -> visit(expression, data)
			is IntLit -> visit(expression, data)
			is DecLit -> visit(expression, data)
			is BoolLit -> visit(expression, data)
		}
	}

	override fun visit(parenExpression: ParenExpression, data: Any) {
		visit(parenExpression.expression, data)
	}

	override fun visit(callExpression: CallExpression, data: Any) {
		callExpression.scope?.let { visit(it, data) }
	}

	override fun visit(getterAccessExpression: GetterAccessExpression, data: Any) {
		getterAccessExpression.scope?.let { visit(it, data) }
	}

	override fun visit(setterAccessExpression: SetterAccessExpression, data: Any) {
		setterAccessExpression.scope?.let { visit(it, data) }
		visit(setterAccessExpression.expression, data)
	}

	override fun visit(thisReference: ThisReference, data: Any) {}

	override fun visit(typeConversion: TypeConversion, data: Any) {
		visit(typeConversion.targetType, data)
		visit(typeConversion.value, data)
	}

	override fun visit(varReference: VarReference, data: Any) {}

	override fun visit(objectCreation: ObjectCreation, data: Any) {
		visit(objectCreation.type, data)
	}

	override fun visit(binaryExpression: BinaryExpression, data: Any) {
		visit(binaryExpression.left, data)
		visit(binaryExpression.right, data)
	}

	override fun visit(unaryExpression: UnaryExpression, data: Any) {
		visit(unaryExpression.value, data)
	}

	override fun visit(intRangeExpression: IntRangeExpression, data: Any) {
		visit(intRangeExpression.start, data)
		visit(intRangeExpression.endExclusive, data)
	}

	// Literale

	override fun visit(intLit: IntLit, data: Any) {}

	override fun visit(boolLit: BoolLit, data: Any) {}

	override fun visit(decLit: DecLit, data: Any) {}

	override fun visit(stringLit: StringLit, data: Any) {}

	// Types

	override fun visit(type: Type, data: Any) {
		when (type) {
			is ObjectOrTypeType -> visit(type, data)
			is PrimitiveType -> visit(type, data)
		}
	}

	override fun visit(objectOrTypeType: ObjectOrTypeType, data: Any) {}

	override fun visit(primitiveType: PrimitiveType, data: Any) {
		when (primitiveType) {
			is BoolType -> visit(primitiveType, data)
			is IntType -> visit(primitiveType, data)
			is DecimalType -> visit(primitiveType, data)
		}
	}

	override fun visit(boolType: BoolType, data: Any) {}

	override fun visit(intType: IntType, data: Any) {}

	override fun visit(decType: DecimalType, data: Any) {}

	override fun visit(unknownType: UnknownType, data: Any) {}

}
