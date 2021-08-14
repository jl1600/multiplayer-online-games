package system.gateways;

import system.entities.template.Template;

import java.io.*;
import java.util.Set;

public interface TemplateDataGateway {

    void addTemplate(Template template) throws IOException;

    void updateTemplate(Template template) throws IOException;

    void deleteTemplate(String templateID) throws IOException;

    Set<Template> getAllTemplates() throws IOException;

    int getTemplateCount() throws IOException;
}
