package ast.nodes;

import java.util.LinkedList;

import environment.Environment;

/**
 * This node represents the tail node structure.
 * @author Zach Kissel
 */
 public class TailNode extends SyntaxNode
 {
   private SyntaxNode list;

   /**
    * Constructs a new list syntax node.
    * @param list the list to apply the tail operation to.
    */
    public TailNode(SyntaxNode list)
    {
      this.list = list;
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
       if (list instanceof TokenNode)
       {
         res = ((TokenNode)list).evaluate(env);
         if (res instanceof LinkedList)
         {
           LinkedList lst = (LinkedList)res;
           if (lst.size() <= 1)
           {
            System.out.println("Can't find tail of list");
            return null;
           }
          lst.remove();
          res = lst;
         }
         else
         {
           System.out.println("List expected!");
           return null;
         }
       }
       else if (list instanceof ListNode || list instanceof TailNode)
       {
         res = list.evaluate(env);
         LinkedList lst = (LinkedList)res;
         if (lst.size() <= 1)
         {
          System.out.println("Can't find tail of list.");
          return null;
         }
        lst.remove();
        res = lst;
       }
       else
       {
         System.out.println("List expected!");
         return null;
       }

       return res;
     }

 }
