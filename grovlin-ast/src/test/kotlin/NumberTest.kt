import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
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
		
		assertThat(sum.left.collectByType<MultiplicationExpression>()[0], present())
		assertThat(sum.right.collectByType<SumExpression>()[0], present())
		assertThat(sum.right.collectByType<SumExpression>()[0], present())
		assertTrue(sum.right.collectByType<SumExpression>()[0].left is IntLit)
		assertThat(sum.right.collectByType<SumExpression>()[0].right.collectByType<SubtractionExpression>(), present())
		assertTrue(sum.right.collectByType<SumExpression>()[0].right.collectByType<SubtractionExpression>()[0].left is IntLit)
		assertThat(sum.right.collectByType<SumExpression>()[0].right.collectByType<SubtractionExpression>()[0].right
				.collectByType<DivisionExpression>()[0], present())
	}
}