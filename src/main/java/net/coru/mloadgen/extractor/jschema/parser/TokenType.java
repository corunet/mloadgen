package net.coru.mloadgen.extractor.jschema.parser;

public enum TokenType {
	  UNKNOWN(' '),
		LCURLY('{'),
		RCURLY('}'),
		LSQUARE('['),
		RSQUARE(']'),
		COMMA(','),
		COLON(':'),
		EOF('\0'),
	  STRING('s'),
	  NUMBER('n'),
	  ERROR('e');

	private final char value;

	TokenType(char value) {
		this.value = value;
	}

	public static TokenType valueOf(char value) {
		switch (value) {
			case '{': return LCURLY;
			case '}': return RCURLY;
			case '[': return LSQUARE;
			case ']': return RSQUARE;
			case ',': return COMMA;
			case ':': return COLON;
			default : return UNKNOWN;
		}
	}
}