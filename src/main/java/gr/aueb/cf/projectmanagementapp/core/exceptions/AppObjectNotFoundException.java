package gr.aueb.cf.projectmanagementapp.core.exceptions;

public class AppObjectNotFoundException extends AppObjectGenericException{
    private static final String DEFAULT_CODE = "NotFound";

    public AppObjectNotFoundException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
