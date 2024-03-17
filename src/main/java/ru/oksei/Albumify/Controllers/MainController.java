package ru.oksei.Albumify.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.oksei.Albumify.DAO.PersonDAO;
import ru.oksei.Albumify.Models.Person;

import java.util.Objects;

@Controller
public class MainController {
    @Autowired
    PersonDAO personDAO;
    String[] data;
    boolean isAuth = false;
    public Person person; // Данные пользователя

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("auth", isAuth);
        return "index";
    }

    // РЕГИСТРАЦИЯ
    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String finalyRegistration(@ModelAttribute("person") Person person) {
        personDAO.registration(person);
        return "redirect:/login";
    }

    // ВХОД
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String finally_login(@ModelAttribute("person") Person person) {
        Person check_user = personDAO.login(person);
        if (Objects.equals(check_user, null)) {
            System.out.println("не вошёл!");
        } else {
            data = new String[]{Integer.toString(check_user.getId()), check_user.getEmail(), check_user.getFio(),
                    check_user.getNickname(), check_user.getPassword()};
            for(String x: data){
                System.out.println(x);
            }
            isAuth = true;
            return "redirect:/";
        }
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("nickname", data[3]);
        return "profile";
    }
    @GetMapping("/{nickname}")
    public String ourProfile(@PathVariable("nickname") String nickname, Model model){
        model.addAttribute("person", personDAO.ourProfile(nickname));
        return "our_profile";
    }
}
