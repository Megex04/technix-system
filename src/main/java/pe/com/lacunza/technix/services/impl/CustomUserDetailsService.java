package pe.com.lacunza.technix.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.lacunza.technix.domain.entities.documents.User;
import pe.com.lacunza.technix.domain.repositories.mongo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Allow users to login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username o email: " + usernameOrEmail));

        // Create authorities list based on role and permissions
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add role as an authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // Add all permissions as authorities
        if (user.getPermissions() != null) {
            for (String permission : user.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con id: " + id));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // Add role as an authority
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        // Add all permissions as authorities
        if (user.getPermissions() != null) {
            for (String permission : user.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
}
