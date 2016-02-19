package com.dailystudio.dataobject;

import com.dailystudio.dataobject.query.Expression;
import com.dailystudio.dataobject.query.ExpressionToken;
import com.dailystudio.dataobject.query.OrderingToken;
import com.dailystudio.development.Logger;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;

public abstract class Column {
	
	public static final int VERSION_LATEST = 0x0;
	public static final int VERSION_1 = 0x1;

	private static final String WHITE_SPACE = " ";
	private static final String CONSTRAINT_NOT_NULL = " NOT NULL";
	private static final String CONSTRAINT_PRIMARY_KEY = " PRIMARY KEY";
	
	private String mTypeName;
	private String mColName;
	
	private boolean mAllowNull;
	private boolean mIsPrimary;
	
	private int mVersion;
	
	public Column(String colName, String typeName) {
		this(colName, typeName, true);
	}
	
	public Column(String colName, String typeName, boolean allowNull) {
		this(colName, typeName, allowNull, false);
	}
	
	public Column(String colName, String typeName, boolean allowNull, boolean isPrimary) {
		this(colName, typeName, allowNull, isPrimary, VERSION_1);
	}
	
	public Column(String colName, String typeName, int version) {
		this(colName, typeName, true, version);
	}
	
	public Column(String colName, String typeName, boolean allowNull, int version) {
		this(colName, typeName, allowNull, false, version);
	}
	
	public Column(String colName, String typeName, boolean allowNull, boolean isPrimary, int version) {
		mTypeName = typeName;
		mColName = colName;
		
		mAllowNull = allowNull;
		mIsPrimary = isPrimary;
		
		mVersion = version; 
	}
	
	public String getName() {
		return mColName;
	}
	
	public String getType() {
		return mTypeName;
	}
	
	public boolean isAllowNull() {
		return mAllowNull;
	}
	
	public boolean isPrimary() {
		return mIsPrimary;
	}
	
	public int getVerion() {
		return  mVersion;
	}
	
	public boolean isValid() {
		return (mColName != null && mTypeName != null);
	}

	@Override
	public String toString() {
		if (mColName == null || mTypeName == null) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(mColName).append(WHITE_SPACE).append(mTypeName);
		if (mAllowNull == false) {
			builder.append(CONSTRAINT_NOT_NULL);
		}
		
		if (mIsPrimary) {
			builder.append(CONSTRAINT_PRIMARY_KEY);
		}
		
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Column == false) {
			return false;
		}
		
		if (mColName == null || mTypeName == null) {
			return false;
		}
		
		Column column = (Column)o;
		if (column.mColName == null || column.mTypeName == null) {
			return false;
		}

