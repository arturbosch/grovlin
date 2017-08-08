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
import io.gitlab.arturbosch.grovlin.ast.Program
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
interface TreeVisitor<in P, out R> {

	fun visit(file: GrovlinFile, data: P): R
	fun visit(program: Program, data: P): R

	// Declarations

	fun visit(typeDeclaration: TypeDeclaration, data: P): R
	fun visit(lambdaDeclaration: LambdaDeclaration, data: P): R
	fun visit(methodDeclaration: MethodDeclaration, data: P): R
	fun visit(objectDeclaration: ObjectDeclaration, data: P): R
	fun visit(varDeclaration: VarDeclaration, data: P): R
	fun visit(propertyDeclaration: PropertyDeclaration, data: P): R
	fun visit(parameterDeclaration: ParameterDeclaration, data: P): R

	// Statements

	fun visit(statement: Statement, data: Any): R
	fun visit(expressionStatement: ExpressionStatement, data: P): R
	fun visit(blockStatement: BlockStatement, data: P): R
	fun visit(ifStatement: IfStatement, data: P): R
	fun visit(elifStatement: ElifStatement, data: P): R
	fun visit(forStatement: ForStatement, data: P): R
	fun visit(whileStatement: WhileStatement, data: P): R
	fun visit(assignment: Assignment, data: P): R
	fun visit(returnStatement: ReturnStatement, data: Any): R

	// Expressions

	fun visit(expression: Expression, data: P): R

	fun visit(parenExpression: ParenExpression, data: P): R
	fun visit(callExpression: CallExpression, data: P): R
	fun visit(getterAccessExpression: GetterAccessExpression, data: P): R
	fun visit(setterAccessExpression: SetterAccessExpression, data: P): R
	fun visit(thisReference: ThisReference, data: P): R
	fun visit(typeConversion: TypeConversion, data: P): R
	fun visit(varReference: VarReference, data: P): R
	fun visit(objectCreation: ObjectCreation, data: P): R

	fun visit(binaryExpression: BinaryExpression, data: P): R
	fun visit(relationExpression: RelationExpression, data: P): R
	fun visit(sumExpression: SumExpression, data: P): R
	fun visit(subtractionExpression: SubtractionExpression, data: P): R
	fun visit(multiplicationExpression: MultiplicationExpression, data: P): R
	fun visit(divisionExpression: DivisionExpression, data: P): R
	fun visit(andExpression: AndExpression, data: P): R
	fun visit(orExpression: OrExpression, data: P): R
	fun visit(xorExpression: XorExpression, data: P): R

	fun visit(unaryExpression: UnaryExpression, data: P): R

	fun visit(intRangeExpression: IntRangeExpression, data: P): R

	// Literals

	fun visit(intLit: IntLit, data: P): R
	fun visit(boolLit: BoolLit, data: P): R
	fun visit(decLit: DecLit, data: P): R
	fun visit(stringLit: StringLit, data: Any): R

	// Types

	fun visit(type: Type, data: Any): R
	fun visit(objectOrTypeType: ObjectOrTypeType, data: P): R
	fun visit(primitiveType: PrimitiveType, data: P): R
	fun visit(boolType: BoolType, data: P): R
	fun visit(intType: IntType, data: P): R
	fun visit(decType: DecimalType, data: P): R
	fun visit(unknownType: UnknownType, data: P): R
	fun visit(voidType: VoidType, data: Any)
}
