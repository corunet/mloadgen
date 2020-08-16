package net.coru.mloadgen.extractor.jschema.parser;

import static java.util.Arrays.asList;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.*;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.COLON;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.COMMA;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.LCURLY;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.LSQUARE;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.RCURLY;
import static net.coru.mloadgen.extractor.jschema.parser.TokenType.RSQUARE;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JSchemaTokenizer {

	private static final Set<Character> numberSet = new HashSet<>(asList('-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'));
	private static final EnumSet<TokenType> specialCharSet = EnumSet.copyOf(asList(LCURLY, RCURLY, LSQUARE, RSQUARE, COMMA, COLON));
	private static final Set<Character> constantSet = new HashSet<>(asList('t', 'f', 'n'));

	private String string;
	private int offset;
	private int line;
	private int column;
	private char ch;
	private int errorCount;

	public JSchemaTokenizer(String string) {
		this.string = string;
		offset = 0;
		line = 1;
		column = 0;
		nextChar();
	}

	//========================================================================================
	//  Main tokenization loop
	//========================================================================================

	public JSchemaToken next() {
		JSchemaToken schemaToken;
		eatWhiteSpace(); // eat leading whitespace
		if (ch == '"') {
			schemaToken = consumeString();
		} else if (numberSet.contains(ch)) {
			schemaToken = consumeNumber();
		} else if (specialCharSet.contains(valueOf(ch))) {
			schemaToken = newToken(valueOf(ch), String.valueOf(ch));
			nextChar();
		} else if (constantSet.contains(ch)) {
			schemaToken = consumeConstant();
		} else if ('\0' == ch) {
			schemaToken = new JSchemaToken(EOF, "EOF", line, column, offset, 0.0);
			string = "";
		} else {
				// unrecognized JSchemaToken
				schemaToken = errorToken( String.valueOf(ch) );
				nextChar();
		}
		return schemaToken;
	}

	//========================================================================================
	//  Tokenization type methods
	//========================================================================================

	private JSchemaToken consumeString() {
		StringBuilder sb = new StringBuilder();
		JSchemaToken schemaToken;
		nextChar();
		while(moreChars() && ch != '"') {
			if(ch == '\\') {
				nextChar();
				switch(ch) {
					case '"':
					case '\\':
					case '/':
						sb.append(ch);
						nextChar();
						break;
					case 'b':
						sb.append('\b');
						nextChar();
						break;
					case 'f':
						sb.append('\f');
						nextChar();
						break;
					case 'n':
						sb.append('\n');
						nextChar();
						break;
					case 'r':
						sb.append('\r');
						nextChar();
						break;
					case 't':
						sb.append('\t');
						nextChar();
						break;
					case 'u':
						nextChar();
						int u = 0;
						for(int i = 0; i < 4; i++) {
							if(isHexDigit(ch)) {
								u = u * 16 + ch - '0';
								if(ch >= 'A') { // handle hex numbers: 'A' = 65, '0' = 48. 'A'-'0' = 17, 17 - 7 = 10
									u = u - 7;
								}
							} else {
								nextChar();
								return errorToken( sb.toString() );
							}
							nextChar();
						}
						sb.append((char) u);
						break;
					default:
						return errorToken( sb.toString() );
				}
			} else {
				sb.append(ch);
				nextChar();
			}
		}
		if(ch == '"') {
			schemaToken = newToken(STRING, sb.toString());
		} else {
			schemaToken = errorToken( sb.toString() );
		}
		nextChar();
		return schemaToken;
	}

	//needs to work for integers and decimals
	private JSchemaToken consumeNumber() {
		StringBuilder sb = new StringBuilder();
		JSchemaToken schemaToken;
		int num = 0;
		int frac = 0;
		int numFracDigit = 0;
		int exp = 0;
		boolean neg = false;
		if(ch == '-') {
			sb.append(ch);
			nextChar();
			neg = true;
		}
		if(ch != '0') {
			num = consumeDigits(sb);
			if(num == -1) {
				return errorToken( sb.toString() );
			}
		} else {
			sb.append(ch);
			nextChar();
		}
		if(ch == '.') {
			sb.append(ch);
			nextChar();
			numFracDigit = sb.length();
			frac = consumeDigits(sb);
			if(frac == -1) {
				return errorToken( sb.toString() );
			}
			numFracDigit = sb.length() - numFracDigit;
		}
		if(ch == 'E' || ch == 'e') {
			sb.append(ch);
			nextChar();
			boolean negExp = false;
			if(ch == '-') {
				sb.append(ch);
				nextChar();
				negExp = true;
			} else if(ch == '+') {
				sb.append(ch);
				nextChar();
			}
			exp = consumeDigits(sb);
			if(negExp) {
				exp = -exp;
			}
		}
		double doubleValue = num;
		if(frac != 0) {
			doubleValue += (frac * Math.pow(10, -numFracDigit));
		}
		if(exp != 0) {
			doubleValue = doubleValue * Math.pow(10, exp);
		}
		if(neg) {
			doubleValue = -doubleValue;
		}
		schemaToken = newNumberToken(NUMBER, sb.toString(), doubleValue);
		return schemaToken;
	}

	private int consumeDigits(StringBuilder sb) {
		int num = 0;
		if(isDigit(ch)) {
			while(moreChars() && isDigit(ch)) {
				sb.append(ch);
				num = num * 10 + ch - '0';
				nextChar();
			}
		} else {
			num = -1;
		}
		return num;
	}

	private boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	private boolean isHexDigit(char ch) {
		return ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f';
	}

	private JSchemaToken consumeConstant() {
		StringBuilder sb = new StringBuilder();
		JSchemaToken schemaToken;
		do {
			sb.append(ch);
			nextChar();
		} while(moreChars() && (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z'));
		String str = sb.toString();
		TokenType type = JSchemaToken.constants.get(str);
		if(type == null) {
			schemaToken = errorToken( str );
		} else {
			schemaToken = newToken(type, str);
		}
		return schemaToken;
	}

	private JSchemaToken errorToken( String str )
	{
		errorCount++;
		return newToken( ERROR, str);
	}

	public int getErrorCount() {
		return errorCount;
	}

	//========================================================================================
	//  Utility methods
	//========================================================================================

	private JSchemaToken newToken(TokenType type, String tokenValue) {
		return new JSchemaToken(type, tokenValue, line, column, offset + 1, 0);
	}

	private JSchemaToken newNumberToken(TokenType type, String tokenValue, double num) {
		return new JSchemaToken(type, tokenValue, line, column, offset + 1, num);
	}

	private void eatWhiteSpace() {
		//while there exists more characters and the current character is white space
		while(moreChars() && (ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ')) {
			nextChar();
		}
	}

	private void nextChar() {
		if(offset < string.length()) {
			ch = string.charAt(offset);
			if(ch == '\n') // if we are at a newline character, bump the line number and reset the column
			{
				line++;
				column = 0;
			}
			offset = offset + 1; // bump offset
			column = column + 1; // bump column
		} else {
			ch = '\0';
		}
	}

	private boolean moreChars() {
		return ch != '\0';
	}

	public List<JSchemaToken> tokenize() {
		ArrayList<JSchemaToken> list = new ArrayList<>();
		JSchemaToken JSchemaToken = next();
		while(JSchemaToken.getTokenType() != EOF) {
			list.add(JSchemaToken);
			JSchemaToken = next();
		}
		return list;
	}
}
