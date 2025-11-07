package it.unipr.lisa.tutorial;

import java.util.Objects;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
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

public class MyIntegerConstantPropagation implements BaseNonRelationalValueDomain<MyIntegerConstantPropagation> {

	private static final MyIntegerConstantPropagation TOP = new MyIntegerConstantPropagation();
	
	private static final MyIntegerConstantPropagation BOTTOM = new MyIntegerConstantPropagation(null, false, true);
	
	private final Integer value;
	
	private final boolean isTop, isBottom;
	
	
	public MyIntegerConstantPropagation() {
		this(null, true, false);
	}
	
	public MyIntegerConstantPropagation(Integer value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isBottom = isBottom;
		this.isTop = isTop;
	}
	
	@Override
	public MyIntegerConstantPropagation lubAux(MyIntegerConstantPropagation other) throws SemanticException {
		return TOP;
	}

	@Override
	public boolean lessOrEqualAux(MyIntegerConstantPropagation other) throws SemanticException {
		return false;
	}

	@Override
	public MyIntegerConstantPropagation top() {
		return TOP;
	}
	
	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isBottom() {
		return isBottom;
	}
	
	@Override
	public MyIntegerConstantPropagation bottom() {
		return BOTTOM;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();
		else if (isBottom())
			return Lattice.bottomRepresentation();
		else
			return new StringRepresentation(value.toString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(isBottom, isTop, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyIntegerConstantPropagation other = (MyIntegerConstantPropagation) obj;
		return isBottom == other.isBottom && isTop == other.isTop && Objects.equals(value, other.value);
	}
	
	
	@Override
	public MyIntegerConstantPropagation evalNonNullConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof Integer i) 
			return new MyIntegerConstantPropagation(i, false, false);
		return top();
	}
	
	@Override
	public MyIntegerConstantPropagation evalUnaryExpression(UnaryOperator operator, MyIntegerConstantPropagation arg,
			ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (arg.isTop())
			return arg;
	
		if (operator == NumericNegation.INSTANCE)
			return new MyIntegerConstantPropagation(-value, false, false);
		return top();
	}
	
	@Override
	public MyIntegerConstantPropagation evalBinaryExpression(BinaryOperator operator, MyIntegerConstantPropagation left,
			MyIntegerConstantPropagation right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (operator instanceof AdditionOperator) {
			return left.isTop() || right.isTop() ? TOP : new MyIntegerConstantPropagation(left.value + right.value, false, false);
		} else if (operator instanceof SubtractionOperator) {
			return left.isTop() || right.isTop() ? TOP : new MyIntegerConstantPropagation(left.value - right.value, false, false);
		} else if (operator instanceof MultiplicationOperator) {
			if (left.isTop())
				return right.isTop() ? top() : right.value == 0 ? right : top();
			if (right.isTop())
				return left.isTop() ? top() : left.value == 0 ? left : top();
			return new MyIntegerConstantPropagation(left.value * right.value, false, false);
		} else if (operator instanceof DivisionOperator) {
			if (left.isTop())
				return right.isTop() ? top() : right.value == 0 ? bottom() : top();
			if (right.isTop())
				return left.isTop() ? top() : left.value == 0 ? left : top();
			if (right.value == 0)
				return bottom();
			return new MyIntegerConstantPropagation(left.value / right.value, false, false);
		} else if (operator instanceof ModuloOperator) {
			// TODO:
		}
		
		return top();
	}
	
	@Override
	public ValueEnvironment<MyIntegerConstantPropagation> assumeBinaryExpression(
			ValueEnvironment<MyIntegerConstantPropagation> environment, BinaryOperator operator, ValueExpression left,
			ValueExpression right, ProgramPoint src, ProgramPoint dest, SemanticOracle oracle)
			throws SemanticException {
		// v == x
		if (operator == ComparisonEq.INSTANCE) {
			if (left instanceof Identifier) { // x == exp
				MyIntegerConstantPropagation eval = eval(right, environment, src, oracle);
				if (eval.isBottom())
					return environment.bottom();
				if (eval.isTop())
					return environment;
				
				MyIntegerConstantPropagation leftEval = environment.getState((Identifier) left);
				if (eval.glb(leftEval).isBottom())
					return environment.bottom();
				return environment.putState((Identifier) left, eval.glb(leftEval));
			} else if (right instanceof Identifier) {
				// TODO: dual
			}
		}
		
		return environment;
	}
}
