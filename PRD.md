ALGOVIZ
DSA Visualization & AI-Powered Learning Platform

Product Requirements Document  |  v1.0  |  March 2026
Android • Supabase Backend • MVVM Architecture • Sarvam AI
 
Table of Contents

  1.  Executive Summary
  2.  Product Vision & Goals
  3.  Target Users & Personas
  4.  MVVM Architecture Overview
  5.  Tech Stack
  6.  Supabase Backend Design
  7.  Feature Set — DSA Visualization Module
  8.  Feature Set — Learning Platform
  9.  Feature Set — Practice & Training Arena
  10.  Feature Set — Profile Management
  11.  Sarvam AI Integration
  12.  Android Application Design
  13.  Non-Functional Requirements
  14.  Milestones & Roadmap
  15.  Risk Register
  16.  Appendix — Data Structures & Algorithms List
 
1. Executive Summary

AlgoViz is a next-generation, AI-powered Data Structures and Algorithms (DSA) learning and visualization platform built as a native Android application. It combines interactive, step-by-step algorithm animations with a structured curriculum, a competitive training arena, rich profile analytics, and full multilingual support powered by Sarvam AI — India's sovereign full-stack AI platform.
AlgoViz is purpose-built for students, competitive programmers, job seekers, and developers across India and South Asia who need a deep, visual understanding of DSA in their preferred language. The platform serves as both a learning tool and a practice environment, enabling users to go from novice to interview-ready.
  Key Highlights
  Interactive step-by-step visualization of 60+ data structures and algorithms
  Structured learning modules with theory, examples, and quizzes
  Coding practice arena with 500+ curated DSA problems
  AI tutor powered by Sarvam AI — multilingual voice & text in 22+ Indian languages
  Supabase backend: auth, real-time DB, storage, edge functions
  MVVM architecture with clean separation of concerns across all layers
  Rich profile system with streaks, badges, progress maps, and leaderboards
  Offline-first design with sync when connectivity is restored

2. Product Vision & Goals

"Democratize DSA education in India through interactive visualization and AI-powered tutoring that works in every Indian language."
2.1 Business Goals
1.	Acquire 500K active Android users within 12 months of launch.
2.	Achieve a 30-day retention rate of ≥ 45% through gamification and streaks.
3.	Monetize through a freemium model: free visualization + paid full practice access.
4.	Establish partnerships with 50+ colleges and coding bootcamps in India.
5.	Build India's most comprehensive multilingual DSA learning resource.
2.2 Product Goals
6.	Provide real-time, controllable visualizations for every major data structure and algorithm.
7.	Make every concept accessible via voice in Hindi, Tamil, Telugu, Kannada, Marathi, Bengali, and 16 more Indian languages.
8.	Offer a code editor with test-case validation for hands-on practice.
9.	Build a personalized learning path that adapts based on performance data.
10.	Ensure sub-2s load time for all visualization screens on mid-range Android devices.
2.3 Success Metrics (KPIs)
KPI	Target	Measurement Method
Daily Active Users (DAU)	50,000 within 6 months	Supabase analytics + Firebase
Avg. session duration	> 18 minutes	Session tracking events
Visualization module completion rate	> 60%	Progress tracking DB
Practice problem submissions/day	> 100,000	Arena submission logs
Sarvam AI tutor CSAT	> 4.3 / 5	In-app rating widget
Crash-free sessions	> 99.5%	Firebase Crashlytics
App Store rating	> 4.5 stars	Google Play Console
Premium conversion rate	> 8%	Subscription DB records

3. Target Users & Personas

3.1 Primary Personas
Arjun, 21 — Engineering Student
Preparing for campus placements at a Tier-2 college. Needs concept clarity, visual examples, and practice problems. Prefers Hindi UI. Uses mid-range Android (Redmi/Realme). Limited data plan — needs offline support.
Core Needs:
•	Visual step-by-step animation of all sorting algorithms
•	Hindi-language explanations via Sarvam AI voice
•	Offline access to at least core visualizations
Priya, 26 — Working Software Engineer
Preparing for FAANG interviews. Already has coding experience but wants to revisit complex topics (graphs, DP). English-first. Uses premium Android device.
Core Needs:
•	Advanced algorithm visualization with complexity breakdowns
•	Hard-level LeetCode-style problems
•	Interview simulation mode
Ravi, 19 — Competitive Programmer
Participates in Codeforces, CodeChef. Wants performance analytics and comparisons. Prefers Tamil. Active on leaderboards.
Core Needs:
•	Leaderboard rankings and streak tracking
•	Custom test-case input in the code editor
•	Tamil voice tutor for concept revision
Dr. Meena, 42 — CS Faculty
Teaching data structures at a college. Wants to use the app as a classroom aid. Needs structured topic coverage aligned with university syllabus.
Core Needs:
•	Classroom-mode sharable visualization links
•	Topic-wise structured curriculum
•	Exportable progress reports for students
4. MVVM Architecture Overview

