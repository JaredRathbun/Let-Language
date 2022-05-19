package ast;

import ast.nodes.SyntaxNode;
import environment.Environment;

/**
 * Represents a syntax tree for the language.
 * @author Zach Kissel
 */
public class SyntaxTree
{
  SyntaxNode root;  // The root of the syntax tree.
  Environment env;  // The executional environment.

  /**
   * Constructs a new syntax tree with root {@code root}.
   * @param root the root node of the tree.
   */
  public SyntaxTree(SyntaxNode root)
  {
    this.root = root;
    this.env = new Environment();
  }

  /**
   * Construct an empty syntax tree.
   */
  public SyntaxTree()
  {
    this(null);
  }

  /**
   * Sets the root node to {@code root}
   * @param root the object to set the root node to.
   */
   public void setRootNode(SyntaxNode root)
   {
     this.root = root;
   }

   /**
    * Gets the root node of the tree.
    * @return a reference to the root node of the tree.
    */
    public SyntaxNode getRootNode()
    {
      return this.root;
    }

   /**
    * Evaluate the syntax tree.
    * @return the object representing the result of the evaluation.
    */
    public Object evaluate()
    {
      Object res = root.evaluate(env);
      return res;
    }

    /**
     * Get a copy of the current executional evironment.
     * @return the environment associated with this exeuction.
     */
    public Environment getEnvironment()
    {
      return env;
    }

    /**
     * Set the executional environment to {@code env}
     * @param env the executional environment.
     */
     public void setEnvironment(Environment env)
     {
       this.env = env;
     }
}
