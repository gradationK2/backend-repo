package core.backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserProfileUpdateRequest {
    private String name;
    private String nationality;
    private MultipartFile image;
}
