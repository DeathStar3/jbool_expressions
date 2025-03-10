package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

import java.util.*;

public class SimplifyAnd<K> extends Rule<And<K>, K> {

  @Override
  public Expression<K> applyInternal(And<K> input, ExprOptions<K> options) {

    for (Expression<K> expr : input.expressions) {
      if (expr instanceof Literal) {
        return getExpressionWithoutLiteral(input, options, (Literal) expr);
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

  protected Expression <K> getExpressionWithoutLiteral(And <K> input, ExprOptions <K> options, Literal expr) {
    Literal l = expr;

    if (l.getValue()) {
      return copyWithoutTrue(input, options);
    } else {
      return Literal.getFalse();
    }
  }

  protected Expression<K> copyWithoutTrue(And<K> input, ExprOptions<K> options){
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

    return options.getExprFactory().and(copy.toArray(new Expression[copy.size()]));
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}
