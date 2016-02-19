package com.dailystudio.dataobject.query;

public class Expression {
	
	public static String ORDER_ASCENDING = "ASC";
	public static String ORDER_DESCENDING = "DESC";
	
	public static String OPERATOR_WITH = ", ";

	public static String OPERATOR_LEFT_BRACE = "( ";
	public static String OPERATOR_RIGHT_BRACE = " )";

	public static String OPERATOR_PLUS = "+";
	public static String OPERATOR_MINUS = "-";
	public static String OPERATOR_MULTIPLE = "*";
	public static String OPERATOR_DIVIDE = "/";
	public static String OPERATOR_MODULO = "%";
	
	public static String OPERATOR_AND = "AND";
	public static String OPERATOR_OR = "OR";

	public static String OPERATOR_EQUALS = "==";
	public static String OPERATOR_NOT_EQUALS = "!=";
	public static String OPERATOR_GREAT_THAN = ">";
	public static String OPERATOR_LESS_THAN = "<";
	public static String OPERATOR_GREAT_THAN_AND_EQUALS = ">=";
	public static String OPERATOR_LESS_THAN_AND_EQUALS = "<=";
	public static String OPERATOR_IN = "IN";
	public static String OPERATOR_NOT_IN = "NOT IN";
	public static String OPERATOR_ISNULL = "ISNULL";
	public static String OPERATOR_NOTNULL = "NOTNULL";
	public static String OPERATOR_LIKE = "LIKE";
	
	public static String OPERATOR_EQ = OPERATOR_EQUALS;
	public static String OPERATOR_NEQ = OPERATOR_NOT_EQUALS;
	public static String OPERATOR_GT = OPERATOR_GREAT_THAN;
	public static String OPERATOR_LT = OPERATOR_LESS_THAN;
	public static String OPERATOR_GTE = OPERATOR_GREAT_THAN_AND_EQUALS;
	public static String OPERATOR_LTE = OPERATOR_LESS_THAN_AND_EQUALS;

}
