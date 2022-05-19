package ast.nodes;

import environment.Environment;
import lexer.Token;
import lexer.TokenType;

import java.util.LinkedList;

/**
 * This node represents the program.
 * 
 * @author Zach Kissel
 */
public class ProgNode extends SyntaxNode {
  private LinkedList<SyntaxNode> exprs;

  /**
   * Constructs a new program node which represents
   * a list of expressions.
   * 
   * @param exprs a linked list of expressions (AST subtress).
   */
  public ProgNode(LinkedList<SyntaxNode> exprs) {
    this.exprs = exprs;
  }

  /**
   * Evaluate the node.
   * 
   * @param env the executional environment we should evaluate the
   *            node under.
   * @return the object representing the result of the evaluation.
   */
  public Object evaluate(Environment env) {
    Object res = null;

    // Loop over the expressions evaluating every
    // non function definition.
    for (SyntaxNode expr : exprs) {
      // If we are a function definition, update the
      // environment such that it holds the function name
      // along with the subtree that describes its
      // computation.
      if (expr instanceof FunNode) {
        FunNode fun = (FunNode) expr;
        env.updateEnvironment(fun.getName(), fun);
      } else if (expr instanceof GlobalNode) {
        // Get the lists for both sides of the assignment.
        LinkedList<Token> varList = ((GlobalNode) expr).getVarList();
        LinkedList<SyntaxNode> varExprList = ((GlobalNode) expr)
          .getVarExprList();

        // Loop over every identifier and value pair, and add to the env.
        for (int i = 0; i < varList.size(); i++) {
          env.updateEnvironment(varList.get(i), varExprList.get(i)
            .evaluate(env));
        }

      } else if (expr instanceof FirstOrderNode) {
        FirstOrderNode fon = (FirstOrderNode) expr;
        env.updateEnvironment(fon.getFunName(), fon);
      }
      else // Simply evaluate being careful to obey let scoping rules.
      {
        // Let expressions and funciton applications define new scope. This
        // results values being added to the environment. We should make a
        // copy of the environment to and restore it after executing the
        // expression to preserve scope rules.
        Environment copy = env.copy();
        res = expr.evaluate(env);
        env = copy;
        if (res == null)
          return null;
      }
    }

    return res;
  }
}
