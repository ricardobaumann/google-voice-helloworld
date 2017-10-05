package helloworld;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
public class UploadController implements Loggable {

    private final AudioRecoService audioRecoService;

    UploadController(final AudioRecoService audioRecoService) {
        this.audioRecoService = audioRecoService;
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") final MultipartFile file,
                                   final RedirectAttributes redirectAttributes) throws IOException {


        try {
            final Optional<String> result = audioRecoService.transcript(file.getBytes());
            if (result.isPresent()) {
                redirectAttributes.addFlashAttribute("message",
                        result.get());
            } else {
                redirectAttributes.addFlashAttribute("message", "No transcription for your audio file");
            }
        } catch (final Exception e) {
            redirectAttributes.addFlashAttribute("message",
                    "Transcription failed. Check logs for more details");
            logger().error("An error occurred trying to transcript", e);
        }

        return "redirect:/";
    }

}
