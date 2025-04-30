// NOTE: Hanya berlaku untuk Java 11+

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;
import java.util.List;
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

  public int getPrecedence() {
    switch (this.token) {
      case "+":
      case "-": return 1;
      case "*":
      case "/": return 2;
      case "^": return 3;
      default: return -1;
    }
  }

  public TokenType getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return this.token;
  }
}

public class InfixConversion {
  private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+[\\.\\,]?\\d*");  

  private static boolean validatePreviousNumber(final Stack<Token> tokens) {
    if (!tokens.isEmpty()) {
      return tokens.peek().getType() != TokenType.OPERAND;
    }

    return true;
  }

  private static boolean validatePreviousOperators(final Stack<Token> tokens) {
    if (tokens.isEmpty()) {
      return false;
    }

    final TokenType previousTokenType = tokens.peek().getType();

    return previousTokenType == TokenType.OPERAND || previousTokenType == TokenType.CLOSING_BRACKET;
  }

  private static Stack<Token> toInfix(String expression) {
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
            return null;
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
            return null;
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
          return null;
        }

        final String number = numberMatcher.group();

        tokens.add(new Token(number, TokenType.OPERAND));

        expression = expression.substring(number.length());
        unary = false;

        continue;
      }

      return null;
    }

    return brackets == 0 && validatePreviousOperators(tokens) ? tokens : null;
  }

  private static Stack<Token> infixToPostfix(final Stack<Token> infixTokens) {
    final Stack<Token> postfixTokens = new Stack<>();
    final Stack<Token> operators = new Stack<>();

    for (final Token token: infixTokens) {
      switch (token.getType()) {
        case TokenType.OPERAND: {
          postfixTokens.add(token);
          break;
        }

        case TokenType.OPERATOR: {
          final int tokenPrecedence = token.getPrecedence();

          while (!operators.isEmpty() && operators.peek().toString().equals('(') && tokenPrecedence <= operators.peek().getPrecedence()) {
            postfixTokens.add(operators.pop());
          }
        }

        case TokenType.OPENING_BRACKET: {
          operators.add(token);
          break;
        }

        case TokenType.CLOSING_BRACKET: {
          while (!operators.isEmpty() && operators.peek().toString().equals('(')) {
            postfixTokens.add(operators.pop());
          }

          if (operators.isEmpty()) {
            return null;
          }

          operators.pop();
          break;
        }
      }
    }

    while (!operators.isEmpty()) {
      postfixTokens.add(operators.pop());
    }

    return postfixTokens;
  }

  private static void addPrefixTokens(Stack<ArrayList<Token>> prefixTokens, Stack<Token> operators) {
    final ArrayList<Token> newPrefixTokens = new ArrayList<>();

    newPrefixTokens.add(operators.pop());
    newPrefixTokens.addAll(prefixTokens.pop());
    newPrefixTokens.addAll(prefixTokens.pop());

    prefixTokens.add(newPrefixTokens);
  }

  private static ArrayList<Token> infixToPrefix(final Stack<Token> infixTokens) {
    final Stack<ArrayList<Token>> prefixTokens = new Stack<>();
    final Stack<Token> operators = new Stack<>();

    for (final Token token: infixTokens) {
      switch (token.getType()) {
        case TokenType.OPERAND: {
          prefixTokens.add(new ArrayList<>(List.of(token)));
          break;
        }

        case TokenType.OPERATOR: {
          final int tokenPrecedence = token.getPrecedence();

          while (!operators.isEmpty() && operators.peek().toString().equals('(') && tokenPrecedence <= operators.peek().getPrecedence()) {
            try {
              addPrefixTokens(prefixTokens, operators);
            } catch (EmptyStackException e) {
              return null;
            }
          }
        }

        case TokenType.OPENING_BRACKET: {
          operators.add(token);
          break;
        }

        case TokenType.CLOSING_BRACKET: {
          while (!operators.isEmpty() && operators.peek().toString().equals('(')) {
            try {
              addPrefixTokens(prefixTokens, operators);
            } catch (EmptyStackException e) {
              return null;
            }
          }

          if (operators.isEmpty()) {
            return null;
          }

          operators.pop();
          break;
        }
      }
    }

    while (!operators.isEmpty()) {
      try {
        addPrefixTokens(prefixTokens, operators);
      } catch (EmptyStackException e) {
        return null;
      }
    }

    return prefixTokens.isEmpty() ? null : prefixTokens.pop();
  }

  private static void displayTokens(final String name, final Collection<Token> tokens) {
    System.out.print(name + ": ");

    for (final Token token: tokens) {
      System.out.print(token.toString() + " ");
    }

    System.out.println();
  }
  
  public static void main(String[] args) {
    final String infixExpression = "5 + 20 - 5 * (40 * 1)";
    
    final Stack<Token> infixTokens = toInfix(infixExpression);

    if (infixTokens == null) {
      System.err.println("Error: Ekspresi infix belum valid.");
      return;
    }

    final Stack<Token> postfixTokens = infixToPostfix(infixTokens);

    if (postfixTokens == null) {
      System.err.println("Error: Tidak dapat mengonversi infix ke postfix.");
      return;
    }

    final ArrayList<Token> prefixTokens = infixToPrefix(infixTokens);

    if (prefixTokens == null) {
      System.err.println("Error: Tidak dapat mengonversi infix ke prefix.");
      return;
    }

    displayTokens("Infix", infixTokens);
    displayTokens("Postfix", postfixTokens);
    displayTokens("Prefix", prefixTokens);
  }
}