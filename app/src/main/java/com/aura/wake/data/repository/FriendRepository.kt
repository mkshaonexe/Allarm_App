package com.aura.wake.data.repository

import com.aura.wake.data.model.Friendship
import com.aura.wake.data.model.Profile
import com.aura.wake.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FriendRepository(private val authRepository: AuthRepository = AuthRepository()) {

    suspend fun getFriends(): List<Profile> = withContext(Dispatchers.IO) {
        val currentUserId = authRepository.currentUser?.id ?: return@withContext emptyList()
        
        // 1. Get all friendship records where user is either sender or receiver
       try {
            // Simplification: We only check where user_id = current. 
            // In a real bidirectional system, checking both sides is needed.
            // For this 'Add Friend' flow, we'll assume: Friendship(me -> friend)
           
            val friendships = SupabaseClient.database.from("friendships")
                .select(columns = Columns.list("friend_id")) {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }.decodeList<Friendship>()

            val friendIds = friendships.map { it.friendId }

            if (friendIds.isEmpty()) return@withContext emptyList()

            // 2. Fetch profiles for these IDs
            SupabaseClient.database.from("profiles")
                .select {
                    filter {
                        isIn("id", friendIds)
                    }
                }.decodeList<Profile>()
       } catch (e: Exception) {
           e.printStackTrace()
           emptyList()
       }
    }

    suspend fun addFriendByEmail(email: String): Boolean = withContext(Dispatchers.IO) {
        val currentUserId = authRepository.currentUser?.id ?: return@withContext false
        try {
            // 1. Find user by email
            val profiles = SupabaseClient.database.from("profiles")
                .select {
                    filter {
                        eq("email", email)
                    }
                }.decodeList<Profile>()
            
            val targetUser = profiles.firstOrNull() ?: return@withContext false

            if (targetUser.id == currentUserId) return@withContext false // Can't add self

            // 2. Insert Friendship
            val friendship = Friendship(
                id = java.util.UUID.randomUUID().toString(),
                userId = currentUserId,
                friendId = targetUser.id
            )
            
            SupabaseClient.database.from("friendships").insert(friendship)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
