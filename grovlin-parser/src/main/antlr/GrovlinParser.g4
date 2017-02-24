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
| ifStmt            #ifStatement
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
: TYPEID (ID nls|assignment nls)
;

defDeclaration
: DEF methodDeclaration #MethodDefinition
| DEF lambdaDeclaration #LambdaDefinition
;

methodDeclaration
: ID LPAREN RPAREN nls LBRACE nls statements RBRACE nls
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
| left=expression operator=(EQUAL|INEQUAL|LESS|LESSEQUAL|GREATER|GREATEREQUAL) right=expression       # binaryOperation
| left=expression operator=(PLUS|MINUS) right=expression        # binaryOperation
| left=expression operator=XOR right=expression                 # binaryOperation
| left=expression operator=(DIV|MUL|AND) right=expression       # binaryOperation
| left=expression operator=OR right=expression                  # binaryOperation
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