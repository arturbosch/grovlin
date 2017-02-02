parser grammar GrovlinParser;

options { tokenVocab=GrovlinLexer; }

grovlinFile
: lines=line+
;

line
: statement (NEWLINE | EOF)
;

statement
: typeDeclaration
| defDeclaration
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
: ID ASSIGN LPAREN ((ID ID COMMA)* ID ID)* RPAREN LBRACE statements RBRACE
;

lambdaDeclaration
: ID ASSIGN LBRACE expression RBRACE
;

statements
: statement*
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

expression : left=expression operator=(DIVISION|ASTERISK) right=expression # binaryOperation
           | left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
           | value=expression AS targetType=type                           # typeConversion
           | LPAREN expression RPAREN                                      # parenExpression
           | ID                                                            # varReference
           | MINUS expression                                              # minusExpression
           | INTLIT                                                        # intLiteral
           | DECLIT                                                        # decimalLiteral ;

type : INT     # integer
     | DECIMAL # decimal ;