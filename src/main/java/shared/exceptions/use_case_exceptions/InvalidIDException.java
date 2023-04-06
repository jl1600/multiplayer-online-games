package shared.exceptions.use_case_exceptions;

import shared.constants.IDType;

/**
 * InvalidIDException Class
 */
public class InvalidIDException extends Exception{
    private final IDType type;
    public InvalidIDException(IDType type) {
        this.type = type;
    }

    public IDType getIDType() {
        return this.type;
    }
}
