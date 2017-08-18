# Grovlin - Just another JVM language - Learning Compilers
[![build status](https://gitlab.com/arturbosch/Grovlin/badges/master/build.svg)](https://gitlab.com/arturbosch/Grovlin/commits/master)

Building an own language I bet is the dream of every programmer who already programmed in a lot of different languages but never was 100% happy with one of them.

In my case I'm nearly 100% happy with Kotlin but missing a bit of the simplicity and java-like syntax of Groovy. After reading the really good blog-series of [Frederico Tomasseti](https://tomassetti.me/getting-started-with-antlr-building-a-simple-expression-language/), I decided
to merge my favorite features of Kotlin and Groovy. So this project was born :).

In summer semester 2017 I had the chance to continue to work on this project in the Compiler Tool Practical of the
University of Bremen.

Antlr -> AST -> Grovlin -> Java -> Bytecode -> JVM

### Build & Run

- `gradle build fatjar`
- `java -jar grovlin-compiler/build/libs/grovlin-compiler-0.1.0.jar run [path/to/program.grovlin]`


### Code examples

```
def main(String args) {
    println("Guess my number (0-9)!")
    var number = rand(10).toString()
    var input = readline()

    if input == number {
        println("You guessed my number!")
    } else {
        println("My number was " + number + " and your number is " + input)
    }
}
```

```
def main(String args) {
    for i : 0..10 {
        print(i)
        if i != 9 {
            print(", ")
        }
    }

    println()
    println(loop(5))
}

def loop(Int n): String {
    var current = n
    while current > 0 {
        print(".")
        current = current - 1
    }
    println()
    return "I 'have' finished!"
}

```

```
trait Node {
    def traverse()
}
trait Leaf extends Node {
    Int data
    override def traverse() {
        print(data)
    }
}
trait Tree extends Node {
    Node left
    Node right
    Node parent
    override def traverse() {
        left.traverse()
        right.traverse()
    }
}

object BinaryTree as Tree {
    override Node left
    override Node right
    override Node parent
}

object LeafImpl as Leaf {
    override Int data = 1
}

def main(String args) {
    var tree = BinaryTree()
    var leaf = LeafImpl()
    leaf.data = 5
    tree.left = leaf
    var tree2 = BinaryTree()
    tree2.parent = tree
    tree.right = tree2
    tree2.left = LeafImpl()
    tree2.right = LeafImpl()

    tree.traverse()
}
```
