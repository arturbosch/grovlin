lexer grammar GrovlinLexer;

// Whitespace
NL            : '\r\n' | 'r' | '\n' ;
WS                 : [\t ]+ -> skip ;

// Keywords
TYPE               : 'type';
DEF                : 'def';
VAR                : 'var';
VAL                : 'val';
HAS                : 'has';
OBJECT             : 'object';
THIS               : 'this';
AS                 : 'as';
IS                 : 'is';
PRINT              : 'print';
PROGRAM            : 'program';

IF                 : 'if';
ELSE               : 'else';
ELIF               : 'elif';

// Defined Types
INT                : 'Int';
DECIMAL            : 'Decimal';
BOOL               : 'Bool';

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
DECLIT             : '0'|[1-9][0-9]* '.' [0-9]+ ;
BOOLLIT            : 'true' | 'false';

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
MUL                : '*' ;
DIV                : '/' ;
MOD                : '%' ;
ASSIGN             : '=' ;
LPAREN             : '(' ;
RPAREN             : ')' ;

// Bool operators
NOT : '!'  ;
OR  : '||' ;
AND : '&&' ;
XOR : '^'  ;

// Misc
POINT              : '.';
COMMA              : ',';
SEMICOLON          : ':';
LBRACE             : '{';
RBRACE             : '}';

// Identifiers
ID                 : [_]*[A-Za-z0-9_]+ ;