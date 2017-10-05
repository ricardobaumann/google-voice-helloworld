package helloworld;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AudioRecoService implements Loggable {

    private final ContentBotRepo contentBotRepo;

    AudioRecoService(final ContentBotRepo contentBotRepo) {
        this.contentBotRepo = contentBotRepo;
    }

    Optional<String> transcript(final byte[] bytes) throws Exception {


        try (final SpeechClient speech = SpeechClient.create()) {
            final ByteString audioBytes = ByteString.copyFrom(bytes);

            // Builds the sync recognize request
            final RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(16000)

                    .setLanguageCode("en-US")
                    .build();
            final RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)
                    .build();

            // Performs speech recognition on the audio file
            final RecognizeResponse response = speech.recognize(config, audio);
            final List<SpeechRecognitionResult> results = response.getResultsList();
            final List<SpeechRecognitionAlternative> alternatives = results.stream()
                    .map(SpeechRecognitionResult::getAlternativesList)
                    .flatMap(List::stream).collect(Collectors.toList());

            return alternatives.stream().sorted(Comparator.comparingDouble(SpeechRecognitionAlternative::getConfidence))
                    .findFirst().flatMap(alternative -> Optional.ofNullable(contentBotRepo.ask(alternative.getTranscript())));

        }
    }
}
