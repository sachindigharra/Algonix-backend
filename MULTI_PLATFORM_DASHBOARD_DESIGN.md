# Multi-Platform Coding Dashboard - Design & Implementation Guide

## 📋 Executive Summary

This document outlines the architecture and implementation strategy for integrating a **unified dashboard** that aggregates data from multiple coding platforms (LeetCode, Codeforces, CodeChef, GitHub, GeeksforGeeks, HackerRank) into a single, coherent user experience.

**Goal:** Users can see all their competitive programming stats, problems solved, contest ratings, contributions, and activity across platforms in one place—eliminating the need to jump between tabs.

---

## 🏗️ Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                           │
│  (Frontend: Multi-Platform Dashboard UI Components)             │
└────────────┬────────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                            │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  PlatformController                                      │   │
│  │  - GET /api/v1/dashboard                  (Unified view) │   │
│  │  - GET /api/v1/platforms/{platform}       (Platform view)│   │
│  │  - POST /api/v1/platforms/connect         (Link account) │   │
│  │  - POST /api/v1/platforms/sync            (Manual sync)  │   │
│  │  - GET /api/v1/analytics                  (Trends)       │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────┬────────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────────┐
│                     SERVICE LAYER                               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  PlatformIntegrationService (Orchestrator)               │   │
│  │  - connectProfile()      → Connect new platform          │   │
│  │  - syncProfile()         → Refresh single platform       │   │
│  │  - syncAllProfiles()     → Batch sync all platforms      │   │
│  │  - getUnifiedDashboard() → Aggregate all data            │   │
│  │  - getAnalytics()        → Compute trends & insights     │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  CodingPlatformFactory (Strategy Pattern)                │   │
│  │  - getService(platform) → Route to correct implementation│   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Platform-Specific Implementations                       │   │
│  │  ├── LeetCodeService                                     │   │
│  │  ├── CodeforcesService                                   │   │
│  │  ├── CodeChefService                                     │   │
│  │  ├── GitHubService                                       │   │
│  │  ├── GeeksforGeeksService                                │   │
│  │  └── HackerRankService                                   │   │
│  │                                                           │   │
│  │  Each implements CodingPlatformService with:             │   │
│  │  - sync(username) → Fetch and return PlatformSyncData    │   │
│  │  - fetchProfile(), fetchContests(), fetchSolvedProblems()│   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Data Persistence Services                               │   │
│  │  ├── PlatformProfileService                              │   │
│  │  ├── ProblemService                                      │   │
│  │  ├── ContestService                                      │   │
│  │  ├── BadgeService (Future)                               │   │
│  │  └── SubmissionCalendarService (Future)                  │   │
│  └──────────────────────────────────────────────────────────┘   │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Scheduler Service (Background Sync)                     │   │
│  │  - syncAllUserProfiles() → Periodic sync (every 6h)      │   │
│  │  - syncProfileWithRetry() → Resilient retry logic        │   │
│  └──────────────────────────────────────────────────────────┘   │
└────────────┬────────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                             │
│  ├── PlatformProfileRepository  (Profile stats)                 │
│  ├── ProblemRepository           (Solved problems)              │
│  ├── ContestRepository           (Contest history)              │
│  ├── SubmissionCalendarRepository (Activity calendar)           │
│  └── BadgeRepository             (Achievements)                 │
└────────────┬────────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────────┐
│                   EXTERNAL PLATFORMS                            │
│  ├── LeetCode GraphQL API                                       │
│  ├── Codeforces REST API                                        │
│  ├── CodeChef API                                               │
│  ├── GitHub API                                                 │
│  ├── GeeksforGeeks API                                          │
│  └── HackerRank API                                             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔑 Core Concepts & Design Patterns

### 1. **Strategy Pattern** (Platform Implementations)
Each platform has its own implementation of `CodingPlatformService`:
- **Why:** Each platform has different API structures, data formats, and requirements
- **Benefit:** Adding new platforms is as simple as implementing the interface
- **Example:** `LeetCodeService` vs `CodeforcesService` have different GraphQL/REST calls but same contract

### 2. **Factory Pattern** (CodingPlatformFactory)
Routes requests to the correct platform service based on `PlatformType` enum:
```java
CodingPlatformService service = factory.getService(PlatformType.LEETCODE);
PlatformSyncData data = service.sync("username");
```
- **Why:** Decouples controller from platform implementations
- **Benefit:** Simple, type-safe routing; extensible for new platforms

### 3. **Facade Pattern** (PlatformIntegrationService)
Single entry point orchestrating multiple steps:
- Connect profile → Fetch data → Persist to DB
- Sync one platform or batch sync all platforms
- Aggregate data for unified dashboard
- **Why:** Hides complexity; handles transactions and error handling

