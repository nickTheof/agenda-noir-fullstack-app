package gr.aueb.cf.projectmanagementapp.dto;

public record UserUpdateDTO(
        String firstname,
        String lastname,
        String password,
        Boolean enabled,
        Boolean verified
) {
    public UserUpdateDTO(String password) {
        this(null, null, password, null, null);
    }
}
