package system.entities.template;

import shared.exceptions.entities_exception.IDAlreadySetException;
import shared.exceptions.entities_exception.IDNotYetSetException;


public abstract class Template {
    private String id;
    private String title;

    public Template(){
        id = null;
    }

    public Template(Template template){
        this.id = template.getID();
        this.title = template.getTitle();
    }

    public void setID(String id) {
        if (this.id != null) {
            throw new IDAlreadySetException();
        }
        this.id = id;
    }

    public String getID() {
        if (id == null) {
            throw new IDNotYetSetException();
        }
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
