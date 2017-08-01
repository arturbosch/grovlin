package io.gitlab.arturbosch.grovlin.parser

import io.gitlab.arturbosch.grovlin.GrovlinParser

/**
 * @author Artur Bosch
 */

fun String.parseStatement() = parse().root?.statements()?.statement()?.get(0) ?: throw AssertionError()

fun String.parseMethod() = (((parseStatement()
		as? GrovlinParser.MemberDeclarationStatementContext)?.memberDeclaration()
		as? GrovlinParser.DefMemberDeclarationContext)?.defDeclaration()
		as? GrovlinParser.MethodDefinitionContext)?.methodDeclaration()
		?: throw AssertionError("No method declaration!")

fun String.parseExpression() = (parseStatement() as? GrovlinParser.ExpressionStatementContext)
		?.expressionStmt()?.expression() ?: throw AssertionError()

fun String.parseMethodCall() = parseExpression() as? GrovlinParser.CallExpressionContext
		?: throw AssertionError("No call expression!")
