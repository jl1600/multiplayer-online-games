package system.gateways;

import shared.exceptions.use_case_exceptions.InsufficientInputException;
import system.entities.template.HangmanTemplate;
import system.entities.template.QuizTemplate;
import system.entities.template.Template;

import java.io.*;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Objects;

public class TemplateDataMapper implements TemplateDataGateway {
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
        File file = new File(templateFolderPath + templateID + ".txt");
        if (!file.delete()) {
            throw new IOException();
        }
    }

    /**
     * @return all Template entities in the template database
     * @throws IOException if the database is not found
     */
    public HashSet<Template> getAllTemplates() throws IOException {
        File folder = new File(templateFolderPath);
        HashSet<Template> templates = new HashSet<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            String templateString = String.join(",", Files.readAllLines(file.toPath()));
            Template template = stringToTemplate(templateString);
            templates.add(template);
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

    private String templateToString(Template template) {
        if (template instanceof QuizTemplate) {
            return quizTemplateToString((QuizTemplate) template);
        } else if (template instanceof HangmanTemplate) {
            return hangmanTemplateToString((HangmanTemplate) template);
        } else {
            return "";
        }
    }

    private String quizTemplateToString(QuizTemplate template) {
        return "Quiz" + "," +
                template.getID() + "," +
                template.getTitle().replace(",", "-") + "," +
                template.hasMultipleScoreCategories() + "," +
                template.hasScoreWeight() + "," +
                template.hasCustomEndingMessage() + "," +
                template.isChooseAllThatApply() + "," +
                template.isMultipleChoice();
    }

    private String hangmanTemplateToString(HangmanTemplate template) {
        return "Hangman" + "," +
                template.getID() + "," +
                template.getTitle().replace(",", "-") + "," +
                template.getNumPuzzles() + "," +
                template.getNumLives() + "," +
                template.getNumHints();
    }

    private Template stringToTemplate(String templateString) {
        String type = templateString.split(",", 2)[0];
        if (type.equals("Quiz")) {
            return stringToQuizTemplate(templateString);
        } else if (type.equals("Hangman")) {
            return stringToHangmanTemplate(templateString);
        } else {
            throw new RuntimeException();
        }
    }

    private Template stringToQuizTemplate(String templateString) {
        String[] info = templateString.split(",");
        QuizTemplate template = new QuizTemplate();
        template.setID(info[1]);
        template.setTitle(info[2]);
        template.setHasMultipleScoreCategories(Boolean.parseBoolean(info[3]));
        template.setHasScoreWeight(Boolean.parseBoolean(info[4]));
        template.setHasCustomEndingMessage(Boolean.parseBoolean(info[5]));
        template.setChooseAllThatApply(Boolean.parseBoolean(info[6]));
        template.setMultipleChoice(Boolean.parseBoolean(info[7]));
        return template;
    }

    private Template stringToHangmanTemplate(String templateString) {
        String[] info = templateString.split(",");
        HangmanTemplate template = new HangmanTemplate();
        template.setID(info[1]);
        template.setTitle(info[2]);
        try {
            template.setNumPuzzles(Integer.parseInt(info[3]));
            template.setNumLives(Integer.parseInt(info[4]));
            template.setNumHints(Integer.parseInt(info[5]));
        } catch (InsufficientInputException e) {
            throw new RuntimeException();
        }
        return template;
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
        File templateFile = new File(templateFolderPath + template.getID() + ".txt");
        Writer wr = new FileWriter(templateFile);
        wr.write(templateToString(template));
        wr.close();

        if (increment) incrementTemplateCount();
    }
}

