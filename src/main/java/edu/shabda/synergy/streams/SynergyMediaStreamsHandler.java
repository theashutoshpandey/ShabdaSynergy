package edu.shabda.synergy.streams;

import edu.cmu.sphinx.api.*;
import edu.shabda.synergy.utils.AppendableAudioInputStream;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.ByteArrayInputStream;


public class SynergyMediaStreamsHandler extends AbstractWebSocketHandler {

    private StreamSpeechRecognizer recognizer;
    private AppendableAudioInputStream inputStream;
    private Configuration configuration;

    public SynergyMediaStreamsHandler() {
        try {
            // Initialize CMU Sphinx LiveSpeechRecognizer
            configuration = new Configuration();

            configuration.setAcousticModelPath("resources/en-us/en-us");
            configuration.setDictionaryPath("resources/en-us/cmudict-en-us.dict");
            configuration.setLanguageModelPath("resources/en-us/en-us.lm.bin");
            configuration.setGrammarPath("resources/grammars");
            configuration.setGrammarName("grammar");
            configuration.setUseGrammar(true);
            configuration.setSampleRate(8000);

//            this.recognizer = new StreamSpeechRecognizer(configuration);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Connection established successfully!");
//        new Thread(()-> {
//            this.inputStream = new AppendableAudioInputStream();
//            this.recognizer.startRecognition(inputStream);
//            processAudioFrame(session);
//        }).start();
    }


    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            byte[] audioChunk = message.getPayload().array();

            if(arrayOutputStream.toByteArray().length >= 32000) {
//                byte[] bytes = arrayOutputStream.toByteArray();
//                inputStream.appendData(bytes,0,bytes.length);
                this.recognizer = new StreamSpeechRecognizer(configuration);
                recognizer.startRecognition(new ByteArrayInputStream(arrayOutputStream.toByteArray()));

                SpeechResult result = recognizer.getResult();

                String recognizedSpeech = result.getHypothesis();
                System.out.println("You said: " + recognizedSpeech);


                arrayOutputStream = new ByteArrayOutputStream();
                arrayOutputStream.write(audioChunk);
            }else{
                arrayOutputStream.write(audioChunk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        System.out.println("going to close ");
    }

    private void processAudioFrame(WebSocketSession session) {
        new Thread(()->{
            try {

                System.out.println("Listening...");
                while (session.isOpen()) {
                    SpeechResult result = recognizer.getResult();

                    if (result != null) {
                        String recognizedSpeech = result.getHypothesis();
                        System.out.println("You said: " + recognizedSpeech);
                    } else {
                        System.out.println("I didn't catch that. Please try again.");
                    }
                    Thread.sleep(5000);
                }
                recognizer.stopRecognition();
                System.out.println(" stopped successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }}).start();
    }
}
