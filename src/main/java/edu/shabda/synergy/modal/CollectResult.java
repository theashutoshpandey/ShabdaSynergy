package edu.shabda.synergy.modal;

import java.util.ArrayList;
import java.util.List;

public class CollectResult {

    public String text;
    List<RecognitionResult> results;

    public CollectResult() {
        this.text = "";
        this.results = new ArrayList<>();
    }

    public void addText(String transcription) {
        this.text += transcription + " ";
    }

    public void addRecognitionResult(RecognitionResult recognitionResult) {
        this.results.add(recognitionResult);
    }
}
