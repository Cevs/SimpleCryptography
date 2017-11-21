package com.simplecryptography.services;

public interface KeyService {
    String getPrivateKey();
    String getPublicKey();
    String getSecretKey();
    void onGenerate();
}
