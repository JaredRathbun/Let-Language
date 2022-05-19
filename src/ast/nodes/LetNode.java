package ast.nodes;

import lexer.Token;
import environment.Environment;
import java.util.LinkedList;

/**
 * This node represents a let expression.
 * 
 * @author Zach Kissel
 */
public class LetNode extends SyntaxNode {
  private LinkedList<Token> varList;
  private LinkedList<SyntaxNode> varExprList;
  private SyntaxNode expr;

  /**
   * Constructs a new binary operation syntax node.
   * 
   * @param var     the variable identifier.
   * @param varExpr the expression that give the variable value.
   * @param expr    the expression that uses the variables value.
   */
  public LetNode(LinkedList<Token> varList, LinkedList<SyntaxNode> varExprList, 
    SyntaxNode expr) {
    this.varList = varList;
    this.varExprList = varExprList;
    this.expr = expr;
  }

  /**
   * Evaluate the node.
   * 
   * @param env the executional environment we should evaluate the
   *            node under.
   * @return the object representing the result of the evaluation.
   */
  public Object evaluate(Environment env) {

    /* For every index in the lists, get the Token and it's corresponding expr,
    then add it to the environment. */
    for (int i = 0; i < varList.size(); i++) {
      Object varVal = varExprList.get(i).evaluate(env);
      Token var = varList.get(i);

      if (varVal instanceof Integer || varVal instanceof Double ||
          varVal instanceof LinkedList)
        env.updateEnvironment(var, varVal);
      else
        System.out.println("Failed to add " + var + "with  value " + varVal
            .getClass());
    }

    Object value = expr.evaluate(env);
    return value;
  }
}
