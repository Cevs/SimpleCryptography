package com.simplecryptography.controllers;

import com.simplecryptography.domain.Cryptography;
import com.simplecryptography.domain.Keys;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        return new ResponseEntity(jsonObject, HttpStatus.OK);
    }

    @PostMapping("/api/asymmetric")
    public ResponseEntity<?> asymmetric(@RequestParam("operation") String operation){
        try{
            cryptography.InitializeAsymmetric();
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
            cryptography.InitializeSymmetric();
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
}
