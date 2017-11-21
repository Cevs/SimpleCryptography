package com.simplecryptography.domain;

import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.util.Base64;

@Component
public class Keys {
    private KeyPairGenerator keyPairGenerator;
    private KeyGenerator keyGenerator;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private SecretKey secretKey;
    private int keyLength = 1024;
    private String sPrivateKey;
    private String sPublicKey;
    private String sSecretKey;

    private final static int PUBLIC_KEY = 1;
    private final static int PRIVATE_KEY = 2;
    private final static int SECRET_KEY = 3;


    public String getPrivateKey() {
        return sPrivateKey;
    }

    public String getPublicKey() {
        return sPublicKey;
    }

    public String getsSecretKey(){
        return sSecretKey;
    }


    public Keys(){}

    public void generateKeys(){
        try{

            createPublicPrivateKeys();
            createSecretKey();

            saveToFile("KeyPair/publicKey", publicKey.getEncoded());
            saveToFile("KeyPair/privateKey", privateKey.getEncoded());

            String path = "GeneratedKeys/";
            saveKeyToFile(path, sPublicKey ,PUBLIC_KEY);
            saveKeyToFile(path, sPrivateKey, PRIVATE_KEY);
            saveKeyToFile(path, sSecretKey, SECRET_KEY);


        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createPublicPrivateKeys() throws NoSuchAlgorithmException{
        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keyLength);

        pair = keyPairGenerator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();

        //Convert to String
        Base64.Encoder encoder = Base64.getEncoder();
        sPrivateKey = encoder.encodeToString(privateKey.getEncoded());
        sPublicKey = encoder.encodeToString(publicKey.getEncoded());
    }

    private void createSecretKey() throws NoSuchAlgorithmException{
        keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        secretKey = keyGenerator.generateKey();

        //Convert to String
        Base64.Encoder encoder = Base64.getEncoder();
        sSecretKey = encoder.encodeToString(secretKey.getEncoded());
    }

    private void saveToFile(String path, byte[] key) throws IOException{

        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();

    }


    private void saveKeyToFile(String path, String key, int type) throws IOException{
        String filePath = path;
        String fileName = "";
        String start = "";
        String end = "";
        switch (type){
            case PUBLIC_KEY : {
                fileName = "PublicKey.txt";
                start = "-----BEGIN PUBLIC KEY-----";
                end = "-----END PUBLIC KEY-----";
                writeToFile(start, end, sPublicKey, filePath, fileName);
                break;
            }
            case PRIVATE_KEY :{
                fileName = "PrivateKey.txt";
                start = "-----BEGIN PRIVATE KEY-----";
                end = "-----END PRIVATE KEY-----";
                writeToFile(start, end, sPrivateKey, filePath, fileName);
                break;
            }
            case SECRET_KEY : {
                fileName = "SecretKey.txt";
                writeToFile(start, end, sPrivateKey, filePath, fileName);
                break;
            }
        }
    }

    private void writeToFile(String start, String end, String key, String filePath, String fileName) throws  IOException{
        File saveLocation = new File(filePath);
        saveLocation.mkdir();

        File myFile = new File(filePath, fileName);
        FileWriter fileWriter = new FileWriter(myFile);
        if(start != "")
            fileWriter.write(start);
        for(int i = 0; i<key.length(); i++){
            if(i%64 == 0){
                fileWriter.write("\n");
            }
            fileWriter.write(key.charAt(i));
        }

        if(end != ""){
            fileWriter.write("\n"); //new line
            fileWriter.write(end);
        }

        fileWriter.close();
    }


}
