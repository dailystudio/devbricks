package com.dailystudio.dataobject.query;

import com.dailystudio.dataobject.Column;

public class ExpressionToken extends QueryToken {
	
	public ExpressionToken() {
		this((String)null);
	}

	public ExpressionToken(Column column) {
		super(column);
	}
	
	public ExpressionToken(int intVal) {
		super(String.valueOf(intVal));
	}
	
	public ExpressionToken(double dbVal) {
		super(String.valueOf(dbVal));
	}
	
	public ExpressionToken(long longVal) {
		super(String.valueOf(longVal));
	}
	
	public ExpressionToken(String token) {
		super(token);
	}
	
	public ExpressionToken plus(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_PLUS, 
				token, true, true, true);
	}
	
	public ExpressionToken minus(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_MINUS, 
				token, true, true, true);
	}
	
	public ExpressionToken multiple(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_MULTIPLE,
				token, true, true, true);
	}
	
	public ExpressionToken divide(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_DIVIDE, 
				token, true, true, true);
	}
	
	public ExpressionToken modulo(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_MODULO, 
				token, true, true, true);
	}
	
	public ExpressionToken and(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_AND, token);
	}
	
	public ExpressionToken or(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_OR, token);
	}
	
	public ExpressionToken eq(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_EQ, token);
	}
	
	public ExpressionToken neq(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_NEQ, token);
	}
	
	public ExpressionToken gt(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_GT, token);
	}
	
	public ExpressionToken gte(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_GTE, token);
	}
	
	public ExpressionToken lt(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_LT, token);
	}
	
	public ExpressionToken lte(ExpressionToken token) {
		return (ExpressionToken) binaryOperator(Expression.OPERATOR_LTE, token);
	}
	
	public ExpressionToken in(ExpressionToken lower, ExpressionToken upper) {
		return this.gte(lower).and(this.lte(upper));
	}
	
	public ExpressionToken outOf(ExpressionToken lower, ExpressionToken upper) {
		return this.lt(lower).or(this.gt(upper));
	}

}