### 4. **Repository Pattern** (Data Access)
Separates data access logic from business logic:
- `PlatformProfileRepository` manages profile entities
- `ProblemRepository` manages solved problems
- **Why:** Easy to swap implementations (DB, cache, etc.); testable

---

## 📊 Data Model & Entities

### PlatformProfile Entity
Stores aggregated stats for each user-platform pair:
```
┌─────────────────────────────────────┐
│      PlatformProfile                │
├─────────────────────────────────────┤
│ id (UUID)                           │
│ user_id (FK to User)                │
│ platform (LEETCODE, CODEFORCES...) │
│ platformUsername (handle)           │
│ profileUrl                          │
│ displayName                         │
│ avatarUrl                           │
├─────────────────────────────────────┤
│ PROBLEM STATS:                      │
│ totalSolved, easySolved,            │
│ mediumSolved, hardSolved            │
├─────────────────────────────────────┤
│ CONTEST STATS:                      │
│ contestRating, maxContestRating     │
│ contestAttended, globalRanking      │
├─────────────────────────────────────┤
│ ACTIVITY STATS:                     │
│ totalSubmissions, activeDays        │
│ currentStreak, maxStreak            │
│ contributions, acceptanceRate       │
├─────────────────────────────────────┤
│ SYNC INFO:                          │
│ connected (Boolean)                 │
│ lastSyncedAt (Timestamp)            │
│ createdAt, updatedAt                │
└─────────────────────────────────────┘
```

### Problem Entity
Tracks individual problems solved:
```
├─ id
├─ user_id
├─ platform
├─ problemId (platform-specific ID)
├─ title
├─ difficulty (EASY, MEDIUM, HARD)
├─ category/topic (e.g., "Array", "DP")
├─ status (SOLVED, ATTEMPTED)
├─ solvedAt
├─ lastSubmittedAt
└─ attemptCount
```

### Contest Entity
Tracks contest participation:
```
├─ id
├─ user_id
├─ platform
├─ contestId (platform-specific)
├─ contestName
├─ rating
├─ rank
├─ participationDate
└─ deltaRating
```

### SubmissionCalendar Entity (Future)
Heat map data for activity tracking:
```
├─ id
├─ user_id
├─ platform
├─ date
├─ submissionCount
└─ status (ACTIVE, INACTIVE)
```

### Badge Entity (Future)
Achievements and badges:
```
├─ id
├─ user_id
├─ platform
├─ badgeId
├─ badgeName
├─ description
└─ earnedAt
```

---

## 🔄 Data Flow: Connect Profile

```
User Action: Click "Connect LeetCode"
│
▼
┌─────────────────────────────────────────┐
│ POST /api/v1/platforms/connect          │
│ {                                       │
│   "platform": "LEETCODE",               │
│   "username": "john_doe"                │
│ }                                       │
└────────────┬────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ PlatformController.connectProfile(userId, request)       │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ PlatformIntegrationService.connectProfile()              │
│ 1. Get platform service from factory                     │
│ 2. Call service.sync("john_doe")                         │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ LeetCodeService.sync(username)                           │
│ 1. Call LeetCode GraphQL API                             │
│ 2. Parse response                                        │
│ 3. Return PlatformSyncData {                             │
│      profile: {...},                                     │
│      solvedProblems: [...],                              │
│      contests: [...],                                    │
│      badges: [...]                                       │
│    }                                                     │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ PlatformIntegrationService.persist(userId, syncData)     │
│ 1. Save/Update PlatformProfile                           │
│ 2. Save/Update Problems (batch)                          │
│ 3. Save/Update Contests (batch)                          │
│ 4. Save Badges (future)                                  │
│ 5. Update submission calendar (future)                   │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
        DATABASE
   (All data persisted)
```

---

## 🔄 Data Flow: Unified Dashboard

