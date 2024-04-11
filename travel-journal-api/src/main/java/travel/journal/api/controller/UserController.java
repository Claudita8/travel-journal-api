package travel.journal.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import travel.journal.api.dto.*;
import travel.journal.api.entities.PasswordResetToken;
import travel.journal.api.entities.User;
import travel.journal.api.exception.ResourceNotFoundException;
import travel.journal.api.exception.UnauthorizedAccesException;
import travel.journal.api.service.PasswordResetTokenServiceImpl;
import travel.journal.api.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private final PasswordResetTokenServiceImpl passwordResetTokenService;

    public UserController(UserServiceImpl userServiceImpl, PasswordResetTokenServiceImpl passwordResetTokenService) {
        this.userServiceImpl = userServiceImpl;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserDTO user) {
        UserDetailsDTO newUser = userServiceImpl.createUser(user);
        if(newUser==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/userById/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Integer userId) {
        UserDetailsDTO userDetailsDTO = userServiceImpl.getUser(userId);
        if(userDetailsDTO==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDetailsDTO);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {
        List<UserDetailsDTO> users = userServiceImpl.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> modifyUser(@PathVariable("id") Integer userId,
                                                        @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDetailsDTO modifiedUser = userServiceImpl.modifyUser(userId, updateUserDTO);
        if(modifiedUser==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modifiedUser);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer userId) {
        boolean deleted= userServiceImpl.deleteUser(userId);
        if(deleted){
            return ResponseEntity.ok().build();
        }
        else{
        return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassordProcess(@RequestBody EmailResetPassword emailResetPassword) {
        Optional<User> user = userServiceImpl.findUserByEmail(emailResetPassword.getEmail());
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        boolean existTicket =passwordResetTokenService.canGenerateNewResetToken(user.get());
        if(!existTicket){
            return ResponseEntity.badRequest().body("Ai un ticket neutilizat");
        }
        User getuser=user.get();
        boolean sendemail = passwordResetTokenService.sendEmail(getuser);
        if (!sendemail) {
            return ResponseEntity.internalServerError().body("Din pacate a fost o eroare la procesarea cererii.");
        }
        return ResponseEntity.ok().body("Un email ti-a fost trimis.");
    }

    @GetMapping("/resetPassword/{token}")
    public  ResponseEntity<?> resetPasswordForm(@PathVariable String token) {
        PasswordResetToken reset = passwordResetTokenService.findByToken(token);
        if(reset==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket-ul nu exista");
        }

        if (passwordResetTokenService.hasValidResetToken(reset)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Ticket-ul a expirat sau a fost utilizat");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> passwordResetProcess(@RequestBody ResetPassword resetPassword) {
        try {
            PasswordResetToken reset = passwordResetTokenService.findByToken(resetPassword.getToken());

            if (reset == null) {
                throw new ResourceNotFoundException("Ticket-ul nu exista");
            }

            if (!passwordResetTokenService.hasValidResetToken(reset)) {
                throw new UnauthorizedAccesException("Token-ul a fost folosit sau a expirat");
            }

            User getuser = reset.getUser();
            getuser.setPassword(resetPassword.getPassword());
            userServiceImpl.saveUser(getuser);

            reset.setUsed(true);
            passwordResetTokenService.saveToken(reset);

            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (UnauthorizedAccesException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("A apărut o eroare la procesarea cererii: " + ex.getMessage());
        }
    }
}
