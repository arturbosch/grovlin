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
EXTENDS            : 'extends';

IF                 : 'if';
ELSE               : 'else';
ELIF               : 'elif';

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
DECLIT             : '0'|[1-9][0-9]* '.' [0-9]+ ;
BOOLLIT            : 'true' | 'false';

// Misc
POINT              : '.';
COMMA              : ',';
SEMICOLON          : ':';
LBRACE             : '{';
RBRACE             : '}';
LPAREN             : '(' ;
RPAREN             : ')' ;

// Relations

EQUAL              : '==' ;
INEQUAL            : '!=' ;
LESSEQUAL          : '<=' ;
LESS               : '<'  ;
GREATEREQUAL       : '>=' ;
GREATER            : '>'  ;

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
MUL                : '*' ;
DIV                : '/' ;
MOD                : '%' ;

ASSIGN             : '=' ;

// Bool operators
NOT : '!'  ;
OR  : '||' ;
AND : '&&' ;
XOR : '^'  ;

// Identifiers
TYPEID             : [A-Z][A-Za-z]* ;
ID                 : [_]*[A-Za-z0-9_]+ ;

// Defined Types
INT                : 'Int';
DECIMAL            : 'Decimal';
BOOL               : 'Bool';