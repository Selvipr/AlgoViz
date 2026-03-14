-- ============================================================
-- AlgoViz — 002: Leaderboard RLS Policy Migration
-- Run this in Supabase Dashboard → SQL Editor
-- ============================================================

-- By default, public.users isolates rows so players can only SELECT their own UUID.
-- However, for the Global Leaderboard and Public Profiles to work,
-- we must allow all authenticated users to read the full table.

-- 1. Discard the restrictive isolated SELECT policy
DROP POLICY IF EXISTS "Users can view own profile" ON public.users;

-- 2. Inject the Global permissive SELECT policy
CREATE POLICY "Users are readable by all" 
    ON public.users 
    FOR SELECT 
    USING (true);
