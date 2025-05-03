// No. 3

package case_1_stack;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;


public class PostfixEvaluator {

    // Evaluasi postfix (public static)
    public static Double evaluate(final Stack<Token> postfixTokens) {
        if (postfixTokens == null || postfixTokens.isEmpty()) {
             System.err.println("Error Evaluasi: Ekspresi postfix kosong atau null.");
             return null;
        }

        Stack<Double> evaluationStack = new Stack<>();
        // Clone postfixTokens agar tidak merusak stack aslinya
        List<Token> tokenList = new ArrayList<>(postfixTokens); // Lebih mudah iterasi pakai List

        for (Token token : tokenList) {
            try {
                if (token.getType() == TokenType.OPERAND) {
                    evaluationStack.push(Double.parseDouble(token.toString()));
                } else if (token.getType() == TokenType.OPERATOR) {
                    if (evaluationStack.size() < 2) {
                         System.err.println("Error Evaluasi: Operand tidak cukup untuk operator '" + token + "'");
                         return null;
                    }
                    double operand2 = evaluationStack.pop();
                    double operand1 = evaluationStack.pop();
                    double result;

                    switch (token.toString()) {
                        case "+": result = operand1 + operand2; break;
                        case "-": result = operand1 - operand2; break;
                        case "*": result = operand1 * operand2; break;
                        case "/":
                            if (operand2 == 0) {
                                System.err.println("Error Evaluasi: Pembagian dengan nol.");
                                return null;
                            }
                            result = operand1 / operand2; break;
                        case "^": result = Math.pow(operand1, operand2); break;
                        default:
                             System.err.println("Error Evaluasi: Operator tidak dikenal '" + token + "'");
                            return null;
                    }
                    evaluationStack.push(result);
                }
                // Kurung seharusnya tidak ada di postfix
                else {
                    System.err.println("Error Evaluasi: Token tidak valid dalam postfix '" + token + "'");
                    return null;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error Evaluasi: Gagal parsing operand '" + token + "'");
                return null;
            } catch (EmptyStackException e) {
                System.err.println("Error Evaluasi: Stack kosong saat pop operand untuk '" + token + "'");
                return null;
            }
        }

        if (evaluationStack.size() == 1) {
            return evaluationStack.pop();
        } else {
             System.err.println("Error Evaluasi: Stack akhir tidak valid (size: " + evaluationStack.size() + ").");
             return null;
        }
    }
}