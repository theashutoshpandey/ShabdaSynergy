package edu.shabda.synergy.streams;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import edu.cmu.sphinx.api.Configuration;
import edu.shabda.synergy.recognizer.LiveSpeechRecognizer;

public class SynergyMediaStreamsHandler extends AbstractWebSocketHandler {

    private Map<WebSocketSession, LiveSpeechRecognizer> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            System.out.println("Connection established successfully!");
            LiveSpeechRecognizer liveSpeechRecognizer = new LiveSpeechRecognizer(session,
                    createConfiguration(8000, false, "en-IN"));
            sessions.put(session, liveSpeechRecognizer);
        } catch (Exception e) {
            System.err.println("Error occur just after connection established");
        }
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            sessions.get(session).loadAudioPayload(message.getPayload().array());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessions.remove(session);
        System.out.println("Connection disconnected successfully Reason : " + closeStatus.getReason());
    }

    /**
     * Helper method to append write result messages to the log file
     */
    private void resultAppender(String result) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("D:\\transcript.json", true))) {
            writer.write(result + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    // try {
    // byte[] audioChunk = message.getPayload().array();
    // ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    // if(arrayOutputStream.toByteArray().length >= 32000) {
    // CollectResult collectResult = LiveSpeechRecognizer.recognizeSpeech(new
    // ByteArrayInputStream(arrayOutputStream.toByteArray()));
    // resultAppender(new Gson().toJson(collectResult));
    // session.sendMessage(new TextMessage(new Gson().toJson(collectResult)));
    // arrayOutputStream = new ByteArrayOutputStream();
    // arrayOutputStream.write(audioChunk);
    // }else{
    // arrayOutputStream.write(Base64.getDecoder().decode(audioChunk));
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

}
