package system.use_cases.managers;

import shared.constants.GameGenre;
import shared.exceptions.use_case_exceptions.*;
import system.entities.template.Template;
import system.gateways.TemplateDataGateway;
import system.use_cases.editors.TemplateEditor;
import system.use_cases.factories.TemplateEditorFactory;
import system.use_cases.factories.TemplateFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TemplateManager {

    private final HashMap<String, Template> templates;
    private final TemplateDataGateway gateway;
    private final IdManager idManager;

    public TemplateManager(TemplateDataGateway gateway) throws IOException {
        templates = new HashMap<>();
        this.gateway = gateway;

        for (Template template: this.gateway.getAllTemplates()) {
            templates.put(template.getID(), template);
        }
        this.idManager = new IdManager(gateway.getTemplateCount() + 1);
    }

    public String getTemplateTitle(String templateID) {
        return templates.get(templateID).getTitle();
    }

    /**
     * Create a default template, save it, and returns the template ID.
     * */
    public void createTemplate(Map<String, String> attrMap, GameGenre type) throws
            NoSuchAttributeException, InvalidInputException {
        TemplateFactory factory = new TemplateFactory();
        Template temp = factory.getTemplate(type);
        TemplateEditorFactory editFactory = new TemplateEditorFactory();
        TemplateEditor editor = editFactory.getTemplateEditor(temp);

        for (String attrName: attrMap.keySet()) {
            editor.editAttribute(attrName, attrMap.get(attrName));
        }
        Template res = editor.getTemplate();
        res.setID(idManager.getNextId());
    }

    /**
     * Returns the default version of a template attribute map.
     * */
    public Map<String, String> getDefaultAttrMap(GameGenre type) {
        TemplateFactory factory = new TemplateFactory();
        Template temp = factory.getTemplate(type);
        TemplateEditorFactory editFactory = new TemplateEditorFactory();
        TemplateEditor editor = editFactory.getTemplateEditor(temp);
        return editor.getAttributeMap();
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
