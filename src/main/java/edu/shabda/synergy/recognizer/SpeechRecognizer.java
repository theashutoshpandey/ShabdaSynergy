package edu.shabda.synergy.recognizer;

import java.io.IOException;
import java.io.InputStream;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.shabda.synergy.modal.CollectResult;
import edu.shabda.synergy.modal.RecognitionResult;

/**
 * This class represents a Speech Recognizer that can process audio files for
 * speech recognition.
 * It uses the CMU Sphinx4 library for speech recognition.
 *
 * @author Ashutosh Pandey
 * @version 1.0
 */
public class SpeechRecognizer {

    private final Configuration configuration;
    private final StreamSpeechRecognizer recognizer;

    public SpeechRecognizer(int sampleRate, boolean useGrammar, String languageCode) throws IOException {
        this.configuration = createConfiguration(sampleRate, useGrammar, languageCode);
        this.recognizer = new StreamSpeechRecognizer(configuration);
    }

    private Configuration createConfiguration(int sampleRate, boolean useGrammar, String languageCode) {
        Configuration config = new Configuration();
        config.setAcousticModelPath("resources/en-us/en-us");
        config.setDictionaryPath("resources/en-us/cmudict-en-us.dict");
        config.setLanguageModelPath("resources/en-us/en-us.lm.bin");
        config.setSampleRate(sampleRate);
        // enable grammars
        config.setGrammarPath("resources/grammars");
        config.setGrammarName("grammar");
        config.setUseGrammar(useGrammar);
        return config;
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
            System.out.println("Failed to recognize speech " + e.getMessage());
        } finally {
            recognizer.stopRecognition();
        }
        return collectResult;
    }

    private void processRecognitionResult(CollectResult collectResult, SpeechResult result) {
        // Process the recognition results
        String transcript = result.getHypothesis();
        collectResult.addText(transcript);

        double confidence = result.getWords().stream()
                .mapToDouble(e -> e.getConfidence())
                .max()
                .orElse(0.0);
        long startTimeMs = result.getWords().get(0).getTimeFrame().getStart();
        long endTimeMs = result.getWords().get(result.getWords().size() - 1).getTimeFrame().getEnd();
        collectResult.addRecognitionResult(new RecognitionResult(transcript, confidence, startTimeMs, endTimeMs));
    }
}
