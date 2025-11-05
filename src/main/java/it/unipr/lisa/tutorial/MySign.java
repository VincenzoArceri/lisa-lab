package it.unipr.lisa.tutorial;

import java.util.Objects;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.AdditionOperator;
import it.unive.lisa.symbolic.value.operator.DivisionOperator;
import it.unive.lisa.symbolic.value.operator.ModuloOperator;
import it.unive.lisa.symbolic.value.operator.MultiplicationOperator;
import it.unive.lisa.symbolic.value.operator.SubtractionOperator;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class MySign implements BaseNonRelationalValueDomain<MySign> {

	public static final MySign POS = new MySign((byte) 4);
	public static final MySign NEG = new MySign((byte) 3);
	public static final MySign ZERO = new MySign((byte) 2);
	public static final MySign TOP = new MySign((byte) 1);
	public static final MySign BOTTOM = new MySign((byte) 0);;

	private final byte sign;

	public MySign() {
		this((byte) 0);
	}

	public MySign(byte b) {
		this.sign = b;
	}


	@Override
	public MySign evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer i) {
			return i == 0 ? ZERO : i > 0 ? POS : NEG;
		}

		return top();
	}

	@Override
	public MySign evalNullConstant(ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return top();
	}

	private boolean isPositive() {
		return this == POS;
	}

	private boolean isZero() {
		return this == ZERO;
	}

	private boolean isNegative() {
		return this == NEG;
	}

	@Override
	public MySign evalUnaryExpression(UnaryOperator operator, MySign arg, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {

		// -x
		if (operator == NumericNegation.INSTANCE) {
			if (arg.isZero())
				return arg;
			else if (arg.isPositive())
				return NEG;
			else if (arg.isNegative())
				return POS;
			else
				return TOP;
		}

		return TOP;
	}

	public MySign opposite() {
		if (isTop() || isBottom())
			return this;
		return isPositive() ? NEG : isNegative() ? POS : ZERO;
	}


	@Override
	public MySign evalBinaryExpression(BinaryOperator operator, MySign left, MySign right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {

		if (operator instanceof AdditionOperator) {
			if (left.equals(right))
				return left;
			else if (left.isZero())
				return right;
			else if (right.isZero())
				return left;
			else
				return TOP;
		} else if (operator instanceof SubtractionOperator) {
			if (left.isZero())
				return right.opposite();
			else if (right.isZero())
				return left;
			else if (left.equals(right) || left.isTop() || right.isTop())
				return TOP;
			else 
				// - -# +, + -# -
				return left;
		} else if (operator instanceof MultiplicationOperator) {
			if (left.isZero() || right.isZero())
				return ZERO;
			else if (left.isTop() || right.isTop())
				return TOP;
			else if (left.equals(right))
				return POS;
			else 
				return NEG;
		} else if (operator instanceof DivisionOperator) {
			if (right.isZero())
				return BOTTOM;
			else if (left.isZero())
				return ZERO;
			else if (left.isTop() || right.isTop())
				return TOP;
			else if (left.equals(right))
				return POS;
			else 
				return NEG;	
		} else if (operator instanceof ModuloOperator) {
			// TODO
			return right;
		}

		return TOP;
	}

	@Override
	public MySign lubAux(MySign other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(MySign other) throws SemanticException {
		return false;
	}

	@Override
	public MySign top() {
		return TOP;
	}

	@Override
	public MySign bottom() {
		return BOTTOM;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		else if (isTop())
			return Lattice.topRepresentation();

		String repr;
		if (this == ZERO)
			repr = "0";
		else if (this == POS)
			repr = "+";
		else
			repr = "-";

		return new StringRepresentation(repr);
	}

	@Override
	public String toString() {
		return representation().toString();
	}
	
	
	@Override
	public ValueEnvironment<MySign> assumeBinaryExpression(ValueEnvironment<MySign> environment,
			BinaryOperator operator, ValueExpression left, ValueExpression right, ProgramPoint src, ProgramPoint dest,
			SemanticOracle oracle) throws SemanticException {
		
		// TODO
		MySign leftSign = eval(left, environment, src, oracle);
		MySign rightSign = eval(left, environment, src, oracle);
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sign);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MySign other = (MySign) obj;
		return sign == other.sign;
	}
}
