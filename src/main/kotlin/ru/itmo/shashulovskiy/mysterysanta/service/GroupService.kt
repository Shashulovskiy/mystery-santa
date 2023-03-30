package ru.itmo.shashulovskiy.mysterysanta.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.shashulovskiy.mysterysanta.domain.Group
import ru.itmo.shashulovskiy.mysterysanta.repository.GroupRepository
import ru.itmo.shashulovskiy.mysterysanta.repository.UserRepository
import java.util.*

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) {
    fun createGroup(name: String, description: String?): Group {
        return groupRepository.save(Group(
            name = name,
            description = description,
        ))
    }

    fun saveGroup(id: Long, name: String, description: String?): Group {
        return groupRepository.save(Group(
            id = id,
            name = name,
            description = description,
        ))
    }

    fun deleteGroup(id: Long) {
        groupRepository.deleteById(id)
    }

    fun listGroups(): Iterable<Group> {
        return groupRepository.findAll()
    }

    fun getGroupById(id: Long): Group? {
        return groupRepository.findByIdOrNull(id)
    }

    fun tossGroup(id: Long): Boolean {
        val groupParticipants = groupRepository.findByIdOrNull(id)?.participants ?: return false

        for (i in groupParticipants.indices) {
            userRepository.save(groupParticipants[i].copy(recipient = groupParticipants[(i + 1) % groupParticipants.size]))
        }

        return true
    }
}