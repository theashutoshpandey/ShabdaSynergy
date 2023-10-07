package edu.shabda.synergy.controller;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.linguist.dictionary.Word;
import edu.cmu.sphinx.result.WordResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
public class SynergyController {

    @GetMapping(value = "/voice", produces = "application/xml")
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Check if the uploaded file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file.");
            }

            // Read the file content into a byte array
            byte[] fileBytes = file.getBytes();

            Configuration configuration = new Configuration();
            configuration.setAcousticModelPath("resources/en-us/en-us");
            configuration.setDictionaryPath("resources/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("resources/en-us/en-us.lm.bin");
            configuration.setSampleRate(8000);

            configuration.setGrammarPath("resources/grammars");
            configuration.setGrammarName("grammar");
            configuration.setUseGrammar(true);

            // Initialize the recognizer
            StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);


            // Start recognition
            recognizer.startRecognition(file.getInputStream());

            StringBuilder builder= new StringBuilder();
            // Process the recognition results
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                System.out.println("Recognized: " + result.getHypothesis());
                for (WordResult wordResult : result.getWords()) {
                    Word text = wordResult.getWord();
                    if(text.toString().contains("<sil>")) {
                    }else{
                        builder.append(text.toString()+" ");
                    }
                    System.out.println("Word: " + wordResult.getWord());
                    System.out.println("Phonemes: " + wordResult.getPronunciation());
                }
            }

            // Stop recognition
            recognizer.stopRecognition();



            // Encode the byte array to Base64
            String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);

            return ResponseEntity.ok(builder.toString());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to process the file: " + e.getMessage());
        }
    }


    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<String> base64EncodedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                byte[] fileBytes = file.getBytes();
                String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
                base64EncodedFiles.add(base64Encoded);
            } catch (IOException e) {
                return ResponseEntity.status(500).body(null);
            }
        }

        return ResponseEntity.ok(base64EncodedFiles);
    }

}
