-- AuraWake Supabase Database Schema

-- Create alarms table
CREATE TABLE IF NOT EXISTS alarms (
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
ALTER TABLE alarms ENABLE ROW LEVEL SECURITY;

-- Policy: Users can only see their own alarms
CREATE POLICY "Users can view own alarms"
    ON alarms FOR SELECT
    USING (auth.uid() = user_id);

-- Policy: Users can insert their own alarms
CREATE POLICY "Users can insert own alarms"
    ON alarms FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Policy: Users can update their own alarms
CREATE POLICY "Users can update own alarms"
    ON alarms FOR UPDATE
    USING (auth.uid() = user_id);

-- Policy: Users can delete their own alarms
CREATE POLICY "Users can delete own alarms"
    ON alarms FOR DELETE
    USING (auth.uid() = user_id);

-- Create index for faster queries
CREATE INDEX IF NOT EXISTS idx_alarms_user_id ON alarms(user_id);
CREATE INDEX IF NOT EXISTS idx_alarms_is_enabled ON alarms(is_enabled);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to automatically update updated_at
CREATE TRIGGER update_alarms_updated_at
    BEFORE UPDATE ON alarms
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Optional: Create a view for active alarms
CREATE OR REPLACE VIEW active_alarms AS
SELECT * FROM alarms
WHERE is_enabled = true
ORDER BY hour, minute;
