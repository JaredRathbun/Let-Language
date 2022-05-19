package ast.nodes;

import environment.Environment;

/**
 * This node represents an if-then-else statement.
 * @author Zach Kissel
 */
 public class IfNode extends SyntaxNode
 {
   private SyntaxNode cond;
   private SyntaxNode trueBranch;
   private SyntaxNode falseBranch;

   /**
    * Constructs a new conditional node.
    * @param cond the boolean condition.
    * @param trueBranch the code in the true branch.
    * @param falseBranch the code in the false branch.
    */
   public IfNode(SyntaxNode cond, SyntaxNode trueBranch, SyntaxNode falseBranch)
   {
     this.cond = cond;
     this.trueBranch = trueBranch;
     this.falseBranch = falseBranch;
   }

   /**
    * Evaluate the node.
    * @param env the executional environment we should evaluate the
    * node under.
    * @return the object representing the result of the evaluation.
    */
   public Object evaluate(Environment env)
   {
     Object res;
     Boolean condVal;

     // Evaluate the condition.
     res = cond.evaluate(env);

     if (res == null || !(res instanceof Boolean))
     {
       System.out.println("Error: condition must evaluate to a Boolean.");
       return null;
     }

     // Evaluate the expression.
     condVal = (Boolean) res;
     if (condVal)
      return trueBranch.evaluate(env);
     return falseBranch.evaluate(env);
   }
 }
