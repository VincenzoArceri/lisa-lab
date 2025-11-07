package it.unipr.lisa.tutorial;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class MyUpperBounds implements BaseNonRelationalValueDomain<MyUpperBounds> {

	private final MyUpperBounds BOTTOM = new MyUpperBounds(new TreeSet<Identifier>());
	
	private final MyUpperBounds TOP = new MyUpperBounds();
	
	private final boolean isTop;
	
	private final Set<Identifier> bounds;
	
	public MyUpperBounds() {
		this.bounds = null;
		this.isTop = true;
	}
	
	public MyUpperBounds(Set<Identifier> bounds) {
		this.bounds = bounds;
		this.isTop = false;
	}
	
	@Override
	public ValueEnvironment<MyUpperBounds> assumeBinaryExpression(ValueEnvironment<MyUpperBounds> environment,
			BinaryOperator operator, ValueExpression left, ValueExpression right, ProgramPoint src, ProgramPoint dest,
			SemanticOracle oracle) throws SemanticException {
		
		if (!(left instanceof Identifier && right instanceof Identifier))
			return environment;

		Identifier x = (Identifier) left;
		Identifier y = (Identifier) right;
		
		// x == y
		if (operator instanceof ComparisonEq) {
			MyUpperBounds ub = environment.getState(x).glb(environment.getState(y));
			return environment.putState(x, ub).putState(y, ub);
		}
		// TODO: x < y, x <= y, x > y, x >= y
		return environment;
	}
	
	@Override
	public MyUpperBounds glbAux(MyUpperBounds other) throws SemanticException {
		Set<Identifier> b = new TreeSet<Identifier>(this.bounds);
		b.addAll(other.bounds);
		return new MyUpperBounds(b);
	}
	
	@Override
	public MyUpperBounds lubAux(MyUpperBounds other) throws SemanticException {
		Set<Identifier> b = new TreeSet<Identifier>(this.bounds);
		b.retainAll(other.bounds);
		if (b.isEmpty())
			return TOP;
		return new MyUpperBounds(b);
	}

	@Override
	public boolean lessOrEqualAux(MyUpperBounds other) throws SemanticException {
		return bounds.containsAll(other.bounds);
	}

	@Override
	public MyUpperBounds top() {
		return TOP;
	}

	@Override
	public MyUpperBounds bottom() {
		return BOTTOM;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();
		else if (isBottom())
			return Lattice.bottomRepresentation();
		else
			return new StringRepresentation(bounds.toString());
	}

	@Override
	public int hashCode() {
		return Objects.hash(BOTTOM, TOP, bounds, isTop);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyUpperBounds other = (MyUpperBounds) obj;
		return Objects.equals(BOTTOM, other.BOTTOM) && Objects.equals(TOP, other.TOP)
				&& Objects.equals(bounds, other.bounds) && isTop == other.isTop;
	}
}
