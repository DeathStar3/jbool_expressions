grammar CPPExpr;

options
{
  language = Java;
  output = AST;
}

@lexer::header {
package com.bpodgursky.jbool_expressions.parsers;
}

@parser::header {
package com.bpodgursky.jbool_expressions.parsers;
}

LPAREN : '(';
RPAREN : ')';
AND : '&';
OR : '|';
NOT : '!';
TIMES : '*';
PLUS : '+';
MINUS : '-';
SUP : '>';
INF : '<';
SUPEQ : '>=';
INFEQ : '<=';
DIFF : '!=';
EQ : '==';
TRUE : 'true';
FALSE : 'false';
NAME : ('A'..'Z' | 'a'..'z' | '_' | '0'..'9')+;
QUOTED_NAME : '\''~('\r' | '\n' | '\'')+'\'';
DOUBLE_QUOTED_NAME : '"'~('\r' | '\n' | '"')+'"';
WS : ( ' ' | '\t' | '\r' | '\n' )+ { $channel = HIDDEN; };

expression : orexpression;
orexpression : andexpression (OR^ andexpression)*;
andexpression : notexpression (AND^ notexpression)*;
notexpression : NOT^ notexpression | nonboolexpression;
nonboolexpression : atom ((TIMES | PLUS | MINUS | SUP | INF | SUPEQ | INFEQ | DIFF | EQ)^ atom)*;
atom : TRUE | FALSE | NAME^ (LPAREN! NAME (','! (NAME | quotedname))* RPAREN!)* | quotedname | LPAREN^ orexpression RPAREN!;
quotedname : QUOTED_NAME | DOUBLE_QUOTED_NAME;

