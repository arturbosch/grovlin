parser grammar GrovlinParser;

options { tokenVocab=GrovlinLexer; }

grovlinFile
:   statements EOF
;

statements
: (statement  ) *
;

statement
: memberDeclaration #memberDeclarationStatement
| varDeclaration    #varDeclarationStatement
| expressionStmt    #expressionStatement
| assignment        #assignmentStatement
| print             #printStatement
| program           #programStatement
| ifStmt            #ifStatement
| forStmt           #forStatement
| whileStmt         #whileStatement
| returnStmt        #returnStatement
;

expressionStmt
: expression
;

ifStmt
: IF expression LBRACE statements RBRACE (elifs=elifStmt)* (elseStmt)?
;

elifStmt
: ELIF expression LBRACE statements RBRACE
;

elseStmt
: ELSE LBRACE statements RBRACE (elifStmt|elseStmt)?
;

forStmt
: FOR ID SEMICOLON expression LBRACE statements RBRACE
;

whileStmt
: WHILE expression LBRACE statements RBRACE
;

returnStmt
: RETURN expression
;

typeDeclaration
: TYPE typeName=TYPEID (EXTENDS extendTypes+=TYPEID (COMMA extendTypes+=TYPEID)*)? (LBRACE memberDeclaration* RBRACE)?
;

objectDeclaration
: OBJECT objectName=TYPEID (EXTENDS extendObject=TYPEID)? (AS extendTypes+=TYPEID (COMMA extendTypes+=TYPEID)*)? (LBRACE
memberDeclaration* RBRACE)?
;

memberDeclaration
: propertyDeclaration   #propertyMemberDeclaration
| typeDeclaration       #typeMemberDeclaration
| objectDeclaration     #objectMemberDeclaration
| defDeclaration        #defMemberDeclaration
;

propertyDeclaration
: (OVERRIDE)? TYPEID (ID|assignment)
;

defDeclaration
: methodDeclaration #MethodDefinition
| lambdaDeclaration #LambdaDefinition
;

methodDeclaration
: DEF ID LPAREN parameterList? RPAREN (SEMICOLON TYPEID)? (LBRACE statements RBRACE)?
;

parameterList
: parameter (COMMA parameter)*
;

parameter
: TYPEID ID
;

lambdaDeclaration
: DEF ID ASSIGN LBRACE statements RBRACE
;

print
: PRINT LPAREN expression RPAREN
;

program
: PROGRAM LBRACE statements RBRACE
;

varDeclaration
: (VAR | VAL) assignment
;

assignment
: ID ASSIGN expression
;

argumentList
: argument (COMMA argument)*
;

argument
: expression
;



expression
: TYPEID LPAREN RPAREN                                          # objectCreationExpression
| THIS                                                          # thisExpression
| scope=expression POINT fieldName=ID                           # getterAccessExpression
| scope=expression POINT assignment                             # setterAccessExpression
| scope=expression POINT methodName=ID LPAREN argumentList? RPAREN  # callExpression
| methodName=ID LPAREN argumentList? RPAREN                  # callExpression
| LPAREN expression RPAREN                                      # parenExpression
| left=expression operator=(EQUAL|INEQUAL|LESS|LESSEQUAL|GREATER|GREATEREQUAL) right=expression # binaryOperation
| left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
| left=expression operator=XOR right=expression                 # binaryOperation
| left=expression operator=(DIV|MUL|AND) right=expression       # binaryOperation
| left=expression operator=OR right=expression                  # binaryOperation
| value=expression AS targetType=type                           # typeConversion
| INTLIT POINT POINT INTLIT                                     # intRangeExpression
| ID                                                            # varReference
| MINUS expression                                              # minusExpression
| NOT expression                                                # notExpression
| INTLIT                                                        # intLiteral
| DECLIT                                                        # decimalLiteral
| BOOLLIT                                                       # boolLiteral
| STRINGLIT                                                     # stringLiteral
;

type
: INT     # integer
| DECIMAL # decimal
| BOOL    # bool
| STRING  # string
;
