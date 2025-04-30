// NOTE: Hanya berlaku untuk Java 11+

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

enum TokenType {
  OPERAND,
  OPERATOR,
  OPENING_BRACKET,
  CLOSING_BRACKET,
}

class Token {
  private final String token;
  private final TokenType type;

  public Token(final String token, final TokenType type) {
    this.token = token;
    this.type = type;
  }

  public TokenType getTokenType() {
    return this.type;
  }

  @Override
  public String toString() {
    return this.token;
  }
}

public class InfixValidator {
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+[\\.\\,]?\\d*");  

  private static boolean validatePreviousNumber(final Stack<Token> tokens) {
    if (!tokens.isEmpty()) {
      return tokens.peek().getTokenType() != TokenType.OPERAND;
    }

    return true;
  }

  private static boolean validatePreviousOperators(final Stack<Token> tokens) {
    if (tokens.isEmpty()) {
      return false;
    }

    final TokenType previousTokenType = tokens.peek().getTokenType();

    return previousTokenType == TokenType.OPERAND || previousTokenType == TokenType.CLOSING_BRACKET;
  }

  private static boolean validateInfix(String expression) {
    final Stack<Token> tokens = new Stack<>();

    long brackets = 0;
    boolean unary = true;

    while (true) {
      expression = expression.stripLeading();

      if (expression.isEmpty()) {
        break;
      }

      final char character = expression.charAt(0);
      
      switch (character) {
        case '-': {
          if (unary) {
            break;
          }
        }

        case '+':
        case '*':
        case '/':
        case '^': {
          if (!validatePreviousOperators(tokens)) {
            return false;
          }

          tokens.add(new Token(Character.toString(character), TokenType.OPERATOR));
          expression = expression.substring(1);
          unary = true;

          continue;
        }

        case '(': {
          brackets++;

          tokens.add(new Token(Character.toString(character), TokenType.OPENING_BRACKET));
          expression = expression.substring(1);
          unary = true;

          continue;
        }

        case ')': {
          if (!validatePreviousOperators(tokens)) {
            return false;
          }

          brackets--;

          tokens.add(new Token(Character.toString(character), TokenType.CLOSING_BRACKET));
          expression = expression.substring(1);
          unary = false;

          continue;
        }
      }

      final Matcher numberMatcher = NUMBER_PATTERN.matcher(expression);

      if (numberMatcher.find()) {
        if (!validatePreviousNumber(tokens)) {
          return false;
        }

        final String number = numberMatcher.group();

        tokens.add(new Token(number, TokenType.OPERAND));

        expression = expression.substring(number.length());
        unary = false;

        continue;
      }

      return false;
    }

    return brackets == 0 && validatePreviousOperators(tokens);
  }
  
  public static void main(String[] args) {
    if (validateInfix("5 + 20 - 5 * (40 * 1)")) {
      System.out.println("OK: Ekspresi infix sudah valid.");
    } else {
      System.err.println("Error: Ekspresi infix belum valid.");
    }
  }
}