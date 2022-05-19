package ast.nodes;

import lexer.TokenType;
import environment.Environment;

/**
 * This node represents the unary op node.
 * @author Zach Kissel
 */
 public class UnaryOpNode extends SyntaxNode
 {
   private TokenType op;
   private SyntaxNode expr;


   /**
    * Constructs a new binary operation syntax node.
    * @param expr the operand.
    * @param op the binary operation to perform.
    */
    public UnaryOpNode(SyntaxNode expr, TokenType op)
    {
      this.op = op;
      this.expr = expr;
    }

    /**
     * Evaluate the node.
     * @param env the executional environment we should evaluate the
     * node under.
     * @return the object representing the result of the evaluation.
     */
     public Object evaluate(Environment env)
     {
        Object val;

        val = expr.evaluate(env);

        if (val == null)
          return null;

        // Make sure the type is sound.
        if(!(val instanceof Boolean))
          return null;

        // Perform the operation based on the type.
        switch(op)
        {
          case NOT:
            return !((Boolean) val);
          default:
            return null;
        }

     }
 }
