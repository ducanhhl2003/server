//package book.service.impl;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import book.entity.UserEntity;
//import book.repository.UserRepository;
//
//import java.util.Collections;
//
//@Service
//public class JwtUserDetailsService implements UserDetailsService {
//    private final UserRepository userRepository;
//
//    public JwtUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        UserEntity user = userRepository.findByUserName(username);
//        if (user == null) {
//            throw new UsernameNotFoundException("User not found");
//        }
//        return new User(user.getUserName(), user.getPassWord(), Collections.emptyList());
//    }
//
//}
//
