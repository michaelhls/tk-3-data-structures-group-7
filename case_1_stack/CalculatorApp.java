// program utama kalkulator, mengatur alur & tampilan. No. 4

package case_1_stack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.Stack;

public class CalculatorApp {

    // Helper untuk menampilkan token (private static di class ini)
    private static void displayTokens(final String name, final Collection<Token> tokens) {
         if (tokens == null) {
             System.out.println(name + ": [Gagal]"); // Tampilkan jika null
             return;
         }
         if (tokens.isEmpty()){
             System.out.println(name + ": [Kosong]"); // Tampilkan jika kosong
             return;
         }
        System.out.print(name + ": ");
        // Gunakan iterator atau for-each loop yang aman untuk collection
        for (final Token token : tokens) {
            System.out.print(token.toString() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Kalkulator Ekspresi Infix ---");
        System.out.println("Masukkan ekspresi infix matematika:");
        String infixExpression = scanner.nextLine();

        // 1. Validasi dan Tokenisasi (Tugas 1 via InfixValidator)
        System.out.println("\n1. Memvalidasi dan Tokenisasi...");
        Stack<Token> infixTokens = InfixValidator.tokenizeAndValidate(infixExpression);

        if (infixTokens == null) {
            System.err.println("\nEkspresi infix tidak valid. Program berhenti.");
            scanner.close();
            return;
        }
        displayTokens("   Infix Tokens", infixTokens);

        // 2. Konversi (Tugas 2 via InfixConverter)
        System.out.println("\n2. Mengonversi ke Postfix dan Prefix...");
        Stack<Token> postfixTokens = InfixConverter.toPostfix(infixTokens);
        ArrayList<Token> prefixTokens = InfixConverter.toPrefix(infixTokens);

        displayTokens("   Postfix", postfixTokens);
        displayTokens("   Prefix ", prefixTokens); // Perhatikan spasi agar rata

        // 3. Evaluasi (Tugas 3 via PostfixEvaluator)
        System.out.println("\n3. Mengevaluasi Ekspresi (menggunakan Postfix)...");
        // Pastikan postfix berhasil dibuat sebelum evaluasi
        Double result = null;
        if (postfixTokens != null) {
             result = PostfixEvaluator.evaluate(postfixTokens);
        } else {
            System.err.println("   Evaluasi dibatalkan karena konversi Postfix gagal.");
        }


        // 4. Tampilkan Hasil Akhir (Tugas 4)
        System.out.println("\n--- Hasil Akhir ---");
        System.out.println("Ekspresi Infix Asli: " + infixExpression);
        if (result != null) {
            System.out.println("Hasil Evaluasi     : " + result);
        } else {
            System.out.println("Hasil Evaluasi     : Gagal (lihat pesan error di atas).");
        }
        System.out.println("-------------------");

        scanner.close();
    }
}