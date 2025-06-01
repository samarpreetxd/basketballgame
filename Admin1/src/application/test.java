package application;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class test {
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int IV_LENGTH = 12;
    private static final int T_LEN = 128; // Tag length in bits (128 è consigliato)
    
    private static final SecretKey key = generateKey(); // Genera una chiave AES sicura

    private static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Chiave a 256 bit (se supportato dalla JVM)
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore nella generazione della chiave AES", e);
        }
    }

    public String encrypt(String text) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            GCMParameterSpec ivSpec = new GCMParameterSpec(T_LEN, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            byte[] ciphertext = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            byte[] encrypted = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la crittografia", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[IV_LENGTH];
            byte[] ciphertext = new byte[decoded.length - IV_LENGTH];

            System.arraycopy(decoded, 0, iv, 0, IV_LENGTH);
            System.arraycopy(decoded, IV_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            GCMParameterSpec ivSpec = new GCMParameterSpec(T_LEN, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);

            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la decrittografia", e);
        }
    }

    public static void main(String[] args) {
        test aes = new test();
        String originalText = "Messaggio segreto";
        String encryptedText = aes.encrypt(originalText);
        String decryptedText = aes.decrypt(encryptedText);

        System.out.println("Testo originale: " + originalText);
        System.out.println("Testo cifrato: " + encryptedText);
        System.out.println("Testo decifrato: " + decryptedText);
    }
}
