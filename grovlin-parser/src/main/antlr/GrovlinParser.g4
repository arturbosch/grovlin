parser grammar GrovlinParser;

options { tokenVocab=GrovlinLexer; }

grovlinFile
: statements EOF
;

nls
: NL*
;

statements
: (statement nls) *
;

statement
: memberDeclaration #memberDeclarationStatement
| varDeclaration    #varDeclarationStatement
| expressionStmt    #expressionStatement
| assignment        #assignmentStatement
| print             #printStatement
| program           #programStatement
;

expressionStmt
: expression nls
;

typeDeclaration
: TYPE ID LBRACE nls memberDeclaration* RBRACE nls
;

memberDeclaration
: propertyDeclaration   #propertyMemberDeclaration
| typeDeclaration       #typeMemberDeclaration
| defDeclaration        #defMemberDeclaration
;

propertyDeclaration
: HAS assignment nls
;

defDeclaration
: DEF methodDeclaration #MethodDefinition
| DEF lambdaDeclaration #LambdaDefinition
;

methodDeclaration
: ID LPAREN RPAREN LBRACE nls statements RBRACE nls
;

lambdaDeclaration
: ID ASSIGN LBRACE nls statements RBRACE nls
;

print
: PRINT LPAREN expression RPAREN
;

program
: PROGRAM LBRACE nls statements RBRACE nls
;

varDeclaration
: (VAR | VAL) assignment nls
;

assignment
: ID ASSIGN expression
;

expression
: LPAREN expression RPAREN                                      # parenExpression
| THIS                                                          # thisExpression
| container=expression POINT methodName=ID LPAREN RPAREN        # callExpression
| methodName=ID LPAREN RPAREN                                   # callExpression
| left=expression operator=(DIV|MUL|AND) right=expression       # binaryOperation
| left=expression operator=XOR right=expression                 # binaryOperation
| left=expression operator=(PLUS|MINUS|OR) right=expression     # binaryOperation
| value=expression AS targetType=type                           # typeConversion
| LPAREN expression RPAREN                                      # parenExpression
| ID                                                            # varReference
| MINUS expression                                              # minusExpression
| NOT expression                                                # notExpression
| INTLIT                                                        # intLiteral
| DECLIT                                                        # decimalLiteral
| BOOLLIT                                                       # boolLiteral
;

type
: INT     # integer
| DECIMAL # decimal
| BOOL    # bool
;