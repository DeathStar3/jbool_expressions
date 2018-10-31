package com.bpodgursky.jbool_expressions;

import com.bpodgursky.jbool_expressions.rules.Rule;
import com.bpodgursky.jbool_expressions.rules.RuleSetCache;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Literal<K> extends Expression<K> {
  public static final String EXPR_TYPE = "literal";

  private final boolean value;

  private static final Literal TRUE = new Literal(true);
  private static final Literal FALSE = new Literal(false);

  @SuppressWarnings("unchecked")
  public static <V> Literal<V> getTrue() {
    return TRUE;
  }

  @SuppressWarnings("unchecked")
  public static <V> Literal<V> getFalse() {
    return FALSE;
  }

  public static <K> Literal<K> of(boolean value) {
    if (value) {
      return getTrue();
    } else {
      return getFalse();
    }
  }

  private Literal(boolean value) {
    this.value = value;
  }

  public String toString() {
    return Boolean.valueOf(value).toString();
  }

  public boolean getValue() {
    return value;
  }

  @Override
  public Expression<K> apply(List<Rule<?, K>> rules, RuleSetCache<K> cache) {
    return this;
  }

  @Override
  public List<Expression<K>> getChildren() {
    return Collections.emptyList();
  }

  @Override
  public Expression<K> map(Function<Expression<K>, Expression<K>> function, ExprFactory<K> factory) {
    return function.apply(this);
  }

  @Override
  public Expression<K> sort(Comparator<Expression> comparator) {
    return this;
  }

  @Override
  public String getExprType() {
    return EXPR_TYPE;
  }

  @Override
  public boolean equals(Object o) {
    return this == o;
  }

  @Override
  public int hashCode() {
    return Boolean.hashCode(value);
  }

  @Override
  public void collectK(Set<K> set, int limit) {
    //  no op
  }

  public Expression<K> replaceVars(Map<K, Expression<K>> m, ExprFactory<K> factory) {
    return this;
  }
}
