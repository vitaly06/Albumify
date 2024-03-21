package ru.oksei.Albumify.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.oksei.Albumify.DAO.AlbumDAO;
import ru.oksei.Albumify.DAO.PersonDAO;
import ru.oksei.Albumify.DAO.PhotoDAO;
import ru.oksei.Albumify.Models.Album;
import ru.oksei.Albumify.Models.Person;
import ru.oksei.Albumify.Models.Photo;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {
    //private final String UPLOAD_DIR = "";
    @Autowired
    PersonDAO personDAO;
    @Autowired
    AlbumDAO albumDAO;
    @Autowired
    PhotoDAO photoDAO;
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
        List<Album> albums = albumDAO.getAllUserAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
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
    public String addContent(Model model){
        List<Album> albums = albumDAO.getAllUserAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
        return "add_content";
    }


    // Загрузка фото в БД
    @PostMapping("/addContent")
    public String loadContent(@ModelAttribute("photo")Photo photo) throws IOException {
        photo.setUserId(Integer.parseInt(data[0]));
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(photo.getFile().getOriginalFilename()));
        try {
            Path path = Paths.get("./src/main/resources/static/data/" + fileName);
            Files.copy(photo.getFile().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoDAO.savePhoto(photo);
        return "redirect:/";
    }

    // Создание альбома
    @PostMapping("/addAlbum")
    public String addAlbum(@ModelAttribute("album")Album album){
        album.setUserId(Integer.parseInt(data[0]));
        albumDAO.addAlbum(album);
        return "redirect:/profile";
    }

    // Просмотр альбома
    @GetMapping("/album/{userId}-{albumname}")
    public String viewAlbum(@PathVariable("userId") int userId, @PathVariable("albumname") String albumname,
                            Model model) throws IOException {
        List<Photo> photos = photoDAO.getPhotos(userId, albumname);
        Album album = albumDAO.getAlbum(albumname);
        model.addAttribute("album", album);
        model.addAttribute("photos", photos);
        return "album";
    }
}