```
User Action: Open Dashboard
│
▼
┌─────────────────────────────────────────┐
│ GET /api/v1/dashboard                   │
│ (Current user's ID from JWT)            │
└────────────┬────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ PlatformController.getUnifiedDashboard(userId)           │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ PlatformIntegrationService.getUnifiedDashboard(userId)   │
│ 1. Fetch all PlatformProfiles for user (cached)          │
│ 2. Aggregate stats:                                      │
│    - Total problems across platforms                     │
│    - Overall contest rating (weighted avg)               │
│    - Combined activity streak                            │
│    - Badge count                                         │
│ 3. Get recent activity                                   │
│ 4. Get trending topics                                   │
│ 5. Compile UnifiedDashboardResponse                      │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
        REPOSITORIES (Read from DB)
   ┌─────────────────────────────────┐
   │ PlatformProfileRepository        │
   │ ProblemRepository (Recent)       │
   │ ContestRepository (Recent)       │
   └─────────────────────────────────┘
             │
             ▼
┌──────────────────────────────────────────────────────────┐
│ UnifiedDashboardResponse {                               │
│   platforms: [                                           │
│     {                                                    │
│       platform: "LEETCODE",                              │
│       stats: {...},                                      │
│       lastSync: "2 hours ago"                            │
│     },                                                   │
│     {                                                    │
│       platform: "CODEFORCES",                            │
│       stats: {...},                                      │
│       lastSync: "4 hours ago"                            │
│     }                                                    │
│   ],                                                     │
│   aggregated: {                                          │
│     totalProblems: 350,                                  │
│     averageRating: 1850,                                 │
│     totalContests: 25,                                   │
│     streak: 15,                                          │
│     recentActivity: [...]                                │
│   }                                                      │
│ }                                                        │
└────────────┬─────────────────────────────────────────────┘
             │
             ▼
           JSON Response to Frontend
```

---

## 📥 Data Sync Strategies

### 1. **On-Demand Sync (User-Triggered)**
**Scenario:** User clicks "Refresh Now" button
```
POST /api/v1/platforms/{platform}/sync
↓
PlatformIntegrationService.syncProfile(userId, platform)
↓
Fetch latest from external API
↓
Update database
↓
Return updated stats
```

**Advantages:**
- Real-time data when user needs it
- User controls frequency

**Disadvantages:**
- External API calls can be slow
- Rate limits on external APIs

### 2. **Periodic Background Sync**
**Scenario:** Scheduler runs every 6 hours
```
@Scheduled(fixedDelay = 360000, initialDelay = 60000)
syncAllUserProfiles()
↓
For each User with connected profiles:
  For each Platform:
    Call PlatformIntegrationService.syncProfile(userId, platform)
↓
Update database
```

**Advantages:**
- Data always fresh
- No user wait time
- Can batch requests

**Disadvantages:**
- Costs (API calls for inactive accounts)
- Database load

### 3. **Hybrid Approach (Recommended)**
```
┌─────────────────────────────┐
│ User opens dashboard        │
└────────┬────────────────────┘
         │
         ▼
    Check lastSyncedAt
         │
         ├─ If < 30 min ago ──→ Show cached data
         │
         └─ If > 30 min ago ──→ Async background refresh
                                (Show cached, update in bg)
                                
Every 6 hours:
  Background job syncs all active users
```

This balance gives fresh data without excessive API calls.

---

## 🛡️ Error Handling & Resilience

### Sync Failure Scenarios

```
1. NETWORK ERROR
   ├─ Retry with exponential backoff (1s, 2s, 4s, 8s)
   ├─ Mark sync as "PENDING" after max retries
   ├─ Show stale data to user
   └─ Notify user of sync issue

2. INVALID USERNAME
   ├─ Show error: "Username not found on platform"
   ├─ Ask user to verify username
   └─ Mark profile as "DISCONNECTED"

3. RATE LIMITED
   ├─ Backoff for reset duration
   ├─ Queue sync for later
   └─ Continue with other platforms

4. PARTIAL SYNC FAILURE
   ├─ Save profile ✓
   ├─ Problems failed ✗
   ├─ Save progress anyway
   ├─ Retry problems on next sync
   └─ Show user which parts succeeded
```

### Implementation: Retry Mechanism
```java
@Transactional
public void syncProfileWithRetry(UUID userId, PlatformType platform) {
    int maxRetries = 3;
    int attempt = 0;
    
    while (attempt < maxRetries) {
        try {
            syncProfile(userId, platform);
            return; // Success
        } catch (TemporaryException e) {
            attempt++;
            if (attempt < maxRetries) {
                Thread.sleep((long) Math.pow(2, attempt) * 1000);
            }
        } catch (PermanentException e) {
            handlePermanentFailure(userId, platform, e);
            return;
        }
    }
    
    markSyncPending(userId, platform);
}
```

---

## 🔍 Analytics & Insights

### Dashboard should show:

1. **Cross-Platform Stats**
   - Total problems solved (all platforms)
   - Average contest rating (weighted)
   - Total streak (combined activity)
   - Common topics across platforms

2. **Trends**
   - Problems solved per week (chart)
   - Contest rating over time
   - Submission frequency
   - Topics mastered (top categories)

