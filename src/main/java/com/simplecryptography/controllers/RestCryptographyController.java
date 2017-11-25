package com.simplecryptography.controllers;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.simplecryptography.domain.AsymmetricCryptography;
import com.simplecryptography.domain.Keys;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Dictionary;

@RestController
public class RestCryptographyController {

    private String text;

    private AsymmetricCryptography asymmetricCryptography;
    @Autowired
    public void setAsymmetricCryptography(AsymmetricCryptography asymmetricCryptography){
        this.asymmetricCryptography = asymmetricCryptography;
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
    public ResponseEntity<?> asymmetric(@RequestParam("type") Integer type){
        try{
            asymmetricCryptography.Initialize();
            if(type == 1){
                text = asymmetricCryptography.encryptFile();
            }
            else{
                text = asymmetricCryptography.decryptFile();
            }
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(""+text, HttpStatus.OK);
    }

    @PostMapping("/api/AsymmetricDecrypt")
    public ResponseEntity<?> asymmetricDecrypt(){
        String text = asymmetricCryptography.decryptFile();
        return new ResponseEntity(""+text, HttpStatus.OK);
    }
}
