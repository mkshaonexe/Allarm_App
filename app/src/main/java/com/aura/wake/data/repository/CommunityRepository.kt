package com.aura.wake.data.repository

import com.aura.wake.data.model.GlobalMessage
import com.aura.wake.data.model.Profile
import com.aura.wake.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommunityRepository(private val authRepository: AuthRepository = AuthRepository()) {

    suspend fun getMessages(): List<Pair<GlobalMessage, Profile?>> = withContext(Dispatchers.IO) {
        try {
            // Fetch messages
            val messages = SupabaseClient.database.from("global_messages")
                .select {
                    order("created_at", Order.DESCENDING)
                    limit(50)
                }.decodeList<GlobalMessage>()

            // Collect User IDs to fetch profiles
            val userIds = messages.map { it.userId }.distinct()
            
            if (userIds.isEmpty()) return@withContext emptyList()

            val profiles = SupabaseClient.database.from("profiles")
                .select {
                    filter {
                        isIn("id", userIds)
                    }
                }.decodeList<Profile>().associateBy { it.id }

            // Combine
            messages.map { msg ->
                msg to profiles[msg.userId]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun sendMessage(content: String): Boolean = withContext(Dispatchers.IO) {
        val currentUserId = authRepository.currentUser?.id ?: return@withContext false
        try {
            val message = GlobalMessage(
                userId = currentUserId,
                content = content
            )
            SupabaseClient.database.from("global_messages").insert(message)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
