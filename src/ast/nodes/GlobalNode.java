package ast.nodes;

import java.util.LinkedList;

import environment.Environment;
import lexer.Token;

/**
 * Holds the necessary identifiers and values for a global expression.
 * 
 * @author Jared Rathbun
 * @author Bernardo Santos
 */
public class GlobalNode extends SyntaxNode {

    private LinkedList<Token> varList;
    private LinkedList<SyntaxNode> varExprList;

    /**
     * Constructs a new GlobalNode.
     * 
     * @param var The list of variable identifiers.
     * @param varExpr The list of expressions.
     */
    public GlobalNode(LinkedList<Token> varList, LinkedList<SyntaxNode> 
        varExprList) {
        this.varList = varList;
        this.varExprList = varExprList;
    }
    
    /**
     * Returns the list of identifiers.
     * 
     * @return A {@code LinkedList<Token>} of identifiers.
     */
    public LinkedList<Token> getVarList() {
        return varList;
    }

    /**
     * Returns the list of expressions (values).
     * 
     * @return A {@code LinkedList<SyntaxNode>} of expressions.
     */
    public LinkedList<SyntaxNode> getVarExprList() {
        return varExprList;
    }

    /**
     * Evaluate the node.
     * 
     * @param env the executional environment we should evaluate the
     *            node under.
     * @return the object representing the result of the evaluation.
     */
    @Override
    public Object evaluate(Environment env) {
        return "GlobalNode";
    }
}
