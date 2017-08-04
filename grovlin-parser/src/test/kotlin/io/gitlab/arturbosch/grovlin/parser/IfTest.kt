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
		val actual = tokens("if true { print(5)if false {} else {}if true {}elif false {}else{} } ")
		val expected = listOf("IF", "BOOLLIT", "LBRACE", "ID", "LPAREN", "INTLIT", "RPAREN", "IF", "BOOLLIT",
				"LBRACE", "RBRACE", "ELSE", "LBRACE", "RBRACE", "IF", "BOOLLIT", "LBRACE", "RBRACE",
				"ELIF", "BOOLLIT", "LBRACE", "RBRACE", "ELSE", "LBRACE", "RBRACE", "RBRACE")

		assertThat(actual, equalTo(expected))
	}

}
