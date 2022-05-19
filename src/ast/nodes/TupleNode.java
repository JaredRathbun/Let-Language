package ast.nodes;

import java.util.LinkedList;

import environment.Environment;
import lexer.TokenType;

public class TupleNode extends SyntaxNode {

    private LinkedList<SyntaxNode> entries;

    public TupleNode(LinkedList<SyntaxNode> entries) {
        this.entries = entries;
    }

    @Override
    public Object evaluate(Environment env) {
        Object currVal;
        Object firstVal;
        LinkedList<Object> lst = new LinkedList<>();
        TokenType type;

        // Handle the empty list.
        if (entries.size() == 0)
            return lst;

        // Get first type of the tuple.
        firstVal = entries.getFirst().evaluate(env);

        if (firstVal instanceof TokenNode) {
            TokenNode tok = (TokenNode) firstVal;
            firstVal = tok.evaluate(env);
            return firstVal;
        } else if (firstVal instanceof Integer ||
                firstVal instanceof Double ||
                firstVal instanceof LinkedList || 
                firstVal instanceof Boolean) {
            lst.add(firstVal);
        } else {
            System.out.println("Unknown tuple type.");
            return null;
        }

        // Walk the tuple evaluating each node if the node
        // is of the correct type, we add it to the current list.
        for (int i = 1; i < entries.size(); i++) {
            currVal = entries.get(i).evaluate(env);

            if (!(currVal instanceof Integer) && !(currVal instanceof Double) &&
                    !(currVal instanceof LinkedList) && !(currVal instanceof Boolean)) {
                System.out.println("Unknown element type in tuple.");
                return null;
            }

            lst.add(currVal);
        }
        return lst;
    }

}
