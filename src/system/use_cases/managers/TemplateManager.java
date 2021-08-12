package system.use_cases.managers;


import shared.DTOs.Responses.TemplateAllAttrsResponseBody;
import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import sun.security.x509.InvalidityDateExtension;
import system.entities.template.Template;
import system.gateways.TemplateDataGateway;
import system.use_cases.builders.interactive_builders.TemplateInteractiveBuilder;
import system.use_cases.editors.TemplateEditor;
import system.use_cases.factories.TemplateBuilderFactory;
import system.use_cases.factories.TemplateEditorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateManager {

    private final HashMap<String, Template> templates;
    private final HashMap<String, TemplateInteractiveBuilder> templateBuilders; // mapping of username to builder object
    private final TemplateDataGateway gateway;
    private final IdManager idManager;

    public TemplateManager(TemplateDataGateway gateway) throws IOException {
        templates = new HashMap<>();
        templateBuilders = new HashMap<>();
        this.gateway = gateway;

        for (Template template: this.gateway.getAllTemplates()) {
            templates.put(template.getID(), template);
        }
        this.idManager = new IdManager(gateway.getTemplateCount() + 1);
    }

    public String getTemplateTitle(String templateID) {
        return templates.get(templateID).getTitle();
    }

    public void editTemplate(Map<String, String> attrMap, String templateID) throws
            NoSuchAttributeException, InvalidInputException, InvalidTemplateIDException {

        if (!templates.containsKey(templateID))
            throw new InvalidTemplateIDException();

        TemplateEditorFactory factory = new TemplateEditorFactory();
        TemplateEditor editor = factory.getTemplateEditor(templates.get(templateID));
        for (String attrName: attrMap.keySet()) {
            editor.editAttribute(attrName, attrMap.get(attrName));
        }
        templates.put(templateID, editor.getTemplate());
        try {
            gateway.updateTemplate(templates.get(templateID));
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to database to update data.");
        }
    }

    public Map<String, String> getAttributeMap(String templateID) throws InvalidTemplateIDException {

        if (!templates.containsKey(templateID))
            throw new InvalidTemplateIDException();

        TemplateEditorFactory factory = new TemplateEditorFactory();
        TemplateEditor editor = factory.getTemplateEditor(templates.get(templateID));
        return editor.getAttributeMap();
    }

    public void initiateTemplateBuilder(String creatorName, GameGenre type) throws CreationInProgressException {

        if (templateBuilders.containsKey(creatorName)) {
            throw new CreationInProgressException();
        }

        TemplateBuilderFactory factory = new TemplateBuilderFactory();
        TemplateInteractiveBuilder builder = factory.getTemplateBuilder(type);

        templateBuilders.put(creatorName, builder);
    }

    public String getDesignQuestion(String creatorName) throws NoCreationInProgressException {

        if (!templateBuilders.containsKey(creatorName)) {
            throw new NoCreationInProgressException();
        }
        return templateBuilders.get(creatorName).getDesignQuestion();
    }

    public void makeDesignChoice(String creatorName, String designChoice)
            throws NoCreationInProgressException, InvalidInputException {

        if (!templateBuilders.containsKey(creatorName)) {
            throw new NoCreationInProgressException();
        }

        templateBuilders.get(creatorName).makeDesignChoice(designChoice);
    }

    public void buildTemplate(String creatorName)
            throws NoCreationInProgressException, InsufficientInputException, IOException {

        if (!templateBuilders.containsKey(creatorName)) {
            throw new NoCreationInProgressException();
        }

        if (!templateBuilders.get(creatorName).isReadyToBuild()) {
            throw new InsufficientInputException();
        }

        addTemplate(templateBuilders.get(creatorName).build(idManager.getNextId()));
        templateBuilders.remove(creatorName);
    }

    private void addTemplate(Template template) throws IOException {
        templates.put(template.getID(), template);
        gateway.addTemplate(template);
    }

    public Set<String> getAllTemplateIDs() {
       return new HashSet<>(templates.keySet());
    }

    public Template getTemplate(String id) throws InvalidIDException {
        if (!templates.containsKey(id))
            throw new InvalidIDException();

        return templates.get(id);
    }

    public void deleteTemplate(String templateID) throws IOException, InvalidIDException {
        if (!templates.containsKey(templateID)) {
            throw new InvalidIDException();
        }
        templates.remove(templateID);
        gateway.deleteTemplate(templateID);
    }

}
