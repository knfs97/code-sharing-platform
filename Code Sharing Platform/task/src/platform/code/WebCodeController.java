package platform.code;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;


@Controller
public class WebCodeController {
    private final CodeService codeService;

    @Autowired
    public WebCodeController(CodeService codeService) {this.codeService = codeService;}

    @GetMapping("/code/{id}")
    public String getCodeAsHtml(@PathVariable UUID id, Model model){
        Code code = codeService.findCodeById(id);

        boolean isTimeRestricted = code.getRestriction() == Code.Restriction.BOTH || code.getRestriction() == Code.Restriction.TIME;
        boolean isViewsRestricted = code.getRestriction() == Code.Restriction.BOTH || code.getRestriction() == Code.Restriction.VIEWS;

        // set up model
        model.addAttribute("snippet", code.getSnippet());
        model.addAttribute("loadedTime", code.getLoadedAt());
        model.addAttribute("time", code.getExpirationTime());
        model.addAttribute("views", code.getViews());
        model.addAttribute("isTimeRestricted", isTimeRestricted);
        model.addAttribute("isViewsRestricted", isViewsRestricted);

        return "show-code";
    }
    @GetMapping("/code/latest")
    public String getLatestListCodeAsHtml(Model model){
        List<Code> listOfCode = codeService.getLatestCodeByLimit(10);
        model.addAttribute("code", listOfCode);
        return "show-list-code";
    }

    @GetMapping("/code")
    public String endPointNoLongerExists() {
        return "404";
    }

    @GetMapping("/code/new")
    public String getFormToCreateCode() {
        return "create-code";
    }

}
