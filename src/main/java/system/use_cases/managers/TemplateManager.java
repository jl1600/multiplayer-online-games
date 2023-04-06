package system.use_cases.managers;

import shared.constants.GameGenre;
import shared.constants.IDType;
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
     * Gets the title of a specified template
     * @param templateID the specified template's id
     * @return the specified template's title
     */
    public String getTemplateTitle(String templateID) {
        return templates.get(templateID).getTitle();
    }

    /**
     * Creates a default template, save it, and returns the template ID
     * @param attrMap a map of template design questions to the choices that the user inputed/selected
     * @param type the genre of the game
     * @throws InvalidInputException if the template id does not exist. This should never be raised because
     *                               we just created the template
     */
    public void createTemplate(Map<String, String> attrMap, GameGenre type) throws
            InvalidInputException {

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
     * @return the default version of a template attribute map
     * */
    public Map<String, String> getDefaultAttrMap(GameGenre type) {
        Template temp =  new TemplateFactory().getTemplate(type);
        temp.setID("-1");
        TemplateEditor editor = new TemplateEditorFactory().getTemplateEditor(temp);
        return editor.getAttributeMap();
    }

    /**
     * Gets the genre of the game given its template
     *
     * @param templateID the template that the game implements
     * @return the genre, a.k.a the type of the template
     * @throws InvalidIDException if the template id does not exist
     */
    public GameGenre getGenre(String templateID) throws InvalidIDException {
        if (!templates.containsKey(templateID))
            throw new InvalidIDException(IDType.TEMPLATE);

        return templates.get(templateID).getGenre();
    }


    /**
     * Edits a specified template by specified attribute values
     *
     * @param attrMap a map of specified attribute values
     * @param templateID the specified template's id
     * @throws InvalidInputException when parameters are illegal and passed a null value
     * @throws InvalidIDException specified template id is not in template list or id is null
     */
    public void editTemplate(Map<String, String> attrMap, String templateID) throws
            InvalidInputException, InvalidIDException {

        if (!templates.containsKey(templateID))
            throw new InvalidIDException(IDType.TEMPLATE);

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
     * Gets a specified templateID's attribute map
     *
     * @param templateID the specified template's id
     * @return that specified template's attribute map
     * @throws InvalidIDException specified template id is not in template list or id is null
     */
    public Map<String, String> getAttributeMap(String templateID) throws InvalidIDException {

        if (!templates.containsKey(templateID))
            throw new InvalidIDException(IDType.TEMPLATE);

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
     * @return all template ids in a set
     */
    public Set<String> getAllTemplateIDs() {
       return new HashSet<>(templates.keySet());
    }

    /**
     * Gets specific template based on specified id
     *
     * @param id the specified id
     * @return the specific template
     * @throws InvalidIDException id is not in template list or id is null
     */
    public Template getTemplate(String id) throws InvalidIDException {
        if (!templates.containsKey(id))
            throw new InvalidIDException(IDType.TEMPLATE);

        return templates.get(id);
    }
}
