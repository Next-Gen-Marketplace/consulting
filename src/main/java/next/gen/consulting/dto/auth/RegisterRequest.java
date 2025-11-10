package next.gen.consulting.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 20, message = "Пароль должен быть от 6 до 20 символов")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%.*?&])[A-Za-z\\d@$!%.*?&]{6,20}$",
            message = "Пароль должен содержать хотя бы одну строчную букву, одну заглавную букву, одну цифру и один специальный символ"
    )
    private String password;
    
    @NotBlank(message = "Имя обязательно")
    @Size(min = 2, max = 255, message = "Имя должно быть от 2 до 255 символов")
    private String fullName;
    
    @NotBlank(message = "Телефон обязателен")
    @Pattern(
            regexp = "^\\+?[0-9]{1,3}(\\s[0-9]{1,4}){2,5}$",
            message = "Телефон должен быть в формате вроде +7 777 000 00 00"
    )
    private String phone;
}
