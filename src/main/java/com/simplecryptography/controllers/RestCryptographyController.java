package com.simplecryptography.controllers;

import com.simplecryptography.domain.AsymmetricCryptography;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestCryptographyController {

    private String text;
    private AsymmetricCryptography asymmetricCryptography;
    @Autowired
    public void setAsymmetricCryptography(AsymmetricCryptography asymmetricCryptography){
        this.asymmetricCryptography = asymmetricCryptography;
    }

    @PostMapping("/api/asymmetric")
    public ResponseEntity<?> asymmetric(@RequestParam("type") Integer type){
        if(type == 1){
            text = asymmetricCryptography.encryptFile();
        }
        else{
            text = asymmetricCryptography.decryptFile();
        }

        return new ResponseEntity(""+text, HttpStatus.OK);
    }

    @PostMapping("/api/AsymmetricDecrypt")
    public ResponseEntity<?> asymmetricDecrypt(){
        String text = asymmetricCryptography.decryptFile();
        return new ResponseEntity(""+text, HttpStatus.OK);
    }
}