AlgoViz strictly follows the Model–View–ViewModel (MVVM) architectural pattern recommended by Google for Android development. This ensures testability, separation of concerns, and scalability across the entire feature set.
4.1 Architecture Layers
  View Layer (UI)
  Jetpack Compose UI components for all screens
  Observes StateFlow / LiveData from ViewModel
  No business logic — only renders state and dispatches user events
  Includes: VisualizationScreen, PracticeScreen, ProfileScreen, LearnScreen, ChatScreen
  Animation engine: Canvas API + Compose Animations for DSA step rendering

  ViewModel Layer
  Holds UI state (StateFlow<UiState>) and exposes it to the View
  Calls UseCases / Repositories — never accesses data sources directly
  Handles: algorithm step generation, visualization control (play/pause/speed)
  Key ViewModels: VisualizationViewModel, PracticeViewModel, ProfileViewModel, AITutorViewModel
  Survives configuration changes via Hilt-injected scoped instances

  Domain Layer (Use Cases)
  Pure Kotlin classes with a single responsibility each
  Examples: ExecuteAlgorithmStepUseCase, SubmitSolutionUseCase, GetUserProgressUseCase
  No Android dependencies — fully unit-testable
  Orchestrates multiple repository calls into business logic flows

  Data Layer (Repositories + Data Sources)
  Repository interfaces defined in domain; implementations in data layer
  Remote data source: Supabase Kotlin SDK (auth, PostgreSQL, Realtime, Storage)
  Local data source: Room database for offline caching
  AI data source: Sarvam AI REST API client (Retrofit + OkHttp)
  Code execution: Judge0 API integration for code running
  Repositories: UserRepository, ProblemRepository, ProgressRepository, AIRepository

4.2 Dependency Injection
Hilt (Dagger 2 wrapper) is used throughout the application for dependency injection. All ViewModels, UseCases, Repositories, and Service clients are injected via Hilt modules, eliminating manual factory boilerplate and supporting proper scoping (Singleton, ActivityRetained, Fragment).
4.3 Data Flow Diagram
The data flow follows a strict unidirectional pattern:
•	User interaction → View dispatches Event to ViewModel
•	ViewModel → calls UseCase with event parameters
•	UseCase → calls Repository (domain interface)
•	Repository → fetches from Remote (Supabase) or Local (Room) source
•	Repository → emits Result<T> back up the chain
•	ViewModel → maps result to UiState and updates StateFlow
•	View → collects StateFlow and re-renders composables reactively
5. Tech Stack

Category	Technology	Purpose
Language	Kotlin 2.x	Primary Android language
UI Framework	Jetpack Compose	Declarative UI with animations
Architecture	MVVM + Clean Architecture	Separation of concerns
DI	Hilt (Dagger 2)	Dependency injection
Navigation	Jetpack Navigation Compose	Screen routing & deep links
Backend BaaS	Supabase	Auth, DB, Storage, Realtime, Edge Functions
Local DB	Room (SQLite)	Offline caching and progress persistence
Networking	Retrofit 2 + OkHttp 4	REST API calls to Sarvam AI, Judge0
Async	Kotlin Coroutines + Flow	Async/reactive data streams
AI — Multilingual	Sarvam AI API	Voice, translation, chat, TTS, STT
Code Execution	Judge0 CE API	Compile and run user code (multi-language)
Image Loading	Coil 3	Async image loading in Compose
Analytics	Firebase Analytics + Crashlytics	Crash reporting and user analytics
Testing	JUnit 5, Mockk, Turbine, Compose UI Test	Unit, integration, and UI tests
CI/CD	GitHub Actions + Firebase App Distribution	Automated build and distribution
Build System	Gradle (KTS)	Build scripts in Kotlin DSL

6. Supabase Backend Design

