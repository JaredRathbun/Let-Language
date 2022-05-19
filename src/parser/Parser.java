package parser;

import java.util.LinkedList;
import lexer.Lexer;
import lexer.TokenType;
import lexer.Token;
import ast.SyntaxTree;
import ast.nodes.*;
import environment.Environment;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Implements a generic super class for parsing files.
 * 
 * @author Zach Kissel
 */
public class Parser {
  private Lexer lex; // The lexer for the parser.
  private boolean errorFound; // True if ther was a parser error.
  private boolean doTracing; // True if we should run parser tracing.
  private Token nextTok; // The current token being analyzed.

  /**
   * Constructs a new parser for the file {@code source} by
   * setting up lexer.
   * 
   * @param src the source code file to parse.
   * @throws FileNotFoundException if the file can not be found.
   */
  public Parser(File src) throws FileNotFoundException {
    lex = new Lexer(src);
    errorFound = false;
    doTracing = false;
  }

  /**
   * Construct a parser that parses the string {@code str}.
   * 
   * @param str the code to evaluate.
   */
  public Parser(String str) {
    lex = new Lexer(str);
    errorFound = false;
    doTracing = false;
  }

  /**
   * Turns tracing on an off.
   */
  public void toggleTracing() {
    doTracing = !doTracing;
  }

  /**
   * Determines if the program has any errors that would prevent
   * evaluation.
   * 
   * @return true if the program has syntax errors; otherwise, false.
   */
  public boolean hasError() {
    return errorFound;
  }

  /**
   * Parses the file according to the grammar.
   * 
   * @return the abstract syntax tree representing the parsed program.
   */
  public SyntaxTree parse() {
    SyntaxTree ast;

    nextToken(); // Get the first token.
    ast = new SyntaxTree(evalProg()); // Start processing at the root of the tree.

    if (nextTok.getType() != TokenType.EOF)
      logError("Parse error, unexpected token " + nextTok);
    return ast;
  }

  /************
   * Private Methods.
   *
   * It is important to remember that all of our non-terminal processing methods
   * maintain the invariant that each method leaves the next unprocessed token
   * in {@code nextTok}. This means each method can assume the value of
   * {@code nextTok} has not yet been processed when the method begins.
   ***********/

  /**
   * Method to handle the expression non-terminal
   *
   * <expr> -> let <id> := <expr> in <expr> |
   * rexpr { ( and | or ) rexpr } |
   * not rexpr |
   * if expr then expr else expr |
   * apply ( id | ( lexpr ) ) expr
   */
  private SyntaxNode evalExpr() {
    trace("Enter <expr>");
    SyntaxNode rexpr;
    TokenType op;
    SyntaxNode expr = null;

    // Swallow a parenthesis if present.
    if (nextTok.getType() == TokenType.LPAREN) {
      nextToken();
    }

    // Handle let.
    if (nextTok.getType() == TokenType.LET) {
      nextToken();
      return handleLet();
    }

    // Handle function application.
    else if (nextTok.getType() == TokenType.APPLY) {
      nextToken();
      return handleApply();
    }

    // Handle conditionals.
    else if (nextTok.getType() == TokenType.IF) {
      nextToken();
      return handleIf();
    }

    // Boolean not.
    else if (nextTok.getType() == TokenType.NOT) {
      op = nextTok.getType();
      nextToken();
      expr = evalRexpr();
      expr = new UnaryOpNode(expr, op);
    }

    // A global statement.
    else if (nextTok.getType() == TokenType.GLOBAL) {
      nextToken();
      return handleGlobal();
    } 
    
    else if (nextTok.getType() == TokenType.LAMBDA) {
      return evalLambdaExpr();
    }

    else // and/or.
    {
      expr = evalRexpr();

      while (nextTok.getType() == TokenType.AND ||
          nextTok.getType() == TokenType.OR) {
        op = nextTok.getType();
        nextToken();
        rexpr = evalRexpr();
        expr = new BinOpNode(expr, op, rexpr);
      }
    }
    trace("Exit <expr>");

    return expr;
  }

