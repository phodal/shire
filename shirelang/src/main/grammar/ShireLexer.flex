package com.phodal.shirelang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import static com.phodal.shirelang.psi.ShireTypes.*;
import com.intellij.psi.TokenType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

%%

%{
  public _ShireLexer() {
    this((java.io.Reader)null);
  }
%}

%class ShireLexer
%class _ShireLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%s YYUSED
%s AGENT_BLOCK
%s VARIABLE_BLOCK

%s COMMAND_BLOCK
%s COMMAND_VALUE_BLOCK

%s EXPR_BLOCK
%s VELOCITY_BLOCK

%s CODE_BLOCK
%s COMMENT_BLOCK
%s LINE_BLOCK
%s FRONT_MATTER_BLOCK
%s FRONT_MATTER_VALUE_BLOCK
%s FRONT_MATTER_VAL_OBJECT
%s PATTERN_ACTION_BLOCK
%s CONDITION_EXPR_BLOCK
%s QUERY_STATEMENT_BLOCK

%s LANG_ID

SPACE                    = [ \t\n\x0B\f\r]+
IDENTIFIER               = [a-zA-Z0-9][_\-a-zA-Z0-9]*
FRONTMATTER_KEY          = [a-zA-Z0-9][_\-a-zA-Z0-9]*
DATE                     = [0-9]{4}-[0-9]{2}-[0-9]{2}
STRING                   = [a-zA-Z0-9][_\-a-zA-Z0-9]*
INDENT                   = "  "

