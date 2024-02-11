package com.jorgesacristan.englishCard.controller;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.*;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for managing operations related to users.
 */
@RestController
@RequestMapping(Configuration.API_V1_PREFIX + "/users")
public class UserController{

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private ModelMapper modelMapper;

    private UserService userService;
    private final UserDtoConverter userDtoConverter;

    public UserController(UserService service, UserDtoConverter userDtoConverter) {
        this.userService=service;
        this.userDtoConverter = userDtoConverter;
    }

    /**
     * SB 3. Get all users
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping("")
    public ResponseEntity findAllUsers (Authentication userLoged) throws Exception{
        log.info("IN USER findAllUsers");
        List<User> usersResponse = new ArrayList<>();
        List<CreateUserDTO> createUsersDto = new ArrayList<CreateUserDTO>();
        try{

            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            if(userLoged.getAuthorities().contains(UserRole.ROLE_ADMIN))
                usersResponse = userService.findAllUser();
            else
                throw new BaseException("You have not permissions to get users", HttpStatus.UNAUTHORIZED.toString());

            if(usersResponse.isEmpty())
                throw new BaseException("No users found", HttpStatus.NOT_FOUND.toString());

            createUsersDto = usersResponse.stream().map(user -> modelMapper.map(user,CreateUserDTO.class)).collect(Collectors.toList());

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(createUsersDto);
    }

    /**
     * SB 4. Get one user by id
     * @param id
     * @param userLoged
     * @return
     * @throws Exception
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findById (@PathVariable Long id,Authentication userLoged) throws Exception{
        log.info("IN USER findById id: " + id);
        Optional<User> userResponse = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            User user = userService.getAndSaveUser(userLoged.getName());
            if(id!=user.getId() && !userLoged.getAuthorities().contains(UserRole.ROLE_ADMIN))
                throw new BaseException("You have not permission to get the user with id " + id,HttpStatus.UNAUTHORIZED.toString());

            userResponse = userService.findUserById(id);

            if(!userResponse.isPresent())
                throw new BaseException("User not found",HttpStatus.NOT_FOUND.toString());

            updateUserDto = modelMapper.map(userResponse, UpdateUserDto.class);


        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        return ResponseEntity.ok().body(updateUserDto);
    }

    /**
     * SB 5. Get user by username
     * @param usernameRequest
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @GetMapping("/username/{usernameRequest}")
    public ResponseEntity<?> findByUsername (@PathVariable String usernameRequest, Authentication userLoged ) throws BaseException,Exception{
        log.info("IN USER findByUsername username: "+ usernameRequest);
        Optional<User> userResponse = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            if(userLoged.getAuthorities().contains(UserRole.ROLE_ADMIN)){
                userResponse = userService.findByUsername(usernameRequest);
            }else{
                if(userLoged.getName().equals(usernameRequest))
                    userResponse = userService.findByUsername(usernameRequest);
                else
                    throw new BaseException("You have not enough permissions to get the user with name: " + usernameRequest);
            }


            updateUserDto = modelMapper.map(userResponse, UpdateUserDto.class);

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.ok().body(updateUserDto);
    }

    /**
     * SB 6. Get user by email
     * @param emailRequest
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @GetMapping("/email/{emailRequest}")
    public ResponseEntity<?> findByEmail (@PathVariable String emailRequest,Authentication userLoged) throws BaseException,Exception {
        log.info("IN USER findByEmail email: "+ emailRequest);
        Optional<User> userResponse = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            User user = userService.getAndSaveUser(userLoged.getName());
            if(!emailRequest.equals(user.getEmail()) && !userLoged.getAuthorities().contains(UserRole.ROLE_ADMIN))
                throw new BaseException("You have not permission to get the user with email " + emailRequest,HttpStatus.UNAUTHORIZED.toString());

            userResponse = userService.findByEmail(emailRequest);

            if(!userResponse.isPresent())
                throw new BaseException("User not found",HttpStatus.NOT_FOUND.toString());

            updateUserDto = modelMapper.map(userResponse, UpdateUserDto.class);

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.ok().body(updateUserDto);
    }

    /**
     * SB 7. Update user by id
     * @param id
     * @param userLoged
     * @param userRequest
     * @return
     * @throws BaseException
     * @throws Exception
     */
   /* @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById (@PathVariable Long id, @AuthenticationPrincipal User userLoged, @RequestBody @Valid UserInUpdateDto userRequest) throws BaseException,Exception {
        log.info("IN USER updateUserById id: " + id);
        User userResponse = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();

        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            Optional<User> userToUpdate = userService.findUserById(id);

            if(!userToUpdate.isPresent())
                throw new BaseException("User not found",HttpStatus.NOT_FOUND.toString());

            if(!userLoged.getRoles().contains(UserRole.ADMIN) && !userToUpdate.get().getUsername().equals(userLoged.getUsername()))
                throw new BaseException("You have not enough permissions to update the user with id " + id, HttpStatus.UNAUTHORIZED.toString());

            userResponse = userService.updateUser(userToUpdate.get(),userRequest);
            updateUserDto = modelMapper.map(userResponse, UpdateUserDto.class);

        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage(),"");
        }

        return ResponseEntity.ok().body(updateUserDto);
    }*/

