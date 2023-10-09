package edu.shabda.synergy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import edu.shabda.synergy.modal.CollectResult;
import edu.shabda.synergy.recognizer.SpeechRecognizer;

@Controller
public class SynergyController {

    @PostMapping(value = "/voice", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "sampleRate", required = false, defaultValue = "8000") int sampleRate,
            @RequestParam(value = "useGrammar", required = false, defaultValue = "false") boolean useGrammar,
            @RequestParam(value = "languageCode", required = false, defaultValue = "en-US") String languageCode) {
        try {
            // Check if the uploaded file is empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please upload a file.");
            }

            SpeechRecognizer recognizer = new SpeechRecognizer(sampleRate, useGrammar, languageCode);
            CollectResult collectResult = recognizer.recognizeSpeech(file.getInputStream());

            return ResponseEntity.ok(new Gson().toJson(collectResult));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to process the file: " + e.getMessage());
        }
    }

}