Supabase serves as the complete backend-as-a-service (BaaS) platform for AlgoViz, providing PostgreSQL database, authentication, real-time subscriptions, object storage, and serverless edge functions — all within a single managed infrastructure.
6.1 Authentication
•	Supabase Auth with email/password, Google OAuth, and phone OTP (via Twilio)
•	JWT-based session tokens stored securely in Android EncryptedSharedPreferences
•	Auto-refresh tokens via Supabase Kotlin SDK
•	Row-Level Security (RLS) policies enforced on all tables — users see only their own data
•	Anonymous guest mode with conversion flow to registered account
6.2 Database Schema (Core Tables)
Table	Key Columns	Purpose
users	id, email, username, avatar_url, streak, tier, sarvam_language	User profiles & preferences
topics	id, category, name, difficulty, order_index, is_premium	DSA topic catalog
visualizations	id, topic_id, steps_json, complexity_time, complexity_space	Visualization step data
lessons	id, topic_id, content_md, examples_json, quiz_json	Lesson content per topic
problems	id, topic_id, title, difficulty, description, constraints, test_cases_json, solution_json	Practice problems
submissions	id, user_id, problem_id, code, language, verdict, runtime_ms, memory_kb, submitted_at	User code submissions
progress	id, user_id, topic_id, lesson_completed, visualization_watched, problems_solved, score	Learning progress tracking
achievements	id, user_id, badge_id, earned_at	Gamification badges earned
leaderboard	id, user_id, week, score, rank	Weekly competitive rankings
ai_sessions	id, user_id, topic_id, messages_json, language, created_at	Sarvam AI chat history

6.3 Realtime Subscriptions
•	Leaderboard updates broadcast in real time to all subscribed clients
•	Submission verdict updated via Supabase Realtime channel after Judge0 callback
•	Collaborative study rooms (future): real-time cursor and code sharing via Broadcast channel
6.4 Storage Buckets
Bucket	Contents	Access
avatars	User profile pictures	Authenticated users (own files)
topic-assets	Algorithm diagrams, illustration images	Public read
certificates	Completion certificate PDFs	Authenticated (own)
ai-audio	Sarvam TTS output cache	Authenticated (own)

6.5 Edge Functions
•	judge0-webhook: Receives verdict callbacks from Judge0, updates submissions table, broadcasts via Realtime
•	sarvam-proxy: Secures Sarvam AI API key — all calls from Android go through this function
•	badge-engine: Evaluates achievement criteria on every progress update
•	leaderboard-refresh: Cron-triggered weekly leaderboard recalculation
•	generate-certificate: Creates PDF completion certificate and stores in Storage
7. Feature Set — DSA Visualization Module

The visualization module is the flagship feature of AlgoViz. Every data structure and algorithm is rendered with an animated, step-by-step visual engine built on Jetpack Compose Canvas and custom drawing APIs.
7.1 Visualization Engine
•	Frame-based step engine: each algorithm operation is broken into discrete, observable steps
•	Controls: Play, Pause, Step Forward, Step Backward, Jump to Step N, Speed (0.25x–4x)
•	Highlight colors: active element (orange), comparing elements (blue), sorted (green), pivot (red)
•	Complexity meter: live O(n) counter updates with each step showing total comparisons and swaps
•	Memory visualization: stack/heap frame panel updates alongside code execution
•	Pseudo-code panel: synchronized line highlighting with current step
•	Custom input: users enter their own array / graph / tree for visualization
•	Export: GIF / MP4 export of any visualization session (Premium)
7.2 Supported Data Structures
Category	Data Structures
Linear	Array, Linked List (Singly, Doubly, Circular), Stack, Queue, Deque
Tree	Binary Tree, BST, AVL Tree, Red-Black Tree, Segment Tree, Fenwick Tree (BIT), Trie, Suffix Tree
Heap	Min Heap, Max Heap, Priority Queue, Fibonacci Heap
Graph	Undirected/Directed Graph, Weighted Graph, DAG, Adjacency List, Adjacency Matrix
Hashing	Hash Map, Hash Set, Chaining, Open Addressing (Linear/Quadratic Probing)
Advanced	Disjoint Set Union (DSU), Sparse Table, Monotonic Stack/Queue, Skip List

