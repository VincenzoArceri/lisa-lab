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
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class MyParity implements BaseNonRelationalValueDomain<MyParity> {

	public static final MyParity TOP = new MyParity((byte) 0);
	public static final MyParity BOTTOM  = new MyParity((byte) 1);
	public static final MyParity EVEN = new MyParity((byte) 2); 
	public static final MyParity ODD = new MyParity((byte) 3);

	private final byte b;

	public MyParity() {
		this((byte) 0);
	}

	public MyParity(byte b) {
		this.b = b;
	}


	@Override
	public MyParity evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer i)
			return i % 2 == 0 ? EVEN : ODD;
		return top();
	}

	@Override
	public MyParity evalUnaryExpression(UnaryOperator operator, MyParity arg, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (operator == NumericNegation.INSTANCE)
			return arg;
		return top();
	}

	@Override
	public MyParity evalBinaryExpression(BinaryOperator operator, MyParity left, MyParity right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		if (left.isTop() || right.isTop())
			return top();

		if (operator instanceof AdditionOperator) {
			if (left.equals(right))
				return EVEN;
			else
				return ODD;
		} else if (operator instanceof SubtractionOperator) {
			if (left.equals(right))
				return EVEN;
			else
				return ODD;
		} else if (operator instanceof MultiplicationOperator) {
			if (left.isOdd() && right.isOdd())
				return ODD;
			else
				return EVEN;
		} else if (operator instanceof DivisionOperator) {
			return TOP;
		} else if (operator instanceof ModuloOperator) {
			return TOP;
		}

		return top();
	}

	@Override
	public ValueEnvironment<MyParity> assumeBinaryExpression(ValueEnvironment<MyParity> environment,
			BinaryOperator operator, ValueExpression left, ValueExpression right, ProgramPoint src, ProgramPoint dest,
			SemanticOracle oracle) throws SemanticException {
		if (operator == ComparisonEq.INSTANCE) {
				MyParity evalRight = eval(right, environment, src, oracle);
				MyParity evalLeft = eval(left, environment, src, oracle);
				return evalRight.equals(evalLeft) ? environment : environment.bottom();
		}
		
		return environment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyParity other = (MyParity) obj;
		return b == other.b;
	}

	@Override
	public MyParity lubAux(MyParity other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(MyParity other) throws SemanticException {
		return false;
	}

	@Override
	public MyParity top() {
		return TOP;
	}

	@Override
	public MyParity bottom() {
		return BOTTOM;
	}

	public boolean isEven() {
		return this == EVEN;
	}

	public boolean isOdd() {
		return this == ODD;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isBottom())
			return Lattice.bottomRepresentation();
		else if (isTop())
			return Lattice.topRepresentation();
		else if (isEven())
			return new StringRepresentation("Even");
		else
			return new StringRepresentation("Odd");
	}

}
