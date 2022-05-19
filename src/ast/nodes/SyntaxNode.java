package ast.nodes;

import environment.Environment;

/**
 * Represents the node of a syntax tree. Each node is slightly
 * different therefore, the class is abstract each derived class
 * is responsible for implementing the evaluate method for that
 * node subtype.
 *
 * @author Zach Kissel
 */
public abstract class SyntaxNode
{

  /**
   * Evaluate the node.
   * @param env the executional environment we should evaluate the
   * node under.
   * @return the object representing the result of the evaluation.
   */
  public abstract Object evaluate(Environment env);
}
