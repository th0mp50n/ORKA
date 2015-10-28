package nl.dtls.annotator.redux.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import nl.dtls.annotator.redux.service.PredicateService;

@RestController
@RequestMapping("/predicates")
public class PredicateController {
    @Autowired
    private PredicateService service;
    
    @RequestMapping(method = RequestMethod.GET)
    public List<String> getPredicates() {
        return service.getPredicates();
    }
}
