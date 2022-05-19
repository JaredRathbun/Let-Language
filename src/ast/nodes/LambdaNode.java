package ast.nodes;

import lexer.Token;
import environment.Environment;

/**
 * This node represents a lambda expression.
 * @author Zach Kissel
 */
 public class LambdaNode extends SyntaxNode
 {
   private Token var;
   private SyntaxNode expr;

   /**
    * Constructs a new function node which represents
    * a function declaration.
    *
    * @param var the free variable in the expression.
    * @param expr the expression to execute.
    */
   public LambdaNode(Token var, SyntaxNode expr)
   {
     this.var = var;
     this.expr = expr;
   }

  /**
   * Get the parameter of the function.
   * 
   * @return a Token representing the parameter name.
   */
   public Token getVar()
   {
     return var;
   }

   /**
    * Gets the expression associated with the lambda expression.
    */
   public SyntaxNode getExpr() {
     return expr;
   }

   /**
    * Evaluate the node.
    * 
    * @param env the executional environment we should evaluate the
    * node under.
    * @return the object representing the result of the evaluation.
    */
   public Object evaluate(Environment env)
   {
     return expr.evaluate(env);
   }
 }
