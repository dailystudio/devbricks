package com.dailystudio.dataobject.query;

import com.dailystudio.dataobject.Column;

public class QueryToken {
	
	protected StringBuilder mTokenBuilder = null;
	
	public QueryToken() {
		this((String)null);
	}

	public QueryToken(Column column) {
		this((column == null) ? 
				(String)null : column.getName());
	}

	public QueryToken(String token) {
		mTokenBuilder = new StringBuilder();
		
		if (token != null) {
			mTokenBuilder.append(token);
		}
	}
	
	protected QueryToken binaryOperator(String operator, QueryToken token) {
		return binaryOperator(operator, token, true, true, false);
	}

	protected QueryToken binaryOperator(String operator, QueryToken token, 
			boolean withSpace, boolean withBrace, boolean prioritized) {
		if (operator == null || token == null) {
			return this;
		}
		
		String tstr = token.toString();
		if (tstr == null || tstr.length() <= 0) {
			return this;
		}
		
		if (mTokenBuilder == null || mTokenBuilder.length() <= 0) {
			return this;
		}
		
		if (prioritized) {
			mTokenBuilder.insert(0, Expression.OPERATOR_LEFT_BRACE);
		}
		
		if (withBrace) {
			mTokenBuilder.insert(0, Expression.OPERATOR_LEFT_BRACE);
			mTokenBuilder.append(Expression.OPERATOR_RIGHT_BRACE);
		}
		
		if (withSpace) {
			mTokenBuilder.append(' ');
		}
		mTokenBuilder.append(operator);
		if (withSpace) {
			mTokenBuilder.append(' ');
		}
		
		if (withBrace) {
			mTokenBuilder.append(Expression.OPERATOR_LEFT_BRACE);
		}
		mTokenBuilder.append(tstr);
		if (withBrace) {
			mTokenBuilder.append(Expression.OPERATOR_RIGHT_BRACE);
		}
		
		if (prioritized) {
			mTokenBuilder.append(Expression.OPERATOR_RIGHT_BRACE);
		}
		
		return this;
	}
	
	@Override
	public String toString() {
		if (mTokenBuilder == null) {
			return "";
		}
		
		return mTokenBuilder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof QueryToken == false) {
			return false;
		}
		
		QueryToken token = (QueryToken)o;
		
		String mstr = toString();
		String ostr = token.toString();
		if (mstr == null && ostr == null) {
			return true;
		} else if (mstr != null && ostr != null) {
			return mstr.equals(ostr);
		}
		
		
		return false; 
	}
	
}
