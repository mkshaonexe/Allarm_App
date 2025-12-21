package com.aura.wake.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val email: String?,
    val username: String?
)

@Serializable
data class Friendship(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("friend_id") val friendId: String
)

@Serializable
data class GlobalMessage(
    val id: String? = null, // Null for new messages
    @SerialName("user_id") val userId: String,
    val content: String,
    @SerialName("created_at") val createdAt: String? = null,
    // Transient field for UI if we join, but for now we might fetch separately or just show ID
    val profile: Profile? = null 
)
