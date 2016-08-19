package cz.cvut.kbss.study.rest;

import cz.cvut.kbss.study.model.PatientRecord;
import cz.cvut.kbss.study.rest.dto.RawJson;
import cz.cvut.kbss.study.service.formgen.FormGenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/formGen")
public class FormGenController extends BaseController {

    @Autowired
    private FormGenService formGenService;

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public RawJson generateForm(@RequestBody PatientRecord data) {
        return formGenService.generateForm(data);
    }

    @RequestMapping("/possibleValues")
    public RawJson getPossibleValues(@RequestParam("query") String query) {
        return formGenService.getPossibleValues(query);
    }
}
