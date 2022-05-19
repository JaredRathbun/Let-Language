package ast.nodes;

import environment.Environment;
import lexer.Token;

public class FirstOrderNode extends SyntaxNode {

    private FunNode funNode;
    private Environment closureEnv;

    public FirstOrderNode(FunNode funNode, Environment closureEnv) {
        this.funNode = funNode;
        this.closureEnv = closureEnv;
    }

    public Token getFunName() {
        return this.funNode.getName();
    }

    public Environment getEnvironment() {
        return this.closureEnv;
    }

    public FunNode getFunNode() {
        return funNode;
    }

    public Token getID() {
        return ((LambdaNode) funNode.getLambdaExpression()).getVar();
    }

    @Override
    public Object evaluate(Environment env) {
        /* Update the global environment with the name of the function and the
        closure. */
        env.updateEnvironment(funNode.getName(), closureEnv);
        return funNode.evaluate(closureEnv);
    }
    
}
