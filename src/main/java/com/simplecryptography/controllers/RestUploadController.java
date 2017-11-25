package com.simplecryptography.controllers;

import com.simplecryptography.model.UploadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestUploadController {
    private final Logger logger = LoggerFactory.getLogger(RestUploadController.class);

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "MyFiles/";

    //Single file upload
    @PostMapping("/api/upload")
    // If not @RestController, uncomment this
    //@ResponseBody
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile uploadfile) {

        logger.debug("Single file upload!");

        if (uploadfile.isEmpty()) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {

            saveUploadedFiles(Arrays.asList(uploadfile));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity("Successfully uploaded - " +
                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

    }

    // Multiple file upload
    @PostMapping("/api/upload/multi")
    public ResponseEntity<?> uploadFileMulti(
            @RequestParam("files") MultipartFile[] uploadFiles) {

        logger.debug("Multiple file upload!");

        String uploadedFileName = Arrays.stream(uploadFiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }


        String textOfFile = "";
        try {

            saveUploadedFiles(Arrays.asList(uploadFiles));
            textOfFile = getTextFromFile(UPLOADED_FOLDER + "text.txt");

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity(""+textOfFile, HttpStatus.OK);
    }


    //Read text from file
    private String getTextFromFile(String path) throws IOException{
        String readLine = "";
        String text = "";
        File file = new File(path);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));


        while ((readLine = bufferedReader.readLine()) != null){
            text += readLine;
        }

        return text;
    }

    //save file
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + "text.txt");
            Files.write(path, bytes);

        }

    }
}
