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

%s SYSTEM_BLOCK

%s CODE_BLOCK
%s COMMENT_BLOCK
%s LINE_BLOCK
%s FRONT_MATTER_BLOCK
%s FRONT_MATTER_VALUE_BLOCK
%s FRONT_MATTER_VAL_OBJECT
%s PATTERN_ACTION_BLOCK
%s PARAMTER_BLOCK

%s LANG_ID

IDENTIFIER               = [a-zA-Z0-9][_\-a-zA-Z0-9]*
FRONTMATTER_KEY          = [a-zA-Z0-9][_\-a-zA-Z0-9]*
DATE                     = [0-9]{4}-[0-9]{2}-[0-9]{2}
STRING                   = [a-zA-Z0-9][_\-a-zA-Z0-9]*
INDENT                   = \s{2}

EscapedChar              = "\\" [^\n]
RegexWord                = [^\r\n\\\"' \t$`()] | {EscapedChar}
REGEX                    = \/{RegexWord}+\/
PATTERN                  = \/{RegexWord}+\/

LPAREN                   = \(
RPAREN                   = \)
PIPE                     = \|

VARIABLE_ID              = [a-zA-Z0-9][_\-a-zA-Z0-9]*
AGENT_ID                 = [a-zA-Z0-9][_\-a-zA-Z0-9]*
COMMAND_ID               = [a-zA-Z0-9][_\-a-zA-Z0-9]*
LANGUAGE_ID              = [a-zA-Z][_\-a-zA-Z0-9 .]*
SYSTEM_ID                = [a-zA-Z][_\-a-zA-Z0-9]*
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

COLON   =:
SHARP   =#
DASH    =-
LBRACKET=\[
RBRACKET=\]

%{
    private boolean isCodeStart = false;
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
        String text = yytext().toString().trim();
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
  {FRONTMATTER_KEY}       { return FRONTMATTER_KEY; }
  ":"                     { yybegin(FRONT_MATTER_VALUE_BLOCK);return COLON; }
  {NEWLINE}               { return NEWLINE; }
  "---"                   { yybegin(YYINITIAL); return FRONTMATTER_END; }
  "  "                    { yybegin(FRONT_MATTER_VAL_OBJECT); return INDENT; }
  [^]                     { yypushback(yylength()); yybegin(YYINITIAL); }
}

<FRONT_MATTER_VAL_OBJECT> {
  {QUOTE_STRING}          { return QUOTE_STRING; }
  {PATTERN}               { yybegin(PATTERN_ACTION_BLOCK); return PATTERN; }
  [^]                     { yypushback(yylength()); yybegin(FRONT_MATTER_BLOCK); }
}

<FRONT_MATTER_VALUE_BLOCK>  {
  {DATE}                  { return DATE; }
  {STRING}                { return STRING; }
  {NUMBER}                { return NUMBER; }
  {BOOLEAN}               { return BOOLEAN; }
  {QUOTE_STRING}          { return QUOTE_STRING; }
  {PATTERN}               { return PATTERN; }
  "["                     { return LBRACKET; }
  "]"                     { return RBRACKET; }
  ","                     { return COMMA; }
  " "                     { return TokenType.WHITE_SPACE; }
  [^]                     { yypushback(yylength()); yybegin(FRONT_MATTER_BLOCK); }
}

<PATTERN_ACTION_BLOCK> {
  "{"                    { return OPEN_BRACE; }
  "}"                    { return CLOSE_BRACE; }
  {WHITE_SPACE}          { return WHITE_SPACE; }
  {IDENTIFIER}           { return IDENTIFIER; }
  {LPAREN}               { yybegin(PARAMTER_BLOCK); return LPAREN; }
  "|"                    { return PIPE; }
  [^]                    { yypushback(yylength()); yybegin(FRONT_MATTER_VALUE_BLOCK); }
}

<PARAMTER_BLOCK> {
  {IDENTIFIER}           { return IDENTIFIER; }
  {QUOTE_STRING}         { return QUOTE_STRING; }
  {REGEX}                { return PATTERN; }
  {NUMBER}               { return NUMBER; }
  {RPAREN}               { yybegin(PATTERN_ACTION_BLOCK); return RPAREN; }
  {WHITE_SPACE}          { return WHITE_SPACE; }
  ","                    { return COMMA; }
  [^]                    { yypushback(yylength()); yybegin(FRONT_MATTER_VAL_OBJECT); }
}

<COMMENT_BLOCK> {
  {COMMENTS}              { return comment(); }
  [^]                     { yypushback(yylength()); yybegin(YYINITIAL); return TEXT_SEGMENT; }
}

<YYUSED> {
  "@"                     { yybegin(AGENT_BLOCK);    return AGENT_START; }
  "/"                     { yybegin(COMMAND_BLOCK);  return COMMAND_START; }
  "$"                     { yybegin(VARIABLE_BLOCK); return VARIABLE_START; }
  "#"                     { yybegin(SYSTEM_BLOCK);   return SYSTEM_START; }

  "```" {IDENTIFIER}?     { yybegin(LANG_ID); if (isCodeStart == true) { isCodeStart = false; return CODE_BLOCK_END; } else { isCodeStart = true; }; yypushback(yylength()); }

  {NEWLINE}               { return NEWLINE; }
  {TEXT_SEGMENT}          { return TEXT_SEGMENT; }
  [^]                     { return TokenType.BAD_CHARACTER; }
}

<COMMAND_BLOCK> {
  {COMMAND_ID}            { return COMMAND_ID; }
  {COLON}                 { yybegin(COMMAND_VALUE_BLOCK); return COLON; }
  " "                     { yypushback(1); yybegin(YYINITIAL); }
  [^]                     { yypushback(1); yybegin(YYINITIAL); }
}

<COMMAND_VALUE_BLOCK> {
  {COMMAND_PROP}          { return command_value();  }
  " "                     { yypushback(1); yybegin(YYINITIAL); }
  [^]                     { yypushback(1); yybegin(YYINITIAL); }
}

<LINE_BLOCK> {
  {LINE_INFO}             { return LINE_INFO; }
  {SHARP}                 { return SHARP; }
  [^]                     { yypushback(yylength()); yybegin(COMMAND_VALUE_BLOCK); }
}

<AGENT_BLOCK> {
  {AGENT_ID}           { yybegin(YYINITIAL); return AGENT_ID; }
  [^]                  { return TokenType.BAD_CHARACTER; }
}

<VARIABLE_BLOCK> {
  {VARIABLE_ID}        { yybegin(YYINITIAL); return VARIABLE_ID; }
  [^]                  { return TokenType.BAD_CHARACTER; }
}

<SYSTEM_BLOCK> {
  {SYSTEM_ID}          { return SYSTEM_ID; }
  {COLON}              { return COLON; }
  {NUMBER}             { return NUMBER; }
  [^]                  { yybegin(YYINITIAL); yypushback(yylength()); }
}

<CODE_BLOCK> {
  {CODE_CONTENT}       { if(isCodeStart) { return codeContent(); } else { yybegin(YYINITIAL); yypushback(yylength()); } }
  {NEWLINE}            { return NEWLINE; }
  <<EOF>>              { isCodeStart = false; yybegin(YYINITIAL); yypushback(yylength()); }
}

<LANG_ID> {
   "```"             { return CODE_BLOCK_START; }
   {LANGUAGE_ID}     { return LANGUAGE_ID;  }
   [^]               { yypushback(1); yybegin(CODE_BLOCK); }
}
