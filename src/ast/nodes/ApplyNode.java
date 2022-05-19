package ast.nodes;

import environment.Environment;
import lexer.Token;
import lexer.TokenType;

/**
 * This node represents the unary op node.
 * 
 * @author Zach Kissel
 */
public class ApplyNode extends SyntaxNode {
  private SyntaxNode func;
  private SyntaxNode arg;

  /**
   * Constructs a new node that represents function application.
   * 
   * @param func the function to apply.
   * @param arg  the argument to apply the function to.
   */
  public ApplyNode(SyntaxNode func, SyntaxNode arg) {
    this.func = func;
    this.arg = arg;
  }

  public SyntaxNode getArg() {
    return this.arg;
  }

  public SyntaxNode getFunc() {
    return this.func;
  }

  /**
   * Evaluate the node.
   * 
   * @param env the executional environment we should evaluate the
   *            node under.
   * @return the object representing the result of the evaluation.
   */
  public Object evaluate(Environment env) {

    SyntaxNode node = null;
    LambdaNode lexpr = null;

    // Check for a nested Apply.
    if (func instanceof ApplyNode) {
      // Need to get a closure (FirstOrderNode) where y = arg of inner apply node.
      node = (SyntaxNode) func.evaluate(env);
      
      env.updateEnvironment(((LambdaNode) func.evaluate(env)).getVar(), arg.evaluate(env));
      return node.evaluate(env);
    }

    // The name of a function is being referenced.
    else if (func instanceof TokenNode) {
      // Lookup the function name and get the FirstOrderNode associated with it.
      FirstOrderNode funLookup = (FirstOrderNode) func.evaluate(env);
      FunNode fun = (FunNode) funLookup.getFunNode();
      lexpr = fun.getLambdaExpression();

      // Binds x to 3.
      env.updateEnvironment(lexpr.getVar(), arg.evaluate(env));
      if (lexpr.getExpr() instanceof BinOpNode) {
        return lexpr.getExpr().evaluate(env);
      } else {
        node = (LambdaNode) lexpr.getExpr();
        return node;
      }
    }
    return null;

  }
}
