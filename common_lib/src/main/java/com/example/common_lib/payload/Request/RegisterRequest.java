package com.example.common_lib.payload.Request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK")
    @Size(min = 3 , max = 50, message ="VALIDATION.REGISTRATION.FIRSTNAME.SIZE")
    private String firstname;

    @NotBlank(message = "VALIDATION.REGISTRATION.LASTNAME.NOT_BLANK")
    @Size(min = 3 , max = 50, message ="VALIDATION.REGISTRATION.LASTNAME.SIZE")
    private String lastname;

    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.NOT_BLANK")
    @Email(message = "VALIDATION.REGISTRATION.EMAIL.NOT_FORMAT")
    //@NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")
    private String email;

    private String phone;

   @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
  @Size(min = 6 , max = 50, message ="VALIDATION.REGISTRATION.PASSWORD.SIZE")
   @Pattern(
          regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = "VALIDATION.REGISTRATION.PASSWORD.WEAK"
   )
    private String password;




}
