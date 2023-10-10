package edu.shabda.synergy.recognizer;

import com.google.gson.Gson;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.shabda.synergy.modal.CollectResult;
import edu.shabda.synergy.modal.RecognitionResult;
import edu.shabda.synergy.utils.AppendableAudioInputStream;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiveSpeechRecognizer {
    private static final Logger logger = LoggerFactory.getLogger(LiveSpeechRecognizer.class);

    private final WebSocketSession session;
    private final Configuration configuration;
    private final StreamSpeechRecognizer recognizer;
    private final AppendableAudioInputStream inputStream;
    private final ExecutorService recognitionExecutor;

    public LiveSpeechRecognizer(WebSocketSession session, Configuration configuration) throws IOException {
        this.session = session;
        this.configuration = configuration;
        this.recognizer = new StreamSpeechRecognizer(configuration);
        this.inputStream = configuration.getUseGrammar() ? null : new AppendableAudioInputStream();
        this.recognitionExecutor = Executors.newSingleThreadExecutor();

        if (!configuration.getUseGrammar()) {
            this.initializeLiveRecognitionAndPublisher();
        }
    }

    public void loadAudioPayload(byte[] audioData) {
        try {
            if (inputStream != null) {
                inputStream.appendData(audioData, 0, audioData.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeLiveRecognitionAndPublisher() {
        recognitionExecutor.execute(() -> {
            try {
                this.recognizer.startRecognition(inputStream);
                System.out.println("Service for live recognition started successfully");
                CollectResult collectResult = new CollectResult();

                while (session.isOpen()) {
                    SpeechResult result = recognizer.getResult();

                    if (result != null) {
                        processRecognitionResult(collectResult, result);
                    }
                    System.out.println("Result : " + new Gson().toJson(collectResult));
                    collectResult.clear();
                    Thread.sleep(5000);
                }

                recognizer.stopRecognition();
                logger.info("live recognition stopped successfully");
            } catch (Exception e) {
                logger.error("Error during recognition: " + e.getMessage(), e);
            }
        });
    }

    public CollectResult recognizeSpeech(InputStream audioStream) throws Exception {
        CollectResult collectResult = new CollectResult();
        try {
            recognizer.startRecognition(audioStream);
            SpeechResult result;
            while ((result = recognizer.getResult()) != null) {
                processRecognitionResult(collectResult, result);
            }
        } catch (Exception e) {
            logger.error("Failed to recognize speech: " + e.getMessage(), e);
        } finally {
            recognizer.stopRecognition();
        }
        return collectResult;
    }

    private void processRecognitionResult(CollectResult collectResult, SpeechResult result) {
        // Process the recognition results
        String transcript = result.getHypothesis();
        collectResult.addText(transcript);

        // double confidence = result.getWords().stream()
        // .mapToDouble(e -> e.getConfidence())
        // .max()
        // .orElse(0.0);
        long startTimeMs = result.getWords().get(0).getTimeFrame().getStart();
        long endTimeMs = result.getWords().get(result.getWords().size() - 1).getTimeFrame().getEnd();
        collectResult.addRecognitionResult(new RecognitionResult(transcript, 0.0, startTimeMs, endTimeMs));
    }
}
