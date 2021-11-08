package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.*;
import com.bpodgursky.jbool_expressions.rules.RuleSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TestExprParser extends JBoolTestCase {

  public void testIt() {
    assertLexEquals(Variable.of("A"), ExprParser.parse("A"));
    assertLexEquals(Variable.of("AA"), ExprParser.parse("AA"));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("!A"));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("   !  A "));
    assertLexEquals(Not.of(Variable.of("A")), ExprParser.parse("  ( !  (A) )"));
    assertLexEquals(And.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("  A & (B)  "));
    assertLexEquals(And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C"))), ExprParser.parse("(  A & (B) & !C )"));
    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B")), ExprParser.parse("(  A | (B)  )"));
    assertLexEquals(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), ExprParser.parse("!(  A | (B)  )"));
    assertLexEquals(Or.of(And.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("  A & (B) | C "));
    assertLexEquals(And.of(Or.of(Variable.of("A"), Variable.of("B")), Variable.of("C")), ExprParser.parse("(A | B) & C"));
    assertLexEquals(And.of(Not.of(Or.of(Variable.of("A"), Variable.of("B"))), Variable.of("C")), ExprParser.parse("!(A | B) & C"));
    assertLexEquals(Or.of(Variable.of("A"), Or.of(Variable.of("B"), Variable.of("C")), And.of(Variable.of("D"), Variable.of("E"))),
        ExprParser.parse("A | (B | C )| D & E"));

    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C"), And.of(Variable.of("D"), Variable.of("E"))),
            ExprParser.parse("A | B | C | D & E"));

    assertLexEquals(Literal.<String>getFalse(), ExprParser.parse("false"));
    assertLexEquals(Literal.<String>getTrue(), ExprParser.parse("true"));
    assertLexEquals(Literal.<String>getTrue(), ExprParser.parse("(true)"));

    assertLexEquals(Not.of(Literal.<String>getTrue()), ExprParser.parse("!(true)"));

    assertLexEquals(And.of(Not.of(Variable.of("' A:aa+)(*&^%$#@!_123'")), Variable.of("A")), ExprParser.parse("!' A:aa+)(*&^%$#@!_123' & A"));

    assertLexEquals(
        Not.of(Not.of(Variable.of("A"))),
        ExprParser.parse("!!A")
    );

    assertLexEquals(
        Not.of(Not.of(Not.of(Variable.of("A")))),
        ExprParser.parse("!!!A")
    );

    assertLexEquals(
        Not.of(Not.of(Or.of(Variable.of("A"), Variable.of("B")))),
        ExprParser.parse("!!(A | B)")
    );

    assertLexEquals(Or.of(Variable.of("A"), Or.of(Variable.of("B"), Variable.of("C"))), ExprParser.parse("(A|(B|C))"));
    assertLexEquals(Or.of(Variable.of("A"), Variable.of("B"), Variable.of("C")  ), ExprParser.parse("(A|B|C)"));

  }

  public void testQuotedMapper() {

    QuotedMapper<Integer> intMapper = new QuotedMapper<Integer>() {
      @Override
      public Integer getValue(String name) {
        return Integer.parseInt(name);
      }
    };

    assertEquals(Variable.of(1), ExprParser.parse("'1'", intMapper));
    assertEquals(Not.of(Variable.of(1)), ExprParser.parse("!'1'", intMapper));
  }

  public void testSingleQuotes(){

    assertEquals(Or.of(Variable.of("'test@gmail.com'"), Variable.of("'test2@gmail.com'")),
        ExprParser.parse("( 'test@gmail.com' | 'test2@gmail.com' )"));


    assertEquals(Or.of(Variable.of("'test & gmail.com'"), Variable.of("'test2 & gmail.com'")),
        ExprParser.parse("( 'test & gmail.com' | 'test2 & gmail.com' )"));

  }


  public void testDoubleQuotes(){

    assertEquals(Or.of(Variable.of("\"test@gmail.com\""), Variable.of("\"test2@gmail.com\"")),
        ExprParser.parse("( \"test@gmail.com\" | \"test2@gmail.com\" )"));

    //  defend against the indefensible
    assertEquals(Or.of(Variable.of("\"test & gmail.com\""), Variable.of("\"test2 & gmail.com\"")),
        ExprParser.parse("( \"test & gmail.com\" | \"test2 & gmail.com\" )"));

  }

  public void testLexSort() {

    assertEquals("(A & B & !C)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toString()
    );

    assertEquals("(!C & A & B)", And.of(Variable.of("A"), Variable.of("B"), Not.of(Variable.of("C")))
        .toLexicographicString()
    );

  }

  public void testSimpleSUP() {
    assertLexEquals(Variable.of("[A]_SUP_[B]"), ExprParser.parse("A > B"));
  }

  public void testSimpleINF() {
    assertLexEquals(Variable.of("[A]_INF_[B]"), ExprParser.parse("A < B"));
  }

  public void testSimpleSUPEQ() {
    assertLexEquals(Variable.of("[A]_SUPEQ_[B]"), ExprParser.parse("A >= B"));
  }

  public void testSimpleINFEQ() {
    assertLexEquals(Variable.of("[A]_INFEQ_[B]"), ExprParser.parse("A <= B"));
  }

  public void testSimplePLUS() {
    assertLexEquals(Variable.of("[A]_PLUS_[B]"), ExprParser.parse("A + B"));
  }

  public void testSimpleMINUS() {
    assertLexEquals(Variable.of("[A]_MINUS_[B]"), ExprParser.parse("A - B"));
  }

  public void testSimpleTIMES() {
    assertLexEquals(Variable.of("[A]_TIMES_[B]"), ExprParser.parse("A * B"));
  }

  public void testSUPOfPLUS() {
    assertLexEquals(Variable.of("[[A]_PLUS_[B]]_SUP_[C]"), ExprParser.parse("(A + B) > C"));
  }

  public void testANDOfSUPAndINF() {
    Expression <String> parse = ExprParser.parse("(A > 3) & (A < 6)");
    assertTrue(parse instanceof And);
    List <Expression <String>> children = parse.getChildren();
    assertTrue(children.stream().allMatch(expr -> expr instanceof Variable));
    assertTrue(children.contains(Variable.of("[A]_SUP_[3]")));
    assertTrue(children.contains(Variable.of("[A]_INF_[6]")));
  }

  public void testOROfINFEQAndSUPEQ() {
    Expression <String> parse = ExprParser.parse("(A <= 10) | (B >= 7)");
    assertTrue(parse instanceof Or);
    List <Expression <String>> children = parse.getChildren();
    assertTrue(children.stream().allMatch(expr -> expr instanceof Variable));
    assertTrue(children.contains(Variable.of("[A]_INFEQ_[10]")));
    assertTrue(children.contains(Variable.of("[B]_SUPEQ_[7]")));
  }

  public void testComplicated() {
    String str = "(((__GNUC__ * 100) + __GNUC_MINOR__) >= 405) & ((__GNUC__) & __GNUC__ >= 3)";
    Expression <String> parse = ExprParser.parse(str);
    assertTrue(parse instanceof And);
    List <Expression <String>> children = parse.getChildren();
    Optional <Expression <String>> variableSide = children.stream().filter(expr -> expr instanceof Variable).collect(Collectors.toList()).stream().findFirst();
    assertTrue(variableSide.isPresent());
    assertEquals("[[[__GNUC__]_TIMES_[100]]_PLUS_[__GNUC_MINOR__]]_SUPEQ_[405]", variableSide.get().toLexicographicString());
    Optional <Expression <String>> andSide = children.stream().filter(expr -> expr instanceof And).collect(Collectors.toList()).stream().findFirst();
    assertTrue(andSide.isPresent());
    List <Expression <String>> andSideChildren = andSide.get().getChildren();
    assertTrue(andSideChildren.stream().allMatch(expr -> expr instanceof Variable));
    assertTrue(andSideChildren.contains(Variable.of("__GNUC__")));
    assertTrue(andSideChildren.contains(Variable.of("[__GNUC__]_SUPEQ_[3]")));
  }

  public void testVariadicMacro() {
    assertLexEquals(Variable.of("MACRO[XXH_HAS_BUILTIN]PARAMS[__builtin_rotateleft32]"), ExprParser.parse("(XXH_HAS_BUILTIN(__builtin_rotateleft32))"));
  }

  public void testVariadicMacroMultipleArguments() {
    assertLexEquals(Variable.of("MACRO[XXH_HAS_BUILTIN]PARAMS[__builtin_rotateleft32,__builtin_rotateleft64]"), ExprParser.parse("(XXH_HAS_BUILTIN(__builtin_rotateleft32,__builtin_rotateleft64))"));
  }

  public void testExpressionWithVariadicMacro() {
    Expression <String> parse = ExprParser.parse("MACRO1(_param1_,__param2) > 10 & (MACRO2(PARAM2) | FEATURE)");
    assertTrue(parse instanceof And);
    List <Expression <String>> children = parse.getChildren();
    Optional <Expression <String>> variableSide = children.stream().filter(expr -> expr instanceof Variable).collect(Collectors.toList()).stream().findFirst();
    assertTrue(variableSide.isPresent());
    assertEquals(Variable.of("[MACRO[MACRO1]PARAMS[_param1_,__param2]]_SUP_[10]"), variableSide.get());
    Optional <Expression <String>> orSide = children.stream().filter(expr -> expr instanceof Or).collect(Collectors.toList()).stream().findFirst();
    assertTrue(orSide.isPresent());
    List <Expression <String>> orChildren = orSide.get().getChildren();
    assertTrue(orChildren.contains(Variable.of("MACRO[MACRO2]PARAMS[PARAM2]")));
    assertTrue(orChildren.contains(Variable.of("FEATURE")));
    System.out.println(parse);
  }

  private void assertLexEquals(Expression expected, Expression actual) {
    Assert.assertEquals(expected, actual);
    Assert.assertEquals(expected.toLexicographicString(), actual.toLexicographicString());
  }

}