  /**
   * Method to handle the factor non-terminal.
   *
   * <factor> -> <id> | <int> | <real> | ( <expr> )
   * | list ( [ (id | num) {, (id | num)}])
   * | tuple ( [(id | num | factor) {, (id, num | factor)}] )
   * | true | false
   */
  private SyntaxNode evalFactor() {
    trace("Enter <factor>");
    SyntaxNode fact = null;

    if (nextTok.getType() == TokenType.ID ||
        nextTok.getType() == TokenType.INT ||
        nextTok.getType() == TokenType.REAL ||
        nextTok.getType() == TokenType.TRUE ||
        nextTok.getType() == TokenType.FALSE) {
      fact = new TokenNode(nextTok);
      nextToken();
    } else if (nextTok.getType() == TokenType.LIST) {
      return evalListExpr();
    } else if (nextTok.getType() == TokenType.TUPLE) {
      return evalTupleExpr();
    } else if (nextTok.getType() == TokenType.LST_HD) {
      nextToken();
      fact = evalFactor();
      return new HeadNode(fact);
    } else if (nextTok.getType() == TokenType.LST_TL) {
      nextToken();
      fact = evalFactor();
      return new TailNode(fact);
    } else if (nextTok.getType() == TokenType.LPAREN) {
      nextToken();
      fact = evalExpr();
      if (nextTok.getType() == TokenType.RPAREN)
        nextToken();
      else
        logError("Expected \")\" received " + nextTok + ".");
    } else {
      logError("Unexpected token " + nextTok);
      System.exit(1);
    }

    trace("Exit <factor>");
    return fact;
  }


  /**
   * This method evaluates a first order Lambda expression.
   * 
   * @param funName name of the function attached to the Lambda expression.
   * @return a SyntaxNode representing the expression, with a function name and a
   *         closure environment.
   */
  private SyntaxNode evalFirstOrderLambda(Token funName) {
    assert (funName != null);

    trace("Enter firstOrderLambda");

    SyntaxNode returnNode = null;
    LambdaNode lNode = null;
    Environment closureEnv = new Environment();

    if (nextTok.getType() == TokenType.LPAREN) {
      nextToken();
      lNode = (LambdaNode) evalLambdaExpr();
      nextToken();

      closureEnv.updateEnvironment(lNode.getVar(), lNode.getExpr());
      returnNode = new FirstOrderNode(new FunNode(funName, lNode), closureEnv);
    }

    trace("Exit firstOrderLambda");
    return returnNode;
  }


  /**
   * Method to hand the fun non-terminal.
   *
   * <fun> -> fun <id> <lexpr> | <expr>
   */
  private SyntaxNode evalFun() {
    // Function definition.
    if (nextTok.getType() == TokenType.FUN) {
      nextToken();
      return handleFun();
    } else // Just an expression.
    {
      return evalExpr();
    }
  }

  /**
   * This method handles a lambda expression.
   * lambda <id> => <expr>
   * 
   * @return a SyntaxNode representing the lambda expression.
   */
  private SyntaxNode evalLambdaExpr() {
    trace("Enter evalLambdaExpr");
    Token var;
    SyntaxNode expr;

    if (nextTok.getType() == TokenType.LAMBDA) {
      nextToken();
      if (nextTok.getType() == TokenType.ID) {
        var = nextTok;
        nextToken();
        if (nextTok.getType() == TokenType.TO) {
          nextToken();
          if (nextTok.getType() == TokenType.LPAREN) {
            nextToken();
            expr = evalLambdaExpr();
            if (nextTok.getType() == TokenType.RPAREN) {
              nextToken();
            } else {
              logError("Closing paren expected.");
            }
          } else {
            expr = evalExpr();
          }
          trace("Exit <evalLambdaExpr>");
          return new LambdaNode(var, expr);
        } else {
          logError("Expected =>.");
        }
      } else {
        logError("Lambda expressions require parameters.");
      }
    } else if (nextTok.getType() == TokenType.TO) {
      nextToken();
      if (nextTok.getType() == TokenType.ID) {
        var = nextTok;
        nextToken();
        if (nextTok.getType() == TokenType.TO) {
          nextToken();
          expr = evalExpr();

          trace("Exit <evalLambdaExpr>");
          return new LambdaNode(var, expr);
        } else {
          logError("Expected =>.");
        }
      } else {
        logError("Lambda expressions require parameters.");
      }
    }

    trace("Exit <evalLambdaExpr>");
    return null;
  }

