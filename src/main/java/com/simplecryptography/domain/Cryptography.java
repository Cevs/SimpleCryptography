package com.simplecryptography.domain;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class Cryptography {

    private Cipher asymmetricCipher;
    private Cipher symmetricCipher;
    private String privateKeyPath = "KeyPair/privateKey";
    private String publicKeyPath = "KeyPair/publicKey";
    private String secretKeyPath = "KeyPair/secretKey";
    private String fileDigestPath = "MyFiles/digest.txt";
    private String sourceFilePath = "MyFiles/text.txt";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey secretKey;
    private static final int SYMMETRIC = 1;
    private static final int ASYMMETRIC = 0;

    public Cryptography() { }

    public void InitializeAsymmetric() throws NoSuchAlgorithmException, NoSuchPaddingException, Exception{
        this.asymmetricCipher = Cipher.getInstance("RSA");
        this.privateKey = getPrivate(privateKeyPath);
        this.publicKey = getPublic(publicKeyPath);
    }

    public void InitializeSymmetric() throws NoSuchAlgorithmException, NoSuchPaddingException, Exception{
        this.symmetricCipher = Cipher.getInstance("AES");
        this.secretKey = getSecret(secretKeyPath);
    }


    private SecretKey getSecret(String path) throws Exception{
        byte[] keyBytes = Files.readAllBytes(new File(path).toPath());
        SecretKey originalKey = new SecretKeySpec(keyBytes, "AES");
        return  originalKey;
    }

    private PrivateKey getPrivate(String path) throws Exception{
        byte[] keyBytes = Files.readAllBytes(new File(path).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private PublicKey getPublic(String path) throws Exception{
        byte[] keyBytes = Files.readAllBytes(new File(path).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    public void encryptFileAsymmetric(byte[] input, File output, PrivateKey privateKey) throws IOException, GeneralSecurityException{
        this.asymmetricCipher.init(Cipher.ENCRYPT_MODE, privateKey);
        writeToFile(output, Base64.getEncoder().encodeToString(asymmetricCipher.doFinal(input)));
    }

    public void decryptFileAsymmetric(String text, File output, PublicKey publicKey) throws IOException, GeneralSecurityException{
        this.asymmetricCipher.init(Cipher.DECRYPT_MODE, publicKey);
        String t2 =  new String (asymmetricCipher.doFinal(Base64.getDecoder().decode(text)));
        writeToFile(output,new String (asymmetricCipher.doFinal(Base64.getDecoder().decode(text))));
    }

    public void encryptFileSymmetric(byte[] input, File output, SecretKey secretKey) throws IOException, GeneralSecurityException{
        this.symmetricCipher.init(Cipher.ENCRYPT_MODE,secretKey);
        writeToFile(output, Base64.getEncoder().encodeToString(symmetricCipher.doFinal(input)));
    }

    public void decryptFileSymmetric(String text, File output, SecretKey secretKey) throws IOException, GeneralSecurityException{
        this.symmetricCipher.init(Cipher.DECRYPT_MODE, secretKey);
        writeToFile(output, new String (symmetricCipher.doFinal(Base64.getDecoder().decode(text))));
    }



    private void writeToFile(File output, String text) throws IllegalBlockSizeException, BadPaddingException, IOException{

        FileWriter fileWriter = new FileWriter(output);
        fileWriter.write(text);
        fileWriter.close();

    }

    public byte[] getFileInBytes(File f) throws IOException{
        FileInputStream fis = new FileInputStream(f);
        byte[] fileBytes = new byte[(int) f.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }

    public String encryptFile(int type){
        String text = "Fail in encryption";
        try{
            if(type == ASYMMETRIC){
                encryptFileAsymmetric(getFileInBytes(new File(sourceFilePath)), new File("MyFiles/text_asymmetric_encrypted.txt"), privateKey);
                text = getFileText("MyFiles/text_asymmetric_encrypted.txt");
            }
            else if(type==SYMMETRIC){
                encryptFileSymmetric(getFileInBytes(new File(sourceFilePath)),new File("MyFiles/text_symmetric_encrypted.txt"), secretKey);
                text = getFileText("MyFiles/text_symmetric_encrypted.txt");
            }

        }catch (Exception e){}

        return text;
    }

    public String decryptFile(int type) {
        String text = "Fail in decryption";
        try {
            if(type == ASYMMETRIC){

                decryptFileAsymmetric(getFileText("MyFiles/text_asymmetric_encrypted.txt"), new File ("MyFiles/text_asymmetric_decrypted.txt"), publicKey);
                text = getFileText("MyFiles/text_asymmetric_decrypted.txt");
            }
            else if (type==SYMMETRIC){
                decryptFileSymmetric(getFileText("MyFiles/text_symmetric_encrypted.txt"),new File("MyFiles/text_symmetric_decrypted.txt"), secretKey);
                text = getFileText("MyFiles/text_symmetric_decrypted.txt");
            }

        }catch (Exception e){}

        return text;
    }

    public String digestFile() throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(getFileInBytes(new File(sourceFilePath)));
        String encoded = Hex.encodeHexString(hash);
        writeToFile(new File(fileDigestPath), encoded);

        return encoded;
    }

    private String getFileText(String path) throws  IOException{
        String readLine = "";
        String text = "";
        File file = new File(path);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        while((readLine = bufferedReader.readLine()) != null){
            text += readLine;
        }

        return  text;
    }

}
