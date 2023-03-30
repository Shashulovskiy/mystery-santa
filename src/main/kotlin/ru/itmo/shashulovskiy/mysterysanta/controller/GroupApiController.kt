package ru.itmo.shashulovskiy.mysterysanta.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.itmo.shashulovskiy.mysterysanta.exceptions.BadRequestException
import ru.itmo.shashulovskiy.mysterysanta.exceptions.NotFoundException
import ru.itmo.shashulovskiy.mysterysanta.service.GroupService
import ru.itmo.shashulovskiy.mysterysanta.service.UserService

@RestController
class MysterySantaApiController(
    private val groupService: GroupService,
    private val userService: UserService,
) {

    @PostMapping("/group")
    fun handleNewGroup(@RequestBody group: DTOs.GroupDTO): ResponseEntity<Long> {
        val id = groupService.createGroup(group.name, group.description).id
        return ResponseEntity.ok(id)
    }

    @GetMapping("/groups")
    fun listAllGroups(): ResponseEntity<Iterable<DTOs.GroupWithIdDTO>> {
        val groups = groupService.listGroups()
        return ResponseEntity.ok(groups.map {
            DTOs.GroupWithIdDTO(
                id = it.id!!,
                name = it.name,
                description = it.description
            )
        })
    }

    @GetMapping("/group/{id}")
    fun getGroup(@PathVariable id: Long): ResponseEntity<DTOs.FullGroupDTO> {
        val group = groupService.getGroupById(id)
        if (group == null) {
            throw NotFoundException("Group not found")
        } else {
            return ResponseEntity.ok(DTOs.FullGroupDTO(
                id = group.id!!,
                name = group.name,
                description = group.description,
                participants = group.participants.map { user ->
                    DTOs.UserWithRecipientDTO(
                        id = user.id!!,
                        name = user.name,
                        wish = user.wish,
                        recipient = user.recipient?.let { recipient ->
                            DTOs.UserWithIdDTO(
                                recipient.id!!,
                                recipient.name,
                                recipient.wish
                            )
                        }
                    )
                }
            ))
        }
    }

    @PutMapping("/group/{id}")
    fun editGroup(@PathVariable id: Long, @RequestBody group: DTOs.GroupDTO): ResponseEntity<Void> {
        groupService.getGroupById(id) ?: throw NotFoundException("Group not found")

        groupService.saveGroup(id, group.name, group.description)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/group/{id}")
    fun deleteGroup(@PathVariable id: Long): ResponseEntity<Void> {
        groupService.getGroupById(id) ?: throw NotFoundException("Group not found")

        groupService.deleteGroup(id)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/group/{id}/participant")
    fun addParticipant(@PathVariable id: Long, @RequestBody user: DTOs.UserDTO): ResponseEntity<Long> {
        val group = groupService.getGroupById(id) ?: throw NotFoundException("Group not found")
        val userId = userService.createUser(user.name, user.wish, group).id

        return ResponseEntity.ok(userId)
    }

    @DeleteMapping("/group/{groupId}/participant/{participantId}")
    fun deleteParticipant(@PathVariable groupId: Long, @PathVariable participantId: Long): ResponseEntity<Void> {
        val group = groupService.getGroupById(groupId) ?: throw NotFoundException("Group not found")
        val user = userService.getUserById(participantId) ?: throw NotFoundException("User not found")

        if (user.group?.id != group.id) {
            throw BadRequestException("User does not belong to group")
        }

        userService.deleteUser(participantId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/group/{id}/toss")
    fun tossParticipants(@PathVariable id: Long): ResponseEntity<List<DTOs.UserWithRecipientDTO>> {
        var group = groupService.getGroupById(id) ?: throw NotFoundException("Group not found")
        if (group.participants.size < 3) {
            throw BadRequestException("Can only toss groups with 3 or more participants")
        }

        groupService.tossGroup(id)

        return ResponseEntity.ok(groupService.getGroupById(id)!!.participants.map { user ->
            DTOs.UserWithRecipientDTO(
                id = user.id!!,
                name = user.name,
                wish = user.wish,
                recipient = user.recipient?.let { recipient ->
                    DTOs.UserWithIdDTO(
                        recipient.id!!,
                        recipient.name,
                        recipient.wish
                    )
                }
            )
        })
    }

    @GetMapping("/group/{groupId}/participant/{participantId}/recipient")
    fun getRecipient(@PathVariable groupId: Long, @PathVariable participantId: Long): ResponseEntity<DTOs.UserWithIdDTO> {
        val group = groupService.getGroupById(groupId) ?: throw NotFoundException("Group not found")
        val user = userService.getUserById(participantId) ?: throw NotFoundException("User not found")

        if (user.group?.id != group.id) {
            throw BadRequestException("User does not belong to group")
        }

        val recipient = user.recipient ?: throw NotFoundException("User's recipient not found")

        return ResponseEntity.ok(
            DTOs.UserWithIdDTO(
                id = recipient.id!!,
                name = recipient.name,
                wish = recipient.wish
            )
        )
    }

    object DTOs {
        data class GroupDTO(val name: String, val description: String?)
        data class GroupWithIdDTO(val id: Long, val name: String, val description: String?)
        data class FullGroupDTO(val id: Long, val name: String, val description: String?, val participants: List<UserWithRecipientDTO>)

        data class UserDTO(val name: String, val wish: String?)
        data class UserWithIdDTO(val id: Long, val name: String, val wish: String?)
        data class UserWithRecipientDTO(val id: Long, val name: String, val wish: String?, val recipient: UserWithIdDTO?)
    }
}