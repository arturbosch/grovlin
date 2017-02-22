package io.gitlab.arturbosch.grovlin.parser

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

/**
 * @author Artur Bosch
 */
class IfTest {

	@Test
	fun parseIfElifElseStatements() {
		val actual = tokens("if (true) { print(5)if(false){} else {}if(true){}elif(false){}else{} } ")
		val expected = listOf("IF", "LPAREN", "BOOLLIT", "RPAREN", "LBRACE", "PRINT", "LPAREN", "INTLIT", "RPAREN", "IF", "LPAREN", "BOOLLIT",
				"RPAREN", "LBRACE", "RBRACE", "ELSE", "LBRACE", "RBRACE", "IF", "LPAREN", "BOOLLIT", "RPAREN", "LBRACE", "RBRACE",
				"ELIF", "LPAREN", "BOOLLIT", "RPAREN", "LBRACE", "RBRACE", "ELSE", "LBRACE", "RBRACE", "RBRACE")

		assertThat(actual, equalTo(expected))
	}

}