package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.And;
import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

import static com.bpodgursky.jbool_expressions.ExprUtil.collapseToSOP;

public class ToSOP<K> extends Rule<And<K>, K> {

  @Override
  public Expression<K> applyInternal(And<K> and, ExprOptions<K> options) {
    //  if there are any children which are ORs,
    for (Expression<K> e : and.expressions) {
      if (e instanceof Or) {
        Or<K> or = (Or<K>) e;

        return collapseToSOP(and, or, null, options);
      }
    }
    return and;
  }

  @Override
  protected boolean isApply(Expression<K> input) {
    return input instanceof And;
  }
}