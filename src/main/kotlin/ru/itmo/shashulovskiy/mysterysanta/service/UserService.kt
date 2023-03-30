package ru.itmo.shashulovskiy.mysterysanta.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.shashulovskiy.mysterysanta.domain.Group
import ru.itmo.shashulovskiy.mysterysanta.domain.User
import ru.itmo.shashulovskiy.mysterysanta.repository.UserRepository

@Service
class UserService(private val userRepository: UserRepository) {
    fun createUser(name: String, wish: String?, group: Group): User {
        return userRepository.save(User(
            name = name,
            wish = wish,
            group = group
        ))
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    fun getUserById(id: Long): User? {
        return userRepository.findByIdOrNull(id)
    }
}