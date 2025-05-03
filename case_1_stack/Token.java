// merepresentasikan sebuah token dalam ekspresi matematika.

package case_1_stack;

public class Token {
    private final String token;
    private final TokenType type;

    public Token(final String token, final TokenType type) {
        // Mengganti koma dengan titik untuk konsistensi parsing double
        this.token = token.replace(',', '.');
        this.type = type;
    }

    public int getPrecedence() {
        switch (this.token) {
            case "+":
            case "-": return 1;
            case "*":
            case "/": return 2;
            case "^": return 3; // Eksponensial
            default: return -1; // Bukan operator atau tidak dikenal
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