7.3 Supported Algorithms
Category	Algorithms
Sorting	Bubble, Selection, Insertion, Merge, Quick, Heap, Counting, Radix, Shell, Tim Sort
Searching	Linear Search, Binary Search, Jump Search, Exponential Search, Ternary Search
Graph Traversal	BFS, DFS, Topological Sort, Tarjan's SCC, Kosaraju's SCC
Shortest Path	Dijkstra, Bellman-Ford, Floyd-Warshall, A*, Johnson's Algorithm
Minimum Spanning Tree	Prim's, Kruskal's, Borůvka's
Dynamic Programming	0/1 Knapsack, LCS, LIS, Matrix Chain, Coin Change, Edit Distance, DP on Trees
Greedy	Activity Selection, Fractional Knapsack, Huffman Coding, Job Scheduling
Backtracking	N-Queens, Sudoku Solver, Rat in Maze, Permutations & Combinations
String Algorithms	KMP, Rabin-Karp, Z-Algorithm, Aho-Corasick, Manacher's, Suffix Array
Mathematical	Sieve of Eratosthenes, GCD/LCM, Fast Exponentiation, Modular Arithmetic
Divide & Conquer	Binary Search, Merge Sort, Closest Pair of Points, Strassen's Matrix
Tree Algorithms	Tree DP, Heavy-Light Decomposition, LCA (Binary Lifting), Euler Tour

7.4 Visualization Screen UX
•	Bottom sheet panel: expandable explanation of the current step in plain language
•	AI Explain button: tap to trigger Sarvam AI voice explanation of the current step
•	Comparison view: side-by-side visualization of two algorithms on the same input (e.g., Merge vs Quick Sort)
•	Dark/Light mode support with accessibility contrast ratios
•	Pinch-to-zoom on graph and tree visualizations for large structures
•	History drawer: timeline of all steps with tap-to-jump navigation
8. Feature Set — Learning Platform

The Learning Platform provides a structured curriculum that takes learners from absolute beginner to advanced competitive programming level. Each topic follows a consistent pedagogical structure: Theory → Visualization → Examples → Quiz → Practice.
8.1 Curriculum Structure
•	5 learning tracks: Beginner DSA, Interview Prep, Competitive Programming, Advanced Algorithms, System Design Prep
•	Each track has 8–20 modules; each module covers one data structure or algorithm family
•	Prerequisites map: visual dependency graph showing what to learn before advancing
•	Estimated completion time displayed per module and track
•	Syllabus alignment: GATE CS, FAANG Interview, IIT/NIT college syllabus modes
8.2 Lesson Components
Component	Description	Format
Theory Card	Concept explanation with definition, properties, real-world use cases	Markdown + diagrams
Complexity Analysis	Time and space complexity with Big-O derivation walkthrough	Animated notation panel
Interactive Visualization	Embedded visualization with preset examples	Compose Canvas
Code Walkthrough	Annotated code in Java, Kotlin, Python, C++	Syntax-highlighted editor
Examples	3–5 worked examples with trace tables	Step cards
Quiz	5–10 MCQ + drag-drop + fill-in questions per lesson	Interactive quiz widget
Summary Card	Key takeaways and cheat sheet	Pinnable card
AI Chat	Ask Sarvam AI any question about the lesson in your language	Chat interface

8.3 Personalized Learning Path
•	Onboarding assessment: 10-question diagnostic quiz assigns initial skill level
•	Adaptive recommendations: after each lesson, Sarvam AI recommends the next optimal topic based on performance
•	Weak spot detection: topics where quiz scores are below 60% are flagged for revision
•	Spaced repetition: revision reminders for topics not visited in 7+ days
•	Daily goal setting: users set a daily XP target; streak tracking rewards consistency
8.4 Content Languages
All lesson text is available in English. Sarvam AI provides real-time translation and voice narration for lessons in 22 Indian languages including Hindi, Tamil, Telugu, Kannada, Malayalam, Marathi, Bengali, Gujarati, Punjabi, Odia, and more.
9. Feature Set — Practice & Training Arena

