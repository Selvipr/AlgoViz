-- ============================================================
-- AlgoViz — Initial Database Schema
-- Run this in Supabase Dashboard → SQL Editor
-- ============================================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- 1. USERS TABLE (extends auth.users)
-- ============================================================
CREATE TABLE public.users (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    username TEXT UNIQUE,
    full_name TEXT,
    avatar_url TEXT,
    bio TEXT,
    college TEXT,
    location TEXT,
    github_url TEXT,
    linkedin_url TEXT,
    streak INTEGER DEFAULT 0,
    max_streak INTEGER DEFAULT 0,
    xp INTEGER DEFAULT 0,
    tier TEXT DEFAULT 'novice' CHECK (tier IN ('novice', 'learner', 'practitioner', 'expert', 'master', 'grandmaster')),
    sarvam_language TEXT DEFAULT 'en',
    ui_language TEXT DEFAULT 'en',
    is_premium BOOLEAN DEFAULT false,
    daily_xp_goal INTEGER DEFAULT 50,
    visualization_speed REAL DEFAULT 1.0,
    last_active_at TIMESTAMPTZ DEFAULT now(),
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 2. TOPICS TABLE (DSA topic catalog)
-- ============================================================
CREATE TABLE public.topics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category TEXT NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    difficulty TEXT NOT NULL CHECK (difficulty IN ('beginner', 'intermediate', 'advanced')),
    order_index INTEGER NOT NULL DEFAULT 0,
    is_premium BOOLEAN DEFAULT false,
    icon_url TEXT,
    prerequisites JSONB DEFAULT '[]',
    estimated_time_minutes INTEGER DEFAULT 30,
    track TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 3. VISUALIZATIONS TABLE
-- ============================================================
CREATE TABLE public.visualizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    topic_id UUID NOT NULL REFERENCES public.topics(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    steps_json JSONB NOT NULL,
    complexity_time TEXT,
    complexity_space TEXT,
    default_input JSONB,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 4. LESSONS TABLE
-- ============================================================
CREATE TABLE public.lessons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    topic_id UUID NOT NULL REFERENCES public.topics(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content_md TEXT NOT NULL,
    examples_json JSONB DEFAULT '[]',
    quiz_json JSONB DEFAULT '[]',
    order_index INTEGER DEFAULT 0,
    estimated_time_minutes INTEGER DEFAULT 15,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 5. PROBLEMS TABLE (500+ practice problems)
-- ============================================================
CREATE TABLE public.problems (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    topic_id UUID REFERENCES public.topics(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    difficulty TEXT NOT NULL CHECK (difficulty IN ('easy', 'medium', 'hard')),
    description TEXT NOT NULL,
    constraints_text TEXT,
    input_format TEXT,
    output_format TEXT,
    test_cases_json JSONB NOT NULL DEFAULT '[]',
    solution_json JSONB,
    hints JSONB DEFAULT '[]',
    company_tags JSONB DEFAULT '[]',
    topic_tags JSONB DEFAULT '[]',
    is_premium BOOLEAN DEFAULT false,
    is_daily_challenge BOOLEAN DEFAULT false,
    editorial_md TEXT,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 6. SUBMISSIONS TABLE
-- ============================================================
CREATE TABLE public.submissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    problem_id UUID NOT NULL REFERENCES public.problems(id) ON DELETE CASCADE,
    code TEXT NOT NULL,
    language TEXT NOT NULL,
    verdict TEXT NOT NULL DEFAULT 'pending'
        CHECK (verdict IN ('pending', 'accepted', 'wrong_answer', 'time_limit_exceeded', 'runtime_error', 'compilation_error')),
    runtime_ms INTEGER,
    memory_kb INTEGER,
    submitted_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- 7. PROGRESS TABLE (per-user per-topic learning progress)
-- ============================================================
CREATE TABLE public.progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    topic_id UUID NOT NULL REFERENCES public.topics(id) ON DELETE CASCADE,
    lesson_completed BOOLEAN DEFAULT false,
    visualization_watched BOOLEAN DEFAULT false,
    problems_solved INTEGER DEFAULT 0,
    quiz_score REAL,
    score INTEGER DEFAULT 0,
    last_accessed_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, topic_id)
);

-- ============================================================
-- 8. ACHIEVEMENTS TABLE
-- ============================================================
CREATE TABLE public.achievements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    badge_id TEXT NOT NULL,
    badge_name TEXT NOT NULL,
    badge_description TEXT,
    badge_icon TEXT,
    earned_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(user_id, badge_id)
);

-- ============================================================
-- 9. LEADERBOARD TABLE
-- ============================================================
CREATE TABLE public.leaderboard (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    week TEXT NOT NULL,
    score INTEGER DEFAULT 0,
    rank INTEGER,
    problems_solved INTEGER DEFAULT 0,
    UNIQUE(user_id, week)
);

-- ============================================================
-- 10. AI SESSIONS TABLE
-- ============================================================
CREATE TABLE public.ai_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.users(id) ON DELETE CASCADE,
    topic_id UUID REFERENCES public.topics(id) ON DELETE SET NULL,
    title TEXT,
    messages_json JSONB NOT NULL DEFAULT '[]',
    language TEXT DEFAULT 'en',
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- ============================================================
-- INDEXES
-- ============================================================
CREATE INDEX idx_topics_category ON public.topics(category);
CREATE INDEX idx_topics_track ON public.topics(track);
CREATE INDEX idx_visualizations_topic ON public.visualizations(topic_id);
CREATE INDEX idx_lessons_topic ON public.lessons(topic_id);
CREATE INDEX idx_problems_topic ON public.problems(topic_id);
CREATE INDEX idx_problems_difficulty ON public.problems(difficulty);
CREATE INDEX idx_submissions_user ON public.submissions(user_id);
CREATE INDEX idx_submissions_problem ON public.submissions(problem_id);
CREATE INDEX idx_progress_user ON public.progress(user_id);
CREATE INDEX idx_progress_topic ON public.progress(topic_id);
CREATE INDEX idx_achievements_user ON public.achievements(user_id);
CREATE INDEX idx_leaderboard_week ON public.leaderboard(week);
CREATE INDEX idx_leaderboard_user ON public.leaderboard(user_id);
CREATE INDEX idx_ai_sessions_user ON public.ai_sessions(user_id);

-- ============================================================
-- ROW LEVEL SECURITY
-- ============================================================
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.topics ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.visualizations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lessons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.problems ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.submissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.progress ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.achievements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.leaderboard ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_sessions ENABLE ROW LEVEL SECURITY;

-- Users: own data only
CREATE POLICY "Users can view own profile" ON public.users FOR SELECT USING (auth.uid() = id);
CREATE POLICY "Users can update own profile" ON public.users FOR UPDATE USING (auth.uid() = id);
CREATE POLICY "Users can insert own profile" ON public.users FOR INSERT WITH CHECK (auth.uid() = id);

-- Public content: readable by all authenticated users
CREATE POLICY "Topics are readable by all" ON public.topics FOR SELECT USING (true);
CREATE POLICY "Visualizations are readable by all" ON public.visualizations FOR SELECT USING (true);
CREATE POLICY "Lessons are readable by all" ON public.lessons FOR SELECT USING (true);
CREATE POLICY "Problems are readable by all" ON public.problems FOR SELECT USING (true);

-- Submissions: own data
CREATE POLICY "Users can view own submissions" ON public.submissions FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can create submissions" ON public.submissions FOR INSERT WITH CHECK (auth.uid() = user_id);

-- Progress: own data
CREATE POLICY "Users can view own progress" ON public.progress FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can upsert own progress" ON public.progress FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own progress" ON public.progress FOR UPDATE USING (auth.uid() = user_id);

-- Achievements: own data read
CREATE POLICY "Users can view own achievements" ON public.achievements FOR SELECT USING (auth.uid() = user_id);

-- Leaderboard: readable by all
CREATE POLICY "Leaderboard is readable by all" ON public.leaderboard FOR SELECT USING (true);

-- AI Sessions: own data
CREATE POLICY "Users can view own AI sessions" ON public.ai_sessions FOR SELECT USING (auth.uid() = user_id);
CREATE POLICY "Users can create AI sessions" ON public.ai_sessions FOR INSERT WITH CHECK (auth.uid() = user_id);
CREATE POLICY "Users can update own AI sessions" ON public.ai_sessions FOR UPDATE USING (auth.uid() = user_id);

-- ============================================================
-- TRIGGER: Auto-create user profile on signup
-- ============================================================
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.users (id, email, username)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'username', split_part(NEW.email, '@', 1))
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

CREATE OR REPLACE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- ============================================================
-- STORAGE BUCKETS
-- ============================================================
INSERT INTO storage.buckets (id, name, public) VALUES ('avatars', 'avatars', false);
INSERT INTO storage.buckets (id, name, public) VALUES ('topic-assets', 'topic-assets', true);
INSERT INTO storage.buckets (id, name, public) VALUES ('certificates', 'certificates', false);
INSERT INTO storage.buckets (id, name, public) VALUES ('ai-audio', 'ai-audio', false);

-- Storage policies
CREATE POLICY "Users can upload own avatar" ON storage.objects FOR INSERT
    WITH CHECK (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);
CREATE POLICY "Users can view own avatar" ON storage.objects FOR SELECT
    USING (bucket_id = 'avatars' AND auth.uid()::text = (storage.foldername(name))[1]);
CREATE POLICY "Anyone can view topic assets" ON storage.objects FOR SELECT
    USING (bucket_id = 'topic-assets');
CREATE POLICY "Users can view own certificates" ON storage.objects FOR SELECT
    USING (bucket_id = 'certificates' AND auth.uid()::text = (storage.foldername(name))[1]);
CREATE POLICY "Users can view own AI audio" ON storage.objects FOR SELECT
    USING (bucket_id = 'ai-audio' AND auth.uid()::text = (storage.foldername(name))[1]);
