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
| assignment        #assignmentStatement
| print             #printStatement
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

varDeclaration
: (VAR | VAL) assignment nls
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