  /**
   * Method to handle the listExpr non-terminal.
   *
   * <listExpr> -> list ( [ (id | num) {, (id | num)}] )
   */
  private SyntaxNode evalListExpr() {
    LinkedList<TokenNode> entries = new LinkedList<>();
    ListNode lst = null;

    trace("Enter <listExpr>");

    if (nextTok.getType() == TokenType.LIST) {
      nextToken();
      if (nextTok.getType() == TokenType.LPAREN) {
        nextToken();

        // We could have an empty list.
        if (nextTok.getType() == TokenType.RPAREN) {
          lst = new ListNode(entries);
          nextToken();
          return lst;
        }
        if (nextTok.getType() == TokenType.INT ||
            nextTok.getType() == TokenType.REAL ||
            nextTok.getType() == TokenType.ID)
          entries.add(new TokenNode(nextTok));
        else {
          logError("Invalid list element.");
          return new ListNode(entries);
        }
        nextToken();
        while (nextTok.getType() == TokenType.COMMA) {
          nextToken();
          if (nextTok.getType() == TokenType.INT ||
              nextTok.getType() == TokenType.REAL ||
              nextTok.getType() == TokenType.ID)
            entries.add(new TokenNode(nextTok));
          else {
            logError("Invalid list element.");
            return new ListNode(entries);
          }
          nextToken();
        }

        // Handle the end of the list.
        if (nextTok.getType() == TokenType.RPAREN) {
          lst = new ListNode(entries);
          nextToken();
        } else {
          logError("Invalid List");
        }
      } else {
        logError("Left paren expected.");
      }
    } else {
      logError("Unexpected list expression.");
    }

    trace("Exit <listExpr>");
    return lst;
  }


  /**
   * Handles the start of the matematics expressions.
   * mexpr -> <term> {(+ | - ) <term>}
   * 
   * @return a SyntaxNode representing the expression.
   */
  private SyntaxNode evalMexpr() {
    SyntaxNode expr = null;
    SyntaxNode rterm = null;
    TokenType op;

    expr = evalTerm();

    while (nextTok.getType() == TokenType.ADD ||
        nextTok.getType() == TokenType.SUB) {
      op = nextTok.getType();
      nextToken();
      rterm = evalTerm();
      expr = new BinOpNode(expr, op, rterm);
    }

    return expr;
  }


    /**
   * Method to handle the program non-terminal.
   *
   * <prog> -> <expr> { <expr> }
   */
  private SyntaxNode evalProg() {
    LinkedList<SyntaxNode> exprs = new LinkedList<>();

    trace("Enter <prog>");
    while (nextTok.getType() != TokenType.EOF)
      exprs.add(evalFun());
    trace("Exit <prog>");
    return new ProgNode(exprs);
  }

  /**
   * Method to handle the term non-terminal.
   *
   * <term> -> <factor> {( * | /) <factor>}
   */
  private SyntaxNode evalTerm() {
    SyntaxNode rfact;
    TokenType op;
    SyntaxNode term;

    trace("Enter <term>");
    term = evalFactor();

    while (nextTok.getType() == TokenType.MULT ||
        nextTok.getType() == TokenType.DIV ||
        nextTok.getType() == TokenType.CONCAT) {
      op = nextTok.getType();
      nextToken();
      rfact = evalFactor();
      term = new BinOpNode(term, op, rfact);
    }

    trace("Exit <term>");
    return term;
  }

  /**
   * 
   * @return
   */
  private SyntaxNode evalTupleExpr() {
    LinkedList<SyntaxNode> entries = new LinkedList<>();
    TupleNode lst = null;
    boolean mixedMode = false;

    trace("Enter <tupleExpr>");

    if (nextTok.getType() == TokenType.TUPLE) {
      nextToken();
      if (nextTok.getType() == TokenType.LPAREN) {
        nextToken();

        // We could have an empty list.
        if (nextTok.getType() == TokenType.RPAREN) {
          lst = new TupleNode(entries);
          nextToken();
          return lst;
        }

        // If integer, real, id or boolean.
        if (nextTok.getType() == TokenType.INT ||
            nextTok.getType() == TokenType.REAL ||
            nextTok.getType() == TokenType.ID ||
            nextTok.getType() == TokenType.TRUE ||
            nextTok.getType() == TokenType.FALSE) {
          entries.add(new TokenNode(nextTok));
        }
        // If list.
        else if (nextTok.getType() == TokenType.LIST) {
          SyntaxNode list = evalListExpr();
          entries.add(list);
          mixedMode = true;
        } else if (nextTok.getType() == TokenType.TUPLE) {
          entries.add(evalTupleExpr());
          mixedMode = true;
        } else {
          logError("Invalid tuple element.");
          return new TupleNode(entries);
        }

        if (!mixedMode) {
          nextToken();
        }

        mixedMode = false;

        while (nextTok.getType() == TokenType.COMMA) {
          nextToken();
          if (nextTok.getType() == TokenType.INT ||
              nextTok.getType() == TokenType.REAL ||
              nextTok.getType() == TokenType.ID ||
              nextTok.getType() == TokenType.TRUE ||
              nextTok.getType() == TokenType.FALSE) {
            entries.add(new TokenNode(nextTok));
          } else if (nextTok.getType() == TokenType.LIST) {
            entries.add(evalListExpr());
            mixedMode = true;
          } else if (nextTok.getType() == TokenType.TUPLE) {
            entries.add(evalTupleExpr());
            mixedMode = true;
          } else {
            logError("Invalid tuple element.");
            return new TupleNode(entries);
          }

          if (!mixedMode) {
            nextToken();
          } else {
            mixedMode = false;
            if (nextTok.getType() == TokenType.INT ||
                nextTok.getType() == TokenType.REAL ||
                nextTok.getType() == TokenType.ID ||
                nextTok.getType() == TokenType.TRUE ||
                nextTok.getType() == TokenType.FALSE) {
              nextToken();
            }
          }
        }

        // Handle the end of the tuple.
        if (nextTok.getType() == TokenType.RPAREN) {
          lst = new TupleNode(entries);
          nextToken();
        } else {
          logError("Invalid Tuple");
        }
      } else {
        logError("Left paren expected.");
      }
    } else {
      logError("Unexpected tuple expression.");
    }

    trace("Exit <tupleExpr>");
    return lst;
  }


