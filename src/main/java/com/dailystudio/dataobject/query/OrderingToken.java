package com.dailystudio.dataobject.query;

public class OrderingToken extends QueryToken {
	
	public OrderingToken() {
		this(null);
	}

	public OrderingToken(String token) {
		super(token);
	}
	
	public OrderingToken with (OrderingToken token) {
		return (OrderingToken) binaryOperator(Expression.OPERATOR_WITH, 
				token, false, false, false);
	}
	
}
