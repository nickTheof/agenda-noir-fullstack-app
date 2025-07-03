package gr.aueb.cf.projectmanagementapp.core.exceptions;

public class AppObjectDeletionConflictException extends AppObjectGenericException{
    private static final String DEFAULT_CODE = "DeletionConflict";

    public AppObjectDeletionConflictException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