  /**
   * Handles relational expressions.
   * rexpr -> <mexpr> [ ( < | > | >= | <= | = ) <mexpr> ]
   * 
   * @return a SyntaxNode representing the relation expression.
   */
  private SyntaxNode evalRexpr() {
    SyntaxNode left = null;
    SyntaxNode right = null;
    TokenType op;

    left = evalMexpr();

    if (nextTok.getType() == TokenType.LT || nextTok.getType() == TokenType.LTE ||
        nextTok.getType() == TokenType.GT || nextTok.getType() == TokenType.GTE ||
        nextTok.getType() == TokenType.EQ || nextTok.getType() == TokenType.NEQ) {
      op = nextTok.getType();
      nextToken();
      right = evalMexpr();
      return new RelOpNode(left, op, right);
    }

    return left;
  }

  /**
   * This method handles function application.
   * apply <id> <expr>
   */
  private SyntaxNode handleApply() {
    trace("Enter <handleApply>");
    Token fun;
    SyntaxNode expr;

    // Process a function identifier.
    if (nextTok.getType() == TokenType.ID) {
      fun = nextTok;
      nextToken();
      expr = evalExpr();
      if (nextTok.getType() == TokenType.RPAREN) {
        // nextToken();
      }

      trace("Exit <handleApply>");
      return new ApplyNode(new TokenNode(fun), expr);
    }

    // Process a lambda expression.
    else if (nextTok.getType() == TokenType.LPAREN) {
      nextToken();

      SyntaxNode lexpr = null;
      if (nextTok.getType() == TokenType.LAMBDA) {
        lexpr = evalLambdaExpr();
        expr = evalExpr();
      } else {
        nextToken();
        SyntaxNode innerNode = handleApply();
        nextToken();
        expr = evalExpr();
        return new ApplyNode(innerNode, expr);
      }

      return new ApplyNode(lexpr, expr);

    } else {
      logError("Function name or lambda expression expected.");
    }
    return null;
  }
  
  /**
   * This method handles a function definition.
   * <id> <id> => <expr>
   * 
   * @return a function node.
   */
  private SyntaxNode handleFun() {
    Token funName;

    SyntaxNode lexpr = null;

    if (nextTok.getType() == TokenType.ID) {
      funName = nextTok;
      nextToken();

      if (nextTok.getType() == TokenType.ASSIGN) {
        nextToken();
        return evalFirstOrderLambda(funName);
      }

      Token var = null;
      SyntaxNode expr = null;
      if (nextTok.getType() == TokenType.ID) {
        var = nextTok;
        nextToken();
        if (nextTok.getType() == TokenType.TO) {
          nextToken();
          expr = evalExpr();
          lexpr = new LambdaNode(var, expr);
        } else {
          logError("Expected =>.");
        }
      }

      return new FunNode(funName, lexpr);

    } else {
      logError("Functions need names.");
    }
    return null;
  }

/**
 * 
 * @return
 */
  private SyntaxNode handleGlobal() {
    trace("Enter handleGlobal");
    LinkedList<Token> varList = null;
    LinkedList<SyntaxNode> varExprList = null;

    // Handle the identifier.
    if (nextTok.getType() == TokenType.ID) {
      // A list for all of the identifiers.
      varList = new LinkedList<>();

      // Add the first identifier seen to the list.
      varList.add(nextTok);
      nextToken();

      // While there is another comma, add the next token to the list.
      while (nextTok.getType() == TokenType.COMMA) {
        nextToken();
        varList.add(nextTok);
        nextToken();
      }

      // Handle the assignment.
      if (nextTok.getType() == TokenType.ASSIGN) {
        varExprList = new LinkedList<>();

        nextToken();
        varExprList.add(evalExpr());

        while (nextTok.getType() == TokenType.COMMA) {
          nextToken();
          varExprList.add(evalExpr());
        }

        /*
         * Check to make sure there are equal number of IDs and exprs,
         * logging if they are different.
         */
        if (varList.size() != varExprList.size()) {
          logError("ID list and expr list must be of same length.");
        } else {
          return (new GlobalNode(varList, varExprList));
        }
      } else {
        logError("Assignment expected.");
      }
    } else {
      logError("Identifier expected.");
    }

    trace("Exit handleGlobal");

    return null;
  }