    /**
     * SB 8. Add user
     * @param userRequest
     * @return
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> addUser (@RequestBody CreateUserDTO userRequest) throws BaseException {
        return userService.createUser(userRequest);
    }

    /**
     * Confirm account by token
     * @param confirmationToken
     * @return
     */
    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken) throws BaseException {
        return userService.confirmEmail(confirmationToken);
    }

    /**
     * Reset password by email
     * @param email
     * @return
     * @throws Exception
     * @throws BaseException
     *//*
    @GetMapping("/rescue-password")
    public ResponseEntity<?> rescuePassword (@RequestParam ("email") String email) throws Exception,BaseException{
        log.info("IN USER rescue-password, email: " + email);
        return userService.sendMailChangePassword(email);
    }

    *//**
     * confirm change password
     * @param savePasswordDto
     * @return
     * @throws Exception
     * @throws BaseException
     *//*
    @PostMapping("/save-password")
    public ResponseEntity<?> savePassword(@RequestBody @Valid SavePasswordDto savePasswordDto) throws Exception,BaseException{
        log.info("IN USER confirm-change-password, username: " + savePasswordDto.getUsername());
        User userResponse;
        try{
            userResponse = userService.savePassword(savePasswordDto.getUsername(),savePasswordDto.getNewPassword(), savePasswordDto.getToken());
        }catch (Exception e){
            throw e;
        }

        return ResponseEntity.ok().body(null);
    }*/


    /**
     * SB 9. Add experience to user
     * @param quantity
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @PutMapping("/experience/{quantity}")
    public ResponseEntity<?> addUserExperience (@PathVariable int quantity, @AuthenticationPrincipal User userLoged) throws BaseException,Exception {
        log.info("IN USER addUserExperience");
        User userResponse = null;
        try{
            userResponse=userService.addUserExperience(quantity,userLoged);
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }
        UpdateUserDto updateUserDto = modelMapper.map(userResponse,UpdateUserDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(updateUserDto);
    }

    /**
     * SB 10. Add gems to user
     * @param quantity
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @PutMapping("/gems/{quantity}")
    public ResponseEntity<?> addUserGems (@PathVariable int quantity, @AuthenticationPrincipal User userLoged) throws BaseException,Exception {
        log.info("IN USER addUserGems");
        User userResponse = null;
        try{
            userResponse=userService.addUserGems(quantity,userLoged);
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        UpdateUserDto updateUserDto = modelMapper.map(userResponse,UpdateUserDto.class);

        return ResponseEntity.status(HttpStatus.OK).body(updateUserDto);
    }

    /**
     * SB 11. Delete gems to user
     */

    /**
     * SB 12. Add logStreak to user
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @PutMapping("/logstreak")
    public ResponseEntity<?> addLogStreak (@AuthenticationPrincipal User userLoged) throws BaseException,Exception {
        log.info("IN USER addLogStreak");
        try{
            userService.addLogStreak(userLoged);
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * SB 13. Delete log
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     */
    @DeleteMapping("/logstreak")
    public ResponseEntity<?> resetLogStreak (@AuthenticationPrincipal User userLoged) throws BaseException,Exception {
        log.info("IN USER resetLogStreak");
        try{
            userService.resetLogStreak(userLoged);
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    /**
     * SB 14. delete user
     * @param id
     * @param userLoged
     * @return
     * @throws BaseException
     * @throws Exception
     *//*
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id, @AuthenticationPrincipal User userLoged) throws BaseException,Exception{
        log.info("IN USER deleteUserById id: " + id);

        try{
            userService.deleteUser(id, userLoged);
        }catch (BaseException e) {
            throw new BaseException(e.getMessage(),e.getCode());
        }
        catch (Exception e){
            throw new BaseException(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }*/

}








