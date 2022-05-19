package ast.nodes;

import lexer.Token;
import environment.Environment;

/**
 * This node represents the a token in the grammar.
 * @author Zach Kissel
 */
 public class TokenNode extends SyntaxNode
 {
   private Token token;   // The token type.

   /**
    * Constructs a new token node.
    * @param token the token to associate with the node.
    */
    public TokenNode(Token token)
    {
      this.token = token;
    }

    /**
     * Evaluate the node.
     * @param env the executional environment we should evaluate the
     * node under.
     * @return the object representing the result of the evaluation.
     */
     public Object evaluate(Environment env)
     {
       switch(token.getType())
       {
         case INT:
          return Integer.valueOf(token.getValue());
         case REAL:
          return Double.valueOf(token.getValue());
         case TRUE:
          return Boolean.valueOf(true);
         case FALSE:
          return Boolean.valueOf(false);
         case ID:
          Object val = env.lookup(token);
          if (val == null)
            System.out.println("Undefined variable " + token.getValue());
          return val;
         default:
          return token;
        }
     }
 }
