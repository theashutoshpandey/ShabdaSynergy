package edu.shabda.synergy.recognizer;

import com.google.gson.Gson;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.shabda.synergy.modal.CollectResult;
import edu.shabda.synergy.modal.RecognitionResult;
import edu.shabda.synergy.utils.AppendableAudioInputStream;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class for live speech recognition using WebSocket and the
 * StreamSpeechRecognizer.
 *
 * @Ashutosh Pandey
 */
public class LiveSpeechRecognizer {

    private final WebSocketSession session;
    private final Configuration configuration;
    private final StreamSpeechRecognizer recognizer;
    private final ExecutorService recognitionExecutor;

    private AppendableAudioInputStream inputStream;
    private ByteArrayOutputStream arrayOutputStream;

    /**
     * Initializes the LiveSpeechRecognizer with the provided WebSocketSession and
     * Configuration.
     */
    public LiveSpeechRecognizer(WebSocketSession session, Configuration configuration) throws IOException {
        this.session = session;
        this.configuration = configuration;
        this.recognizer = new StreamSpeechRecognizer(configuration);
        this.inputStream = configuration.getUseGrammar() ? null : new AppendableAudioInputStream();
        this.arrayOutputStream = configuration.getUseGrammar() ? new ByteArrayOutputStream() : null;
        this.recognitionExecutor = Executors.newSingleThreadExecutor();

        if (!configuration.getUseGrammar()) {
            initializeLiveRecognitionAndPublisher();
        }
    }

    /**
     * Loads audio data into the recognizer for live recognition.
     */
    public void loadAudioPayload(byte[] audioData) {
        try {
            if (inputStream != null) {
                inputStream.appendData(audioData, 0, audioData.length);

            } else if (arrayOutputStream != null && arrayOutputStream.size() >= 32000) {
                initializeGrammarRecognitionAndPublisher(new ByteArrayInputStream(arrayOutputStream.toByteArray()));

                this.arrayOutputStream = new ByteArrayOutputStream();
                arrayOutputStream.write(audioData, 0, audioData.length);

            } else if (arrayOutputStream != null) {
                arrayOutputStream.write(audioData, 0, audioData.length);

            } else {
                System.out.println("Not found any service to process");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes live recognition and publishes results asynchronously.
     */
    public void initializeLiveRecognitionAndPublisher() {
        recognitionExecutor.execute(() -> {
            try {
                recognizer.startRecognition(inputStream);
                System.out.println("Live speech recognition service started successfully.");

                CollectResult collectResult = new CollectResult();

                while (session.isOpen()) {
                    SpeechResult result = recognizer.getResult();

                    if (result != null) {
                        processRecognitionResult(collectResult, result);
                    }

                    System.out.println("Recognition Result: " + new Gson().toJson(collectResult));
                    collectResult.clear();
                    Thread.sleep(3200);
                }

            } catch (Exception e) {
                System.out.println("Error during recognition: " + e.getMessage());
            } finally {
                recognizer.stopRecognition();
                System.out.println("Live speech recognition service stopped successfully.");
            }
        });
    }

    /**
     * Recognizes speech from an InputStream.
     */
    public void initializeGrammarRecognitionAndPublisher(InputStream audioStream) {
        CollectResult collectResult = new CollectResult();
        try {
            recognizer.startRecognition(audioStream);
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                processRecognitionResult(collectResult, result);
            }
            System.out.println("Recognition Result: " + new Gson().toJson(collectResult));
        } catch (Exception e) {
            System.err.println("Failed to recognize speech: " + e.getMessage());
        } finally {
            recognizer.stopRecognition();
        }
    }

    // Process the recognition results
    private void processRecognitionResult(CollectResult collectResult, SpeechResult result) {
        String transcript = result.getHypothesis();
        collectResult.addText(transcript);

        double confidence = calculateConfidence(result);
        long startTimeMs = result.getWords().get(0).getTimeFrame().getStart();
        long endTimeMs = result.getWords().get(result.getWords().size() - 1).getTimeFrame().getEnd();

        collectResult.addRecognitionResult(new RecognitionResult(transcript, confidence, startTimeMs, endTimeMs));
    }

    // Helper method to calculate confidence
    private double calculateConfidence(SpeechResult result) {
        try {
            return result.getWords().stream()
                    .mapToDouble(e -> e.getConfidence())
                    .max()
                    .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