3. **Comparisons**
   - Performance by platform
   - Easy vs Hard distribution
   - Time invested per platform
   - Rank percentile across platforms

### Implementation Example:
```java
@Service
public class AnalyticsService {
    
    public DashboardAnalytics getAnalytics(UUID userId) {
        // 1. Get all platforms for user
        List<PlatformProfile> profiles = 
            profileRepo.findByUserId(userId);
        
        // 2. Aggregate data
        int totalProblems = profiles.stream()
            .mapToInt(PlatformProfile::getTotalSolved)
            .sum();
        
        // 3. Calculate trends
        List<WeeklyStat> weeklyStats = 
            problemRepo.getWeeklyStats(userId);
        
        // 4. Find common topics
        Map<String, Integer> topicFreq = 
            problemRepo.getTopicDistribution(userId);
        
        return DashboardAnalytics.builder()
            .totalProblems(totalProblems)
            .weeklyStats(weeklyStats)
            .topicFrequency(topicFreq)
            .build();
    }
}
```

---

## 🚀 Implementation Roadmap

### Phase 1: Foundation (Weeks 1-2)
- [ ] Create missing platform service implementations (Codeforces, CodeChef, GitHub)
- [ ] Implement PlatformIntegrationService fully
- [ ] Add PlatformProfileService complete CRUD
- [ ] Create unified dashboard DTOs
- [ ] Write integration tests

### Phase 2: Dashboard API (Weeks 3-4)
- [ ] Create PlatformController endpoints
- [ ] Implement getUnifiedDashboard() logic
- [ ] Add caching layer (Redis)
- [ ] Implement rate limiting

### Phase 3: Background Sync (Week 5)
- [ ] Create PlatformSyncScheduler
- [ ] Implement retry mechanism
- [ ] Add sync status tracking
- [ ] Create monitoring/alerting

### Phase 4: Analytics & Insights (Week 6)
- [ ] Implement AnalyticsService
- [ ] Create trend calculation
- [ ] Add topic analysis
- [ ] Performance optimization

### Phase 5: Advanced Features (Week 7+)
- [ ] Badges system
- [ ] Submission calendar
- [ ] Goal tracking
- [ ] Social features (friend comparison)

---

## 📁 File Structure

```
src/main/java/com/algonix/server/
├── controller/
│   ├── PlatformController.java          (NEW - Unified endpoints)
│   └── AnalyticsController.java         (NEW - Insights)
├── service/
│   ├── CodingPlatformService.java       (Existing interface)
│   ├── PlatformIntegrationService.java  (Existing interface)
│   ├── AnalyticsService.java            (NEW)
│   └── impl/
│       ├── PlatformIntegrationServiceImpl.java (Existing)
│       ├── LeetCodeService.java         (Existing, needs completion)
│       ├── CodeforcesService.java       (NEW)
│       ├── CodeChefService.java         (NEW)
│       ├── GitHubService.java           (NEW)
│       ├── GeeksforGeeksService.java    (NEW)
│       ├── HackerRankService.java       (NEW)
│       ├── PlatformSyncScheduler.java   (NEW)
│       └── AnalyticsServiceImpl.java     (NEW)
├── dto/
│   ├── PlatformSyncData.java            (Existing)
│   ├── UnifiedDashboardResponse.java    (NEW)
│   ├── DashboardAnalytics.java          (NEW)
│   └── ... (platform-specific DTOs)
├── entity/
│   ├── PlatformProfile.java             (Existing)
│   ├── Problem.java                     (Existing)
│   ├── Contest.java                     (Existing)
│   ├── SubmissionCalendar.java          (NEW)
│   └── Badge.java                       (NEW)
├── repository/
│   ├── PlatformProfileRepository.java   (Existing)
│   ├── ProblemRepository.java           (Existing)
│   ├── ContestRepository.java           (Existing)
│   ├── SubmissionCalendarRepository.java (NEW)
│   └── BadgeRepository.java             (NEW)
└── config/
    └── CodingPlatformFactory.java       (Existing)
```

---

## 🧪 Testing Strategy

### Unit Tests
```java
@Test
public void testSyncSuccessfullyFetchesAndPersistsData() {
    // Arrange
    PlatformSyncData mockData = createMockSyncData();
    when(leetCodeService.sync("john_doe"))
        .thenReturn(mockData);
    
    // Act
    integrationService.connectProfile(userId, request);
    
    // Assert
    verify(profileRepo, times(1)).save(any());
    verify(problemRepo, times(mockData.getSolvedProblems().size()))
        .save(any());
}
```

