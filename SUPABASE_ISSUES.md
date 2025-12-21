# Supabase Integration - Known Issues

## Current Status: ⚠️ Dependencies Temporarily Disabled

### Issue
The Supabase Kotlin SDK dependencies are causing build failures:
```
Could not find io.github.jan-tennert.supabase:postgrest-kt:3.2.6
```

### What Was Done
1. ✅ Created `.env` with your Supabase credentials
2. ✅ Created `BuildConfig.kt` for accessing credentials
3. ✅ Created `SupabaseClient.kt` - Client singleton
4. ✅ Created `SupabaseAlarmRepository.kt` - Cloud sync repository
5. ✅ Created `supabase_schema.sql` - Database schema
6. ✅ Added Supabase dependencies to `libs.versions.toml`
7. ⚠️ **TEMPORARILY COMMENTED OUT** dependencies in `build.gradle.kts` to fix build

### Files Ready (But Not Active)
- `app/src/main/java/com/aura/wake/data/remote/SupabaseClient.kt`
- `app/src/main/java/com/aura/wake/data/remote/SupabaseAlarmRepository.kt`
- `supabase_schema.sql`
- `.env` with your credentials

### To Fix Later
1. Investigate correct Supabase Kotlin SDK repository/version
2. Uncomment dependencies in `app/build.gradle.kts`:
   ```kotlin
   // Supabase
   implementation(libs.supabase.postgrest)
   implementation(libs.supabase.realtime)
   implementation(libs.supabase.auth)
   
   // Ktor
   implementation(libs.ktor.client.android)
   implementation(libs.ktor.client.core)
   ```
3. Sync Gradle
4. Test build

### Alternative: Use Supabase REST API Directly
If SDK continues to fail, you can use Retrofit/OkHttp to call Supabase REST API directly using your anon key.

---

**Your credentials are safe in `.env` (git-ignored) and ready to use when dependencies are fixed!**
