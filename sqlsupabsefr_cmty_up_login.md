-- ==============================================================================
-- AuraWake Supabase Consolidated SQL Schema
-- Includes: Profiles, Friendships, Communities, Messages, and Alarms
-- File: sqlsupabsefr_cmty_up_login.md
-- ==============================================================================

-- =====================================================
-- 1. PROFILES (Users)
-- =====================================================
-- Stores user profile data like username and avatar.
-- Linked to auth.users.

CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    username TEXT UNIQUE,
    full_name TEXT,
    avatar_url TEXT,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- RLS: Public profiles are viewable by everyone.
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public profiles are viewable by everyone."
    ON public.profiles
    FOR SELECT
    USING (true);

-- RLS: Users can insert their own profile.
CREATE POLICY "Users can insert their own profile."
    ON public.profiles
    FOR INSERT
    WITH CHECK (auth.uid() = id);

-- RLS: Users can update their own profile.
CREATE POLICY "Users can update own profile."
    ON public.profiles
    FOR UPDATE
    USING (auth.uid() = id);

-- =====================================================
-- 2. FRIENDSHIPS
-- =====================================================
-- Stores status of relationships between users.
-- status: 'pending', 'accepted', 'blocked'

CREATE TABLE IF NOT EXISTS public.friendships (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    friend_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE NOT NULL,
    status TEXT CHECK (status IN ('pending', 'accepted', 'blocked')) DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, friend_id)
);

-- RLS: Users can view their own friendships
ALTER TABLE public.friendships ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own friendships"
    ON public.friendships
    FOR SELECT
    USING (auth.uid() = user_id OR auth.uid() = friend_id);

-- RLS: Authenticated users can insert friend requests
CREATE POLICY "Users can insert friend requests"
    ON public.friendships
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- RLS: Users can update friendships they are involved in
CREATE POLICY "Users can update friendships"
    ON public.friendships
    FOR UPDATE
    USING (auth.uid() = user_id OR auth.uid() = friend_id);

-- =====================================================
-- 3. COMMUNITIES
-- =====================================================
-- Defines the different community groups (Global, Tech, etc.)

CREATE TABLE IF NOT EXISTS public.communities (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT,
    icon_name TEXT, 
    member_count INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- RLS: Viewable by everyone.
ALTER TABLE public.communities ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Communities are viewable by everyone."
    ON public.communities
    FOR SELECT
    USING (true);

-- Insert Default Communities
INSERT INTO public.communities (name, description, icon_name)
VALUES 
    ('Global Community', 'Talk to everyone!', 'globe'),
    ('Early Birds', 'For the mornining risers', 'sun'),
    ('Night Owls', 'Who stays up late?', 'moon'),
    ('Motivation', 'Get inspired', 'star'),
    ('Tech Talk', 'Discuss gadgets and code', 'code'),
    ('Wellness', 'Health and mindfulness', 'leaf'),
    ('Support', 'Help and questions', 'help')
ON CONFLICT DO NOTHING;

-- =====================================================
-- 4. MESSAGES (Community Chat)
-- =====================================================
-- Stores chat messages for each community.

CREATE TABLE IF NOT EXISTS public.messages (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    community_id UUID REFERENCES public.communities(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE public.messages ENABLE ROW LEVEL SECURITY;

-- Policy: Everyone can read messages.
CREATE POLICY "Everyone can read messages"
    ON public.messages
    FOR SELECT
    USING (true);

-- Policy: Authenticated users can insert messages.
CREATE POLICY "Authenticated users can insert messages"
    ON public.messages
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- =====================================================
-- 5. ALARMS
-- =====================================================
-- Stores user alarms for cloud backup/sync.

CREATE TABLE IF NOT EXISTS public.alarms (
    id TEXT PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    hour INTEGER NOT NULL,
    minute INTEGER NOT NULL,
    is_enabled BOOLEAN DEFAULT true,
    label TEXT,
    days_of_week TEXT[], -- Array of days: ['MON', 'TUE', 'WED', etc.]
    ringtone_uri TEXT,
    vibrate BOOLEAN DEFAULT true,
    snooze_enabled BOOLEAN DEFAULT true,
    snooze_duration INTEGER DEFAULT 5,
    challenge_type TEXT, -- 'MATH', 'QR', 'TYPING', 'NONE'
    challenge_difficulty TEXT, -- 'EASY', 'MEDIUM', 'HARD'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Enable Row Level Security (RLS)
ALTER TABLE public.alarms ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only see their own alarms
CREATE POLICY "Users can view own alarms"
    ON public.alarms
    FOR SELECT
    USING (auth.uid() = user_id);

-- Policy: Users can insert their own alarms
CREATE POLICY "Users can insert own alarms"
    ON public.alarms
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Policy: Users can update their own alarms
CREATE POLICY "Users can update own alarms"
    ON public.alarms
    FOR UPDATE
    USING (auth.uid() = user_id);

-- Policy: Users can delete their own alarms
CREATE POLICY "Users can delete own alarms"
    ON public.alarms
    FOR DELETE
    USING (auth.uid() = user_id);

-- Create indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_alarms_user_id ON public.alarms(user_id);
CREATE INDEX IF NOT EXISTS idx_alarms_is_enabled ON public.alarms(is_enabled);

-- =====================================================
-- 6. TRIGGERS & FUNCTIONS
-- =====================================================

-- Function to handle new user signup -> Create Profile
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO public.profiles (id, full_name, avatar_url)
  VALUES (new.id, new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'avatar_url');
  RETURN new;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger to call handle_new_user on auth.users insert
CREATE OR REPLACE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE PROCEDURE public.handle_new_user();

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for Alarms
CREATE TRIGGER update_alarms_updated_at
    BEFORE UPDATE ON public.alarms
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
