// No. 1
// NOTE: Hanya berlaku untuk Java 11+

package case_1_stack;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfixValidator {

    // Pattern di-private agar hanya digunakan di class ini
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(?:[.,]\\d*)?");

    // Helper validasi juga private
    private static boolean validatePreviousNumber(final Stack<Token> tokens) {
       if (!tokens.isEmpty()) {
            TokenType previousType = tokens.peek().getType();
            return previousType != TokenType.OPERAND && previousType != TokenType.CLOSING_BRACKET;
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

    // Metode utama dibuat public static agar bisa dipanggil dari luar
    public static Stack<Token> tokenizeAndValidate(String expression) {
        final Stack<Token> tokens = new Stack<>();
        long brackets = 0;
        boolean expectOperand = true;

        while (true) {
            expression = expression.stripLeading();
            if (expression.isEmpty()) {
                break;
            }

            final char character = expression.charAt(0);
            boolean isUnaryMinus = character == '-' && expectOperand;

            // Handle Angka (Operand)
            Matcher numberMatcher = NUMBER_PATTERN.matcher(expression);
             if (!isUnaryMinus && expectOperand && numberMatcher.lookingAt() && !numberMatcher.group().equals("-")) {
                 if (!validatePreviousNumber(tokens)) {
                     System.err.println("Error Validasi: Penempatan operand tidak valid.");
                     return null;
                 }
                 final String number = numberMatcher.group();
                 tokens.add(new Token(number, TokenType.OPERAND));
                 expression = expression.substring(number.length());
                 expectOperand = false;
                 continue;
             }
             // Handle unary minus
             if (isUnaryMinus) {
                 Matcher unaryNumberMatcher = NUMBER_PATTERN.matcher(expression);
                 if(unaryNumberMatcher.lookingAt()){
                      if (!validatePreviousNumber(tokens)) {
                           System.err.println("Error Validasi: Penempatan operand (unary) tidak valid.");
                           return null;
                       }
                     final String number = unaryNumberMatcher.group();
                     tokens.add(new Token(number, TokenType.OPERAND));
                     expression = expression.substring(number.length());
                     expectOperand = false;
                     continue;
                 } else if (expression.length() > 1 && expression.charAt(1) == '(') {
                     tokens.add(new Token("-1", TokenType.OPERAND));
                     tokens.add(new Token("*", TokenType.OPERATOR));
                     // Lanjut proses '(' di switch case bawah
                 } else {
                     System.err.println("Error Validasi: Penggunaan unary '-' tidak valid.");
                     return null;
                 }
                 if(unaryNumberMatcher.lookingAt() || (expression.length() > 1 && expression.charAt(1) == '(')) {
                    // Continue sudah dipanggil di atas
                 }
             }

            // Handle Operator dan Kurung
            switch (character) {
                case '+': case '-': case '*': case '/': case '^': {
                    if (expectOperand) {
                         System.err.println("Error Validasi: Operator '" + character + "' tidak di posisi yang benar.");
                        return null;
                    }
                     // Allow operator after opening bracket case e.g. (+5) -> handled by unary logic above
                    if (!validatePreviousOperators(tokens) && tokens.peek().getType() != TokenType.OPENING_BRACKET) {
                         System.err.println("Error Validasi: Operator '" + character + "' tidak mengikuti operand/kurung tutup.");
                         return null;
                    }
                    tokens.add(new Token(Character.toString(character), TokenType.OPERATOR));
                    expression = expression.substring(1);
                    expectOperand = true;
                    break;
                }
                case '(': {
                    if (!expectOperand) {
                       System.err.println("Error Validasi: Kurung buka '(' tidak di posisi yang benar.");
                       return null;
                    }
                    brackets++;
                    tokens.add(new Token(Character.toString(character), TokenType.OPENING_BRACKET));
                    expression = expression.substring(1);
                    expectOperand = true;
                    break;
                }
                case ')': {
                     if (expectOperand && (tokens.isEmpty() || tokens.peek().getType() != TokenType.OPENING_BRACKET)) {
                        System.err.println("Error Validasi: Kurung tutup ')' tidak di posisi yang benar.");
                        return null;
                    }
                     if (tokens.isEmpty() || tokens.peek().getType() == TokenType.OPERATOR){
                         System.err.println("Error Validasi: Kurung tutup ')' tidak mengikuti operand atau kurung buka.");
                         return null;
                     }
                    if (brackets <= 0) {
                        System.err.println("Error Validasi: Kurung tutup ')' berlebih.");
                        return null;
                    }
                    brackets--;
                    tokens.add(new Token(Character.toString(character), TokenType.CLOSING_BRACKET));
                    expression = expression.substring(1);
                    expectOperand = false;
                    break;
                }
                default: {
                    System.err.println("Error Validasi: Karakter tidak dikenal: '" + character + "'");
                    return null;
                }
            } // end switch
        } // end while

        if (brackets != 0) {
             System.err.println("Error Validasi: Jumlah kurung buka dan tutup tidak cocok.");
            return null;
        }
        if (expectOperand && !tokens.isEmpty()) {
             System.err.println("Error Validasi: Ekspresi berakhir secara tidak terduga.");
             return null;
        }
         if (tokens.isEmpty()){
              System.err.println("Error Validasi: Ekspresi kosong.");
              return null;
         }

        System.out.println("Validasi Infix: OK");
        return tokens; // Valid dan tokenized
    }
}