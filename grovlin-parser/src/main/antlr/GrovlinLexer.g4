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

// Defined Types
INT                : 'Int';
DECIMAL            : 'Decimal';

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
DECLIT             : '0'|[1-9][0-9]* '.' [0-9]+ ;

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
MUL                : '*' ;
DIV                : '/' ;
MOD                : '%' ;
ASSIGN             : '=' ;
LPAREN             : '(' ;
RPAREN             : ')' ;

// Misc
POINT              : '.';
COMMA              : ',';
SEMICOLON          : ':';
LBRACE             : '{';
RBRACE             : '}';

// Identifiers
ID                 : [_]*[A-Za-z0-9_]+ ;