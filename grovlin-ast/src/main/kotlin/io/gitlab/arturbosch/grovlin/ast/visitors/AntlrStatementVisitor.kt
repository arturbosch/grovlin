package io.gitlab.arturbosch.grovlin.ast.visitors

import io.gitlab.arturbosch.grovlin.GrovlinParser
import io.gitlab.arturbosch.grovlin.GrovlinParserBaseVisitor
import io.gitlab.arturbosch.grovlin.ast.Assignment
import io.gitlab.arturbosch.grovlin.ast.BlockStatement
import io.gitlab.arturbosch.grovlin.ast.DEFAULT_GROVLIN_FILE_NAME
import io.gitlab.arturbosch.grovlin.ast.ElifStatement
import io.gitlab.arturbosch.grovlin.ast.ExpressionStatement
import io.gitlab.arturbosch.grovlin.ast.ForStatement
import io.gitlab.arturbosch.grovlin.ast.GrovlinFile
import io.gitlab.arturbosch.grovlin.ast.IfStatement
import io.gitlab.arturbosch.grovlin.ast.LambdaDeclaration
import io.gitlab.arturbosch.grovlin.ast.MethodDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectDeclaration
import io.gitlab.arturbosch.grovlin.ast.ObjectOrTypeType
import io.gitlab.arturbosch.grovlin.ast.Program
import io.gitlab.arturbosch.grovlin.ast.PropertyDeclaration
import io.gitlab.arturbosch.grovlin.ast.Reference
import io.gitlab.arturbosch.grovlin.ast.ReturnStatement
import io.gitlab.arturbosch.grovlin.ast.Statement
import io.gitlab.arturbosch.grovlin.ast.Type
import io.gitlab.arturbosch.grovlin.ast.TypeDeclaration
import io.gitlab.arturbosch.grovlin.ast.VarDeclaration
import io.gitlab.arturbosch.grovlin.ast.WhileStatement
import java.util.ArrayList

/**
 * @author Artur Bosch
 */
fun GrovlinParser.GrovlinFileContext?.asGrovlinFile(): GrovlinFile {
	this ?: throw AssertionError("Grovlin file context must not be null!")
	val visitor = AntlrStatementVisitor()
	val block = visitor.visitStatements(this.statements())
	return GrovlinFile(DEFAULT_GROVLIN_FILE_NAME, block)
}

class AntlrStatementVisitor : GrovlinParserBaseVisitor<Statement>() {

	private val exprVisitor = AntlrExpressionVisitor()

	override fun visitLambdaDeclaration(ctx: GrovlinParser.LambdaDeclarationContext): Statement {
		val block = visitStatements(ctx.statements()) ?: throw AssertionError("Body is missing! ($ctx)")
		return LambdaDeclaration(ctx.ID().text, block)
	}

	override fun visitPropertyDeclaration(ctx: GrovlinParser.PropertyDeclarationContext): PropertyDeclaration {
		val name = ctx.assignment()?.ID()?.text ?: ctx.ID().text
		val initializer = ctx.assignment()?.expression()?.let { exprVisitor.visit(it) }
		return PropertyDeclaration(Type.of(ctx.TYPEID().text), name, initializer)
	}

	override fun visitMethodDeclaration(ctx: GrovlinParser.MethodDeclarationContext): MethodDeclaration {
		val block = visitStatements(ctx.statements())
		val returnType = Type.of(ctx.TYPEID()?.text ?: "Void")
		return MethodDeclaration(ctx.ID().text, block, returnType)
	}

	override fun visitTypeDeclaration(ctx: GrovlinParser.TypeDeclarationContext): TypeDeclaration {
		val extended = ctx.extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) }
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visit(it) }
		val block = BlockStatement(memberDecls)
		val type = ObjectOrTypeType(ctx.typeName.text)
		return TypeDeclaration(type, extended, block)
	}

	override fun visitObjectDeclaration(ctx: GrovlinParser.ObjectDeclarationContext): ObjectDeclaration {
		val extendedObject = ctx.extendObject?.let { ObjectOrTypeType(it.text) }
		val extended = ctx.extendTypes.mapTo(ArrayList()) { ObjectOrTypeType(it.text) }
		val memberDecls = ctx.memberDeclaration().mapTo(ArrayList()) { visit(it) }
		val block = BlockStatement(memberDecls)
		val type = ObjectOrTypeType(ctx.objectName.text)
		return ObjectDeclaration(type, extendedObject, extended, block)
	}

	override fun visitVarDeclaration(ctx: GrovlinParser.VarDeclarationContext): Statement {
		val assignment = visitAssignment(ctx.assignment())
		val isVal = ctx.VAL() != null
		return VarDeclaration(assignment.reference.name, assignment.value, isVal)
	}

	override fun visitStatements(ctx: GrovlinParser.StatementsContext?): BlockStatement? {
		val statements = ctx?.statement()?.mapTo(ArrayList()) { visit(it) }
		return statements?.let { BlockStatement(it) }
	}

	override fun visitExpressionStatement(ctx: GrovlinParser.ExpressionStatementContext): ExpressionStatement {
		return ExpressionStatement(exprVisitor.visit(ctx.expressionStmt().expression()))
	}

	override fun visitAssignment(ctx: GrovlinParser.AssignmentContext): Assignment {
		return Assignment(Reference(ctx.ID().text), exprVisitor.visit(ctx.expression()))
	}

	override fun visitProgramStatement(ctx: GrovlinParser.ProgramStatementContext): Program {
		val block = visitStatements(ctx.program().statements())
		return Program(DEFAULT_GROVLIN_FILE_NAME, block)
	}

	override fun visitExpressionStmt(ctx: GrovlinParser.ExpressionStmtContext): ExpressionStatement {
		return ExpressionStatement(exprVisitor.visit(ctx.expression()))
	}

	override fun visitIfStmt(ctx: GrovlinParser.IfStmtContext): IfStatement {
		val condition = exprVisitor.visit(ctx.expression())
		val thenBlock = visit(ctx.statements()) as BlockStatement
		val elifs = ctx.elifStmt().mapTo(ArrayList()) { visitElifStmt(it) } as MutableList<ElifStatement>
		val elseStmt = ctx.elseStmt()?.let { visitElseStmt(it) }
		return IfStatement(condition, thenBlock, elifs, elseStmt)
	}

	override fun visitElifStmt(ctx: GrovlinParser.ElifStmtContext): ElifStatement {
		val condition = exprVisitor.visit(ctx.expression())
		val block = visitStatements(ctx.statements()) ?: throw AssertionError("Body is missing!")
		return ElifStatement(condition, block)
	}

	override fun visitElseStmt(ctx: GrovlinParser.ElseStmtContext): BlockStatement? {
		return visitStatements(ctx.statements())
	}

	override fun visitForStmt(ctx: GrovlinParser.ForStmtContext): ForStatement {
		val varName = ctx.ID().text
		val condition = exprVisitor.visit(ctx.expression())
		val block = visitStatements(ctx.statements()) ?: throw AssertionError("Body is missing!")
		return ForStatement(varName, condition, block)
	}

	override fun visitWhileStmt(ctx: GrovlinParser.WhileStmtContext): WhileStatement {
		val condition = exprVisitor.visit(ctx.expression())
		val block = visitStatements(ctx.statements()) ?: throw AssertionError("Body is missing!")
		return WhileStatement(condition, block)
	}

	override fun visitReturnStmt(ctx: GrovlinParser.ReturnStmtContext): ReturnStatement {
		return ReturnStatement(exprVisitor.visit(ctx.expression()))
	}
}