		return (mColName.equals(column.mColName)
				&& mTypeName.equals(column.mTypeName)
				&& (mAllowNull == column.mAllowNull)
				&& (mIsPrimary == column.mIsPrimary));
	}

	abstract boolean matchColumnType(Object value);
	
	abstract void setValue(ContentValues container, Object value);
	abstract Object getValue(ContentValues container);
	
	abstract protected void attachValueTo(Intent intent, ContentValues container);
	abstract protected void parseValueFrom(Cursor cursor, ContentValues container);
	
	abstract public String convertValueToString(Object value);
	
	/*
	 * Expression Stuff
	 */
	private static final ExpressionToken sEmtpyExpToken = new ExpressionToken();
	private static final OrderingToken sEmtpyOrderToken = new OrderingToken();

	public ExpressionToken plus(Column column) {
		return new ExpressionToken(this).plus(new ExpressionToken(column));
	}

	public ExpressionToken minus(Column column) {
		return new ExpressionToken(this).minus(new ExpressionToken(column));
	}

	public ExpressionToken multiple(Column column) {
		return new ExpressionToken(this).multiple(new ExpressionToken(column));
	}

	public ExpressionToken divide(Column column) {
		return new ExpressionToken(this).divide(new ExpressionToken(column));
	}

	public ExpressionToken modulo(Column column) {
		return new ExpressionToken(this).modulo(new ExpressionToken(column));
	}

	public ExpressionToken eq(Object value) {
		return binaryOperator(
				Expression.OPERATOR_EQ, value);
	}
	
	public ExpressionToken neq(Object value) {
		return binaryOperator(
				Expression.OPERATOR_NEQ, value);
	}
	
	public ExpressionToken gt(Object value) {
		return binaryOperator(
				Expression.OPERATOR_GT, value);
	}
	
	public ExpressionToken gte(Object value) {
		return binaryOperator(
				Expression.OPERATOR_GTE, value);
	}
	
	public ExpressionToken lt(Object value) {
		return binaryOperator(
				Expression.OPERATOR_LT, value);
	}
	
	public ExpressionToken lte(Object value) {
		return binaryOperator(
				Expression.OPERATOR_LTE, value);
	}
	
	public ExpressionToken in(Object lower, Object upper) {
		return this.gte(lower).and(this.lte(upper));
	}
	
	public ExpressionToken inValues(Object[] values) {
		return binaryOperator(
				Expression.OPERATOR_IN, values);
	}

	public ExpressionToken outOf(Object lower, Object upper) {
		return this.lt(lower).or(this.gt(upper));
	}
	
	public ExpressionToken outOfValues(Object[] values) {
		return binaryOperator(
				Expression.OPERATOR_NOT_IN, values);
	}
	
	public ExpressionToken isNull() {
		String token = String.format("%s %s",
				mColName, Expression.OPERATOR_ISNULL);
		if (token == null) {
			return sEmtpyExpToken;
		}
		
		return new ExpressionToken(token);
	}

	public ExpressionToken notNull() {
		String token = String.format("%s %s",
				mColName, Expression.OPERATOR_NOTNULL);
		if (token == null) {
			return sEmtpyExpToken;
		}
		
		return new ExpressionToken(token);
	}

	public OrderingToken groupBy() {
		return new OrderingToken(mColName);
	}
	
	public OrderingToken orderByAscending() {
		return orderBy(Expression.ORDER_ASCENDING);
	}
	
	public OrderingToken orderByDescending() {
		return orderBy(Expression.ORDER_DESCENDING);
	}
	
	private OrderingToken orderBy(String order) {
		if (order == null) {
			order = Expression.ORDER_ASCENDING;
		}
		
		String token = String.format("%s %s",
				mColName, order);
		if (token == null) {
			return sEmtpyOrderToken;
		}
		
		return new OrderingToken(token);
	}
	
	protected ExpressionToken binaryOperator(String operator, Object value) {
		if (operator == null) {
			return sEmtpyExpToken;
		}
		
		if (value == null) {
			return sEmtpyExpToken;
		}
		
		if (mColName == null || mTypeName == null) {
			return sEmtpyExpToken;
		}
		
		if (matchColumnType(value) == false) {
			Logger.warnning("Illegal operation(%s) on column(type: %s) with value(%s)",
					operator,
					mTypeName,
					value.getClass().getSimpleName());
			return sEmtpyExpToken;
		}
		
		String valStr = convertValueToString(value);
		if (valStr == null) {
			return sEmtpyExpToken;
		}
		
		/*
		 * DATE:	2011/10/25
		 * AUTHOR:	Nan YE
		 * CONTENT:	remove \' around value, this will be handle
		 * 			by Column.convertValueToString(). '\ around
		 * 			numeric value will cause mismatching during
		 * 			execute "select" with some runtime defined 
		 * 			column (e.g. (longa - longb) > '0') 
		 */
		String token = String.format("%s %s %s",
				mColName, operator, valStr);
		if (token == null) {
			return sEmtpyExpToken;
		}
		
		return new ExpressionToken(token);
	}
	
	protected ExpressionToken binaryOperator(String operator, Object[] values) {
		if (operator == null) {
			return sEmtpyExpToken;
		}
		
		if (values == null || values.length <= 0) {
			return sEmtpyExpToken;
		}
		
		if (mColName == null || mTypeName == null) {
			return sEmtpyExpToken;
		}
		
		final int N = values.length;
		
		Object value = null;
		String valStr = null;
		
		StringBuilder builder = new StringBuilder(Expression.OPERATOR_LEFT_BRACE);
		for (int i = 0; i < N; i++) {
			value = values[i];
			
			if (matchColumnType(value) == false) {
				Logger.warnning("Illegal operation(%s) on column(type: %s) with value(%s)",
						operator,
						mTypeName,
						value.getClass().getSimpleName());
				return sEmtpyExpToken;
			}
		
			valStr = convertValueToString(value);
			if (valStr == null) {
				continue;
			}

			builder.append(valStr);
			builder.append((i == (N - 1) ? Expression.OPERATOR_RIGHT_BRACE 
					: Expression.OPERATOR_WITH));
		}
		
		/*
		 * DATE:	2011/10/25
		 * AUTHOR:	Nan YE
		 * CONTENT:	remove \' around value, this will be handle
		 * 			by Column.convertValueToString(). '\ around
		 * 			numeric value will cause mismatching during
		 * 			execute "select" with some runtime defined 
		 * 			column (e.g. (longa - longb) > '0') 
		 */
		String token = String.format("%s %s %s",
				mColName, operator, builder.toString());
		if (token == null) {
			return sEmtpyExpToken;
		}
		
		return new ExpressionToken(token);
	}

}
