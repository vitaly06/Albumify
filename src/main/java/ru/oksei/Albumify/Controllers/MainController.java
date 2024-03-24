package ru.oksei.Albumify.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.oksei.Albumify.DAO.AlbumDAO;
import ru.oksei.Albumify.DAO.PersonDAO;
import ru.oksei.Albumify.DAO.PhotoDAO;
import ru.oksei.Albumify.Models.Album;
import ru.oksei.Albumify.Models.DeletePhoto;
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
        List<Photo> photos = photoDAO.getIndexPhoto();
        for (Photo photo : photos){
            System.out.println(photo.getFile().getOriginalFilename());
        }
        model.addAttribute("photos", photos);
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
        if (!Objects.equals(person.getPassword(), person.getRepassword())){
            ObjectError error = new ObjectError("globalError", "Пароли не совпадают");
            bindingResult.addError(error);
        }
        if (!personDAO.checkEmail(person.getEmail())){
            ObjectError error = new ObjectError("globalError", "Аккаунт с таким email уже существует");
            bindingResult.addError(error);
        }
        if (!personDAO.checkNickname(person.getNickname())){
            ObjectError error = new ObjectError("globalError", "Аккаунт с таким nickname уже существует");
            bindingResult.addError(error);
        }
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
    public String finally_login(@ModelAttribute("person") Person person, BindingResult bindingResult) {
        Person check_user = personDAO.login(person);
        if (Objects.equals(check_user, null)) {
            ObjectError error = new ObjectError("globalError", "Неверная почта или пароль");
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()){
            return "/login";
        }
        data = new String[]{Integer.toString(check_user.getId()), check_user.getEmail(), check_user.getFio(),
                check_user.getNickname(), check_user.getPassword()};
        for (String x : data) {
            System.out.println(x);
        }
        isAuth = true;
        return "redirect:/";
    }

    // Профиль
    @GetMapping("/profile")
    public String profile(Model model) {
        List<Album> albums = albumDAO.getAllUserAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
        model.addAttribute("nickname", data[3]);
        model.addAttribute("auth", isAuth);
        model.addAttribute("id", Integer.parseInt(data[0]));
        return "profile";
    }

    // Закрытые альбомы в профиле
    @GetMapping("/profile-closed")
    public String closedAlbumsProfile(Model model){
        List<Album> albums = albumDAO.getAllUserClosedAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
        model.addAttribute("nickname", data[3]);
        model.addAttribute("auth", isAuth);
        model.addAttribute("id", Integer.parseInt(data[0]));
        return "profile";
    }

    // Групповые альбомы в профиле
    @GetMapping("/profile-groupe")
    public String groupeAlbumsProfile(Model model){
        List<Album> albums = albumDAO.getAllUserGroupeAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
        model.addAttribute("nickname", data[3]);
        model.addAttribute("auth", isAuth);
        model.addAttribute("id", Integer.parseInt(data[0]));
        return "profile";
    }

    // Личные альбомы в профиле
    @GetMapping("/profile-personal")
    public String personalAlbumsProfile(Model model){
        List<Album> albums = albumDAO.getAllUserPersonalAlbums(Integer.parseInt(data[0]));
        model.addAttribute("albums", albums);
        model.addAttribute("nickname", data[3]);
        model.addAttribute("auth", isAuth);
        model.addAttribute("id", Integer.parseInt(data[0]));
        return "profile";
    }


    // Профили других людей
    @GetMapping("/{nickname}")
    public String ourProfile(@PathVariable("nickname") String nickname, Model model) {
        Person person = personDAO.ourProfile(nickname);
        List<Album> albums = albumDAO.getAllOurProfileAlbums(person.getId());
        model.addAttribute("person");
        model.addAttribute("auth", isAuth);
        model.addAttribute("albums", albums);
        return "our_profile";
    }

    @GetMapping("/{nickname}-groupe")
    public String ourProfileGroupe(@PathVariable("nickname") String nickname, Model model) {
        Person person = personDAO.ourProfile(nickname);
        List<Album> albums = albumDAO.getAllUserGroupeAlbums(person.getId());
        model.addAttribute("person");
        model.addAttribute("auth", isAuth);
        model.addAttribute("albums", albums);
        return "our_profile";
    }

    @GetMapping("/{nickname}-personal")
    public String ourProfilePersonal(@PathVariable("nickname") String nickname, Model model) {
        Person person = personDAO.ourProfile(nickname);
        List<Album> albums = albumDAO.getAllUserPersonalAlbums(person.getId());
        model.addAttribute("person");
        model.addAttribute("auth", isAuth);
        model.addAttribute("albums", albums);
        return "our_profile";
    }

    // Добавление контента
    @GetMapping("/addContent")
    public String addContent(Model model){
        List<Album> albums = albumDAO.getAllUserAlbums(Integer.parseInt(data[0]));
        model.addAttribute("auth", isAuth);
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
        return "redirect:/addContent";
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
        if (!Objects.equals(userId, Integer.parseInt(data[0]))){
            return "redirect:/";
        }
        List<Photo> photos = photoDAO.getPhotos(userId, albumname);
        Album album = albumDAO.getAlbum(albumname);
        model.addAttribute("album", album);
        model.addAttribute("photos", photos);
        model.addAttribute("auth", isAuth);
        return "album";
    }

    // Просмотр изображения
    @GetMapping("/content/{userId}-{photoName}")
    public String viewContent(@PathVariable("userId") int userId, @PathVariable("photoName") String photoName,
                              Model model){
        Photo photo = photoDAO.getPhotoForSite(userId, photoName);
        Person author = personDAO.photoAuthor(userId);
        model.addAttribute("author", author.getNickname());
        model.addAttribute("photo", photo);
        return "content";
    }

    @GetMapping("/change-profile/{id}")
    public String changeProfile(@PathVariable("id") int id, Model model){
        if (!isAuth){
            return "redirect:/login";
        }
        if (Integer.parseInt(data[0]) != id){
            return "redirect:/";
        }
        Person person = personDAO.photoAuthor(id);
        model.addAttribute("person", person);
        model.addAttribute("id", id);
        return "change_profile";
    }

    @PostMapping("/change-profile/{id}")
    public String editProfile(@Valid @ModelAttribute("person") Person person, BindingResult bindingResult){
        Person person2 = personDAO.photoAuthor(person.getId()); // старые данные аккаунта

        if (Objects.equals(person.getPassword(), "")){
            person.setPassword("-");
        }
        else{
            if (!Objects.equals(person.getPassword(), person2.getPassword())){
                System.out.println(1);
                ObjectError error = new ObjectError("globalError", "Старый пароль неверный");
                bindingResult.addError(error);
            }
        }
        if (Objects.equals(person.getRepassword(), "")){
            person.setRepassword("-");
        }
        System.out.println(person.getPassword() + " " + person.getRepassword());
        if (!personDAO.checkEmail(person.getEmail()) && !Objects.equals(person.getEmail(), person2.getEmail())){
            System.out.println(2);
            ObjectError error = new ObjectError("globalError", "Аккаунт с таким email уже существует");
            bindingResult.addError(error);
        }
        if (!personDAO.checkNickname(person.getNickname()) && !Objects.equals(person.getNickname(), person2.getNickname())){
            ObjectError error = new ObjectError("globalError", "Аккаунт с таким nickname уже существует");
            bindingResult.addError(error);
        }
        if (bindingResult.hasErrors()){
            return "redirect:/change-profile/{id}";
        }
        personDAO.updatePerson(person); // обновляем данные в таблице
        data = new String[]{Integer.toString(person.getId()), person.getEmail(), person.getFio(),
                person.getNickname(), person.getPassword()}; // записываем новые данные в переменную
        return "redirect:/profile";
    }

    // Изменение альбома
    @GetMapping("/edit-album/{userId}-{albumname}")
    public String editAlbum(@PathVariable("userId") int userId, @PathVariable("albumname") String albumName, Model model) throws IOException {
        List<Photo> photos = photoDAO.getPhotos(userId, albumName);
        Album album = albumDAO.getAlbum(albumName);
        model.addAttribute("album", album);
        model.addAttribute("photos", photos);
        model.addAttribute("auth", isAuth);
        return "change_album";
    }

    @PostMapping("/edit-album/{userId}-{albumname}")
    public String editAlbumPost(@ModelAttribute("deletePhoto") DeletePhoto deletePhoto, @PathVariable("albumname") String albumName){
        deletePhoto.setAlbumName(albumName);
        photoDAO.deletePhotos(deletePhoto.getFileName(), deletePhoto.getAlbumName());
        return "redirect:/";
    }
}
