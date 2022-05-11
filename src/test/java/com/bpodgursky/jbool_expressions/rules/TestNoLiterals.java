package com.bpodgursky.jbool_expressions.rules;

import com.bpodgursky.jbool_expressions.Expression;
import com.bpodgursky.jbool_expressions.JBoolTestCase;
import com.bpodgursky.jbool_expressions.Literal;
import com.bpodgursky.jbool_expressions.options.ExprOptions;
import com.bpodgursky.jbool_expressions.parsers.ExprParser;

import java.util.*;

public class TestNoLiterals extends JBoolTestCase {

  // OR CASES //

  public void testOrFalseShouldDisappearForOrSimplificationSimpleCases() {
    Expression <String> expr = ExprParser.parse("A | false");
    assertEquals(2, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(1, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());

    expr = ExprParser.parse("!A | false");
    assertEquals(2, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    simplified = applyOrRule(expr);
    assertEquals(1, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testOrTrueShouldNotDisappearForOrSimplificationSimpleCases() {
    // TODO: 11/05/2022 check what should be the correct behaviour
    Expression <String> expr = ExprParser.parse("A | true");
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(ExprParser.parse("true"), simplified);
//    assertEquals(expr, simplified);

    expr = ExprParser.parse("!A | true");
    simplified = applyOrRule(expr);
    assertEquals(ExprParser.parse("true"), simplified);
//    assertEquals(expr, simplified);
  }

  public void testOrFalseShouldDisappearForOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A | false");
    assertEquals(4, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(3, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndFalseShouldNotChangeExpressionForOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A & false");
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(expr, simplified);
  }

  public void testOrFalseorFalseShouldDisappearForOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A | false | false");
    assertEquals(5, expr.getChildren().size());
    assertEquals(2, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(3, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndFalseOrFalseShouldRemoveAFalseForOrSimplification() {
    Expression <String> expr = ExprParser.parse("(A | A | !A) & (false | false)");
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(ExprParser.parse("(A | A | !A) & false"), simplified);
  }

  public void testAndTrueShouldNotChangeExpressionForOrSimplification() {
    Expression <String> expr = ExprParser.parse("A & A & !A & true");
    Expression <String> simplified = applyOrRule(expr);
    assertEquals(expr, simplified);
  }

  // AND CASES //

  public void testAndTrueShouldDisappearForAndSimplificationSimpleCases() {
    Expression <String> expr = ExprParser.parse("A & true");
    assertEquals(2, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyAndRule(expr);
    assertEquals(1, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());

    expr = ExprParser.parse("!A & true");
    assertEquals(2, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    simplified = applyAndRule(expr);
    assertEquals(1, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndFalseShouldNotDisappearForAndSimplificationSimpleCases() {
    // TODO: 11/05/2022 check what should be the correct behaviour
    Expression <String> expr = ExprParser.parse("A & false");
    Expression <String> simplified = applyAndRule(expr);
    assertEquals(ExprParser.parse("false"), simplified);
//    assertEquals(expr, simplified);

    expr = ExprParser.parse("!A & false");
    simplified = applyAndRule(expr);
    assertEquals(ExprParser.parse("false"), simplified);
//    assertEquals(expr, simplified);
  }

  public void testAndTrueShouldDisappearForAndSimplification() {
    Expression <String> expr = ExprParser.parse("(A & A & !A) & true");
    assertEquals(2, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyAndRule(expr);
    assertEquals(1, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndTrueShouldDisappearForAndSimplification2() {
    Expression <String> expr = ExprParser.parse("A & A & !A & true");
    assertEquals(4, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyAndRule(expr);
    assertEquals(3, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  // AND + OR CASES //

  public void testOrFalseShouldDisappearForAndOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A | false");
    assertEquals(4, expr.getChildren().size());
    assertEquals(1, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(3, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndFalseShouldChangeExpressionForAndOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A & false");
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(ExprParser.parse("A | A"), simplified);
  }

  public void testOrFalseorFalseShouldDisappearForAndOrSimplification() {
    Expression <String> expr = ExprParser.parse("A | A | !A | false | false");
    assertEquals(5, expr.getChildren().size());
    assertEquals(2, expr.getChildren().stream().filter(e -> e instanceof Literal).count());
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(3, simplified.getChildren().size());
    assertEquals(0, simplified.getChildren().stream().filter(e -> e instanceof Literal).count());
  }

  public void testAndFalseOrFalseShouldMakeItFalseForAndOrSimplification() {
    Expression <String> expr = ExprParser.parse("(A | A | !A) & (false | false)");
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(ExprParser.parse("false"), simplified);
  }

  public void testAndTrueShouldDisappearForAndOrSimplification() {
    Expression <String> expr = ExprParser.parse("A & A & !A & true");
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(ExprParser.parse("A & A & !A"), simplified);
  }

  public void testRealCase() {
    Expression <String> expr = ExprParser.parse("(((true & true & (MOZ_DEBUG & ENABLE_TESTS & MOZ_SANDBOX)) & ((true & true & true) & ((true & (true & true & true)) & (true & true & MOZ_SANDBOX)))) & (!mozilla_SandboxTestingChild_h & (!MOZ_DEBUG | !ENABLE_TESTS | !MOZ_SANDBOX)))");
    Expression <String> simplified = applyAndOrRules(expr);
    assertEquals(RuleSet.toDNF(ExprParser.parse("(((MOZ_DEBUG & ENABLE_TESTS & MOZ_SANDBOX) & MOZ_SANDBOX) & (!mozilla_SandboxTestingChild_h & (!MOZ_DEBUG | !ENABLE_TESTS | !MOZ_SANDBOX)))")), RuleSet.toDNF(simplified));
  }

  public Expression <String> applyOrRule(Expression<String> expression) {
    return applyRule(expression, new NoLiteralsOrExpression <>());
  }
  public Expression <String> applyAndRule(Expression<String> expression) {
    return applyRule(expression, new NoLiteralsAndExpression <>());
  }
  public Expression <String> applyAndOrRules(Expression<String> expression) {
    return applyRule(expression, new NoLiteralsAndExpression <>(), new NoLiteralsOrExpression<>());
  }
  public Expression <String> applyRule(Expression<String> expression, Rule... rules) {
    ArrayList <Rule> rulesList = new ArrayList <>();
    Collections.addAll(rulesList, rules);
    return RulesHelper.applySet(expression, new RuleList(rulesList), ExprOptions.noCaching());
  }

}