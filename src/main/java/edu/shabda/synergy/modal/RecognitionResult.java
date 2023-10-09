package edu.shabda.synergy.modal;

public class RecognitionResult {

    private String transcript; // The recognized text
    private double confidence; // Confidence score (higher is better)
    private long startTimeMs; // Start time of the recognized segment in milliseconds
    private long endTimeMs; // End time of the recognized segment in milliseconds

    public RecognitionResult(
            String transcript, double confidence,
            long startTimeMs, long endTimeMs) {
        this.transcript = transcript;
        this.confidence = confidence;
        this.startTimeMs = startTimeMs;
        this.endTimeMs = endTimeMs;
    }

    public String getTranscript() {
        return transcript;
    }

    public double getConfidence() {
        return confidence;
    }

    public long getStartTimeMs() {
        return startTimeMs;
    }

    public long getEndTimeMs() {
        return endTimeMs;
    }
}