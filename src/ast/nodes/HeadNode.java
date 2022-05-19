package ast.nodes;

import java.util.LinkedList;
import environment.Environment;

/**
 * This node represents the head node structure.
 * @author Zach Kissel
 */
 public class HeadNode extends SyntaxNode
 {
   private SyntaxNode list;

   /**
    * Constructs a new list syntax node.
    * @param list a list to apply the head operation to.
    */
    public HeadNode(SyntaxNode list)
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
       Object res = null;

       if (list instanceof TokenNode)
       {
         res = ((TokenNode)list).evaluate(env);

         if (res == null)
          return res;

         if (res instanceof LinkedList)
         {
           LinkedList lst = (LinkedList)res;
           if (lst.size() == 0)
           {
            System.out.println("empty list!");
            res = null;
           }
          res = lst.getFirst();
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
         if (res == null)
          return res;

         LinkedList lst = (LinkedList)res;
         if (lst.size() == 0)
         {
          System.out.println("empty list!");
          return null;
         }
        res = lst.getFirst();
       }
       else
       {
         System.out.println("List expected!");
         return null;
       }

       return res;
     }
 }
