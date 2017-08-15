package io.gitlab.arturbosch.grovlin.parser

import org.junit.Test

/**
 * @author Artur Bosch
 */
class TraitsAndObjectsTest {

	@Test
	fun parseSimple() {
		"""
			trait Iterable {
				def iterator(): Iterator
			}
			trait Iterator {
				def hasNext(): Boolean
				def next(): Any
			}
			object Pair as Iterable {

				constructor(Any @first,
							Any @second)

				override def iterator(): Iterator {
					return PairIterator(this)
				}
			}
			object PairIterator as Iterator {

				constructor(Pair @pair) {
					Int @counter = 2
				}

				override def hasNext(): Boolean {
					return @counter != 0
				}

				override def next(): Any {
					if hasNext() {
						if counter == 2 {
							@counter = @counter - 1
							return @pair.first
						}
						if counter == 1 {
							@counter = @counter - 1
							return @pair.second
						}
					} else {
						throw NoSuchElementException()
					}
				}
			}
		"""
	}
}
