package system.gateways;

import system.entities.template.Template;

import java.io.*;
import java.util.Set;

/**
 * TemplateDataGateway Interface
 */
public interface TemplateDataGateway {
    /**
     * Adds a template to the database and increments the template count by 1.
     *
     * @param template the template to add
     * @throws IOException if there is a problem saving to the database
     */
    void addTemplate(Template template) throws IOException;

    /**
     * Updates the input user in the database.
     *
     * @param template the template to update
     * @throws IOException if the database is not found
     */
    void updateTemplate(Template template) throws IOException;

    /**
     * Deletes the template with the specified id from the database.
     *
     * @param templateId templateId of the template to delete
     * @throws IOException if the database is not found
     */
    void deleteTemplate(String templateId) throws IOException;

    /**
     * Returns a set of all templates in the database.
     *
     * @return a set of all templates in the database
     * @throws IOException if there is a problem reading from the database
     */
    Set<Template> getAllTemplates() throws IOException;

    /**
     * Returns the total number of templates ever created by the program.
     * <p>
     * This number does not decrease when a template is deleted.
     *
     * @return the total number of templates ever created
     * @throws IOException if there is a problem reading from the database
     */
    int getTemplateCount() throws IOException;
}
