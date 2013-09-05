// $ANTLR 2.7.7 (20060906): "groovy.g" -> "GroovyRecognizer.java"$

package org.codehaus.groovy.antlr.parser;
import org.codehaus.groovy.antlr.*;
import java.util.*;
import java.io.InputStream;
import java.io.Reader;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.CommonToken;
import org.codehaus.groovy.GroovyBugError;
import antlr.TokenStreamRecognitionException;

public interface GroovyTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int BLOCK = 4;
	int MODIFIERS = 5;
	int OBJBLOCK = 6;
	int SLIST = 7;
	int METHOD_DEF = 8;
	int VARIABLE_DEF = 9;
	int INSTANCE_INIT = 10;
	int STATIC_INIT = 11;
	int TYPE = 12;
	int CLASS_DEF = 13;
	int INTERFACE_DEF = 14;
	int PACKAGE_DEF = 15;
	int ARRAY_DECLARATOR = 16;
	int EXTENDS_CLAUSE = 17;
	int IMPLEMENTS_CLAUSE = 18;
	int PARAMETERS = 19;
	int PARAMETER_DEF = 20;
	int LABELED_STAT = 21;
	int TYPECAST = 22;
	int INDEX_OP = 23;
	int POST_INC = 24;
	int POST_DEC = 25;
	int METHOD_CALL = 26;
	int EXPR = 27;
	int IMPORT = 28;
	int UNARY_MINUS = 29;
	int UNARY_PLUS = 30;
	int CASE_GROUP = 31;
	int ELIST = 32;
	int FOR_INIT = 33;
	int FOR_CONDITION = 34;
	int FOR_ITERATOR = 35;
	int EMPTY_STAT = 36;
	int FINAL = 37;
	int ABSTRACT = 38;
	int UNUSED_GOTO = 39;
	int UNUSED_CONST = 40;
	int UNUSED_DO = 41;
	int STRICTFP = 42;
	int SUPER_CTOR_CALL = 43;
	int CTOR_CALL = 44;
	int CTOR_IDENT = 45;
	int VARIABLE_PARAMETER_DEF = 46;
	int STRING_CONSTRUCTOR = 47;
	int STRING_CTOR_MIDDLE = 48;
	int CLOSABLE_BLOCK = 49;
	int IMPLICIT_PARAMETERS = 50;
	int SELECT_SLOT = 51;
	int DYNAMIC_MEMBER = 52;
	int LABELED_ARG = 53;
	int SPREAD_ARG = 54;
	int SPREAD_MAP_ARG = 55;
	int LIST_CONSTRUCTOR = 56;
	int MAP_CONSTRUCTOR = 57;
	int FOR_IN_ITERABLE = 58;
	int STATIC_IMPORT = 59;
	int ENUM_DEF = 60;
	int ENUM_CONSTANT_DEF = 61;
	int FOR_EACH_CLAUSE = 62;
	int ANNOTATION_DEF = 63;
	int ANNOTATIONS = 64;
	int ANNOTATION = 65;
	int ANNOTATION_MEMBER_VALUE_PAIR = 66;
	int ANNOTATION_FIELD_DEF = 67;
	int ANNOTATION_ARRAY_INIT = 68;
	int TYPE_ARGUMENTS = 69;
	int TYPE_ARGUMENT = 70;
	int TYPE_PARAMETERS = 71;
	int TYPE_PARAMETER = 72;
	int WILDCARD_TYPE = 73;
	int TYPE_UPPER_BOUNDS = 74;
	int TYPE_LOWER_BOUNDS = 75;
	int CLOSURE_LIST = 76;
	int SH_COMMENT = 77;
	int LITERAL_package = 78;
	int LITERAL_import = 79;
	int LITERAL_static = 80;
	int LITERAL_def = 81;
	int LBRACK = 82;
	int RBRACK = 83;
	int IDENT = 84;
	int STRING_LITERAL = 85;
	int LT = 86;
	int DOT = 87;
	int LPAREN = 88;
	int LITERAL_class = 89;
	int LITERAL_interface = 90;
	int LITERAL_enum = 91;
	int AT = 92;
	int QUESTION = 93;
	int LITERAL_extends = 94;
	int LITERAL_super = 95;
	int GT = 96;
	int COMMA = 97;
	int SR = 98;
	int BSR = 99;
	int LITERAL_void = 100;
	int LITERAL_boolean = 101;
	int LITERAL_byte = 102;
	int LITERAL_char = 103;
	int LITERAL_short = 104;
	int LITERAL_int = 105;
	int LITERAL_float = 106;
	int LITERAL_long = 107;
	int LITERAL_double = 108;
	int STAR = 109;
	int LITERAL_as = 110;
	int LITERAL_private = 111;
	int LITERAL_public = 112;
	int LITERAL_protected = 113;
	int LITERAL_transient = 114;
	int LITERAL_native = 115;
	int LITERAL_threadsafe = 116;
	int LITERAL_synchronized = 117;
	int LITERAL_volatile = 118;
	int RPAREN = 119;
	int ASSIGN = 120;
	int BAND = 121;
	int LCURLY = 122;
	int RCURLY = 123;
	int SEMI = 124;
	int LITERAL_default = 125;
	int LITERAL_throws = 126;
	int LITERAL_implements = 127;
	int LITERAL_this = 128;
	int TRIPLE_DOT = 129;
	int CLOSABLE_BLOCK_OP = 130;
	int COLON = 131;
	int LITERAL_if = 132;
	int LITERAL_else = 133;
	int LITERAL_while = 134;
	int LITERAL_switch = 135;
	int LITERAL_for = 136;
	int LITERAL_in = 137;
	int LITERAL_return = 138;
	int LITERAL_break = 139;
	int LITERAL_continue = 140;
	int LITERAL_throw = 141;
	int LITERAL_assert = 142;
	int PLUS = 143;
	int MINUS = 144;
	int LITERAL_case = 145;
	int LITERAL_try = 146;
	int LITERAL_finally = 147;
	int LITERAL_catch = 148;
	int SPREAD_DOT = 149;
	int OPTIONAL_DOT = 150;
	int MEMBER_POINTER = 151;
	int LITERAL_false = 152;
	int LITERAL_instanceof = 153;
	int LITERAL_new = 154;
	int LITERAL_null = 155;
	int LITERAL_true = 156;
	int PLUS_ASSIGN = 157;
	int MINUS_ASSIGN = 158;
	int STAR_ASSIGN = 159;
	int DIV_ASSIGN = 160;
	int MOD_ASSIGN = 161;
	int SR_ASSIGN = 162;
	int BSR_ASSIGN = 163;
	int SL_ASSIGN = 164;
	int BAND_ASSIGN = 165;
	int BXOR_ASSIGN = 166;
	int BOR_ASSIGN = 167;
	int STAR_STAR_ASSIGN = 168;
	int ELVIS_OPERATOR = 169;
	int LOR = 170;
	int LAND = 171;
	int BOR = 172;
	int BXOR = 173;
	int REGEX_FIND = 174;
	int REGEX_MATCH = 175;
	int NOT_EQUAL = 176;
	int EQUAL = 177;
	int IDENTICAL = 178;
	int NOT_IDENTICAL = 179;
	int COMPARE_TO = 180;
	int LE = 181;
	int GE = 182;
	int SL = 183;
	int RANGE_INCLUSIVE = 184;
	int RANGE_EXCLUSIVE = 185;
	int INC = 186;
	int DIV = 187;
	int MOD = 188;
	int DEC = 189;
	int STAR_STAR = 190;
	int BNOT = 191;
	int LNOT = 192;
	int STRING_CTOR_START = 193;
	int STRING_CTOR_END = 194;
	int NUM_INT = 195;
	int NUM_FLOAT = 196;
	int NUM_LONG = 197;
	int NUM_DOUBLE = 198;
	int NUM_BIG_INT = 199;
	int NUM_BIG_DECIMAL = 200;
	int NLS = 201;
	int DOLLAR = 202;
	int WS = 203;
	int ONE_NL = 204;
	int SL_COMMENT = 205;
	int ML_COMMENT = 206;
	int STRING_CH = 207;
	int REGEXP_LITERAL = 208;
	int DOLLAR_REGEXP_LITERAL = 209;
	int REGEXP_CTOR_END = 210;
	int DOLLAR_REGEXP_CTOR_END = 211;
	int ESCAPED_SLASH = 212;
	int ESCAPED_DOLLAR = 213;
	int REGEXP_SYMBOL = 214;
	int DOLLAR_REGEXP_SYMBOL = 215;
	int ESC = 216;
	int STRING_NL = 217;
	int HEX_DIGIT = 218;
	int VOCAB = 219;
	int LETTER = 220;
	int DIGIT = 221;
	int EXPONENT = 222;
	int FLOAT_SUFFIX = 223;
	int BIG_SUFFIX = 224;
}
