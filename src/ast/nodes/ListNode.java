package ast.nodes;

import java.util.LinkedList;
import lexer.TokenType;
import environment.Environment;

/**
 * This node represents the list structure.
 * @author Zach Kissel
 */
 public class ListNode extends SyntaxNode
 {
   private LinkedList<TokenNode> entries;

   /**
    * Constructs a new list syntax node.
    * @param entries the linked list of syntax node entries.
    */
    public ListNode(LinkedList<TokenNode> entries)
    {
      this.entries = entries;
    }

    /**
     * Evaluate the node.
     * @param env the executional environment we should evaluate the
     * node under.
     * @return the object representing the result of the evaluation.
     */
     public Object evaluate(Environment env)
     {
        Object currVal;
        Object firstVal;
        LinkedList<Object> lst = new LinkedList<>();

        TokenType type;

        // Handle the empty list.
        if (entries.size() == 0)
          return lst;

        // The type of the list is the type of the first element
        // of the list.
        firstVal = entries.getFirst().evaluate(env);

        if (firstVal instanceof TokenNode)
        {
            TokenNode tok = (TokenNode)firstVal;
            firstVal = tok.evaluate(env);
            return firstVal;
        }
        else if (firstVal instanceof Integer ||
                 firstVal instanceof Double)
            lst.add(firstVal);
        else if (firstVal instanceof LinkedList)
        {
          System.out.println("Nested lists not supported.");
          return null;
        }
        else
        {
          System.out.println("Unknown list type.");
          return null;
        }

        // Walk the list evaluating each node if the node
        // is of the correct type, we add it to the current list.
        for (int i = 1; i < entries.size(); i++)
        {
          currVal = entries.get(i).evaluate(env);

          if (!(currVal instanceof Integer) && !(currVal instanceof Double) &&
              !(currVal instanceof LinkedList))
          {
            System.out.println("Unknown element type.");
            return null;
          }

          if (firstVal.getClass() != currVal.getClass())
          {
            System.out.println("Mixed mode list not supported.");
            return null;
          }

          lst.add(currVal);
        }
        return lst;
     }
 }
