package system.controllers;

import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import shared.request.Request;
import shared.request.template_request.*;
import shared.response.misc.ErrorMessageResponse;
import shared.response.Response;

import shared.response.misc.SimpleTextResponse;
import shared.response.template.StartTemplateEditResponse;
import shared.response.template.TemplateInfoMapResponse;
import system.use_cases.managers.TemplateManager;
import system.use_cases.managers.UserManager;

import java.io.IOException;


public class TemplateRequestHandler implements RequestHandler {

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

    /**
     * @param request the request to handle
     * @return a response message regarding the success of the request and send back appropriate requested info
     */
    @Override
    public Response handleRequest(Request request) {
        if (request instanceof DeleteTemplateRequest) {
            return handleDeleteTemplateRequest((DeleteTemplateRequest) request);
        } else if (request instanceof NewTemplateRequest) {
            return handleNewTemplateRequest((NewTemplateRequest) request);
        } else if (request instanceof MakeTemplateDesignChoiceRequest) {
            return handleMakeDesignChoiceRequest((MakeTemplateDesignChoiceRequest) request);
        } else if (request instanceof GetAllTemplateInfoRequest) {
            return handleGetAllTemplateInfoRequest((GetAllTemplateInfoRequest) request);
        } else if (request instanceof StartTemplateEditRequest) {
            return handleStartTemplateEditRequest((StartTemplateEditRequest) request);
        } else if (request instanceof EditTemplateAttributeRequest) {
            return handleEditTemplateAttributeRequest((EditTemplateAttributeRequest) request);
        } else if (request instanceof CancelTemplateEditRequest) {
            return handleCancelTemplateEditRequest((CancelTemplateEditRequest) request);
        } else if (request instanceof SaveTemplateEditRequest) {
            return handleSaveTemplateEditRequest((SaveTemplateEditRequest) request);
        }
        else {
            return new ErrorMessageResponse(request.getSessionID(), "Error: unidentified request");
        }
    }

    private Response handleSaveTemplateEditRequest(SaveTemplateEditRequest request) {
        try {
            templateManager.saveTemplateEdit(request.getTemplateID());
            return new SimpleTextResponse(request.getSessionID(), "Successfully saved template.");
        } catch (NoEditingInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: no such editing is in progress");
        }
    }

    private Response handleCancelTemplateEditRequest(CancelTemplateEditRequest request) {
        try {
            templateManager.cancelTemplateEdit(request.getTemplateID());
            return new SimpleTextResponse(request.getSessionID(), "Successfully cancelled template editing.");
        } catch (NoEditingInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: no such editing is in progress.");
        }
    }

    private Response handleStartTemplateEditRequest(StartTemplateEditRequest request) {
        try {
             return new StartTemplateEditResponse(request.getSessionID(),
                     templateManager.startTemplateEdit(request.getTemplateID()));

        } catch (InvalidTemplateIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Template does not exist.");
        } catch (EditingInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(),
                    "Error: someone is currently editing this template.");
        }
    }

    private Response handleDeleteTemplateRequest(DeleteTemplateRequest request) {
        try {
            templateManager.deleteTemplate(request.getTemplateID());
            return new SimpleTextResponse(request.getSessionID(), "Template deleted");
        } catch (InvalidIDException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Template does not exist");
        } catch (IOException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Database not found");
        }
    }

    private Response handleEditTemplateAttributeRequest(EditTemplateAttributeRequest request) {
        try{
            templateManager.editTemplate(request.getTemplateID(), request.getAttributeName(),
                    request.getAttributeValue());
            return new SimpleTextResponse(request.getSessionID(),"Success.");
        } catch (NoEditingInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: No such an editing is in progress.");
        } catch (NoSuchAttributeException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Template has no such attribute");
        } catch (InvalidInputException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: Invalid input");
        }

    }

    private Response handleGetAllTemplateInfoRequest(GetAllTemplateInfoRequest request) {
        return new TemplateInfoMapResponse(request.getSessionID(), templateManager.getAllIdAndTitles());
    }

    private Response handleNewTemplateRequest(NewTemplateRequest request) {

        String sender = request.getSenderID();
        String sessionID = request.getSessionID();
        try {
            templateManager.initiateTemplateBuilder(sender, GameGenre.QUIZ);
        } catch (CreationInProgressException e) {
            return new ErrorMessageResponse(sessionID, "Error: A Template is currently in creation process.");
        } return getDesignQuestion(request);
    }

    private Response handleMakeDesignChoiceRequest(MakeTemplateDesignChoiceRequest request) {
        String sender = request.getSenderID();
        String sessionID = request.getSessionID();
        try {
            templateManager.makeDesignChoice(sender, request.getUserInput());
            try {
                templateManager.buildTemplate(sender);
            } catch (InsufficientInputException e) {
                return getDesignQuestion(request);
            } catch (IOException e) {
                return new ErrorMessageResponse(sessionID, "Database not found");
            }
            return new SimpleTextResponse(sessionID, "Template successfully built!");
        }
        catch (NoCreationInProgressException e1) {
            return new ErrorMessageResponse(sessionID, "Error: No template creating is in progress.");
        }
        catch (InvalidInputException e2) {
            return new ErrorMessageResponse(sessionID,
                    "Error: Invalid input, please re-enter a different input");
        }
    }

    private Response getDesignQuestion(TemplateRequest request) {
        try {
            return new SimpleTextResponse(request.getSessionID(),
                    templateManager.getDesignQuestion(request.getSenderID()));
        }

        catch (NoCreationInProgressException e) {
            return new ErrorMessageResponse(request.getSessionID(), "Error: No game creation is in progress.");
        }
    }
}
