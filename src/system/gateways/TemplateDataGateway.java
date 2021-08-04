package system.gateways;

import system.entities.template.Template;

import java.io.*;
import java.util.HashSet;

public interface TemplateDataGateway {
    String path = System.getProperty("user.dir");
    String templateFolderPath = path + "/src/system/database/templates/";
    File templateCountFile = new File(path + "/src/system/database/countFiles/template.txt");

    void addTemplate(Template template) throws IOException;

    void updateTemplate(Template template) throws IOException;

    void deleteTemplate(String templateID) throws IOException;

    HashSet<Template> getAllTemplates() throws IOException;

    int getTemplateCount() throws IOException;
}
