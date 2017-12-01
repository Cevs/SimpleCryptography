package com.simplecryptography.controllers;

import com.simplecryptography.domain.Cryptography;
import com.simplecryptography.domain.Keys;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class RestCryptographyController {

    private String text;

    private Cryptography cryptography;
    @Autowired
    public void setCryptography(Cryptography cryptography){
        this.cryptography = cryptography;
    }

    private Keys keys;
    @Autowired
    public void setKeys(Keys keys){
        this.keys = keys;
    }

    @PostMapping("/api/keys")
    public ResponseEntity<JSONObject> generateKeys(){
        keys.generateKeys();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("SecretKey", keys.getsSecretKey());
        jsonObject.put("PublicKey", keys.getPublicKey());
        jsonObject.put("PrivateKey", keys.getPrivateKey());

        cryptography.InitializeKeys();
        return new ResponseEntity(jsonObject, HttpStatus.OK);
    }

    @PostMapping("/api/asymmetric")
    public ResponseEntity<?> asymmetric(@RequestParam("operation") String operation){
        try{
            if(operation.equals("Encrypt")){
                text = cryptography.encryptFile(0);
            }
            else if (operation.equals("Decrypt")){
                text = cryptography.decryptFile(0);
            }
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(""+text, HttpStatus.OK);
    }

    @PostMapping("/api/symmetric")
    public ResponseEntity<?> symmetric(@RequestParam("operation") String operation){
        try{
            if(operation.equals("Encrypt")){
                text = cryptography.encryptFile(1);
            }
            else if(operation.equals("Decrypt")){
                text = cryptography.decryptFile(1);
            }
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(text,HttpStatus.OK);
    }

    @PostMapping("/api/hash")
    public ResponseEntity<?> digest(){
        String digest = "";
        try {
            digest = cryptography.digestFile();
        } catch (Exception e) {
            return  new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity(digest, HttpStatus.OK);
    }

    @PostMapping("/api/digital-signature")
    public ResponseEntity<?> sign(){
        String digitalSignature = "";
        try{
            digitalSignature = cryptography.signMessage();
        }catch (Exception e){
            return  new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return  new ResponseEntity(digitalSignature, HttpStatus.OK);
    }

    @PostMapping("/api/check-signature")
    public ResponseEntity<?>checkDigitalSignature(
            @RequestParam("files")MultipartFile[] files)
    {
        boolean match = false;
        try{
            match = cryptography.checkDigitalSignature(files[0], files[1]);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ResponseEntity(match, HttpStatus.OK);
    }
}
