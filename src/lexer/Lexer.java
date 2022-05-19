package lexer;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.FileReader;
import java.io.File;
import java.util.HashMap;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This file implements a basic lexical analyzer.
 * 
 * @author Zach Kissel
 */
public class Lexer {
  private BufferedReader input; // The input to the lexer.
  private char nextChar; // The next character read.
  private boolean skipRead; // Whether or not to skip the next char
                            // read.
  private long currentLineNumber; // The current line number being processed.

  // The dictionary of language keywords
  private HashMap<String, TokenType> keywords;

  private enum CharacterClass {
    LETTER, DIGIT, WHITE_SPACE, OTHER, END, COMMENT
  };

  CharacterClass nextClass; // The character class of the nextChar.

  /**
   * Constructs a new lexical analyzer whose source
   * input is a file.
   * 
   * @param file the file to open for lexical analysis.
   * @throws FileNotFoundException if the file can not be opened.
   */
  public Lexer(File file) throws FileNotFoundException {
    input = new BufferedReader(new FileReader(file));
    currentLineNumber = 1;
    loadKeywords();
  }

  /**
   * Constructs a new lexical analyzer whose source is a string.
   * 
   * @param input the input to lexically analyze.
   */
  public Lexer(String input) {
    this.input = new BufferedReader(new StringReader(input));
    currentLineNumber = 1;
    loadKeywords();
  }

  /**
   * Gets the next token from the stream.
   * 
   * @return the next token.
   */
  public Token nextToken() {
    String value = ""; // The value to be associated with the token.

    getNonBlank();
    switch (nextClass) {
      // The state where we are recognizing identifiers.
      // Regex: [A-Za-Z][0-9a-zA-z]*
      case LETTER:
        value += nextChar;
        getChar();

        // Read the rest of the identifier.
        while (nextClass == CharacterClass.DIGIT ||
            nextClass == CharacterClass.LETTER) {
          value += nextChar;
          getChar();
        }
        unread(); // The symbol just read is part of the next token.

        // This could be an identifier or a token, if it's not in
        // the keyword dictionary, it is an indentifier.
        if (keywords.containsKey(value))
          return new Token(keywords.get(value), "");
        return new Token(TokenType.ID, value);

      // The state where we are recognizing digits.
      // Regex: [0-9]+
      case DIGIT:
        value += nextChar;
        getChar();

        while (nextClass == CharacterClass.DIGIT) {
          value += nextChar;
          getChar();
        }

        if (nextChar == '.') // Decimal point.
        {
          value += nextChar;
          getChar();
          while (nextClass == CharacterClass.DIGIT) {
            value += nextChar;
            getChar();
          }
          unread();
          return new Token(TokenType.REAL, value);
        }
        unread(); // The symbol just read is part of the next token.

        return new Token(TokenType.INT, value);

      // Handles all special character symbols.
      case OTHER:
        return lookup();

      // We reached the end of our input.
      case END:
        return new Token(TokenType.EOF, "");

      // This should never be reached.
      default:
        return new Token(TokenType.UNKNOWN, "");
    }
  }

  /**
   * Get the current line number being processed.
   * 
   * @return the current line number being processed.
   */
  public long getLineNumber() {
    return currentLineNumber;
  }

  /************
   * Private Methods
   ************/

