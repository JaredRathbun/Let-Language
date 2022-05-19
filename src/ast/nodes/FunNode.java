package ast.nodes;

import lexer.Token;
import environment.Environment;

/**
 * This node represents a function (named lambda expression).
 * @author Zach Kissel
 */
 public class FunNode extends SyntaxNode
 {
   private Token name;
   private SyntaxNode lexpr;

   /**
    * Constructs a new function node which represents
    * a function declaration.
    * @param name the name of the function.
    * @param lexpr the lambda-expression associatex with the name.
    */
   public FunNode(Token name, SyntaxNode lexpr)
   {
     this.name = name;
     this.lexpr = lexpr;
   }

   /**
    * Returns the name of the function.
    * @return a Token representing the name of the function.
    */
    public Token getName()
    {
      return name;
    }

   /**
    * Returns the lambda expression associated with the name.
    * @return the root of the AST that represents the function body.
    */
   public LambdaNode getLambdaExpression()
   {
     return (LambdaNode) lexpr;
   }

   /**
    * Evaluate the node.
    * @param env the executional environment we should evaluate the
    * node under.
    * @return the object representing the result of the evaluation.
    */
   public Object evaluate(Environment env)
   {
     return "Function " + name.getValue() + ".";
   }
 }
