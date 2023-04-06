package system.entities.template;

import shared.constants.GameGenre;
import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.IDNotYetSetException;


/**
 * Abstract Template Class
 */
public abstract class Template {
    private String id;
    private String title;
    /**
     * Constructor of Template
     */
    public Template(){
        id = null;
    }

    /**
     * Constructor of Template if given a template
     * @param template the inputted template
     */
    public Template(Template template){
        this.id = template.getID();
        this.title = template.getTitle();
    }

    /**
     * set template id
     * @param id the desired id
     */
    public void setID(String id) {
        if (this.id != null) {
            throw new IDAlreadySetException();
        }
        this.id = id;
    }

    /**
     * @return this template's id
     */
    public String getID() {
        if (id == null) {
            throw new IDNotYetSetException();
        }
        return id;
    }

    /**
     * set the template title
     * @param title the input title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return this template's title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return the genre of the game
     */
    public abstract GameGenre getGenre();
}
