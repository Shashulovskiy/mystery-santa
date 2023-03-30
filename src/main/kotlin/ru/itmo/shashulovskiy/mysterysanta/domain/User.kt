package ru.itmo.shashulovskiy.mysterysanta.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name")
    val name: String,
    @Column(name = "wish")
    val wish: String?,

    @OneToOne
    val recipient: User? = null,

    @ManyToOne
    @JoinColumn(name = "group_id")
    val group: Group? = null
)