  /**
   * This method handles conditionals.
   * if <expr> then <expr> else <expr>
   */
  private SyntaxNode handleIf() {

    SyntaxNode cond;
    SyntaxNode trueBranch;
    SyntaxNode falseBranch;

    cond = evalExpr();

    if (nextTok.getType() == TokenType.THEN) {
      nextToken();
      trueBranch = evalExpr();
      if (nextTok.getType() == TokenType.ELSE) {
        nextToken();
        falseBranch = evalExpr();
        return new IfNode(cond, trueBranch, falseBranch);
      } else {
        logError("Else expected.");
      }
    } else {
      logError("Expected then.");
    }
    return null;
  }

  /**
   * This method handles a let expression
   * <id> := <expr> in <expr>
   * 
   * @return a let node.
   */
  private SyntaxNode handleLet() {
    LinkedList<Token> varList = null;
    LinkedList<SyntaxNode> varExprList = null;
    SyntaxNode expr;

    trace("enter handleLet");

    // Handle the identifier.
    if (nextTok.getType() == TokenType.ID) {
      // A list for all of the identifiers.
      varList = new LinkedList<>();

      // Add the first identifier seen to the list.
      varList.add(nextTok);
      nextToken();

      // While there is another comma, add the next token to the list.
      while (nextTok.getType() == TokenType.COMMA) {
        nextToken();
        varList.add(nextTok);
        nextToken();
      }

      // Handle the assignment.
      if (nextTok.getType() == TokenType.ASSIGN) {
        varExprList = new LinkedList<>();

        nextToken();
        varExprList.add(evalExpr());

        while (nextTok.getType() == TokenType.COMMA) {
          nextToken();
          varExprList.add(evalExpr());
        }

        /*
         * Check to make sure there are equal number of IDs and exprs,
         * logging if they are different.
         */
        if (varList.size() != varExprList.size()) {
          logError("ID list and expr list must be of same length.");
        }

        // Handle the in expr.
        if (nextTok.getType() == TokenType.IN) {
          nextToken();
          expr = evalExpr();
          return new LetNode(varList, varExprList, expr);
        } else {
          logError("Let expression expected in, saw " + nextTok + ".");
        }
      } else {
        logError("Let expression missing assignment!");
      }

    } else
      logError("Let expression missing variable.");
    trace("exit handleLet");
    return null;
  }

  /**
   * Logs an error to the console.
   * 
   * @param msg the error message to display.
   */
  private void logError(String msg) {
    System.err.println("Error (" + lex.getLineNumber() + "): " + msg);
    errorFound = true;
  }

  /**
   * This prints a message to the screen on if {@code doTracing} is
   * true.
   * 
   * @param msg the message to display to the screen.
   */
  private void trace(String msg) {
    if (doTracing)
      System.out.println(msg);
  }

  /**
   * Gets the next token from the lexer potentially logging that
   * token to the screen.
   */
  private void nextToken() {
    // Get next token.
    nextTok = lex.nextToken();

    // If opening comment symbol, ignore whatever is after the symbol until a
    // closing comment symbol is found.
    while (nextTok.getType() == TokenType.COMMENT) {
      nextTok = lex.nextToken();

      // While the closing comment symbol is not found, keep updating next token and
      // do not parse what is there.
      while (nextTok.getType() != TokenType.COMMENT) {

        // If only opening comment symbol is found, log error.
        if (nextTok.getType() == TokenType.EOF) {
          logError("Unsupported comment syntax.");
          System.exit(1);
        }

        nextTok = lex.nextToken();// Get next token.
      }
      nextTok = lex.nextToken(); // Get next token.
    }

    if (doTracing)
      System.out.println("nextToken: " + nextTok);

  }

}