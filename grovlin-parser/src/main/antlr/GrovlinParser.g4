parser grammar GrovlinParser;

options { tokenVocab=GrovlinLexer; }

grovlinFile
: lines=line+
;

line
: statement (NEWLINE | EOF)
;

statements
: statement*
;

statement
: memberDeclaration
| varDeclaration
| assignment
| print
;

typeDeclaration
: TYPE ID LBRACE memberDeclaration* RBRACE
;

memberDeclaration
: propertyDeclaration
| typeDeclaration
| defDeclaration
;

propertyDeclaration
: HAS assignment
;

defDeclaration
: DEF methodDeclaration
| DEF lambdaDeclaration
;

methodDeclaration
: ID LPAREN RPAREN LBRACE statements RBRACE
;

lambdaDeclaration
: ID ASSIGN LBRACE statements RBRACE
;

print
: PRINT LPAREN expression RPAREN
;

varDeclaration
: (VAR | VAL) assignment
;

assignment
: ID ASSIGN expression
;

expression : left=expression operator=(DIV|MUL) right=expression # binaryOperation
           | left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
           | value=expression AS targetType=type                           # typeConversion
           | LPAREN expression RPAREN                                      # parenExpression
           | ID                                                            # varReference
           | MINUS expression                                              # minusExpression
           | INTLIT                                                        # intLiteral
           | DECLIT                                                        # decimalLiteral ;

type : INT     # integer
     | DECIMAL # decimal ;