  /**
   * Processes the {@code nextChar} and returns the resulting token.
   * 
   * @return the new token.
   */
  private Token lookup() {
    String value = "";

    switch (nextChar) {
      case '#':
        return new Token(TokenType.COMMENT, "");
      case '.': // A double with just a leading dot.
        value += ".";
        getChar();
        if (nextClass != CharacterClass.DIGIT) {
          unread();
          return new Token(TokenType.UNKNOWN, "." + String.valueOf(nextChar));
        }
        while (nextClass == CharacterClass.DIGIT) {
          value += nextChar;
          getChar();
        }
        unread();
        return new Token(TokenType.REAL, value);
      case ':': // A Pascal style assignment.
        getChar();
        if (nextChar == '=')
          return new Token(TokenType.ASSIGN, "");
        else {
          unread(); // In case the character is part of a different token.
          return new Token(TokenType.UNKNOWN, ":" + String.valueOf(nextChar));
        }
      case '+':
        getChar();
        if (nextChar == '+')
          return new Token(TokenType.CONCAT, "");
        else {
          unread(); // Charater is part of a different token.
          return new Token(TokenType.ADD, "");
        }
      case '-':
        return new Token(TokenType.SUB, "");
      case '*': // Get ready for next token.
        return new Token(TokenType.MULT, "");
      case '/': // Get ready for next token.
        return new Token(TokenType.DIV, "");
      case '(':
        return new Token(TokenType.LPAREN, "");
      case ')':
        return new Token(TokenType.RPAREN, "");
      case ',':
        return new Token(TokenType.COMMA, "");
      case '=':
        getChar();
        if (nextChar == '>')
          return new Token(TokenType.TO, "");
        else {
          unread();
          return new Token(TokenType.EQ, "");
        }
      case '!':
        getChar();
        if (nextChar == '=')
          return new Token(TokenType.NEQ, "");
        else {
          unread();
          return new Token(TokenType.UNKNOWN, "");
        }
      case '>':
        getChar();
        if (nextChar == '=')
          return new Token(TokenType.GTE, "");
        else {
          unread();
          return new Token(TokenType.GT, "");
        }
      case '<':
        getChar();
        if (nextChar == '=')
          return new Token(TokenType.LTE, "");
        else {
          unread();
          return new Token(TokenType.LT, "");
        }

      default:
        return new Token(TokenType.UNKNOWN, String.valueOf(nextChar));
    }
  }

  /**
   * Gets the next character from the buffered reader. This updates
   * potentially both {@code nextChar} and {@code nextClass}.
   */
  private void getChar() {
    int c = -1;

    // Handle the unread operation.
    if (skipRead) {
      skipRead = false;
      return;
    }

    try {
      c = input.read();
    } catch (IOException ioe) {
      System.err.println("Internal error (getChar()): " + ioe);
      nextChar = '\0';
      nextClass = CharacterClass.END;
    }

    if (c == -1) // If there is no character to read, we've reached the end.
    {
      nextChar = '\0';
      nextClass = CharacterClass.END;
      return;
    }

    // Set the character and determine it's class.
    nextChar = (char) c;
    if (Character.isLetter(nextChar))
      nextClass = CharacterClass.LETTER;
    else if (Character.isDigit(nextChar))
      nextClass = CharacterClass.DIGIT;
    else if (Character.isWhitespace(nextChar))
      nextClass = CharacterClass.WHITE_SPACE;
    else
      nextClass = CharacterClass.OTHER;

    // Update the line counter for error checking.
    if (nextChar == '\n')
      currentLineNumber++;
  }

  /**
   * Gets the next non-blank character. This updates
   * potentially both {@code nextChar} and {@code nextClass}.
   */
  private void getNonBlank() {
    getChar();

    while (nextClass != CharacterClass.END &&
        Character.isWhitespace(nextChar))
      getChar();
  }

  /**
   * Save the previous character for a future read operation.
   */
  private void unread() {
    skipRead = true;
  }

  /**
   * Sets up the dictionary with all of the keywords.
   */
  private void loadKeywords() {
    keywords = new HashMap<String, TokenType>();
    keywords.put("let", TokenType.LET);
    keywords.put("in", TokenType.IN);
    keywords.put("hd", TokenType.LST_HD);
    keywords.put("tl", TokenType.LST_TL);
    keywords.put("list", TokenType.LIST);
    keywords.put("and", TokenType.AND);
    keywords.put("or", TokenType.OR);
    keywords.put("not", TokenType.NOT);
    keywords.put("fun", TokenType.FUN);
    keywords.put("apply", TokenType.APPLY);
    keywords.put("if", TokenType.IF);
    keywords.put("then", TokenType.THEN);
    keywords.put("else", TokenType.ELSE);
    keywords.put("true", TokenType.TRUE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("global", TokenType.GLOBAL);
    keywords.put("tuple", TokenType.TUPLE);
    keywords.put("lambda", TokenType.LAMBDA);
  }
}
