import io.gitlab.arturbosch.grovlin.ast.DivisionExpression
import io.gitlab.arturbosch.grovlin.ast.IntLit
import io.gitlab.arturbosch.grovlin.ast.MultiplicationExpression
import io.gitlab.arturbosch.grovlin.ast.SubtractionExpression
import io.gitlab.arturbosch.grovlin.ast.SumExpression
import io.gitlab.arturbosch.grovlin.ast.asGrovlinFile
import io.gitlab.arturbosch.grovlin.ast.operations.collectByType
import org.junit.Test
import kotlin.test.assertTrue

/**
 * @author Artur Bosch
 */
class NumberTest {

	@Test
	fun parseComplexExpressions() {
		val grovlinFile = "val a = (5 * 5) + (1 + (8 - (4 / 2)))".asGrovlinFile()
		val sum = grovlinFile.collectByType<SumExpression>()[0]

		assertTrue(sum.left is MultiplicationExpression)
		assertTrue(sum.right is SumExpression)
		assertTrue((sum.right as SumExpression).left is IntLit)
		assertTrue((sum.right as SumExpression).right is SubtractionExpression)
		assertTrue(((sum.right as SumExpression).right as SubtractionExpression).right is DivisionExpression)
	}
}