The Training Arena is the hands-on coding environment within AlgoViz. It provides a full-featured in-app code editor, 500+ curated problems, contests, and AI-assisted hints.
9.1 Problem Library
•	500+ problems curated across all DSA topics, 3 difficulty levels: Easy, Medium, Hard
•	Problem tags: topic-based (Graph, DP, String) + company tags (Google, Amazon, Flipkart)
•	Problem detail screen: description, constraints, sample I/O, editorial (Premium), hints
•	Daily challenge: one new problem every day with bonus XP for first 100 solvers
•	Problem sets: curated lists e.g., 'Top 100 Interview Questions', 'Graph Marathon', 'DP Bootcamp'
9.2 Code Editor
•	Languages supported: Java, Kotlin, Python 3, C, C++, JavaScript
•	Syntax highlighting, auto-indentation, bracket matching, line numbers
•	Custom test case input panel — run code against custom inputs before submission
•	Submission verdict: Accepted, Wrong Answer, Time Limit Exceeded, Runtime Error, Compilation Error
•	Runtime and memory stats shown after each submission
•	Keyboard shortcuts optimized for mobile (custom toolbar above system keyboard)
•	Execution backend: Judge0 Community Edition via Supabase Edge Function proxy
9.3 AI-Powered Hints (Sarvam AI)
•	3-level hint system: Nudge (direction), Approach (strategy), Pseudo-code (full outline)
•	Hints delivered via Sarvam AI chat in user's preferred language
•	Voice hint mode: Sarvam TTS reads the hint aloud (great for mobile use)
•	Code review: after submission, AI provides line-by-line feedback and optimization suggestions
•	Complexity analysis: AI explains the time/space complexity of the submitted code
9.4 Contests & Challenges
•	Weekly rated contests: 2-hour, 4-problem format with live rankings
•	Topic-specific sprints: 30-minute blitz on a single topic (e.g., 'Graph Blitz')
•	Global leaderboard (all-time) and Weekly leaderboard with resets every Monday
•	Virtual contests: attempt any past contest in simulated mode with ranking vs historical participants
•	Group contests: create a private contest and invite friends (Premium)
9.5 Visualization-to-Code Bridge
A unique AlgoViz feature: after watching a visualization, users can tap 'Try It' to load a pre-scaffolded version of the algorithm's code in the editor. They complete the implementation gaps guided by the same step annotations they saw in the visualization.
10. Feature Set — Profile Management

10.1 User Profile Screen
•	Avatar (camera upload or avatar builder with Supabase Storage)
•	Username, bio, college/organization, location, social links (GitHub, LinkedIn)
•	Tier badge: Novice, Learner, Practitioner, Expert, Master, Grandmaster (based on XP)
•	Current streak counter with calendar heatmap (GitHub-style contribution graph)
•	Total problems solved breakdown by difficulty (Easy/Medium/Hard pie chart)
•	Language preference for Sarvam AI (stored in users table, applies globally)
10.2 Progress Dashboard
•	Topic mastery map: visual grid of all DSA topics, color-coded by completion percentage
•	Weekly activity chart: problems solved, lessons completed, visualization sessions
•	Time spent studying: daily average and all-time total
•	Strongest topics and weakest topics (auto-identified from quiz scores)
•	XP history graph: points earned per day over the last 30 days
10.3 Achievements & Gamification
Badge	Trigger Condition
First Steps	Complete first visualization
Consistent Learner	7-day streak
Century Club	Solve 100 problems
Speed Demon	Solve a Hard problem in under 30 minutes
Polyglot	Submit solutions in 4 different languages
Graph Guru	Complete all Graph topic visualizations
AI Student	Have 10 conversations with Sarvam AI tutor
Contest Finisher	Complete a weekly rated contest
Top 10	Finish in top 10 of a weekly leaderboard
AlgoViz Legend	Reach Grandmaster tier

10.4 Social Features
•	Follow other users and see their public progress on a social feed
•	Solution sharing: share elegant solutions with code + visualization replay (Premium)
•	Discussion threads per problem: upvote, comment, tag approaches
•	Study groups: create a group, track collective progress on a shared dashboard
10.5 Settings & Preferences
•	Language preference: UI language and Sarvam AI response language (independent settings)
•	Visualization speed default, color theme, font size (accessibility)
•	Notification preferences: daily reminder, contest alerts, streak warnings
•	Data & Privacy: download my data (DPDPA compliant), delete account
•	Subscription management: upgrade, cancel, billing history
11. Sarvam AI Integration

Sarvam AI is India's full-stack sovereign AI platform with purpose-built models for Indian languages. AlgoViz integrates Sarvam AI as the complete intelligence layer of the platform — from voice tutoring to adaptive hints to multilingual content delivery.
11.1 Sarvam AI Model Portfolio Used
Sarvam Model	Capability	AlgoViz Use Case
sarvam-m	Multilingual hybrid reasoning LLM (Mistral-based, 24B params)	AI tutor chat, code review, adaptive hints, DSA Q&A
Sarvam 30B	30B parameter multilingual chat model	Deep conceptual explanations, curriculum recommendations
Sarvam 105B (MoE)	Flagship 105B MoE model	Premium users: interview simulation, complex problem analysis
bulbul:v2 (TTS)	Indian Text-to-Speech, 11 languages	Voice narration of lessons and hints
Shuka (STT / ASR)	Speech-to-text, 23 languages (22 Indian + English)	Voice queries to AI tutor, voice code dictation
sarvam-translate:v1	Translation across 22 Indian languages + English	Lesson content translation, UI localization
Speech-to-Text-Translate	ASR + translation in one pass	Speak in Hindi, get answer in Tamil

