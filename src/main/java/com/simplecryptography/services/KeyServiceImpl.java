package com.simplecryptography.services;

import com.simplecryptography.domain.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeyServiceImpl implements KeyService {

    private Keys keys;

    @Autowired
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    @Override
    public String getPrivateKey() {
        return (keys.getPrivateKey());
    }

    @Override
    public String getPublicKey() {
        return (keys.getPublicKey());
    }

    @Override
    public String getSecretKey() {
        return (keys.getsSecretKey());
    }

    @Override
    public void onGenerate() {
        keys.generateKeys();
    }

}
