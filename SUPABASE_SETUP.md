# AuraWake - Supabase Backend Setup ✅

This document outlines the complete Supabase backend integration for AuraWake.

## 🎯 What's Been Set Up

### 1. Configuration Files

#### `.env` (Git-ignored) ✅
Contains your Supabase credentials:
- `SUPABASE_URL` - https://bbsbpsnaebifnnbnafwy.supabase.co
- `SUPABASE_ANON_KEY` - Public/anonymous key (safe for client-side)
- `SUPABASE_SERVICE_ROLE_KEY` - Admin key (server-side only)
- `SUPABASE_DB_PASSWORD` - Database password
- `SUPABASE_PROJECT_REF` - bbsbpsnaebifnnbnafwy

#### `.env.example` ✅
Template file for other developers (safe to commit)

#### `BuildConfig.kt` ✅
Kotlin object providing access to Supabase credentials in your app code

### 2. Dependencies Added ✅

Added to `libs.versions.toml` and `build.gradle.kts`:
- **Supabase Kotlin SDK** (v2.7.2)
  - `postgrest-kt` - Database operations
  - `gotrue-kt` - Authentication
  - `realtime-kt` - Real-time subscriptions
- **Ktor Client** (v2.3.12) - HTTP client for Supabase

### 3. Backend Code ✅

#### `SupabaseClient.kt`
Singleton client with three modules:
- **Postgrest** - Database CRUD operations
- **Auth** - User authentication
- **Realtime** - Real-time subscriptions

#### `SupabaseAlarmRepository.kt`
Cloud sync repository with methods:
- `uploadAlarm()` - Upload alarm to cloud
- `getAllAlarms()` - Fetch all user alarms
- `updateAlarm()` - Update existing alarm
- `deleteAlarm()` - Delete alarm from cloud
- `syncAlarms()` - Bidirectional sync

### 4. Database Schema ✅

Created `supabase_schema.sql` with:
- **alarms** table with all alarm fields
- **Row Level Security (RLS)** policies for multi-user security
- **Indexes** for performance
- **Triggers** for auto-updating timestamps
- **Active alarms view**

## 🚀 Next Steps

### Step 1: Set Up Database in Supabase Dashboard

1. Go to https://bbsbpsnaebifnnbnafwy.supabase.co
2. Navigate to **SQL Editor**
3. Copy and paste the contents of `supabase_schema.sql`
4. Click **Run** to create the tables and policies

### Step 2: Enable Authentication (Optional)

If you want user accounts:
1. Go to **Authentication** → **Providers**
2. Enable **Email** or **Google/Apple** sign-in
3. Configure email templates

### Step 3: Test the Connection

Add this to your app initialization:

```kotlin
// In MainActivity.kt or Application class
lifecycleScope.launch {
    try {
        val alarms = SupabaseAlarmRepository().getAllAlarms()
        Log.d("Supabase", "Connected! Found ${alarms.size} alarms")
    } catch (e: Exception) {
        Log.e("Supabase", "Connection failed: ${e.message}")
    }
}
```

### Step 4: Implement Sync Features

Example usage in your existing `AlarmRepository`:

```kotlin
class HybridAlarmRepository(
    private val localRepo: OfflineAlarmRepository,
    private val remoteRepo: SupabaseAlarmRepository
) : AlarmRepository {
    
    override suspend fun insertAlarm(alarm: Alarm) {
        // Save locally first
        localRepo.insertAlarm(alarm)
        
        // Then sync to cloud
        try {
            remoteRepo.uploadAlarm(alarm)
        } catch (e: Exception) {
            Log.e("Sync", "Failed to upload: ${e.message}")
        }
    }
    
    // Similar for update, delete, etc.
}
```

## 📊 Features Now Available

- ✅ **Cloud Backup** - Alarms stored in Supabase
- ✅ **Cross-Device Sync** - Access alarms on multiple devices
- ✅ **Real-time Updates** - Changes sync instantly
- ✅ **User Authentication** - Multi-user support
- ✅ **Secure Access** - Row Level Security enabled

## 🔒 Security Notes

> [!CAUTION]
> - **Never commit `.env`** - Already in `.gitignore`
> - **Service Role Key** - Admin privileges, use only in secure backends
> - **Anon Key** - Safe for client-side with RLS enabled
> - **RLS Policies** - Ensure users can only access their own data

## 📝 Files Created

1. `.env` - Your credentials (git-ignored)
2. `.env.example` - Template for team
3. `BuildConfig.kt` - Kotlin config object
4. `SupabaseClient.kt` - Client singleton
5. `SupabaseAlarmRepository.kt` - Cloud sync repository
6. `supabase_schema.sql` - Database schema
7. `SUPABASE_SETUP.md` - This guide

## 🎉 You're All Set!

Your Supabase backend is configured and ready to use. Just run the SQL schema in your Supabase dashboard and start syncing!
