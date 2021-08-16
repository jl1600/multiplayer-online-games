package system.use_cases.managers;

/**
 * IdManager Class
 */
public class IdManager {
    private Integer nextId;

    /**
     * @param start the id to start counting from
     */
    public IdManager(int start) {
        nextId = start;
    }

    /**
     * @return a unique id that has not been used before
     */
    public String getNextId() {
        return (nextId++).toString();
    }

}
