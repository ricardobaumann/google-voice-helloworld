package helloworld;

// Imports the Google Cloud client library

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class AudioRecognizerApplication {

    private final ContentBotRepo contentBotRepo;

    AudioRecognizerApplication(final ContentBotRepo contentBotRepo) {
        this.contentBotRepo = contentBotRepo;
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            // Instantiates a client
            final SpeechClient speech = SpeechClient.create();

            // The path to the audio file to transcribe
            final String fileName = args[0];

            // Reads the audio file into memory
            final Path path = Paths.get(fileName);
            final byte[] data = Files.readAllBytes(path);
            final ByteString audioBytes = ByteString.copyFrom(data);

            // Builds the sync recognize request
            final RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)

                    .setLanguageCode("en-US")
                    .build();
            final RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            final RecognizeResponse response = speech.recognize(config, audio);
            final List<SpeechRecognitionResult> results = response.getResultsList();
            System.out.println("results: " + results);
            for (final SpeechRecognitionResult result : results) {
                final List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                for (final SpeechRecognitionAlternative alternative : alternatives) {
                    System.out.printf("Transcription: %s%n", contentBotRepo.ask(alternative.getTranscript()));
                }
            }
            speech.close();
        };
    }

    public static void main(final String... args) throws Exception {
        SpringApplication.run(AudioRecognizerApplication.class, args);
    }
}