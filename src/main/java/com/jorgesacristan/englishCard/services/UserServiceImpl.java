package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.configuration.Configuration;
import com.jorgesacristan.englishCard.dtos.CreateUserDTO;
import com.jorgesacristan.englishCard.dtos.UserAuthDto;
import com.jorgesacristan.englishCard.dtos.UserInUpdateDto;
import com.jorgesacristan.englishCard.enums.Provider;
import com.jorgesacristan.englishCard.enums.UserRole;
import com.jorgesacristan.englishCard.exceptions.BaseException;
import com.jorgesacristan.englishCard.models.ConfirmationToken;
import com.jorgesacristan.englishCard.models.User;
import com.jorgesacristan.englishCard.repositories.ConfirmationTokenRepository;
import com.jorgesacristan.englishCard.repositories.UserRepository;
import com.jorgesacristan.englishCard.response.ApiResponse;
import com.jorgesacristan.englishCard.response.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.management.relation.Role;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;

    /*public UserServiceImpl(PasswordEncoder passwordEncoder){
       this.passwordEncoder = passwordEncoder;
    }*/

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private Configuration configuration;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @SneakyThrows
    @Override
    public Optional<User> findUserById(Long id){
        return this.userRepository.findById(id);
    }

    @Override
    public ResponseEntity<?> createUser(CreateUserDTO newUserRequest) throws BaseException{
        log.info("IN USER addUser username: " + newUserRequest.getUsername());

        User user = User.builder()
                .username(newUserRequest.getUsername())
                .password(passwordEncoder.encode(newUserRequest.getPassword()))
                .isEnabled(false)
                .avatar("")
                .roles(Collections.singleton(UserRole.ROLE_USER))
                .email(newUserRequest.getEmail())
                .provider(Provider.LOCAL)
                .isEnabled(false)
                .build();

        try {

            this.userRepository.save(user);
            ConfirmationToken confirmationToken = getConfirmationToken(user);

            String text = "To confirm your account, please click here : "
                    +"http://"+ configuration.getUrlAws() +"/check-token/token/"+confirmationToken.getConfirmationToken()+"/register/1";
            emailService.sendEmail(user.getEmail(),"Complete Registration!",text);

            System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());
            log.info("OUT USER addUser username: " + newUserRequest.getUsername());
            //return ResponseEntity.ok().build();
            return responseBuilder.buildResponse(
                    HttpStatus.OK.value(),
                    String.format("User %s saved successfully, waitting confirmation via email", user.getUsername()),
                    user);
        }
        catch (DataIntegrityViolationException ex) {
            throw new BaseException( "The username already exist",HttpStatus.BAD_REQUEST.toString());
        }

    }

   /* @Override
    public User updateUser(User userToUpdate, UserInUpdateDto userRequest) throws Exception {
        User userResponse = null;
        try {
            userToUpdate.setUsername(userRequest.getUsername());
            userToUpdate.setAvatar(userRequest.getAvatar());
            userToUpdate.setEmail(userRequest.getEmail());
            userToUpdate.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            userToUpdate.setLevel(userRequest.getLevel());
            userToUpdate.setExperience(userRequest.getExperience());
            userToUpdate.setLogStreak(userRequest.getLogStreak());
            userToUpdate.setGems(userRequest.getGems());
            userResponse = userRepository.save(userToUpdate);
        }
        catch (Exception e){
            throw e;
        }

        return userResponse;
    }*/


    /*@Override
    public void deleteUser(Long id, User userLoged) throws BaseException,Exception{
        try{
            if(userLoged==null)
                throw new BaseException("Authentication error, try to login again", HttpStatus.UNAUTHORIZED.toString());

            Optional<User> userToUpdate = userRepository.findById(id);

            if(!userToUpdate.isPresent())
                throw new BaseException("User not found",HttpStatus.NO_CONTENT.toString());
//            if(!userLoged.getRoles().contains(UserRole.ADMIN)){
//                if(!userLoged.getUsername().equals(userToUpdate.get().getUsername())){
//                    throw new BaseException("You have not permission to delete the user with id " + id ,HttpStatus.UNAUTHORIZED.toString());
//                }
//            }

        }catch (BaseException e) {
            throw e;
        }
        catch (Exception e){
            throw e;
        }

        this.userRepository.deleteById(id);


    }*/

    /*public User findByUsernamePassword(String username, String password){
        try{
            List<User> usuarios = userRepository.findAll();
            for(int i=0;i<usuarios.size();i++){
                log.info(usuarios.get(i).getUsername());
                if(usuarios.get(i).getUsername().equals(username) &&
                        usuarios.get(i).getPassword().equals(password))
                    return usuarios.get(i);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

       return null;
    }*/



    /*@Override
    public User findByUsername(String username) {
        List<User> usuarios = super.findAll();
        for(int i=0;i<usuarios.size();i++){
            log.info(usuarios.get(i).getUsername());
            if(usuarios.get(i).getUsername().equals(username))
                return usuarios.get(i);
        }

        return null;
    }*/

    @Override
    public Optional<User> findByUsername(String username){
        return this.userRepository.findByUsername(username);
    }




    @Override
    public Optional<User> findByEmail(String email) throws Exception
    {
        Optional<User> user = null;
        try{
            user=userRepository.findByEmail(email);
        }catch (Exception e){
            throw e;
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User addUserExperience(int quantity, User userLoged) throws BaseException,Exception{
        Optional<User> user = null;
        User userResponse = null;

        int experience = 0;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                if(addExperience (user.get(),quantity))
                    userResponse=userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }


    @Override
    public User addUserGems (int quantity, User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        User userResponse = null;

        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setGems(user.get().getGems()+quantity);
                userResponse=userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }

    @Override
    public void addLogStreak (User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setLogStreak(user.get().getLogStreak()+1);
                addExperience(user.get(),calculateLogStreakExperience(user.get().getLogStreak()));
                userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }
    }

    private Boolean addExperience(User user, int quantity) {
        int experienceTemp = 0;
        int levelUp = 0;

        if(user.getLevel()<Configuration.LEVEL_MAX){
            experienceTemp = user.getExperience() + quantity;
            if(experienceTemp>Configuration.EXPERIENCE_MAX){
                while (experienceTemp-Configuration.EXPERIENCE_MAX>0){
                    experienceTemp = experienceTemp - Configuration.EXPERIENCE_MAX;
                    levelUp++;
                }
                user.setLevel(user.getLevel()+levelUp);
                if(user.getLevel()>Configuration.LEVEL_MAX)
                    user.setLevel(Configuration.LEVEL_MAX);

                user.setExperience(experienceTemp);

            }else if(experienceTemp==Configuration.EXPERIENCE_MAX){
                user.setLevel(user.getLevel()+1);
                if(user.getLevel()>Configuration.LEVEL_MAX)
                    user.setLevel(Configuration.LEVEL_MAX);
                user.setExperience(0);
            }else{
                user.setExperience(experienceTemp);
            }

            return true;
        }else
            return false;
    }

    private int calculateLogStreakExperience (int actualLogStreak){
        if(actualLogStreak==1)
            return 10;
        else if (actualLogStreak==2)
            return 20;
        else if (actualLogStreak==3)
            return 30;
        else if (actualLogStreak==4)
            return 40;
        else if (actualLogStreak==5)
            return 50;
        else if (actualLogStreak==6)
            return 60;
        else if (actualLogStreak==7)
            return 70;
        else if (actualLogStreak==8)
            return 80;
        else if (actualLogStreak==9)
            return 90;
        else
            return 100;
    }

    @Override
    public void resetLogStreak (User userLoged) throws BaseException,Exception {
        Optional<User> user = null;
        try{
            user=userRepository.findByUsername(userLoged.getUsername());
            if(user.isPresent()){
                user.get().setLogStreak(0);
                userRepository.save(user.get());
            }else
                throw new BaseException("User not found");
        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public User getAndSaveUser(String username) throws BaseException {
        Optional<User> user = userRepository.findByUsername(username);
        if(!user.isPresent()){
            User newUser = new User();
            newUser.setUsername(username);
            user = Optional.of(userRepository.save(newUser));
        }
        return user.get();
    }

    @Override
    public User getAndSaveUserFromOauth2(OAuth2User userLoged) throws BaseException {
        Optional<User> user = userRepository.findByEmail((String) userLoged.getAttributes().get("email"));
        if(!user.isPresent()){
            User newUser = new User();
            newUser.setUsername((String) userLoged.getAttributes().get("given_name"));
            newUser.setEmail((String) userLoged.getAttributes().get("email"));
            newUser.setProvider(Provider.GOOGLE);
            user = Optional.of(userRepository.save(newUser));
        }
        return user.get();
    }


    /*@Override
    public void processOAuthPostLogin(String username) {

        Optional<User> existUser = userRepository.findByUsername(username);

        if (!existUser.isPresent()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEnabled(true);

            userRepository.save(newUser);

            System.out.println("Created new user: " + username);
        }

    }
*/


    @Override
    public ResponseEntity<?> sendMailChangePassword(String email) throws Exception,BaseException{
        Optional<User> user = null;
        ConfirmationToken confirmationToken;
        try {
            user = userRepository.findByEmail(email);
            if (user.isPresent()) {
                confirmationToken = getConfirmationToken(user.get());
                String text = "To confirm your account, please click here : "
                        +"http://"+configuration.getUrlAws() +":80/new-password/token/"+confirmationToken.getConfirmationToken();
//                String text = "If you have requested a password change on your word flash card account, please use the following link to confirm it:   : "
//                        + "http://localhost:8080/api/v1/users/confirm-change-password?token=" + confirmationToken.getConfirmationToken();
                emailService.sendEmail(user.get().getEmail(), "Confirm password change", text);
            } else
                throw new BaseException(String.format("Email user not found"), HttpStatus.NOT_FOUND.toString());
        }catch (BaseException e){
            log.error(e.getMessage());
            throw e;
        }catch (Exception e){
            log.error(e.getMessage());
            throw e;
        }
        return ResponseEntity.ok().body(confirmationToken);
    }

    @Override
    public ResponseEntity<?> confirmEmail(String confirmationToken) throws BaseException {
        log.info(String.format("IN USER confirm-account, token: %s",confirmationToken));
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if(!Objects.isNull(token))
        {
            Optional<User> user = userRepository.findByUsername(token.getUser().getUsername());
            if(user.isEmpty())
                throw new BaseException(HttpStatus.NOT_FOUND.toString(), "User not found");
            user.get().setEnabled(true);
            userRepository.save(user.get());
            log.info(String.format("OUT USER confirm-account, token: %s, result ok",confirmationToken));
            return responseBuilder.buildResponse(HttpStatus.OK.value(),"Email verified successfully!");
        }
        log.info(String.format("OUT USER confirm-account, token: %s, result non ok",confirmationToken));
        return responseBuilder.buildResponse(HttpStatus.BAD_REQUEST.value(),"Error: Couldn't verify email");
    }

    @Override
    public User savePassword(String username, String password, String confirmationToken) throws Exception,BaseException{
        User userResponse;
        try {
            ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

            if(token != null)
            {
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isPresent()) {
                    user.get().setPassword(passwordEncoder.encode(password));
                    //user.get().setEnabled(true);
                    userResponse = userRepository.save(user.get());
                } else
                    throw new BaseException(String.format("Email user not found"), HttpStatus.NOT_FOUND.toString());
            }else
                throw new BaseException("Error: Couldn't verify account", HttpStatus.UNAUTHORIZED.toString());


        }catch (BaseException e){
            throw e;
        }catch (Exception e){
            throw e;
        }

        return userResponse;
    }

    private ConfirmationToken getConfirmationToken(User user){
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByUserEmail(user.getEmail());
        if(CollectionUtils.isEmpty(confirmationTokens)){
            ConfirmationToken confirmationToken = new ConfirmationToken(user);
            confirmationTokenRepository.save(confirmationToken);
            confirmationTokens.add(confirmationToken);
        }
        return confirmationTokens.get(0);
    }







}
