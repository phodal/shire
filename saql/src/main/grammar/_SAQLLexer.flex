/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* SQLite grammar adapted from http://www.sqlite.org/docsrc/doc/trunk/art/syntax/all-bnf.html
 * This should correspond directly to diagrams in the "SQL Syntax" part of SQLite documentation,
 * e.g. https://sqlite.org/lang_select.html. See also all diagrams here: http://www.sqlite.org/syntaxdiagrams.html
 *
 * Unfortunately the grammar linked above skips the most basic definitions, like string-literal,
 * table-name or digit, so we need to fill in these gaps ourselves.
 *
 * The grammar for expressions (`expr`) also needed to be reworked, see below.
 *
 * This file is used by Grammar-Kit to generate the lexer, parser, node types and PSI classes for Android SQL.
 */

package com.phodal.shirelang.saql.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
//import static com.phodal.shirelang.saql.psi.ShireTypes.*;
import com.intellij.psi.TokenType;

%%

%{
  public _ShireSqlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _ShireSqlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%caseless


WHITE_SPACE=\s+

COMMENT="/*" ( ([^"*"]|[\r\n])* ("*"+ [^"*""/"] )? )* ("*" | "*"+"/")?
IDENTIFIER=([[:jletter:]--$])[:jletterdigit:]*
LINE_COMMENT=--[^\r\n]*
NUMERIC_LITERAL=(([0-9]+(\.[0-9]*)?|\.[0-9]+)(E(\+|-)?[0-9]+)?)|(0x[0-9a-f]+)
NAMED_PARAMETER=[:@$][:jletterdigit:]+
NUMBERED_PARAMETER=\?\d*

UNTERMINATED_SINGLE_QUOTE_STRING_LITERAL=X?\'(\'\'|[^\'])*
SINGLE_QUOTE_STRING_LITERAL={UNTERMINATED_SINGLE_QUOTE_STRING_LITERAL} \'
UNTERMINATED_DOUBLE_QUOTE_STRING_LITERAL=X?\"(\"\"|[^\"])*
DOUBLE_QUOTE_STRING_LITERAL={UNTERMINATED_DOUBLE_QUOTE_STRING_LITERAL} \"
UNTERMINATED_BACKTICK_LITERAL=\`(\`\`|[^\`])*
BACKTICK_LITERAL={UNTERMINATED_BACKTICK_LITERAL} \`
UNTERMINATED_BRACKET_LITERAL=\[[^\]]*
BRACKET_LITERAL={UNTERMINATED_BRACKET_LITERAL} \]

%%
<YYINITIAL> {
  {WHITE_SPACE}                       { return WHITE_SPACE; }


  {BACKTICK_LITERAL}                  { return BACKTICK_LITERAL; }
  {BRACKET_LITERAL}                   { return BRACKET_LITERAL; }
  {COMMENT}                           { return COMMENT; }
  {DOUBLE_QUOTE_STRING_LITERAL}       { return DOUBLE_QUOTE_STRING_LITERAL; }
  {IDENTIFIER}                        { return IDENTIFIER; }
  {LINE_COMMENT}                      { return LINE_COMMENT; }
  {NUMERIC_LITERAL}                   { return NUMERIC_LITERAL; }
  {NAMED_PARAMETER}                   { return NAMED_PARAMETER; }
  {NUMBERED_PARAMETER}                { return NUMBERED_PARAMETER; }
  {SINGLE_QUOTE_STRING_LITERAL}       { return SINGLE_QUOTE_STRING_LITERAL; }

  {UNTERMINATED_SINGLE_QUOTE_STRING_LITERAL} { return UNTERMINATED_SINGLE_QUOTE_STRING_LITERAL; }
  {UNTERMINATED_DOUBLE_QUOTE_STRING_LITERAL} { return UNTERMINATED_DOUBLE_QUOTE_STRING_LITERAL; }
  {UNTERMINATED_BRACKET_LITERAL}             { return UNTERMINATED_BRACKET_LITERAL; }
  {UNTERMINATED_BACKTICK_LITERAL}            { return UNTERMINATED_BACKTICK_LITERAL; }
}

[^] { return BAD_CHARACTER; }
