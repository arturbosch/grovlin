package io.gitlab.arturbosch.grovlin.ast

import io.gitlab.arturbosch.grovlin.GrovlinParser.*
import java.util.ArrayList
import java.util.LinkedList

/**
 * @author Artur Bosch
 */
fun GrovlinFileContext.toAsT(fileName: String = "Program"): GrovlinFile {
	val statements = statements().statement().mapTo(ArrayList()) { it.toAst(fileName) }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply { position = toPosition() }
	} else null
	return GrovlinFile(fileName, blockStatement).apply { position = toPosition() }
}

fun StatementContext.toAst(fileName: String = "Program"): Statement = when (this) {
	is ExpressionStatementContext -> ExpressionStatement(expressionStmt().expression().toAst())
	is MemberDeclarationStatementContext -> memberDeclaration().toAst()
	is VarDeclarationStatementContext -> VarDeclaration(varDeclaration().assignment().ID().text,
			varDeclaration().assignment().expression().toAst())
	is AssignmentStatementContext -> Assignment(Reference(assignment().ID().text), assignment().expression().toAst())
	is PrintStatementContext -> Print(print().expression().toAst())
	is ProgramStatementContext -> program().transformToBlockStatement(fileName)
	is IfStatementContext -> transformToIfStatement()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}.apply { position = toPosition() }

private fun ProgramContext.transformToBlockStatement(fileName: String): Program {
	val statements = statements().statement().mapTo(ArrayList()) { it.toAst() }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply {
			position = Position(LBRACE().symbol.startPoint(), RBRACE().symbol.endPoint())
		}
	} else null
	return Program(fileName, blockStatement).apply { position = toPosition() }
}

fun MemberDeclarationContext.toAst(): Statement = when (this) {
	is PropertyMemberDeclarationContext -> transformToProperty()
	is TypeMemberDeclarationContext -> typeDeclaration().transformToTypeDeclaration()
	is ObjectMemberDeclarationContext -> objectDeclaration().transformToObjectDeclaration()
	is DefMemberDeclarationContext -> defDeclaration().toAst()
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}

private fun TypeDeclarationContext.transformToTypeDeclaration(): TypeDeclaration {
	val type = ObjectOrTypeType(typeName.text)
	val extendedTypes = extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) }
	val statements: ArrayList<Statement> = memberDeclaration().mapTo(ArrayList()) { it.toAst() }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply {
			position = Position(LBRACE().symbol.startPoint(), RBRACE().symbol.endPoint())
		}
	} else null
	return TypeDeclaration(type, extendedTypes, blockStatement).apply { position = toPosition() }
}

private fun ObjectDeclarationContext.transformToObjectDeclaration(): ObjectDeclaration {
	val type = ObjectOrTypeType(objectName.text)
	val extendedObject = extendObject?.let { ObjectOrTypeType(it.text) }
	val extendedTypes = extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) }
	val statements: ArrayList<Statement> = memberDeclaration().mapTo(ArrayList()) { it.toAst() }
	val blockStatement = if (statements.isNotEmpty()) {
		BlockStatement(statements).apply {
			position = Position(LBRACE().symbol.startPoint(), RBRACE().symbol.endPoint())
		}
	} else null
	return ObjectDeclaration(type, extendedObject, extendedTypes, blockStatement).apply { position = toPosition() }
}

private fun PropertyMemberDeclarationContext.transformToProperty(): PropertyDeclaration {
	val assignment = propertyDeclaration().assignment()
	return PropertyDeclaration(Type.of(propertyDeclaration().TYPEID().text),
			assignment?.ID()?.text ?: propertyDeclaration().ID().text,
			assignment?.expression()?.toAst()).apply { position = toPosition() }
}

fun DefDeclarationContext.toAst(): Statement = when (this) {
	is MethodDefinitionContext -> {
		val context = methodDeclaration().statements()
		val block = if (context != null) {
			BlockStatement(context.statement().mapTo(ArrayList()) { it.toAst() }).apply {
				position = context.toPosition()
			}
		} else null
		@Suppress("USELESS_CAST")
		MethodDeclaration(methodDeclaration().ID().text, block) as Statement
	}
	is LambdaDefinitionContext -> {
		LambdaDeclaration(lambdaDeclaration().ID().text,
				lambdaDeclaration().statements().statement().mapTo(ArrayList()) { it.toAst() })
	}
	else -> throw UnsupportedOperationException("not implemented ${javaClass.canonicalName}")
}.apply { position = toPosition() }

