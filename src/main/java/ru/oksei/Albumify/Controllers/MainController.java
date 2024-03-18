package ru.oksei.Albumify.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.oksei.Albumify.DAO.AlbumDAO;
import ru.oksei.Albumify.DAO.PersonDAO;
import ru.oksei.Albumify.Models.Album;
import ru.oksei.Albumify.Models.Person;

import javax.validation.Valid;
import java.util.Objects;

@Controller
public class MainController {
    @Autowired
    PersonDAO personDAO;
    @Autowired
    AlbumDAO albumDAO;
    String[] data;
    boolean isAuth = false;
    public Person person; // Данные пользователя

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("auth", isAuth);
        return "index";
    }

    // Регистрация
    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person, Model model) {
        model.addAttribute("person", person);
        return "registration";
    }

    @PostMapping("/registration")
    public String finalyRegistration(@Valid @ModelAttribute("person") Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "registration";
        }
        personDAO.registration(person);
        return "redirect:/login";
    }

    // Вход
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("person", new Person());
        return "login";
    }

    @PostMapping("/login")
    public String finally_login(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "/login";
        }
        Person check_user = personDAO.login(person);
        if (Objects.equals(check_user, null)) {
            System.out.println("не вошёл!");
        } else {
            data = new String[]{Integer.toString(check_user.getId()), check_user.getEmail(), check_user.getFio(),
                    check_user.getNickname(), check_user.getPassword()};
            for (String x : data) {
                System.out.println(x);
            }
            isAuth = true;
            return "redirect:/";
        }
        return "redirect:/";
    }

    // Профиль
    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("nickname", data[3]);
        return "profile";
    }

    // Профили других людей
    @GetMapping("/{nickname}")
    public String ourProfile(@PathVariable("nickname") String nickname, Model model) {
        model.addAttribute("person", personDAO.ourProfile(nickname));
        return "our_profile";
    }

    // Добавление контента
    @GetMapping("/addContent")
    public String addContent(){
        return "add_content";
    }

    // Просмотр альбома
    @GetMapping("/album")
    public String album(){
        return "album";
    }

    // Создание альбома
    @PostMapping("/addAlbum")
    public String addAlbum(@ModelAttribute("album")Album album){
        album.setUserId(Integer.parseInt(data[0]));
        albumDAO.addAlbum(album);
        return "redirect:/profile";
    }
}
