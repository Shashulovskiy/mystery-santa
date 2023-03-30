package ru.itmo.shashulovskiy.mysterysanta.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.shashulovskiy.mysterysanta.domain.Group

interface GroupRepository: JpaRepository<Group, Long>