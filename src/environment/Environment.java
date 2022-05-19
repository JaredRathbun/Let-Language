package environment;

import lexer.Token;
import lexer.TokenType;
import java.util.HashMap;

/**
 * A simple representation of an executional environment.
 * @author Zach Kissel
 */
public class Environment
{
  private HashMap<String, Object> env;

  /**
   * Sets up the initial environment.
   */
  public Environment()
  {
    env = new HashMap<>();
  }

  /**
   * Returns the evironment value associated with a token.
   * @param tok the token to look up the value of.
   * @return the value of {@code tok} in the environment. A value of null
   * is returned if hte token is not in the environment.
   */
  public Object lookup(Token tok)
  {
    return env.get(tok.getValue());
  }

  /**
   * Update the environment such that token {@code tok} has
   * the given value {@code val}.
   * @param tok the token to update.
   * @param val the value to associate with the token.
   */
  public void updateEnvironment(Token tok, Object val)
  {
    if (env.replace(tok.getValue(), val) == null)
      env.put(tok.getValue(), val);
  }

  /**
   * Makes a copy of the current environment.
   * @return a copy of the environment.
   */
  public Environment copy()
  {
    Environment newEnv = new Environment();
    newEnv.env.putAll(env);
    return newEnv;
  }

  /**
   * Provides a string representing the environment.
   * @return a string representation of the environment.
   */
  @Override
  public String toString()
  {
    return env.toString();
  }
}
