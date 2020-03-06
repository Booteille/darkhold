package com.quiz.darkhold.options.controller;

import com.quiz.darkhold.options.service.OptionsService;
import com.quiz.darkhold.preview.model.PublishInfo;
import com.quiz.darkhold.preview.service.PreviewService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class OptionsController {
    private final Log log = LogFactory.getLog(OptionsController.class);
    @Autowired
    private OptionsService optionsService;

    @Autowired
    private PreviewService previewService;

    /**
     * on to create challenge page
     * @return create challenge page
     */
    @PostMapping("/createChallenge")
    public String createChallenge() {
        log.info("into the createChallenge method");
        return "createchallenge";
    }

    /**
     * on to view challenge page, with a view of challenges
     * @param model model
     * @return view challenge page
     */
    @PostMapping("/viewChallenge")
    public String viewChallenges(Model model) {
        log.info("into the viewChallenge method");
        model.addAttribute("challengeInfo", optionsService.populateChallengeInfo());
        return "viewchallenges";
    }

    /**
     * show the currently running challenge
     * @param model model
     * @param principal auth
     * @return publish page
     */
    @PostMapping("/activeChallenge")
    public String activeChallenges(Model model, Principal principal) {
        log.info("into the activeChallenge method");
        PublishInfo publishInfo = previewService.getActiveChallenge();
        model.addAttribute("quizPin", publishInfo.getPin());
        //todo : find a way to get all users binded
        model.addAttribute("user", publishInfo.getModerator());
        log.info("activeChallenges method, quizPin : " + publishInfo.getPin());
        return "publish";
    }
}
