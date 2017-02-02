lexer grammar GrovlinLexer;

// Whitespace
NEWLINE            : '\r\n' | 'r' | '\n' ;
WS                 : [\t ]+ -> skip ;

// Keywords
TYPE               : 'type';
DEF                : 'def';
VAR                : 'var';
VAL                : 'val';
HAS                : 'has';
OBJECT             : 'object';
SELF               : 'self';
AS                 : 'as';
IS                 : 'is';
PRINT              : 'print';

// Defined Types
INT                : 'Int';
DECIMAL            : 'Decimal';

// Literals
INTLIT             : '0'|[1-9][0-9]* ;
DECLIT             : '0'|[1-9][0-9]* '.' [0-9]+ ;

// Operators
PLUS               : '+' ;
MINUS              : '-' ;
ASTERISK           : '*' ;
DIVISION           : '/' ;
ASSIGN             : '=' ;
LPAREN             : '(' ;
RPAREN             : ')' ;

// Misc
COMMA              : ',';
SEMICOLON          : ':';
LBRACE             : '{';
RBRACE             : '}';

// Identifiers
ID                 : [_]*[a-z][A-Za-z0-9_]* ;