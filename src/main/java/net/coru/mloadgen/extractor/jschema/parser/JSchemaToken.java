package net.coru.mloadgen.extractor.jschema.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JSchemaToken
{
	public enum TokenType
	{
		LCURLY,
		RCURLY,
		LSQUARE,
		RSQUARE,
		COMMA,
		COLON,
		STRING,
		NUMBER,
		TRUE,
		FALSE,
		NULL,
		ERROR,
		EOF
	}

	private TokenType _tokenType;
	private String _value;
	private int _lineNumber;
	private int _column;
	private int _offset;
	private double _num;
	static final Map<String, TokenType> constants = initializeConstants();
	private Map test;

	public JSchemaToken( TokenType tokenType, String value, int lineNumber, int column, int offset, double num )
	{
		_tokenType = tokenType;
		_value = value;
		_lineNumber = lineNumber;
		_column = column;
		_offset = offset;
		_num = num;
	}

	private static Map<String, TokenType> initializeConstants()
	{
		HashMap<String, TokenType> map = new HashMap();
		map.put( "true", TokenType.TRUE );
		map.put( "false", TokenType.FALSE );
		map.put( "null", TokenType.NULL );
		return Collections.unmodifiableMap( map );
	}

	public String getTokenValue()
	{
		return _value;
	}

	public double getTokenNumberValue()
	{
		return _num;
	}

	public TokenType getTokenType()
	{
		return _tokenType;
	}

	public int getLineNumber()
	{
		return _lineNumber;
	}

	public int getColumn()
	{
		return _column;
	}

	public int getOffset()
	{
		return _offset;
	}

	@Override
	public String toString()
	{
		return "[" + _tokenType + "]" + _value + ":" + _offset;
	}
}