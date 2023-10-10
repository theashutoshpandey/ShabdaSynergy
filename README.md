# ShabdaSynergy - Offline Speech-to-Text with Real-time Audio Streaming and Java Backend API Support.

## Overview
ShabdaSynergy is an innovative speech-to-text solution that empowers your applications with offline speech recognition capabilities. It offers a comprehensive set of features, including Automatic Speech Recognition (ASR), grammar-based speech recognition, language model-based speech recognition, real-time audio streaming through WebSocket, and seamless API integration for Java backend systems.

## Key Features

### Offline Speech-to-Text
ShabdaSynergy allows you to convert spoken language into written text without the need for a constant internet connection. This ensures data privacy and reliability, making it an ideal choice for various applications where offline functionality is essential.

### Automatic Speech Recognition (ASR) Technology
Our ASR technology ensures high accuracy and reliability in transcribing spoken words. Whether you're capturing lectures, interviews, or any other spoken content, ShabdaSynergy delivers precise results.

### Grammar-Based Speech Recognition
ShabdaSynergy includes a grammar-based speech recognition system, enhancing its ability to understand and transcribe content with specific language rules and structures. This feature is invaluable for applications that require context-aware transcription.

### Language Model-Based Speech Recognition
With language model-based speech recognition, ShabdaSynergy can adapt to a wide range of language styles and accents, providing accurate transcriptions across diverse language contexts.

### Real-time Audio Streaming via WebSocket
ShabdaSynergy offers real-time audio streaming capabilities through WebSocket, making it suitable for applications that require live transcription, such as voice assistants, live captioning, and more.

### API Support for Java Backend Integration
Integrating ShabdaSynergy into your Java backend is a breeze, thanks to its robust API support. You can effortlessly incorporate this powerful speech-to-text solution into your existing applications, enhancing their functionality and user experience.

## Use Cases
- Educational platforms for transcribing lectures and discussions.
- Voice-controlled applications and devices.
- Customer service basic chatbots with voice input.
- Accessibility tools for the hearing-impaired.
- Podcast and content transcription services.
- And much more!

## Transcribe Voice File to Text API Documentation

### Endpoint

`POST http://127.0.0.1:5070/voice`

### Description

This API endpoint allows you to transcribe voice data to text. You can specify parameters such as `sampleRate`, `useGrammar`, and `languageCode`.

### Request Parameters

- `sampleRate` (integer, optional): The sample rate of the voice data. Default is 8000.
- `useGrammar` (boolean, optional): Whether to use grammar rules during transcription. Default is true.
- `languageCode` (string, optional): The language code for the voice data. Default is "en-US".

### Request Body

- `file` (file, required): The voice data file to be transcribed supported (.mp3 amd .wav).

### Example Request

```shell
curl --location 'http://127.0.0.1:5004/voice?sampleRate=8000&useGrammar=true&languageCode=en-US' \
--form 'file=@"/C:/Users/Ashutosh/VoiceSpacePool/2aef8300b3bb543c1190ba50b6b7d4d1.wav"'
```
### Result Response

```json
{
    "text": "great may i know your name please ",
    "results": [
        {
            "transcript": "great",
            "confidence": 0.0,
            "startTimeMs": 260,
            "endTimeMs": 640
        },
        {
            "transcript": "may i know your name please",
            "confidence": 0.0,
            "startTimeMs": 880,
            "endTimeMs": 2120
        }
    ]
}
```

ShabdaSynergy is the go-to choice for businesses and developers seeking a versatile and almost accurate offline speech-to-text solution with real-time streaming and easy integration into Java backend systems.


