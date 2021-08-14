package system.gateways;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import system.entities.template.HangmanTemplate;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;

public class TemplateDataMapper implements TemplateDataGateway {

    //Gson gson = new Gson();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String[] SUB_FOLDERS = {"quiz/" , "hangman/"};
    private final String SUFFIX = ".json";

    /**
     * Adds a template to the database and increases the total number of templates created by 1
     *
     * @param template template to add to the database
     * @throws IOException if the database is not found
     */
    public void addTemplate(Template template) throws IOException {
        addTemplate(template, true);
    }

    /**
     * Updates the user in the database
     *
     * @param template template do update
     * @throws IOException if the database is not found
     */
    public void updateTemplate(Template template) throws IOException {
        deleteTemplate(template.getID());
        addTemplate(template, false);
    }

    /**
     * Deletes the template with the specified id from the database
     *
     * @param templateID id of the template to delete
     * @throws IOException if the database is not found
     */
    public void deleteTemplate(String templateID) throws IOException {
        boolean deleted = false;
        for (String subfolder : SUB_FOLDERS) {
            File file = new File(templateFolderPath + subfolder + templateID + SUFFIX);
            if (file.delete()) {
                deleted = true;
            }
        }
        if (!deleted) {
            throw new IOException();
        }
    }

    /**
     * @return all Template entities in the template database
     * @throws IOException if the database is not found
     */
    public HashSet<Template> getAllTemplates() throws IOException {
        HashSet<Template> templates = new HashSet<>();

        for (String subfolder : SUB_FOLDERS) {
            File folder = new File(templateFolderPath + subfolder);
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.getName().equals(".gitkeep")) {
                    continue;
                }
                String templateString = String.join("\n", Files.readAllLines(file.toPath()));
                Template template = jsonToTemplate(templateString, subfolder);
                templates.add(template);
            }
        }
        return templates;
    }

    /**
     * @return number of templates ever created. This number does not decrease when a template is deleted
     * @throws IOException if the database is not found
     */
    public int getTemplateCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(templateCountFile));
        return new Integer(rd.readLine());
    }

    private String templateToJson(Template template) {
        return gson.toJson(template);
    }

    private Template jsonToTemplate(String templateString, String subfolder) {
        if (subfolder.equals(SUB_FOLDERS[0])) {
            return gson.fromJson(templateString, QuizTemplate.class);
        } else if (subfolder.equals(SUB_FOLDERS[1])) {
            return gson.fromJson(templateString, HangmanTemplate.class);
        } else {
            throw new RuntimeException();
        }
    }

    private void incrementTemplateCount() throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader(templateCountFile));
        int count = Integer.parseInt(rd.readLine()) + 1;
        rd.close();

        Writer wr = new FileWriter(templateCountFile, false);
        wr.write(count + System.getProperty("line.separator"));
        wr.close();
    }

    private void addTemplate(Template template, boolean increment) throws IOException {
        String subfolder;
        if (template instanceof QuizTemplate) {
            subfolder = SUB_FOLDERS[0];
        } else if (template instanceof HangmanTemplate) {
            subfolder = SUB_FOLDERS[1];
        } else {
            throw new RuntimeException();
        }

        File templateFile = new File(templateFolderPath + subfolder + template.getID() + SUFFIX);
        Writer wr = new FileWriter(templateFile);
        wr.write(templateToJson(template));
        wr.close();

        if (increment) {
            incrementTemplateCount();
        }
    }
}