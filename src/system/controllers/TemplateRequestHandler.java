package system.controllers;

import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;


public class TemplateRequestHandler {

    private final TemplateManager templateManager;
    private final UserManager userManager;

    /**
     * Constructor for TemplateRequestHandler()
     * @param templateManager template manager that contains all templates and able to make change to them
     * @param userManager user manager that contains all user entities and able to make change to them
     */
    public TemplateRequestHandler(TemplateManager templateManager, UserManager userManager) {
        this.templateManager = templateManager;
        this.userManager = userManager;
    }


}
