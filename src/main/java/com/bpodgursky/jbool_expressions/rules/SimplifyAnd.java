package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.util.ExprFactory;

import java.util.*;

public class SimplifyAnd<K> extends Rule<And<K>, K> {

  @Override
  public Expression<K> applyInternal(And<K> input, RuleSetCache<K> cache) {

    for (Expression<K> expr : input.expressions) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        if (l.getValue()) {
          return copyWithoutTrue(input, cache.factory());
        } else {
          return Literal.getFalse();
        }
      }

      //  succeed immediately if require something or its opposite
      if( expr instanceof Not){
        Expression<K> notChild = ((Not<K>)expr).getE();
        for(Expression<K> child: input.expressions){
          if(child.equals(notChild)){
            return Literal.getFalse();
          }
        }
      }
    }

    return input;
  }

  private Expression<K> copyWithoutTrue(And<K> input, ExprFactory<K> factory){
    List<Expression<K>> copy = new ArrayList<>();
    for (Expression<K> expr : input.expressions) {
      if (expr instanceof Literal) {
        Literal l = (Literal) expr;

        if (l.getValue()) {
          continue;
        }
      }
      copy.add(expr);
    }

    if (copy.isEmpty()) {
      return Literal.getTrue();
    }

    return factory.and(copy.toArray(new Expression[copy.size()]));
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}