fun ExpressionContext.toAst(): Expression = when (this) {
	is ParenExpressionContext -> ParenExpression(expression().toAst())
	is ThisExpressionContext -> ThisReference(Reference("this"))
	is ObjectCreationExpressionContext -> ObjectCreation(ObjectOrTypeType(TYPEID().text))
	is CallExpressionContext -> CallExpression(scope?.toAst(), methodName.text)
	is GetterAccessExpressionContext -> GetterAccessExpression(scope?.toAst(), fieldName.text)
	is SetterAccessExpressionContext -> SetterAccessExpression(scope?.toAst(),
			assignment().ID().text, assignment().expression().toAst())
	is BinaryOperationContext -> toAst()
	is MinusExpressionContext -> MinusExpression(expression().toAst())
	is NotExpressionContext -> NotExpression(expression().toAst())
	is IntLiteralContext -> IntLit(text)
	is DecimalLiteralContext -> DecLit(text)
	is BoolLiteralContext -> BoolLit(text.toBoolean())
	is VarReferenceContext -> VarReference(Reference(text))
	is TypeConversionContext -> TypeConversion(expression().toAst(), targetType.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}.apply { position = toPosition() }

fun BinaryOperationContext.toAst(): Expression = when (operator.text) {
	"+" -> SumExpression(left.toAst(), right.toAst())
	"-" -> SubtractionExpression(left.toAst(), right.toAst())
	"*" -> MultiplicationExpression(left.toAst(), right.toAst())
	"/" -> DivisionExpression(left.toAst(), right.toAst())
	"&&" -> AndExpression(left.toAst(), right.toAst())
	"||" -> OrExpression(left.toAst(), right.toAst())
	"^" -> XorExpression(left.toAst(), right.toAst())
	"==" -> EqualExpression(left.toAst(), right.toAst())
	"!=" -> UnequalExpression(left.toAst(), right.toAst())
	">=" -> GreaterEqualExpression(left.toAst(), right.toAst())
	"<=" -> LessEqualExpression(left.toAst(), right.toAst())
	">" -> GreaterExpression(left.toAst(), right.toAst())
	"<" -> LessExpression(left.toAst(), right.toAst())
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}.apply { position = toPosition() }

fun TypeContext.toAst(): Type = when (this) {
	is IntegerContext -> IntType
	is DecimalContext -> DecimalType
	is BoolContext -> BoolType
	else -> throw UnsupportedOperationException(this.javaClass.canonicalName)
}

private fun IfStatementContext.transformToIfStatement(): IfStatement = IfStatement(condition = ifStmt().expression().toAst(),
		thenStatement = transformThenBlock(),
		elifs = transformElifStatements(),
		elseStatement = transformElseBlock()).apply { position = toPosition() }

private fun IfStatementContext.transformThenBlock() = BlockStatement(
		ifStmt().statements().statement().mapTo(ArrayList()) { it.toAst() }
).apply { position = ifStmt().statements().toPosition() }

private fun IfStatementContext.transformElseBlock(): BlockStatement? {
	val elseStmt = ifStmt().elseStmt() ?: return null
	return BlockStatement(elseStmt.statements().statement().mapTo(ArrayList()) { it.toAst() }).apply {
		position = elseStmt.statements().toPosition()
	}
}

private fun IfStatementContext.transformElifStatements(): MutableList<ElifStatement> {
	val elifs = ifStmt().elifStmt()
	return elifs?.mapTo(LinkedList()) {
		ElifStatement(condition = it.expression().toAst(),
				thenStatement = BlockStatement(it.statements().statement().mapTo(ArrayList()) { it.toAst() })
						.apply { position = toPosition() })
				.apply { position = it.statements().toPosition() }
	} ?: mutableListOf()
}

