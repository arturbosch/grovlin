parser grammar GrovlinParser;

options { tokenVocab=GrovlinLexer; }

grovlinFile
: nls statements EOF
;

nls
: NL*
;

commaNL
: COMMA nls
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
| ifStmt            #ifStatement
| forStmt           #forStatement
| whileStmt         #whileStatement
;

expressionStmt
: expression nls
;

ifStmt
: IF nls LPAREN expression RPAREN nls LBRACE nls statements nls RBRACE nls (elifs=elifStmt)* (elseStmt)?
;

elifStmt
: ELIF nls LPAREN expression RPAREN nls LBRACE nls statements nls RBRACE nls
;

elseStmt
: ELSE nls LBRACE nls statements nls RBRACE nls (elifStmt|elseStmt)? nls
;

forStmt
: FOR ID SEMICOLON expression LBRACE nls statements nls RBRACE nls
;

whileStmt
: WHILE expression LBRACE nls statements nls RBRACE nls
;

typeDeclaration
: TYPE typeName=TYPEID (EXTENDS extendTypes+=TYPEID (COMMA extendTypes+=TYPEID)*)? (LBRACE nls memberDeclaration* RBRACE nls)?
;

objectDeclaration
: OBJECT objectName=TYPEID (EXTENDS extendObject=TYPEID)? (AS extendTypes+=TYPEID (COMMA extendTypes+=TYPEID)*)? (LBRACE nls
memberDeclaration* RBRACE nls)?
;

memberDeclaration
: propertyDeclaration   #propertyMemberDeclaration
| typeDeclaration       #typeMemberDeclaration
| objectDeclaration     #objectMemberDeclaration
| defDeclaration        #defMemberDeclaration
;

propertyDeclaration
: (OVERRIDE)? TYPEID (ID nls|assignment nls)
;

defDeclaration
: methodDeclaration #MethodDefinition
| lambdaDeclaration #LambdaDefinition
;

methodDeclaration
: DEF ID LPAREN parameterList? RPAREN nls (LBRACE nls statements RBRACE nls)?
;

parameterList
: parameter (commaNL parameter)*
;

parameter
: TYPEID ID
;

lambdaDeclaration
: DEF ID ASSIGN LBRACE nls statements RBRACE nls
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

argumentList
: argument (commaNL argument)*
;

argument
: ID
;

expression
: LPAREN expression RPAREN                                      # parenExpression
| TYPEID LPAREN RPAREN                                          # objectCreationExpression
| THIS                                                          # thisExpression
| scope=expression POINT fieldName=ID                           # getterAccessExpression
| scope=expression POINT assignment                             # setterAccessExpression
| scope=expression POINT methodName=ID LPAREN argumentList? RPAREN  # callExpression
| methodName=ID LPAREN argumentList? RPAREN                  # callExpression
| left=expression operator=(EQUAL|INEQUAL|LESS|LESSEQUAL|GREATER|GREATEREQUAL) right=expression # binaryOperation
| left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
| left=expression operator=XOR right=expression                 # binaryOperation
| left=expression operator=(DIV|MUL|AND) right=expression       # binaryOperation
| left=expression operator=OR right=expression                  # binaryOperation
| value=expression AS targetType=type                           # typeConversion
| LPAREN expression RPAREN                                      # parenExpression
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
