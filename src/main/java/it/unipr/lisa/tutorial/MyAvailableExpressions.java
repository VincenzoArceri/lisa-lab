package it.unipr.lisa.tutorial;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

public class MyAvailableExpressions implements 
	DataflowElement<DefiniteDataflowDomain<MyAvailableExpressions>, 
	MyAvailableExpressions> {

	private final ValueExpression expression;

	public MyAvailableExpressions() {
		this(null);
	}

	public MyAvailableExpressions(ValueExpression expression) {
		this.expression = expression;
	}

	@Override
	public Collection<Identifier> getInvolvedIdentifiers() {
		return getIdentifierOperands(expression);
	}

	public static Collection<Identifier> getIdentifierOperands(ValueExpression expression) {
		Collection<Identifier> result = new HashSet<>();

		if (expression == null)
			return result;

		if (expression instanceof Identifier)
			result.add((Identifier) expression);

		if (expression instanceof UnaryExpression) {
			result.addAll(getIdentifierOperands((ValueExpression) ((UnaryExpression) expression).getExpression()));
		}

		if (expression instanceof BinaryExpression bin) {
			result.addAll(getIdentifierOperands((ValueExpression) bin.getLeft()));
			result.addAll(getIdentifierOperands((ValueExpression) bin.getRight()));
		}

		if (expression instanceof TernaryExpression ter) {
			result.addAll(getIdentifierOperands((ValueExpression) ter.getLeft()));
			result.addAll(getIdentifierOperands((ValueExpression) ter.getMiddle()));
			result.addAll(getIdentifierOperands((ValueExpression) ter.getRight()));
		}

		return result;
	}

	@Override
	public Collection<MyAvailableExpressions> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<MyAvailableExpressions> domain) throws SemanticException {
		Collection<MyAvailableExpressions> result = new HashSet<>();
		MyAvailableExpressions ae = new MyAvailableExpressions(expression);
		if (!ae.getInvolvedIdentifiers().contains(id) && filter(expression))
			result.add(ae);
		return result;
	}

	private boolean filter(ValueExpression expression) {
		if (expression instanceof Identifier)
			return false;
		if (expression instanceof Constant)
			return false;
		if (expression instanceof Skip)
			return false;
		if (expression instanceof PushAny)
			return false;
		return true;
	}

	@Override
	public Collection<MyAvailableExpressions> gen(ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<MyAvailableExpressions> domain) throws SemanticException {
		Collection<MyAvailableExpressions> result = new HashSet<>();
		MyAvailableExpressions ae = new MyAvailableExpressions(expression);
		if (filter(expression))
			result.add(ae);
		return result;
	}

	@Override
	public Collection<MyAvailableExpressions> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<MyAvailableExpressions> domain) throws SemanticException {
		Collection<MyAvailableExpressions> result = new HashSet<>();
		for (MyAvailableExpressions ae : domain.getDataflowElements()) {
			Collection<Identifier> ids = getIdentifierOperands(ae.expression);
			if (ids.contains(id))
				result.add(ae);
		}
		
		return result;
	}

	@Override
	public Collection<MyAvailableExpressions> kill(ValueExpression expression, ProgramPoint pp,
			DefiniteDataflowDomain<MyAvailableExpressions> domain) throws SemanticException {
		return Collections.emptyList();
	}


	@Override
	public MyAvailableExpressions pushScope(ScopeToken token) throws SemanticException {
		return new MyAvailableExpressions((ValueExpression) expression.pushScope(token));
	}

	@Override
	public MyAvailableExpressions popScope(ScopeToken token) throws SemanticException {
		return new MyAvailableExpressions((ValueExpression) expression.popScope(token));
	}

	@Override
	public StructuredRepresentation representation() {
		return new StringRepresentation(expression);
	}

	@Override
	public String toString() {
		return expression.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(expression);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyAvailableExpressions other = (MyAvailableExpressions) obj;
		return Objects.equals(expression, other.expression);
	}
}
