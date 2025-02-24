package core.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String name;
    private String nationality;
    private String photoUrl;
}
