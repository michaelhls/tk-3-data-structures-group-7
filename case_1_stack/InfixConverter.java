// NOTE: Hanya berlaku untuk Java 11+
// No. 2

package case_1_stack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class InfixConverter {

    /**
     * Mengonversi tumpukan token infix ke tumpukan token postfix.
     * Menggunakan algoritma Shunting-yard.
     *
     * @param infixTokens Tumpukan token dalam notasi infix.
     * @return Tumpukan token dalam notasi postfix, atau null jika input null atau terjadi error.
     */
    public static Stack<Token> toPostfix(final Stack<Token> infixTokens) {
        // Handle jika input null
        if (infixTokens == null) {
            System.err.println("Error Konversi Postfix: Input infixTokens tidak boleh null.");
            return null;
        }

        // Stack untuk hasil postfix
        final Stack<Token> postfixTokens = new Stack<>();
        // Stack untuk operator sementara dan kurung buka
        final Stack<Token> operators = new Stack<>();

        // --- PERBAIKAN: Membuat salinan (clone) yang aman ---
        // Buat stack baru untuk salinan
        Stack<Token> infixClone = new Stack<>();
        // Salin semua elemen dari stack asli ke stack baru
        infixClone.addAll(infixTokens);
        // --- Akhir Perbaikan ---

        // Iterasi melalui setiap token dalam salinan infix
        for (final Token token : infixClone) {
            switch (token.getType()) {
                // Jika token adalah operand, langsung tambahkan ke output postfix
                case OPERAND:
                    postfixTokens.add(token);
                    break;

                // Jika token adalah operator
                case OPERATOR:
                    // Selama stack operator tidak kosong, DAN operator di puncak BUKAN '(',
                    // DAN precedence token saat ini <= precedence operator di puncak stack
                    while (!operators.isEmpty()
                           && operators.peek().getType() != TokenType.OPENING_BRACKET // Cek tipe, bukan string
                           && token.getPrecedence() <= operators.peek().getPrecedence()) {
                        // Pop operator dari stack operator ke output postfix
                        postfixTokens.add(operators.pop());
                    }
                    // Push operator saat ini ke stack operator
                    operators.add(token);
                    break;

                // Jika token adalah kurung buka, push ke stack operator
                case OPENING_BRACKET:
                    operators.add(token);
                    break;

                // Jika token adalah kurung tutup
                case CLOSING_BRACKET:
                    // Pop operator dari stack ke output sampai bertemu '('
                    while (!operators.isEmpty() && operators.peek().getType() != TokenType.OPENING_BRACKET) {
                        postfixTokens.add(operators.pop());
                    }

                    // Jika stack kosong sebelum menemukan '(', berarti kurung tidak cocok
                    if (operators.isEmpty()) {
                        System.err.println("Error Konversi Postfix: Kurung tutup tidak cocok dengan kurung buka.");
                        return null; // Error
                    }
                    // Pop '(' dari stack operator, tapi tidak dimasukkan ke output
                    operators.pop();
                    break;

                // Default case, seharusnya tidak terjadi jika tokenisasi benar
                default:
                     System.err.println("Error Konversi Postfix: Tipe token tidak dikenal: " + token.getType());
                     return null; // Sebaiknya tidak terjadi
            }
        } // Akhir loop for token

        // Setelah semua token infix diproses, pop semua sisa operator dari stack ke output
        while (!operators.isEmpty()) {
             // Jika masih ada kurung buka tersisa, berarti ekspresi tidak valid
             if(operators.peek().getType() == TokenType.OPENING_BRACKET) {
                 System.err.println("Error Konversi Postfix: Kurung buka tersisa di stack operator.");
                 return null; // Error: kurung tidak seimbang
             }
            postfixTokens.add(operators.pop());
        }

        // Kembalikan stack hasil konversi postfix
        return postfixTokens;
    }

    /**
     * Mengonversi tumpukan token infix ke ArrayList token prefix.
     * Menggunakan metode: Balik Infix -> Konversi ke Postfix -> Balik Hasil.
     *
     * @param infixTokens Tumpukan token dalam notasi infix.
     * @return ArrayList token dalam notasi prefix, atau null jika input null atau terjadi error.
     */
    public static ArrayList<Token> toPrefix(final Stack<Token> infixTokens) {
         // Handle jika input null
         if (infixTokens == null) {
             System.err.println("Error Konversi Prefix: Input infixTokens tidak boleh null.");
             return null;
         }

        // Langkah 1: Balik urutan token infix & Tukar kurung buka/tutup
        Stack<Token> reversedInfix = new Stack<>();
        // Gunakan List untuk membalik urutan dengan mudah
        List<Token> infixList = new ArrayList<>(infixTokens); // Salinan aman
        Collections.reverse(infixList); // Balik urutan list

        for (Token t : infixList) {
             if (t.getType() == TokenType.OPENING_BRACKET) {
                 // Ganti '(' dengan ')'
                 reversedInfix.push(new Token(")", TokenType.CLOSING_BRACKET));
             } else if (t.getType() == TokenType.CLOSING_BRACKET) {
                 // Ganti ')' dengan '('
                 reversedInfix.push(new Token("(", TokenType.OPENING_BRACKET));
             } else {
                 // Biarkan operand dan operator apa adanya
                 reversedInfix.push(t);
             }
        }


        // Langkah 2: Konversi infix yang sudah dibalik ke "postfix" (pseudo-postfix)
        // Kita bisa memanggil ulang toPostfix(reversedInfix) di sini,
        // tetapi untuk menghindari dependensi siklik atau kerumitan,
        // kita ulangi logika Shunting-yard yang sedikit dimodifikasi jika perlu
        // (Dalam kasus ini, logika standar postfix cukup)

         Stack<Token> pseudoPostfix = new Stack<>();
         Stack<Token> operators = new Stack<>();

        for (final Token token : reversedInfix) { // Proses infix yang sudah dibalik
            switch (token.getType()) {
                case OPERAND:
                    pseudoPostfix.add(token);
                    break;
                case OPERATOR:
                     // Logika sama persis dengan konversi ke postfix standar
                     while (!operators.isEmpty()
                           && operators.peek().getType() != TokenType.OPENING_BRACKET
                           && token.getPrecedence() <= operators.peek().getPrecedence()) {
                        pseudoPostfix.add(operators.pop());
                     }
                    operators.add(token);
                    break;
                case OPENING_BRACKET: // Ini adalah ')' dari infix asli
                    operators.add(token);
                    break;
                case CLOSING_BRACKET: // Ini adalah '(' dari infix asli
                    while (!operators.isEmpty() && operators.peek().getType() != TokenType.OPENING_BRACKET) {
                        pseudoPostfix.add(operators.pop());
                    }
                     if (operators.isEmpty()) {
                          System.err.println("Error Konversi Prefix: Kurung tidak cocok saat memproses infix terbalik.");
                         return null;
                     }
                    operators.pop(); // Pop '(' (yang merupakan ')' asli)
                    break;
                default:
                     System.err.println("Error Konversi Prefix: Tipe token tidak dikenal saat memproses infix terbalik: " + token.getType());
                     return null;
            }
        }
         // Pop sisa operator
         while (!operators.isEmpty()) {
             if(operators.peek().getType() == TokenType.OPENING_BRACKET) {
                 System.err.println("Error Konversi Prefix: Kurung buka tersisa di stack operator saat memproses infix terbalik.");
                 return null;
             }
             pseudoPostfix.add(operators.pop());
         }


        // Langkah 3: Balik hasil "postfix" untuk mendapatkan prefix yang sebenarnya
        ArrayList<Token> prefixTokens = new ArrayList<>(pseudoPostfix);
        Collections.reverse(prefixTokens); // Balik list hasil

         // Validasi hasil akhir (minimalis)
         if (prefixTokens.isEmpty() && !infixTokens.isEmpty()) {
             // Jika infix asli tidak kosong tapi hasil prefix kosong, kemungkinan ada error
             System.err.println("Error Konversi Prefix: Hasil prefix kosong padahal input infix tidak kosong.");
             return null;
         }

        // Kembalikan hasil konversi prefix
        return prefixTokens;
    }
}