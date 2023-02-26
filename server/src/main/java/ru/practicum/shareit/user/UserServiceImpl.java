package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ValidationFailException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateEmail(user.getEmail());
        User createdUser = userRepository.save(user);
        log.info("Created new User: {}.", createdUser);
        return UserMapper.toDto(createdUser);
    }

    @Override
    public UserDto read(long id) {
        User readUser = userRepository.findById(id).orElseThrow();
        log.info("Read User: {}.", readUser);
        return UserMapper.toDto(readUser);
    }

    @Transactional
    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userToUpdate = userRepository.findById(id).orElseThrow();
        Optional.ofNullable(user.getName()).ifPresent(name -> {
            if (!name.isBlank()) {
                userToUpdate.setName(name);
            }
        });
        Optional.ofNullable(user.getEmail()).ifPresent(email -> {
            if (!email.isBlank()) {
                validateEmail(email);
                userToUpdate.setEmail(email);
            }
        });
        User updatedUser = userRepository.save(userToUpdate);
        log.info("Updated User: {}.", updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Transactional
    @Override
    public UserDto delete(long id) {
        User deletedUser = userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        log.info("Deleted User: {}.", deletedUser);
        return UserMapper.toDto(deletedUser);
    }

    @Override
    public List<UserDto> readAll() {
        List<User> readUsers = userRepository.findAll();
        log.info("Read Users: {}.", readUsers);
        return readUsers.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.find()) {
            throw new ValidationFailException(String.format("Email '%s' is not valid!", email));
        }
    }
}