### Integration Tests
```java
@Test
@Transactional
public void testGetUnifiedDashboardAggregatesAllPlatforms() {
    // Setup: Create user with 2 platform profiles
    User user = createTestUser();
    createPlatformProfile(user, LEETCODE, 100, 5000);
    createPlatformProfile(user, CODEFORCES, 150, 4800);
    
    // Act
    UnifiedDashboardResponse response = 
        integrationService.getUnifiedDashboard(user.getId());
    
    // Assert
    assertEquals(250, response.getAggregated().getTotalProblems());
    assertEquals(2, response.getPlatforms().size());
}
```

### API Tests (MockMvc)
```java
@Test
public void testDashboardEndpointReturnsAggregatedData() {
    mockMvc.perform(get("/api/v1/dashboard"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.aggregated.totalProblems").exists())
        .andExpect(jsonPath("$.platforms[0].platform").exists());
}
```

---

## 🔐 Security Considerations

1. **Authentication:** JWT token required for all endpoints
2. **Authorization:** Users can only see their own data
3. **Rate Limiting:** 100 requests/minute per user
4. **Data Validation:** Sanitize platform usernames (SQL injection)
5. **API Credentials:** Store platform API keys in secure vault (AWS Secrets Manager)
6. **HTTPS Only:** All external API calls use HTTPS

---

## 📊 Database Considerations

### Indexes (Performance)
```sql
CREATE INDEX idx_user_id ON platform_profiles(user_id);
CREATE INDEX idx_platform ON platform_profiles(platform);
CREATE INDEX idx_user_platform ON platform_profiles(user_id, platform);
CREATE INDEX idx_problem_user_platform ON problems(user_id, platform);
CREATE INDEX idx_contest_date ON contests(user_id, participation_date DESC);
```

### Caching Strategy
```
- PlatformProfile: Cache for 30 minutes
- UnifiedDashboard: Cache for 15 minutes (invalidate on sync)
- Analytics data: Cache for 1 hour
- User's platform list: Cache for 1 day
```

---

## 🎯 Key API Endpoints

### Dashboard Endpoints
```
GET    /api/v1/dashboard                   → Unified dashboard
GET    /api/v1/dashboard/analytics         → Trends & insights
GET    /api/v1/platforms                   → List connected platforms
GET    /api/v1/platforms/{platform}        → Single platform details
POST   /api/v1/platforms/connect           → Connect new platform
DELETE /api/v1/platforms/{platform}        → Disconnect platform
POST   /api/v1/platforms/{platform}/sync   → Manual refresh
```

### Response Example
```json
{
  "platforms": [
    {
      "platform": "LEETCODE",
      "stats": {
        "totalSolved": 250,
        "easySolved": 100,
        "mediumSolved": 100,
        "hardSolved": 50,
        "contestRating": 1850,
        "maxContestRating": 1900,
        "streak": 15,
        "acceptanceRate": 78.5
      },
      "recentActivity": [
        {
          "date": "2024-08-08",
          "count": 3,
          "difficulty": "MEDIUM"
        }
      ],
      "lastSync": "2024-08-08T10:30:00Z",
      "connected": true
    }
  ],
  "aggregated": {
    "totalProblems": 450,
    "totalContests": 25,
    "averageRating": 1800,
    "overallStreak": 20,
    "topTopics": ["Array", "DP", "Graph"],
    "thisWeekSolved": 18,
    "thisMonthSolved": 85
  }
}
```

---

## 🚦 Next Steps

1. **Review** this design with your team
2. **Create** detailed API specification (Swagger/OpenAPI)
3. **Start Phase 1** with platform implementations
4. **Build** comprehensive test suite
5. **Deploy** to staging environment
6. **Get feedback** from beta users
7. **Iterate** and improve based on usage patterns

---

## 📝 Notes for Implementation

### Key Takeaways:
1. **Abstraction over Specifics:** Each platform service is isolated; controller doesn't care which platform
2. **Single Responsibility:** Each class has one reason to change
3. **DRY Principle:** Common logic in PlatformIntegrationService, specific logic in each platform service
4. **Error Resilience:** Partial syncs, retries, fallback to cached data
5. **Scalability:** Ready for adding new platforms without modifying core logic
6. **Performance:** Caching, batching, async operations

### Common Pitfalls to Avoid:
- ❌ Hardcoding platform-specific logic in controller
- ❌ Not handling API rate limits
- ❌ Syncing all users on every request
- ❌ Storing API credentials in code
- ❌ Ignoring data validation from external APIs
- ❌ Not tracking sync status/errors

---

**Document Version:** 1.0  
**Last Updated:** 2024-08-08  
**Author:** AI Design Review