11.2 AI Tutor (Core Feature)
The Sarvam AI Tutor is a contextual, multi-turn chat assistant embedded throughout the app. It is aware of the currently viewed topic, the user's progress, and their preferred language.
•	Context-aware: system prompt includes current topic, user's skill level, recent mistakes
•	Hybrid reasoning: uses sarvam-m in 'think' mode for complex DSA problem analysis
•	Multilingual: responds in the language the user selects (22 Indian languages supported)
•	Persistent sessions: conversation history stored in Supabase ai_sessions table (last 30 days)
•	Voice mode: full STT → LLM → TTS pipeline for entirely voice-based tutoring
•	Code understanding: user can paste code; AI explains, optimizes, or finds bugs
•	Floating AI button: accessible from every screen — no need to navigate to a chat screen
11.3 Voice Pipeline Architecture
11.	User presses microphone button → audio recorded on device
12.	Audio sent to Supabase Edge Function (sarvam-proxy)
13.	Edge Function calls Sarvam STT API (Shuka) → returns transcript
14.	Transcript sent to sarvam-m with topic context system prompt
15.	LLM response returned → Edge Function calls Sarvam TTS (bulbul:v2)
16.	Audio stream returned to Android → played via ExoPlayer
17.	Text transcript displayed in chat UI simultaneously
11.4 Translation & Localization
•	On-demand lesson translation: tap a translate button on any lesson card
•	sarvam-translate:v1 used for lesson body, quiz questions, and problem statements
•	User interface language: Hindi, Tamil, Telugu, Kannada, Marathi, Bengali, Gujarati, English
•	Romanized Indian language input supported via sarvam-m (type Hindi in English letters)
11.5 AI-Powered Features Summary
Feature	Sarvam AI Component	Trigger
Step explanation narration	bulbul:v2 TTS	Tap 'AI Explain' in visualization
Voice query to AI tutor	Shuka STT + sarvam-m + bulbul:v2 TTS	Microphone button in chat
3-level hints for problems	sarvam-m (reasoning mode)	Tap 'Hint' in practice arena
Code review & optimization	sarvam-m	Tap 'AI Review' after submission
Lesson translation	sarvam-translate:v1	Tap translate icon on lesson card
Next topic recommendation	sarvam-m	After lesson/quiz completion
Complexity analysis of code	sarvam-m	Auto-triggered after each submission
Interview Q&A simulation	Sarvam 105B (Premium)	Interview Mode screen
Speech-to-text in code editor	Shuka STT	Tap mic in editor toolbar
Daily learning tip	sarvam-m	Push notification at user's preferred time

11.6 API Integration Details
•	All Sarvam API calls are proxied through Supabase Edge Functions to protect API keys
•	Android client sends JWT-authenticated requests to the Edge Function endpoints
•	Audio data transmitted as base64 in JSON or as multipart form (for STT)
•	TTS audio cached in Supabase Storage bucket (ai-audio) to reduce repeated API calls
•	WebSocket-based real-time STT used for low-latency voice interaction (Sarvam STT WebSocket beta)
•	Rate limiting applied at Edge Function level: free tier (20 AI queries/day), premium (unlimited)
12. Android Application Design

12.1 Navigation Structure
Bottom navigation bar with 5 primary destinations:
Tab	Icon	Screens
Home	House	Dashboard, Daily Challenge, Recommendations
Learn	Book	Topic Browser, Lesson Detail, Quiz
Visualize	Play	Algorithm Picker, Visualization Screen, Comparison Screen
Arena	Code	Problem List, Problem Detail, Code Editor, Contests, Leaderboard
Profile	Person	My Profile, Progress Dashboard, Achievements, Settings, AI Chat History

