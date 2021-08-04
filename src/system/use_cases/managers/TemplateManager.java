package system.use_cases.managers;


import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import system.entities.template.Template;
import system.gateways.TemplateDataGateway;
import system.use_cases.builders.interactive_builders.TemplateInteractiveBuilder;
import system.use_cases.editors.TemplateEditor;
import system.use_cases.factories.TemplateBuilderFactory;
import system.use_cases.factories.TemplateEditorFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateManager {

    private final HashMap<String, Template> templates;
    private final HashMap<String, TemplateInteractiveBuilder> templateBuilders; // mapping of username to builder object
    private final HashMap<String, TemplateEditor> templateEditors;
    private final TemplateDataGateway gateway;
    private final IdManager idManager;

    public TemplateManager(TemplateDataGateway gateway) throws IOException {
        templates = new HashMap<>();
        templateBuilders = new HashMap<>();
        templateEditors = new HashMap<>();
        this.gateway = gateway;

        for (Template template: this.gateway.getAllTemplates()) {
            templates.put(template.getID(), template);
        }
        this.idManager = new IdManager(gateway.getTemplateCount() + 1);
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

    public Map<String, String> getAllIdAndTitles() {
       Map<String, String> idToTile = new HashMap<>();
        for (String id: templates.keySet()) {
            idToTile.put(id, templates.get(id).getTitle());
        }
        return idToTile;
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

    /**
     * Returns a mapping of attribute names to string representations of attribute values of the target template.
     * This method also creates a TemplateEditor object that can be stored
     *
     * @param templateID The unique string identifier of the template.
     * @return attribute names to string representations of attribute values of the template.
     * */
    public Map<String, String> startTemplateEdit(String templateID)
            throws InvalidTemplateIDException, EditingInProgressException {
        if (templateEditors.containsKey(templateID)) {
            throw new EditingInProgressException();
        } else if (!templates.containsKey(templateID)) {
            throw new InvalidTemplateIDException();
        }
        TemplateEditorFactory factory = new TemplateEditorFactory();
        TemplateEditor editor = factory.getTemplateEditor(templates.get(templateID));
        templateEditors.put(templateID, editor);

        return editor.getAttributeMap();
    }
    public void editTemplate(String templateID, String attributeName, String value)
            throws NoEditingInProgressException, NoSuchAttributeException, InvalidInputException {
        if (!templateEditors.containsKey(templateID))
            throw new NoEditingInProgressException();

        templateEditors.get(templateID).editAttribute(attributeName, value);
    }

    public void cancelTemplateEdit(String templateID) throws NoEditingInProgressException {
        if (!templateEditors.containsKey(templateID))
            throw new NoEditingInProgressException();

        templateEditors.remove(templateID);
    }

    public void saveTemplateEdit(String templateID) throws NoEditingInProgressException {
        if (!templateEditors.containsKey(templateID))
            throw new NoEditingInProgressException();

        templates.put(templateID, templateEditors.get(templateID).getTemplate());
        templateEditors.remove(templateID);
        try {
            gateway.updateTemplate(templates.get(templateID));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
