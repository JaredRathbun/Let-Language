package lexer;

/**
 * An enumeration of token types.
 */
public enum TokenType {
  /**
   * An integer token.
   */
  INT,

  /**
   * A real number token.
   */
  REAL,

  /**
   * An identifier token.
   */
  ID,

  /**
   * Add operation token.
   */
  ADD,

  /**
   * Subtract operation token.
   */
  SUB,

  /**
   * Multiply operation token.
   */
  MULT,

  /**
   * Divide operation token.
   */
  DIV,

  /**
   * A let token
   */
  LET,

  /**
   * An in token.
   */
  IN,

  /**
   * Assign operation.
   */
  ASSIGN,

  /**
   * A left parenthesis.
   */
  LPAREN,

  /**
   * A right parenthesis
   */
  RPAREN,

  /**
   * A list keyword.
   */
  LIST,

  /**
   * Head of list.
   */
  LST_HD,

  /**
   * Tail or remainder of list.
   */
  LST_TL,

  /**
   * Comma
   */
  COMMA,

  /**
   * List concatenation
   */
  CONCAT,

  /**
   * Boolean AND.
   */
  AND,

  /**
   * Boolean OR.
   */
  OR,

  /**
   * Boolean NOT.
   */
  NOT,

  /**
   * Equality.
   */
  EQ,

  /**
   * less than.
   */
  LT,

  /**
   * Greater than.
   */
  GT,

  /**
   * Less than or equal.
   */
  LTE,

  /**
   * Greater than or equal.
   */
  GTE,

  /**
   * Not equal.
   */
  NEQ,

  /**
   * Function definition.
   */
  FUN,

  /**
   * To Arrow.
   */
  TO,

  /**
   * Function Application.
   */
  APPLY,

  /**
   * An unknown token.
   */
  UNKNOWN,

  /**
   * If statement
   */
  IF,

  /**
   * Then statement.
   */
  THEN,

  /**
   * Else statement.
   */
  ELSE,

  /**
   * True value.
   */
  TRUE,

  /**
   * False value.
   */
  FALSE,

  /**
   * The end of the file token.
   */
  EOF,

  /**
   * A comment character. (#)
   */
  COMMENT,

  /**
   * A global keyword.
   */
  GLOBAL,

  /**
   * A Tuple keyword.
   */
  TUPLE,

  /**
   * A Lambda keyword.
   */
  LAMBDA
}
