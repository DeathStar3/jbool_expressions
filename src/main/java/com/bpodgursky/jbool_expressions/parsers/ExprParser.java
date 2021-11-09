package com.bpodgursky.jbool_expressions.parsers;

import com.bpodgursky.jbool_expressions.*;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExprParser {

  public static Expression<String> parse(String expression) {
    return parse(expression, new IdentityMap());
  }

  public static <T> Expression<T> parse(String expression, TokenMapper<T> mapper) {
    try {
      //lexer splits input into tokens
      ANTLRStringStream input = new ANTLRStringStream(expression);
      TokenStream tokens = new CommonTokenStream(new CPPExprLexer(input));

      //parser generates abstract syntax tree
      CPPExprParser parser = new CPPExprParser(tokens);
      CPPExprParser.expression_return ret = parser.expression();

      //acquire parse result
      CommonTree ast = (CommonTree)ret.getTree();
      return parse(ast, mapper);
    } catch (RecognitionException e) {
      throw new IllegalStateException("Recognition exception is never thrown, only declared.");
    }
  }

  public static <T> Expression<T> parse(Tree tree, TokenMapper<T> mapper) {
    if (tree.getType() == CPPExprParser.AND) {
      List<Expression<T>> children = new ArrayList<>();
      for (int i = 0; i < tree.getChildCount(); i++) {
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if (child.getType() == CPPExprParser.AND) {
          children.addAll(Arrays.asList(((And<T>)parse).expressions));
        } else {
          children.add(parse);
        }
      }

      return And.of(children);
    } else if (tree.getType() == CPPExprParser.OR) {
      List<Expression<T>> children = new ArrayList<>();
      for (int i = 0; i < tree.getChildCount(); i++) {
        Tree child = tree.getChild(i);
        Expression<T> parse = parse(child, mapper);
        if (child.getType() == CPPExprParser.OR) {
          children.addAll(Arrays.asList(((Or<T>)parse).expressions));
        } else {
          children.add(parse);
        }
      }
      return Or.of(children);
    } else if (tree.getType() == CPPExprParser.NOT) {
      return Not.of(parse(tree.getChild(0), mapper));
    } else if (tree.getType() == CPPExprParser.NAME) {
      if (tree.getChildCount() == 0) {
        return Variable.of(mapper.getVariable(tree.getText()));
      } else {
        List<String> params = new ArrayList <>();
        for (int i = 0 ; i < tree.getChildCount() ; i++) {
          params.add(tree.getChild(i).getText());
        }
        String variableText = String.format("MACRO[%s]PARAMS[%s]", tree.getText(), String.join(",", params));
        return Variable.of(mapper.getVariable(variableText));
      }
    } else if (tree.getType() == CPPExprParser.QUOTED_NAME) {
      return Variable.of(mapper.getVariable(tree.getText()));
    } else if (tree.getType() == CPPExprParser.DOUBLE_QUOTED_NAME) {
      return Variable.of(mapper.getVariable(tree.getText()));
    } else if (tree.getType() == CPPExprParser.TRUE) {
      return Literal.getTrue();
    } else if (tree.getType() == CPPExprParser.FALSE) {
      return Literal.getFalse();
    } else if (tree.getType() == CPPExprParser.LPAREN) {
      return parse(tree.getChild(0), mapper);
    } else if (tree.getType() == CPPExprParser.SUP) {
      return getVariable(tree, mapper, "SUP");
    } else if (tree.getType() == CPPExprParser.SUPEQ) {
      return getVariable(tree, mapper, "SUPEQ");
    } else if (tree.getType() == CPPExprParser.INF) {
      return getVariable(tree, mapper, "INF");
    } else if (tree.getType() == CPPExprParser.INFEQ) {
      return getVariable(tree, mapper, "INFEQ");
    } else if (tree.getType() == CPPExprParser.PLUS) {
      return getVariable(tree, mapper, "PLUS");
    } else if (tree.getType() == CPPExprParser.MINUS) {
      return getVariable(tree, mapper, "MINUS");
    }  else if (tree.getType() == CPPExprParser.TIMES) {
      return getVariable(tree, mapper, "TIMES");
    }  else if (tree.getType() == CPPExprParser.DIFF) {
      return getVariable(tree, mapper, "DIFF");
    }  else if (tree.getType() == CPPExprParser.EQ) {
      return getVariable(tree, mapper, "EQ");
    } else {
      throw new RuntimeException("Unrecognized! " + tree.getType() + " " + tree.getText());
    }
  }

  private static <T> Variable <T> getVariable(Tree tree, TokenMapper <T> mapper, String joiner) {
    return Variable.of(mapper.getVariable(String.format("[%s]_%s_[%s]", parseAndToString(tree, mapper, 0), joiner, parseAndToString(tree, mapper, 1))));
  }

  private static <T> String parseAndToString(Tree tree, TokenMapper <T> mapper, int childIndex) {
    return parse(tree.getChild(childIndex), mapper).toLexicographicString();
  }

}
