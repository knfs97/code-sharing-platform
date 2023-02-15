package platform.code;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
public class CodeRestController {

    private final CodeService codeService;

    @Autowired
    public CodeRestController(CodeService codeService) {this.codeService = codeService;}

    public ResponseEntity<String> convertCodeAsJson(Code code) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        String codeAsJSON = objectMapper.writeValueAsString(code);
        return ResponseEntity.ok().headers(responseHeaders).body(codeAsJSON);
    }
    public ResponseEntity<String> convertListOfCodeAsJson(List<Code> code) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        String codeAsJSON = objectMapper.writeValueAsString(code);
        return ResponseEntity.ok().headers(responseHeaders).body(codeAsJSON);
    }
    @GetMapping("/api/code/{id}")
    public ResponseEntity<String> getCodeAsJsonById(@PathVariable UUID id) throws JsonProcessingException {
        Code code = codeService.findCodeById(id);
        code.setId(null);
        return convertCodeAsJson(code);
    }
    @GetMapping("/api/code/latest")
    public ResponseEntity<String> getLatestListCodeAsJson() throws JsonProcessingException {
        List<Code> code = codeService.getLatestCodeByLimit(10);
        code.forEach(codeObj -> codeObj.setId(null));
        return convertListOfCodeAsJson(code);
    }
    @GetMapping("/api/code")
    public ResponseEntity<String> apiNoLongerExists() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return ResponseEntity.ok().headers(responseHeaders).body("{}");
    }
    @PostMapping("/api/code/new")
    public ResponseEntity<String> createNewCodeSnippet(@RequestBody Code jsonCodeObject) throws JsonProcessingException {
        jsonCodeObject.checkAndSetRestriction();
        Code code = codeService.addNewCode(jsonCodeObject);
        return convertCodeAsJson(new Code(code.getId()));
    }
}
