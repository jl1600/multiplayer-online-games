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

/**
 * TemplateManager Class
 */
public class TemplateManager {

    private final HashMap<String, Template> templates;
    private final TemplateDataGateway gateway;
    private final IdManager idManager;

    /**
     * Constructor of Template Manager
     * @param gateway the gateway used to communicate with database
     * @throws IOException If issue regarding input-output is detected
     */
    public TemplateManager(TemplateDataGateway gateway) throws IOException {
        templates = new HashMap<>();
        this.gateway = gateway;

        for (Template template: this.gateway.getAllTemplates()) {
            templates.put(template.getID(), template);
        }
        this.idManager = new IdManager(gateway.getTemplateCount() + 1);
    }

    /**
     * get the title of a specified template
     * @param templateID the specified template's id
     * @return the specified template's title
     */
    public String getTemplateTitle(String templateID) {
        return templates.get(templateID).getTitle();
    }

    /**
     * Create a default template, save it, and returns the template ID.
     * */
    public void createTemplate(Map<String, String> attrMap, GameGenre type) throws
            NoSuchAttributeException, InvalidInputException {

        Template temp =  new TemplateFactory().getTemplate(type);
        temp.setID(idManager.getNextId());
        TemplateEditor editor = new TemplateEditorFactory().getTemplateEditor(temp);

        for (String attrName: attrMap.keySet()) {
            editor.editAttribute(attrName, attrMap.get(attrName));
        }
        Template res = editor.getTemplate();
        addTemplate(res);
    }

    /**
     * Returns the default version of a template attribute map.
     * */
    public Map<String, String> getDefaultAttrMap(GameGenre type) {
        Template temp =  new TemplateFactory().getTemplate(type);
        temp.setID("-1");
        TemplateEditor editor = new TemplateEditorFactory().getTemplateEditor(temp);
        return editor.getAttributeMap();
    }

    /**
     * edit a specified template by specified attribute values
     * @param attrMap a map of specified attribute vlaues
     * @param templateID the specified template's id
     * @throws NoSuchAttributeException specified attribute is in template's attribute last or attribute is null
     * @throws InvalidInputException when parameters are illegal and passed a null value
     * @throws InvalidTemplateIDException specified template id is not in template list or id is null
     */
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

    /**
     * get a specified templateID's attribute map
     * @param templateID the specified template's id
     * @return that specified template's attribute map
     * @throws InvalidTemplateIDException specified template id is not in template list or id is null
     */
    public Map<String, String> getAttributeMap(String templateID) throws InvalidTemplateIDException {

        if (!templates.containsKey(templateID))
            throw new InvalidTemplateIDException();

        TemplateEditorFactory factory = new TemplateEditorFactory();
        TemplateEditor editor = factory.getTemplateEditor(templates.get(templateID));
        return editor.getAttributeMap();
    }

    private void addTemplate(Template template) {
        templates.put(template.getID(), template);
        try {
            gateway.addTemplate(template);
        } catch (IOException e) {
            throw new RuntimeException("Fatal: Cannot save template data.");
        }
    }

    /**
     * get all template ids
     * @return all template ids in a set
     */
    public Set<String> getAllTemplateIDs() {
       return new HashSet<>(templates.keySet());
    }

    /**
     * get specific template based on specified id
     * @param id the specified id
     * @return the specific template
     * @throws InvalidIDException id is not in template list or id is null
     */
    public Template getTemplate(String id) throws InvalidIDException {
        if (!templates.containsKey(id))
            throw new InvalidIDException();

        return templates.get(id);
    }

    /**
     * Remove a specified template based on specified id
     * @param templateID the specified template id
     * @throws IOException if issue regarding input-output is detected
     * @throws InvalidIDException if the specifed id is not in the template's list or is null
     */
    public void deleteTemplate(String templateID) throws IOException, InvalidIDException {
        if (!templates.containsKey(templateID)) {
            throw new InvalidIDException();
        }
        templates.remove(templateID);
        gateway.deleteTemplate(templateID);
    }
}
