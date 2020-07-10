package org.launchcode.javawebdevtechjobsauthentication.controllers;

import org.launchcode.javawebdevtechjobsauthentication.models.DTO.LoginFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.DTO.RegistrationFormDTO;
import org.launchcode.javawebdevtechjobsauthentication.models.User;
import org.launchcode.javawebdevtechjobsauthentication.models.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.websocket.Session;
import java.util.Optional;

@Controller
public class AuthenticationController {

    @Autowired
    UserRepository userRepository;

    private static final String userSessionKey = "user";


    //view controllers
    //Registratioin Controller
    @GetMapping("/register")
    public String displayRegistrationForm (Model model){
        model.addAttribute(new RegistrationFormDTO());
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationForm (@ModelAttribute @Valid RegistrationFormDTO registrationFormDTO, Errors errors, Model model, HttpServletRequest request){
        if (errors.hasErrors()){
            model.addAttribute("title", "Register");
            return "register";
        }

        User existingUser = userRepository.findByUsername(registrationFormDTO.getUsername());

        if (existingUser != null){
            errors.rejectValue("username","username.alreadyexists", "This username has already been used");
            model.addAttribute("title", "Register");
            return "register";
        }

        String password = registrationFormDTO.getPassword();
        String verifyPassword = registrationFormDTO.getVerifyPassword();
        if (!password.equals(verifyPassword)){
            errors.rejectValue("password","password.notmatching", "Passwords do not match");
            model.addAttribute("title", "Register");
            return "register";
        }

        User newUser = new User(registrationFormDTO.getUsername(), registrationFormDTO.getPassword());
        userRepository.save(newUser);
        setUserInSession(request.getSession(), newUser);
        return "redirect:";
    }


    //Login view controller
    @GetMapping("/login")
    public String displayLoginForm (Model model){
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title", "Login");
        return "login";
    }

    //Login form submission
    @PostMapping("/login")
    public String processLoginForm (@ModelAttribute @Valid LoginFormDTO loginFormDTO, Errors errors, Model model, HttpServletRequest session){
        if (errors.hasErrors()){
            model.addAttribute("title", "Login");
            return "login";
        }

        User theUser = userRepository.findByUsername(loginFormDTO.getUsername());

        if (theUser==null){
            errors.rejectValue("username", "username.nouser", "There is no user under this name");
            model.addAttribute("title", "Login");
            return "login";
        }

        if (!theUser.isMatchingPassword(loginFormDTO.getPassword())){
            errors.rejectValue("password", "password.incorrectPassword","The password provided does not match our records");
            model.addAttribute("title", "Login");
            return "login";
        }

        setUserInSession(session.getSession(), theUser);

        return "redirect:";
    }

    //Logout session
    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/login";
    }




    //helper methods
    public User getUserFromSession (HttpSession session){
        Integer userId = (Integer) session.getAttribute(userSessionKey);
        if (userId==null){
            return null;
        }

        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()){
            return null;
        }

        return user.get();
    }

    private static void setUserInSession (HttpSession session, User user){
        session.setAttribute(userSessionKey, user.getId());
    }
}
