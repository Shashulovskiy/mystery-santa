package ru.itmo.shashulovskiy.mysterysanta.domain

import jakarta.persistence.*
import org.hibernate.annotations.Fetch

@Entity
@Table(name = "groups")
data class Group(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name")
    val name: String,
    @Column(name = "description")
    val description: String?,

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    val participants: List<User> = emptyList(),
)