12.2 Key Screens
Home Dashboard
•	Personalized greeting with current streak
•	Daily challenge card with countdown timer
•	AI-recommended next topic card
•	Recent activity feed (visualizations watched, problems solved)
•	Quick action tiles: Resume lesson, Today's contest, Random problem
Visualization Screen
•	Full-screen canvas with algorithm animation
•	Bottom control panel: play/pause/step controls and speed slider
•	Floating pseudo-code panel (collapsible)
•	Step counter and complexity live display
•	AI Explain FAB (Floating Action Button) for voice narration
•	Input editor sheet: modify starting array/graph/tree
Code Editor Screen
•	Split-pane: code editor (top) + I/O panel (bottom)
•	Language selector dropdown
•	Custom input toggle and Run button
•	Submission result card with verdict, runtime, memory
•	Hint drawer (slide in from right)
•	AI Review sheet (slide up from bottom) after submission
AI Tutor Screen
•	Chat bubble UI with user and AI messages
•	Language selector at top (22+ options)
•	Microphone FAB for voice input
•	Context chip showing current topic
•	Message actions: copy, translate, read aloud (TTS)
•	Session history accessible from header menu
Profile Screen
•	Avatar + username + tier badge at top
•	Streak heatmap calendar
•	Stats row: problems solved, lessons done, contest rank
•	Progress ring chart per topic category
•	Achievements gallery
•	Social: followers, following, public solutions
12.3 Design System
•	Material 3 design language with dynamic color theming (Material You)
•	Brand colors: Deep Navy (#0A1F44) primary, Mint Accent (#00C297), Orange (#FF6B35)
•	Typography: Outfit font (headings), Inter (body), JetBrains Mono (code)
•	Dark mode fully supported with semantic color tokens
•	Minimum tap target size 48dp across all interactive elements (WCAG AA)
•	Adaptive layouts: phone (portrait primary), tablet (two-panel layout support)
12.4 Performance Requirements
•	App cold start: < 2 seconds on mid-range Android (Snapdragon 680 class)
•	Visualization frame rate: ≥ 60 fps on devices with ≥ 3GB RAM
•	Offline mode: all previously viewed visualizations and lessons cached in Room DB
•	APK size: < 25 MB (use on-demand asset delivery for visualization assets)
•	Min SDK: Android 8.0 (API 26), Target SDK: Android 15 (API 35)
•	Foreground service for long code executions with progress notification
13. Non-Functional Requirements

13.1 Security
•	All API keys stored in Supabase Edge Function environment variables — never in APK
•	Supabase RLS enforces data isolation — no user can access another user's data
•	Android Keystore for encrypting local auth tokens (EncryptedSharedPreferences)
•	Certificate pinning for all API endpoints
•	Code submission sandboxed via Judge0 isolated containers
•	DPDPA (Digital Personal Data Protection Act) compliance for Indian user data
13.2 Scalability
•	Supabase PgBouncer connection pooling handles concurrent DB connections
•	Supabase Edge Functions auto-scale based on request volume
•	CDN for static assets (topic images, visualization presets via Supabase Storage CDN)
•	Read replicas planned for leaderboard and problem library tables at 100K+ DAU
13.3 Accessibility
•	TalkBack screen reader compatibility for all core screens
•	Dynamic font size support (system accessibility text size respects app layout)
•	Color-blind friendly palette with non-color differentiation for visualization states
•	All media content (visualizations) paired with text descriptions and AI narration
13.4 Testing Strategy
Test Type	Tool	Coverage Target
Unit tests	JUnit 5 + Mockk	≥ 80% for UseCases and ViewModels
Integration tests	JUnit 5 + Supabase test project	All repository methods
UI tests	Jetpack Compose UI Test	All primary user flows
Flow/coroutine tests	Turbine	All StateFlow emissions
API contract tests	Retrofit Mock Server	All Sarvam AI endpoints
Performance tests	Android Benchmark	Visualization frame rate, startup time
Accessibility tests	Accessibility Test Framework	All screens

14. Milestones & Roadmap

Phase	Timeline	Deliverables
Phase 0 — Foundation	Weeks 1–4	Supabase project setup, MVVM skeleton, Auth flow, Room DB, CI/CD pipeline
Phase 1 — Visualization MVP	Weeks 5–10	Visualization engine, 20 core algorithms, Compose Canvas renderer, custom input
Phase 2 — Learning Platform	Weeks 11–16	Topic browser, lesson system, quiz engine, progress tracking, curriculum for 30 topics
Phase 3 — Practice Arena	Weeks 17–22	Code editor, Judge0 integration, 200 problems, submission system, basic leaderboard
Phase 4 — Sarvam AI Integration	Weeks 23–27	AI tutor chat, STT/TTS pipeline, voice hints, lesson translation, adaptive recommendations
Phase 5 — Profile & Gamification	Weeks 28–31	Full profile, heatmap, badges, streak system, social features, weekly contests
Phase 6 — Polish & Launch	Weeks 32–36	Performance optimization, full offline support, 500+ problems, Play Store launch
Phase 7 — Growth (Post-launch)	Months 10–12	College partnerships, group study, certificate generation, Sarvam 105B Premium tier

15. Risk Register

Risk	Likelihood	Impact	Mitigation
Sarvam AI API latency > 3s for voice pipeline	Medium	High	Cache TTS audio; implement streaming STT WebSocket; show typing indicator
Judge0 API rate limits during peak contest traffic	Medium	High	Self-host Judge0 CE on Supabase-compatible VM above 50K DAU threshold
Visualization Canvas performance on low-end devices	High	Medium	Frame-skip mode for devices < 2GB RAM; raster fallback for complex graphs
Supabase Realtime connection drops on poor mobile networks	Medium	Medium	Optimistic UI updates with background sync; exponential backoff reconnect
Content piracy (premium problems leaked)	Low	Medium	Obfuscate problem IDs; enforce server-side content delivery; watermarking
Sarvam AI hallucination in DSA explanations	Medium	High	Validate AI responses against curated knowledge base; user report button
DPDPA compliance for storing conversation data	Low	High	Data residency in India (Supabase India region); explicit consent flows; data TTL

16. Appendix — Complete DSA Topic List

All topics covered by AlgoViz across visualizations and learning modules:
Foundational Concepts
Complexity Analysis (Big-O, Omega, Theta)  |  Recursion & Recurrences  |  Bit Manipulation  |  Two Pointers  |  Sliding Window  |  Prefix Sum
Linear Data Structures
Array Operations  |  Singly Linked List  |  Doubly Linked List  |  Circular Linked List  |  Stack (Array & LL)  |  Queue (Array & LL)  |  Circular Queue  |  Deque
Sorting Algorithms
Bubble Sort  |  Selection Sort  |  Insertion Sort  |  Merge Sort  |  Quick Sort  |  Heap Sort  |  Counting Sort  |  Radix Sort  |  Shell Sort  |  Tim Sort
Searching Algorithms
Linear Search  |  Binary Search  |  Jump Search  |  Interpolation Search  |  Exponential Search  |  Ternary Search
Tree Structures
Binary Tree  |  Binary Search Tree (BST)  |  AVL Tree  |  Red-Black Tree  |  Segment Tree  |  Fenwick Tree (BIT)  |  Trie  |  Suffix Tree  |  Suffix Array  |  B-Tree
Heap & Priority Queue
Min Heap  |  Max Heap  |  Priority Queue  |  Heap Operations  |  Fibonacci Heap
Hashing
Hash Table (Chaining)  |  Hash Table (Open Addressing)  |  Hash Map  |  Hash Set  |  Rolling Hash
Graph Algorithms
BFS  |  DFS  |  Topological Sort (DFS & Kahn's)  |  Dijkstra's Shortest Path  |  Bellman-Ford  |  Floyd-Warshall  |  Prim's MST  |  Kruskal's MST  |  Tarjan's SCC  |  Kosaraju's SCC  |  A* Search  |  Bipartite Check  |  Eulerian Path/Circuit  |  Hamiltonian Path
Dynamic Programming
Memoization vs Tabulation  |  0/1 Knapsack  |  Unbounded Knapsack  |  Longest Common Subsequence  |  Longest Increasing Subsequence  |  Matrix Chain Multiplication  |  Coin Change  |  Edit Distance  |  DP on Trees  |  DP on Graphs (DAG)
Greedy Algorithms
Activity Selection  |  Fractional Knapsack  |  Huffman Coding  |  Job Scheduling  |  Minimum Platforms
Backtracking
N-Queens  |  Sudoku Solver  |  Rat in a Maze  |  Word Search  |  Subset Sum  |  Graph Coloring
String Algorithms
KMP Pattern Matching  |  Rabin-Karp  |  Z-Algorithm  |  Aho-Corasick  |  Manacher's (Palindrome)  |  Longest Common Prefix
Advanced DSA
Disjoint Set Union (DSU)  |  Sparse Table (RMQ)  |  Monotonic Stack  |  Monotonic Queue  |  Deque-based DP  |  Heavy-Light Decomposition  |  LCA (Binary Lifting)  |  Centroid Decomposition  |  Skip List
Mathematical Algorithms
Sieve of Eratosthenes  |  Prime Factorization  |  GCD / LCM (Euclidean)  |  Modular Exponentiation  |  Chinese Remainder Theorem  |  Catalan Numbers


AlgoViz PRD v1.0  —  Confidential  —  March 2026
