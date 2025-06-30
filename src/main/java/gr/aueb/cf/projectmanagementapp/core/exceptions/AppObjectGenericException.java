package gr.aueb.cf.projectmanagementapp.core.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppObjectGenericException extends Exception {
    private String code;

    public AppObjectGenericException(String code, String message) {
        super(message);
        this.code = code;
    }
}
