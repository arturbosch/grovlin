# Grovlin - Just another JVM language - My playground
[![build status](https://gitlab.com/arturbosch/Grovlin/badges/master/build.svg)](https://gitlab.com/arturbosch/Grovlin/commits/master)

Building an own language I bet is the dream of every programmer who already programmed in a lot of different languages but never was 100% happy with one of them.

In my case I'm nearly 100% happy with Kotlin but missing a bit of the simplicity and java-like syntax of Groovy. After reading the really good blog-series of [Frederico Tomasseti](https://tomassetti.me/getting-started-with-antlr-building-a-simple-expression-language/), I decided
to merge my favorite features of Kotlin and Groovy. So this project was born :).

### Milestone 1 - v0.1

Antlr -> AST -> Grovlin -> Java -> Bytecode -> JVM

A stupid grovlin program can now be run on the jvm:

```
program {
    var x = 5
    var y = 4
    print(x + y)
}
```

`java -jar grovlin-compiler/build/libs/grovlin-compiler-0.1.0.jar run [path/to/program.grovlin]`

##### Upcoming Type&Object Support

```
type Node {
    def traverse()
}
type Leaf extends Node {
    Int data
    def traverse() {
        print(data)
    }
}
type Tree extends Node {
    Node left
    Node right
    Node parent
    def traverse() {
        left.traverse()
        right.traverse()
    }
}

object TreeImpl as Tree {
    override Node left
    override Node right
    override Node parent
}

object LeafImpl as Leaf {
    override Int data = 1
}

program {
    var tree = TreeImpl()
    tree.left = LeafImpl()
    tree.left.data = 5
    var tree2 = TreeImpl()
    tree2.parent = tree
    tree.right = tree2
    tree2.left = LeafImpl()
    tree2.right = LeafImpl()

    tree.traverse()
}
```