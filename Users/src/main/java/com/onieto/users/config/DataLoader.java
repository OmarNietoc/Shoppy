package com.onieto.users.config;

import com.onieto.users.model.Role;
import com.onieto.users.model.User;
import com.onieto.users.repository.RoleRepository;
import com.onieto.users.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DataLoader(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Crear roles si no existen
        Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() ->
                roleRepository.save(new Role("ADMIN")));

        Role userRole = roleRepository.findByName("USER").orElseGet(() ->
                roleRepository.save(new Role("USER")));

        Role instructorRole = roleRepository.findByName("INSTRUCTOR").orElseGet(() ->
                roleRepository.save(new Role("INSTRUCTOR")));

        Role supportRole = roleRepository.findByName("SUPPORT").orElseGet(() ->
                roleRepository.save(new Role("SUPPORT")));

        Role managerRole = roleRepository.findByName("MANAGER").orElseGet(() ->
                roleRepository.save(new Role("MANAGER")));

        // Crear usuarios
        User user1 = new User("Eric Cartman", "eric.cartman@southpark.com", "user123", userRole, 0);  // South Park
        User user2 = new User("Kenny McCormick", "kenny.mccormick@southpark.com", "user123", userRole, 0);  // South Park
        User user3 = new User("Chuck McGill", "chuck.mcgill@bettercallsaul.com", "user123", userRole, 0);  // Better Call Saul
        User user4 = new User("Kim Wexler", "kim.wexler@bettercallsaul.com", "user123", userRole, 0);  // Better Call Saul
        User user5 = new User("Summer Smith", "summer.smith@rickandmorty.com", "user123", userRole, 0);  // Rick and Morty
        User user6 = new User("Beth Smith", "beth.smith@rickandmorty.com", "user123", userRole, 0);  // Rick and Morty


        User admin1 = new User("Stan Marsh", "stan.m@cartman.com", "admin123", adminRole, 1);
        User admin2 = new User("Kyle Broflovski", "kyle.b@cartman.com", "admin123", adminRole, 1);

        User support1 = new User("Homer Simpson", "homer.simpson@cartman.com", "support123", supportRole, 0);
        User support2 = new User("Marge Simpson", "marge.simpson@cartman.com", "support123", supportRole, 0);
        User support3 = new User("Bart Simpson", "bart.simpson@cartman.com", "support123", supportRole, 0);

        User instructor1 = new User("Saul Goodman", "saul.goodman@cartman.com", "instructor123", instructorRole, 1);
        User instructor2 = new User("Mike Ehrmantraut", "mike.ehrmantraut@cartman.com", "instructor123", instructorRole, 1);
        User instructor3 = new User("Gus Fring", "gus.fring@cartman.com", "instructor123", instructorRole, 1);

        User manager1 = new User("Rick Sanchez", "rick.sanchez@cartman.com", "manager123", managerRole, 1);
        User manager2 = new User("Morty Smith", "morty.smith@cartman.com", "manager123", managerRole, 1);

        // Guardar usuarios
        userRepository.save(admin1);
        userRepository.save(admin2);

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);

        userRepository.save(support1);
        userRepository.save(support2);
        userRepository.save(support3);

        userRepository.save(instructor1);
        userRepository.save(instructor2);
        userRepository.save(instructor3);

        userRepository.save(manager1);
        userRepository.save(manager2);

    }
}