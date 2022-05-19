package ast.nodes;

import lexer.TokenType;
import environment.Environment;

/**
 * This node represents relational operations.
 * @author Zach Kissel
 */
 public class RelOpNode extends SyntaxNode
 {
   private TokenType op;
   private SyntaxNode leftExpr;
   private SyntaxNode rightExpr;

   /**
    * Constructs a new binary operation syntax node.
    * @param lexpr the left operand.
    * @param op the binary operation to perform.
    * @param rexpr the right operand.
    */
    public RelOpNode(SyntaxNode lexpr, TokenType op, SyntaxNode rexpr)
    {
      this.op = op;
      this.leftExpr = lexpr;
      this.rightExpr = rexpr;
    }

    /**
     * Evaluate the node.
     * @param env the executional environment we should evaluate the
     * node under.
     * @return the object representing the result of the evaluation.
     */
     public Object evaluate(Environment env)
     {
        Object lval;
        Object rval;
        boolean useDouble = false;

        lval = leftExpr.evaluate(env);
        rval = rightExpr.evaluate(env);

        if (lval == null || rval == null)
          return null;

        // Make sure the type is sound.
        if(!(lval instanceof Integer || lval instanceof Double) &&
           !(rval instanceof Double || rval instanceof Integer))
          return null;


        if (lval.getClass() !=  rval.getClass())
        {
          System.out.println("Error: mixed type expression.");
          return null;
        }

        if (lval instanceof Double)
          useDouble = true;

        // Perform the operation base on the type.
        switch(op)
        {
          case LT:
            if (useDouble)
              return (Double) lval < (Double) rval;
            return (Integer) lval < (Integer) rval;
          case LTE:
            if (useDouble)
              return (Double) lval <= (Double) rval;
            return (Integer) lval <= (Integer) rval;
          case GT:
            if (useDouble)
              return (Double) lval > (Double) rval;
            return (Integer)lval > (Integer) rval;
          case GTE:
            if (useDouble)
              return (Double) lval >= (Double) rval;
            return (Integer) lval >= (Integer) rval;
          case EQ:
            return lval.equals(rval);
          case NEQ:
            return !(lval.equals(rval));
          default:
            return null;
        }

     }
 }
