package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.Or;
import com.bpodgursky.jbool_expressions.options.ExprOptions;

public class NoLiteralsOrExpression <K> extends SimplifyOr <K> {
    @Override
    public Expression <K> applyInternal(Or <K> input, ExprOptions <K> options) {
        for (Expression<K> expr : input.expressions) {
            if (expr instanceof Literal) {
                return super.getExpressionWithoutLiteral(input, options, (Literal) expr);
            }

        }
        return input;
    }

    @Override
    protected boolean isApply(Expression <K> input) {
        return super.isApply(input);
    }
}
