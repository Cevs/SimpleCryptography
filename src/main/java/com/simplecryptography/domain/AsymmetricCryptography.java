package com.simplecryptography.domain;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class AsymmetricCryptography {

    private Cipher cipher;
    private String privateKeyPath = "KeyPair/privateKey";
    private String publicKeyPath = "KeyPair/publicKey";
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public AsymmetricCryptography() { }

    public void Initialize() throws NoSuchAlgorithmException, NoSuchPaddingException, Exception{
        this.cipher = Cipher.getInstance("RSA");
        this.privateKey = getPrivate(privateKeyPath);
        this.publicKey = getPublic(publicKeyPath);
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

    public void encryptFile(byte[] input, File output, PrivateKey privateKey) throws IOException, GeneralSecurityException{
        this.cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        writeToFile(output, this.cipher.doFinal(input));
    }

    public void decryptFile(byte[] input, File output, PublicKey publicKey) throws IOException, GeneralSecurityException{
        this.cipher.init(Cipher.DECRYPT_MODE, publicKey);
        writeToFile(output, this.cipher.doFinal(input));
    }

    private void writeToFile(File output, byte[] toWrite) throws IllegalBlockSizeException, BadPaddingException, IOException{
        FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }



    public byte[] getFileInBytes(File f) throws IOException{
        FileInputStream fis = new FileInputStream(f);
        byte[] fileBytes = new byte[(int) f.length()];
        fis.read(fileBytes);
        fis.close();
        return fileBytes;
    }


    public String encryptFile(){
        String text = "Fail in encryption";
        try{
            encryptFile(getFileInBytes(new File("MyFiles/text.txt")), new File("MyFiles/text_asymmetric_encrypted.txt"), privateKey);
            text = getFileText("MyFiles/text_asymmetric_encrypted.txt");
        }catch (Exception e){}

        return text;
    }

    public String decryptFile() {
        String text = "Fail in decryption";
        try {
            decryptFile(getFileInBytes(new File("MyFiles/text_asymmetric_encrypted.txt")), new File ("MyFiles/text_asymmetric_decrypted.txt"), publicKey);
            text = getFileText("MyFiles/text_asymmetric_decrypted.txt");
        }catch (Exception e){}

        return text;
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
