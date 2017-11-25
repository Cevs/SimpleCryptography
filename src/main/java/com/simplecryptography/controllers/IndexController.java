package com.simplecryptography.controllers;

import com.simplecryptography.services.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {


    private KeyService keyService;


    @Autowired
    public void setKeyService(KeyService keyService) {
        this.keyService = keyService;
    }



    @RequestMapping("/")
    public String index(){
        return "index";
    }


    @RequestMapping(value="/", params = "actionGenerateKeys")
    public String generateKeys(Model model){
        keyService.onGenerate();
        model.addAttribute("publicKey", keyService.getPublicKey());
        model.addAttribute("privateKey", keyService.getPrivateKey());
        model.addAttribute("secretKey", keyService.getSecretKey());
        return "index";
    }

}