COMMENT                  = "//"[^\r\n]*
BLOCK_COMMENT            = "/"[*][^*]*[*]+([^/*][^*]*[*]+)*"/"

EscapedChar              = "\\" [^\n]
RegexWord                = [^\r\n\\\"' \t$`()] | {EscapedChar}
REGEX                    = \/{RegexWord}+\/
PATTERN_EXPR             = \/{RegexWord}+\/

LPAREN                   = \(
RPAREN                   = \)
PIPE                     = \|

VARIABLE_ID              = [a-zA-Z0-9][_\-a-zA-Z0-9]*
AGENT_ID                 = [a-zA-Z0-9][_\-a-zA-Z0-9]*
COMMAND_ID               = [a-zA-Z0-9][_\-a-zA-Z0-9]*
LANGUAGE_ID              = [a-zA-Z][_\-a-zA-Z0-9 .]*
NUMBER                   = [0-9]+
BOOLEAN                  = true|false|TRUE|FALSE|"true"|"false"

TEXT_SEGMENT             = [^$/@#\n]+
WHITE_SPACE              = [ \t]+
DOUBLE_QUOTED_STRING     = \"([^\\\"\r\n]|\\[^\r\n])*\"?
SINGLE_QUOTED_STRING     = '([^\\'\r\n]|\\[^\r\n])*'?
QUOTE_STRING             = {DOUBLE_QUOTED_STRING}|{SINGLE_QUOTED_STRING}

// READ LINE FORMAT: L2C2-L0C100 or L1-L1
LINE_INFO                = L[0-9]+(C[0-9]+)?(-L[0-9]+(C[0-9]+)?)?
COMMAND_PROP             = [^\ \t\r\n]*
CODE_CONTENT             = [^\n]+
COMMENTS                 = \[ ([^\]]+)? \] [^\t\r\n]*
NEWLINE                  = \n | \r | \r\n

COLON                    =:
SHARP                    =#
LBRACKET                 =\[
RBRACKET                 =\]

DEFAULT                  =default
CASE                     =case
ARROW                    ==>
WHEN                     =when
IF                       =if
ELSE                     =else
ELSEIF                   =elseif
ENDIF                    =endif
END                      =end

%{
    private boolean isCodeStart = false;
    private boolean isInsideShireTemplate = false;
%}

%{
    private IElementType codeContent() {
        yybegin(YYINITIAL);

        // handle for end which is \n```
        String text = yytext().toString().trim();
        if ((text.equals("\n```") || text.equals("```")) && isCodeStart == true ) {
            isCodeStart = false;
            return CODE_BLOCK_END;
        }

        // new line
        if (text.equals("\n")) {
            return NEWLINE;
        }

        if (isCodeStart == false) {
            return TEXT_SEGMENT;
        }

        return CODE_CONTENT;
    }

    private IElementType content() {
        String text = yytext().toString().trim();
        if (isCodeStart == true && text.equals("```")) {
            return codeContent();
        }

        if (isCodeStart == false && text.startsWith("```")) {
            isCodeStart = true;
            yypushback(yylength() - 3);
            yybegin(LANG_ID);

            return CODE_BLOCK_START;
        }

        if (isCodeStart) {
            return CODE_CONTENT;
        } else {
            if (text.startsWith("[")) {
                yybegin(COMMENT_BLOCK);
                return comment();
            }

            yypushback(yylength());
            yybegin(YYUSED);

            return TEXT_SEGMENT;
        }
    }

    private IElementType comment() {
        String text = yytext().toString();
        if (text.contains("[") && text.contains("]")) {
            return COMMENTS;
        } else {
            return TEXT_SEGMENT;
        }
    }

    private IElementType command_value() {
        String text = yytext().toString().trim();
        String [] split = text.split("#");

        if (split.length == 1) {
            return COMMAND_PROP;
        }

        // split by # if it is a line info
        String last = split[split.length - 1];
        Pattern compile = Pattern.compile("L\\d+(C\\d+)?(-L\\d+(C\\d+)?)?");
        Matcher matcher = compile.matcher(last);
        if (matcher.matches()) {
            // before # is command prop, after # is line info
            int number = last.length() + "#".length();
            if (number > 0) {
                yypushback(number);
                yybegin(LINE_BLOCK);
                return COMMAND_PROP;
            } else {
                return COMMAND_PROP;
            }
        }

        return COMMAND_PROP;
    }
%}

%%
<YYINITIAL> {
  "---"                   { yybegin(FRONT_MATTER_BLOCK); return FRONTMATTER_START; }
  {CODE_CONTENT}          { return content(); }
  {NEWLINE}               { return NEWLINE;  }
  "["                     { yypushback(yylength()); yybegin(COMMENT_BLOCK);  }
  [^]                     { yypushback(yylength()); return TEXT_SEGMENT; }
}

<FRONT_MATTER_BLOCK> {
  {WHEN}                  { return WHEN; }
  {FRONTMATTER_KEY}       { return FRONTMATTER_KEY; }
  {PATTERN_EXPR}          { return PATTERN_EXPR; }
  ":"                     { yybegin(FRONT_MATTER_VALUE_BLOCK);return COLON; }
  {NEWLINE}               { return NEWLINE; }
  "---"                   { yybegin(YYINITIAL); return FRONTMATTER_END; }
  "{"                     { yybegin(QUERY_STATEMENT_BLOCK); return OPEN_BRACE; }
  "  "                    { yybegin(FRONT_MATTER_VAL_OBJECT); return INDENT; }
  [^]                     { yypushback(yylength()); yybegin(YYINITIAL); }
}

<FRONT_MATTER_VAL_OBJECT> {
  {QUOTE_STRING}          { return QUOTE_STRING; }
  [^]                     { yypushback(yylength()); yybegin(FRONT_MATTER_BLOCK); }
}

<FRONT_MATTER_VALUE_BLOCK>  {
  {NUMBER}                { return NUMBER; }
  {DATE}                  { return DATE; }
  {BOOLEAN}               { return BOOLEAN; }
  {IDENTIFIER}            { return IDENTIFIER; }
  {QUOTE_STRING}          { return QUOTE_STRING; }
  {PATTERN_EXPR}          { yybegin(PATTERN_ACTION_BLOCK); return PATTERN_EXPR; }
  "["                     { return LBRACKET; }
  "]"                     { return RBRACKET; }
  ","                     { return COMMA; }
  " "                     { return TokenType.WHITE_SPACE; }
  "!"                     { return NOT; }
  "&&"                    { return ANDAND; }
  "||"                    { return OROR; }
  "."                     { return DOT; }
  "=="                    { return EQEQ; }
  "!="                    { return NEQ; }
  "<"                     { return LT; }
  "<="                    { return LTE; }
  ">"                     { return GT; }
  ">="                    { return GTE; }
  " "                     { return TokenType.WHITE_SPACE; }
  "$"                     { return VARIABLE_START; }
  "("                     { return LPAREN; }
  ")"                     { return RPAREN; }
  [^]                     { yypushback(yylength()); yybegin(FRONT_MATTER_BLOCK); }
}

<PATTERN_ACTION_BLOCK> {
  "{"                    { return OPEN_BRACE; }
  "}"                    { return CLOSE_BRACE; }
  "|"                    { return PIPE; }
  ","                    { return COMMA; }
  "("                    { return LPAREN; }
  ")"                    { return RPAREN; }
  {WHITE_SPACE}          { return WHITE_SPACE; }
  {NEWLINE}              { return NEWLINE; }

  // keywords
  "case"                 { return CASE; }
  "default"              { return DEFAULT; }

  {NUMBER}               { return NUMBER; }
  {IDENTIFIER}           { return IDENTIFIER; }
  {QUOTE_STRING}         { return QUOTE_STRING; }
  {PATTERN_EXPR}         { return PATTERN_EXPR; }
  "=>"                   { return ARROW; }
  [^]                    { yypushback(yylength()); yybegin(FRONT_MATTER_VALUE_BLOCK); }
}

<COMMENT_BLOCK> {
  {COMMENTS}              { return comment(); }
  [^]                     { yypushback(yylength()); yybegin(YYINITIAL); return TEXT_SEGMENT; }
}

<QUERY_STATEMENT_BLOCK> {
  "from"                  { return FROM; }
  "where"                 { return WHERE; }
  "select"                { return SELECT; }
  {IDENTIFIER}            { return IDENTIFIER; }

  "{"                     { return OPEN_BRACE; }
  "}"                     { return CLOSE_BRACE; }
  {SPACE}                 { return SPACE; }
  {COMMENT}               { return COMMENT; }
  {BLOCK_COMMENT}         { return BLOCK_COMMENT; }

  ","                     { return COMMA; }
  [^]                     { yypushback(yylength()); yybegin(FRONT_MATTER_BLOCK); }
}

<YYUSED> {
  "@"                     { yybegin(AGENT_BLOCK);    return AGENT_START; }
  "/"                     { yybegin(COMMAND_BLOCK);  return COMMAND_START; }
  "$"                     { yybegin(VARIABLE_BLOCK); return VARIABLE_START; }

  "```" {IDENTIFIER}?     { yybegin(LANG_ID); if (isCodeStart == true) { isCodeStart = false; return CODE_BLOCK_END; } else { isCodeStart = true; }; yypushback(yylength()); }

  {NEWLINE}               { return NEWLINE; }
  {TEXT_SEGMENT}          { return TEXT_SEGMENT; }
  {SHARP}                 { yybegin(EXPR_BLOCK); return SHARP; }
  [^]                     { return TokenType.BAD_CHARACTER; }
}

<COMMAND_BLOCK> {
  {COMMAND_ID}            { return COMMAND_ID; }
  {COLON}                 { yybegin(COMMAND_VALUE_BLOCK); return COLON; }
  [^]                     { yypushback(1); yybegin(YYINITIAL); }
}

<COMMAND_VALUE_BLOCK> {
  {COMMAND_PROP}          { return command_value();  }
  [^]                     { yypushback(1); yybegin(YYINITIAL); }
}

<LINE_BLOCK> {
  {SHARP}                 { return SHARP; }
  {LINE_INFO}             { return LINE_INFO; }
  [^]                     { yypushback(yylength()); yybegin(COMMAND_VALUE_BLOCK); }
}

<AGENT_BLOCK> {
  {AGENT_ID}           { yybegin(YYINITIAL); return AGENT_ID; }
  [^]                  { return TokenType.BAD_CHARACTER; }
}

<VARIABLE_BLOCK> {
  {VARIABLE_ID}        { return VARIABLE_ID; }
  {IDENTIFIER}         { return IDENTIFIER; }
  "{"                  { return OPEN_BRACE; }
  "}"                  { return CLOSE_BRACE; }
  "."                  { return DOT; }
  "("                  { return LPAREN; }
  ")"                  { return RPAREN; }
  [^]                  { yypushback(yylength()); yybegin(YYINITIAL); }
}

<EXPR_BLOCK> {
  {IF}                 { return IF; }
  {ELSE}               { return ELSE; }
  {ELSEIF}             { return ELSEIF; }
  {ENDIF}              { return ENDIF; }
  {END}                { return END; }
  "("                  { return LPAREN; }
  ")"                  { return RPAREN; }
  "<"                  { return LT; }
  "["                  { return LBRACKET; }
  "]"                  { return RBRACKET; }
  ","                  { return COMMA; }
  "!"                  { return NOT; }
  "&&"                 { return ANDAND; }
  "||"                 { return OROR; }
  "."                  { return DOT; }
  "=="                 { return EQEQ; }
  "!="                 { return NEQ; }
  "<"                  { return LT; }
  "<="                 { return LTE; }
  ">"                  { return GT; }
  ">="                 { return GTE; }
  "$"                  { return VARIABLE_START; }
  "{"                  { return OPEN_BRACE; }
  "}"                  { return CLOSE_BRACE; }

  {NUMBER}             { return NUMBER; }
  {IDENTIFIER}         { return IDENTIFIER; }
  {WHITE_SPACE}        { return WHITE_SPACE; }
  [^]                  { yypushback(yylength()); if (isInsideShireTemplate) { yybegin(CODE_BLOCK); } else { yybegin(YYINITIAL); } }
}

<CODE_BLOCK> {
  {CODE_CONTENT}       { if(isCodeStart) { return codeContent(); } else { yybegin(YYINITIAL); yypushback(yylength()); } }
  {NEWLINE}            { return NEWLINE; }
  <<EOF>>              { isCodeStart = false; isInsideShireTemplate = false; yybegin(YYINITIAL); yypushback(yylength()); }
}

<LANG_ID> {
   "```"             { return CODE_BLOCK_START; }
   {LANGUAGE_ID}     { return LANGUAGE_ID;  }
   "$"               { isInsideShireTemplate = true; yybegin(EXPR_BLOCK); return VARIABLE_START; }
   [^]               { yypushback(yylength()); yybegin(CODE_BLOCK); }
}
