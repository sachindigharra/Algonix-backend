# Architecture Patterns - Quick Reference Guide

## 🎯 Why These Patterns Matter

### Problem You're Solving
**Without patterns:** You'd have platform-specific code scattered everywhere
```
Controller
├─ if (platform == LEETCODE) → LeetCode API logic
├─ if (platform == CODEFORCES) → Codeforces API logic
├─ if (platform == CODECHEF) → CodeChef API logic
└─ repeat 100 times → CHAOS 😱
```

**With patterns:** Clean, maintainable, scalable code
```
Controller → Factory → Service Interface ← Multiple Implementations
                                           ├─ LeetCode
                                           ├─ Codeforces
                                           └─ CodeChef
```

---

## 1️⃣ STRATEGY PATTERN (Platform-Specific Implementations)

### What It Does
Defines a family of algorithms (platform APIs), encapsulates each one, and makes them interchangeable.

### The Pattern
```
┌─────────────────────────────────┐
│   CodingPlatformService         │ ← Interface (Contract)
│  (Abstract Strategy)            │
│                                 │
│ + getPlatform()                 │
│ + sync(username)                │
│ + fetchProfile(username)        │
│ + fetchContests(username)       │
│ + fetchSolvedProblems(username) │
└────┬────────────┬────────────┬──┘
     │            │            │
     ▼            ▼            ▼
  LeetCode    Codeforces    CodeChef
  Service     Service       Service
  (Concrete)  (Concrete)    (Concrete)
```

### Real Example: LeetCode vs Codeforces
```java
// Interface - Same contract for all
public interface CodingPlatformService {
    PlatformSyncData sync(String username);
    PlatformType getPlatform();
}

// LeetCode Implementation - GraphQL API
@Service
public class LeetCodeService implements CodingPlatformService {
    @Override
    public PlatformSyncData sync(String username) {
        // Uses GraphQL to fetch data
        String graphqlQuery = "{ userProfile(username: \"" + username + "\") { ... } }";
        // Call LeetCode GraphQL endpoint
        // Parse response
        // Return PlatformSyncData
    }
}

// Codeforces Implementation - REST API
@Service
public class CodeforcesService implements CodingPlatformService {
    @Override
    public PlatformSyncData sync(String username) {
        // Uses REST API to fetch data
        // Call https://codeforces.com/api/user.info?handles=username
        // Parse JSON response
        // Return PlatformSyncData
    }
}
```

### Why This Matters ✅
- **Open/Closed Principle:** Open for extension (add new platforms), closed for modification (don't touch existing code)
- **No If-Else Chains:** No controller saying `if (platform == LEETCODE) { ... } else if (platform == CODEFORCES) { ... }`
- **Easy Testing:** Mock each service independently
- **Easy Adding Platforms:** Just implement the interface, no changes needed elsewhere

### Anti-Pattern (What NOT to Do) ❌
```java
// WRONG - Strategy pattern not used
public void syncProfile(UUID userId, PlatformType platform, String username) {
    if (platform == LEETCODE) {
        // LeetCode GraphQL logic
        syncLeetCode(username);
    } else if (platform == CODEFORCES) {
        // Codeforces REST logic
        syncCodeforces(username);
    } else if (platform == CODECHEF) {
        // CodeChef logic
        syncCodeChef(username);
    }
    // Add more platforms = Add more if-else = Modify existing code
}
```

---

## 2️⃣ FACTORY PATTERN (Route to Correct Service)

### What It Does
Creates objects (services) without specifying their exact classes. Decouples creation from usage.

### The Pattern
```
Controller needs a service
         │
         ▼
    "Give me LEETCODE service"
         │
         ▼
┌──────────────────────────────┐
│   CodingPlatformFactory      │
│                              │
│ - services: Map<Platform,    │
│             Service>         │
│                              │
│ getService(platform)         │
│   ↓                          │
│   services.get(platform)     │
│   ↓                          │
│   return Service             │
└──────────────────────────────┘
         │
         ▼
    Return LeetCodeService
```

### Real Example
```java
// Factory Implementation
@Component
public class CodingPlatformFactory {
    private final Map<PlatformType, CodingPlatformService> services;
    
    public CodingPlatformFactory(List<CodingPlatformService> serviceList) {
        // Spring auto-discovers all @Service implementing CodingPlatformService
        // Build map: LEETCODE → LeetCodeService, CODEFORCES → CodeforcesService, etc.
        this.services = serviceList.stream()
            .collect(Collectors.toMap(
                CodingPlatformService::getPlatform,  // Key: platform type
                Function.identity()                   // Value: service instance
            ));
    }
    
    public CodingPlatformService getService(PlatformType platform) {
        return services.get(platform);  // O(1) lookup
    }
}

// Usage in Integration Service
@Service
public class PlatformIntegrationServiceImpl implements PlatformIntegrationService {
    private final CodingPlatformFactory factory;
    
    @Override
    public void connectProfile(UUID userId, ConnectProfileRequest request) {
        // Get correct service (factory returns it)
        CodingPlatformService service = factory.getService(request.getPlatform());
        
        // Service handles platform-specific logic
        PlatformSyncData data = service.sync(request.getUsername());
        
        // Save to DB
        persist(userId, data);
    }
}
```

### Why This Matters ✅
- **Decoupling:** Controller doesn't know which service will be used
- **Centralized Logic:** Service routing in one place
- **Type-Safe:** Uses enum instead of strings
- **Easy to Extend:** Add new service → auto-registered by Spring → No factory code changes

### When Spring Auto-Registers Services
```java
@Service
public class LeetCodeService implements CodingPlatformService {
    @Override
    public PlatformType getPlatform() {
        return PlatformType.LEETCODE;  // ← Spring uses this for mapping
    }
}

@Service
public class CodeforcesService implements CodingPlatformService {
    @Override
    public PlatformType getPlatform() {
        return PlatformType.CODEFORCES;  // ← Spring uses this for mapping
    }
}

// Factory automatically knows both services exist!
```

---

## 3️⃣ FACADE PATTERN (Single Entry Point)

### What It Does
Provides unified interface to a set of interfaces in a subsystem. Simplifies complex interactions.

### The Pattern
```
User wants to: "Connect my LeetCode profile"
         │
         ▼
    Controller
         │
         ▼
┌──────────────────────────────────────────┐
│   PlatformIntegrationService (Facade)    │
│                                          │
│ - connectProfile(userId, request)        │
│   ├─ Call factory.getService()           │
│   ├─ Call service.sync()                 │
│   ├─ Call profileService.save()          │
│   ├─ Call problemService.save()          │
│   ├─ Call contestService.save()          │
│   └─ Handle transactions & errors        │
│                                          │
│ - getUnifiedDashboard(userId)            │
│   ├─ Query all profiles                  │
│   ├─ Query recent problems               │
│   ├─ Query contests                      │
│   ├─ Aggregate data                      │
│   └─ Return single response              │
└──────────────────────────────────────────┘
         │
         ▼
    Multiple services handle details
    ├─ CodingPlatformFactory
    ├─ PlatformProfileService
    ├─ ProblemService
    ├─ ContestService
    └─ Repositories
```

### Real Example
```java
// Facade - Hides complexity
@Service
@Transactional
public class PlatformIntegrationServiceImpl implements PlatformIntegrationService {
    
    private final CodingPlatformFactory factory;
    private final PlatformProfileService profileService;
    private final ProblemService problemService;
    private final ContestService contestService;
    
    @Override
    public void connectProfile(UUID userId, ConnectProfileRequest request) {
        // Step 1: Get platform service
        CodingPlatformService platformService = factory.getService(request.getPlatform());
        
        // Step 2: Fetch data from external API
        PlatformSyncData syncData = platformService.sync(request.getUsername());
        
        // Step 3: Save profile
        if (syncData.getProfile() != null) {
            profileService.saveOrUpdate(userId, syncData.getProfile());
        }
        
        // Step 4: Save problems (batch)
        if (syncData.getSolvedProblems() != null) {
            problemService.syncProblems(userId, syncData.getSolvedProblems());
        }
        
        // Step 5: Save contests (batch)
        if (syncData.getContests() != null) {
            contestService.syncContests(userId, syncData.getContests());
        }
        
        // All in one transaction - either all succeed or all rollback
    }
    
    @Override
    public UnifiedDashboardResponse getUnifiedDashboard(UUID userId) {
        // Single call hides complexity
        List<PlatformProfile> profiles = profileService.getAllProfiles(userId);
        List<Problem> recentProblems = problemService.getRecent(userId, 10);
        List<Contest> recentContests = contestService.getRecent(userId, 5);
        
        return aggregateData(profiles, recentProblems, recentContests);
    }
}

// Controller - Simple (no need to know details)
@RestController
@RequestMapping("/api/v1/platforms")
public class PlatformController {
    private final PlatformIntegrationService integrationService;
    
    @PostMapping("/connect")
    public ResponseEntity<?> connectProfile(
        @AuthenticationPrincipal User user,
        @RequestBody ConnectProfileRequest request) {
        
        // Controller just delegates - one line!
        integrationService.connectProfile(user.getId(), request);
        return ResponseEntity.ok("Connected");
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal User user) {
        // One line - facade handles everything
        return ResponseEntity.ok(
            integrationService.getUnifiedDashboard(user.getId())
        );
    }
}
```

### Why This Matters ✅
- **Single Responsibility:** Controller doesn't orchestrate - just delegates
- **Consistency:** All syncs follow same flow (error handling, transactions)
- **Reusability:** Both manual sync and scheduled sync use same logic
- **Maintainability:** Change sync logic in one place

### What It Hides (Complexity Encapsulation)
```
User doesn't see:
❌ - Which platform service was selected
❌ - How many repositories are being updated
❌ - Transaction boundaries
❌ - Error retry logic
❌ - Partial failure handling

User just sees:
✅ - One simple call
✅ - One response
✅ - All data persisted or nothing (atomicity)
```

---

## 4️⃣ REPOSITORY PATTERN (Data Access Abstraction)

### What It Does
Abstracts data access logic. Services talk to repositories, not directly to database.

### The Pattern
```
Service Layer
     │
     ├─ profileService.getProfile(userId)
     │         ↓
     │    ProfileRepository (Interface)
     │         ↓
     │    ProfileRepositoryImpl (JPA)
     │         ↓
     │    Database
     │
     └─ Benefit: Easy to swap implementations
        (Mock for testing, Redis for caching, etc.)
```

### Real Examples
```java
// Repository Interface - Contract
public interface PlatformProfileRepository extends JpaRepository<PlatformProfile, UUID> {
    
    List<PlatformProfile> findByUserId(UUID userId);
    
    Optional<PlatformProfile> findByUserIdAndPlatform(UUID userId, PlatformType platform);
    
    List<PlatformProfile> findByUserIdAndConnectedTrue(UUID userId);
    
    // Custom queries for aggregation
    @Query("SELECT AVG(p.contestRating) FROM PlatformProfile p WHERE p.user.id = :userId")
    Double getAverageRating(@Param("userId") UUID userId);
}

// Repository Implementation - Automatic via Spring Data JPA
// (Spring generates implementation at runtime)

// Service Usage
@Service
public class PlatformProfileServiceImpl implements PlatformProfileService {
    
    private final PlatformProfileRepository repository;
    
    public PlatformProfile getProfile(UUID userId, PlatformType platform) {
        return repository.findByUserIdAndPlatform(userId, platform)
            .orElseThrow(() -> new ProfileNotFoundException(...));
    }
    
    public List<PlatformProfile> getAllProfiles(UUID userId) {
        return repository.findByUserIdAndConnectedTrue(userId);
    }
    
    public void saveOrUpdate(UUID userId, PlatformProfileData data) {
        PlatformProfile profile = repository
            .findByUserIdAndPlatform(userId, data.getPlatform())
            .orElse(new PlatformProfile());
        
        // Update fields
        profile.setDisplayName(data.getDisplayName());
        profile.setTotalSolved(data.getTotalSolved());
        profile.setContestRating(data.getContestRating());
        profile.setLastSyncedAt(Instant.now());
        
        repository.save(profile);  // INSERT or UPDATE
    }
}
```

### Why This Matters ✅
- **Testability:** Mock repository in unit tests
- **Flexibility:** Change DB later without touching services
- **Query Optimization:** Repository methods are optimized queries
- **DRY:** Common queries defined once

---

## 5️⃣ TEMPLATE METHOD PATTERN (Batch Operations)

### What It Does
Defines skeleton of sync process; subclasses implement platform-specific parts.

### The Pattern
```
Sync Process (Template)
├─ Step 1: Validate username    ← Common
├─ Step 2: Fetch data           ← Platform-specific
├─ Step 3: Parse response       ← Platform-specific
├─ Step 4: Map to DTOs          ← Platform-specific
└─ Step 5: Return result        ← Common
```

### Example
```java
// Abstract base class (Template)
public abstract class BasePlatformService implements CodingPlatformService {
    
    protected final RestTemplate restTemplate;
    
    // Template method - defines overall structure
    @Override
    public final PlatformSyncData sync(String username) {
        // Step 1: Validate
        if (!isValidUsername(username)) {
            throw new InvalidUsernameException(username);
        }
        
        // Step 2: Fetch (subclass implements)
        String rawData = fetchData(username);
        
        // Step 3: Parse (subclass implements)
        ParsedData parsed = parseData(rawData);
        
        // Step 4: Map to DTOs (subclass implements)
        PlatformSyncData syncData = mapToDTOs(parsed);
        
        // Step 5: Validate result (common)
        validateSyncData(syncData);
        
        return syncData;
    }
    
    // Concrete implementation - all platforms
    protected boolean isValidUsername(String username) {
        return username != null && !username.isBlank();
    }
    
    // Abstract - each platform implements differently
    protected abstract String fetchData(String username);
    protected abstract ParsedData parseData(String rawData);
    protected abstract PlatformSyncData mapToDTOs(ParsedData parsed);
}

// Concrete implementation
@Service
public class LeetCodeService extends BasePlatformService {
    
    @Override
    protected String fetchData(String username) {
        // LeetCode specific: GraphQL query
        return graphqlClient.query(getLeetCodeGraphQLQuery(username));
    }
    
    @Override
    protected ParsedData parseData(String rawData) {
        // Parse GraphQL response
        return LeetCodeGraphQLParser.parse(rawData);
    }
    
    @Override
    protected PlatformSyncData mapToDTOs(ParsedData parsed) {
        return LeetCodeMapper.toDTOs(parsed);
    }
}

@Service
public class CodeforcesService extends BasePlatformService {
    
    @Override
    protected String fetchData(String username) {
        // Codeforces specific: REST API
        return restTemplate.getForObject(
            "https://codeforces.com/api/user.info?handles=" + username,
            String.class
        );
    }
    
    @Override
    protected ParsedData parseData(String rawData) {
        // Parse JSON response
        return CodeforcesJsonParser.parse(rawData);
    }
    
    @Override
    protected PlatformSyncData mapToDTOs(ParsedData parsed) {
        return CodeforcesMapper.toDTOs(parsed);
    }
}
```

### Why This Matters ✅
- **Code Reuse:** Common steps in one place
- **Consistency:** All platforms follow same process
- **Easy to Add:** Just implement abstract methods
- **Maintenance:** Bug fixes in template benefit all

---

## 🔄 How These Patterns Work Together

```
User Request: "Connect my Codeforces account"
│
▼
┌─────────────────────────────────────────────┐
│ Controller (Simple Orchestrator)            │ Facade pattern
│ - Takes request                             │ (hides complexity)
│ - Calls integrationService.connect()        │
└─────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ PlatformIntegrationService (Facade)         │ Strategy + Factory
│ - Gets service from factory                 │ pattern
│ - Calls service.sync()                      │ (route to correct
│ - Delegates data save to repositories       │  implementation)
└─────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ Factory (Route to correct service)          │ Factory pattern
│ factory.getService(CODEFORCES)              │ (returns correct
│ ↓                                           │  service)
│ Returns: CodeforcesService                  │
└─────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ CodeforcesService (Strategy)                │ Strategy pattern
│ (Concrete implementation)                   │ (platform-specific)
│ - Calls Codeforces REST API                 │
│ - Parses JSON response                      │
│ - Maps to PlatformSyncData DTOs             │
│ - Returns data                              │
└─────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ Repositories (Data Access)                  │ Repository pattern
│ - profileRepo.save()                        │ (abstract DB access)
│ - problemRepo.saveAll()                     │
│ - contestRepo.saveAll()                     │
└─────────────────────────────────────────────┘
│
▼
DATABASE
(All data persisted)
```

---

## 💡 Key Takeaways

| Pattern | Purpose | Benefit |
|---------|---------|---------|
| **Strategy** | Platform-specific implementations | No if-else chains; easy to add platforms |
| **Factory** | Route to correct service | Decoupling; centralized routing |
| **Facade** | Single entry point | Simple controller; consistent logic |
| **Repository** | Data access abstraction | Testable; flexible database |
| **Template Method** | Define process skeleton | Code reuse; consistency |

---

## When to Apply Each Pattern

### Strategy Pattern
- ✅ When you have multiple ways to do something (platforms)
- ✅ When you need to switch implementations at runtime
- ✅ When you want to avoid conditionals

### Factory Pattern
- ✅ When object creation is complex
- ✅ When you need decoupling between creator and created
- ✅ When you have many implementations to choose from

### Facade Pattern
- ✅ When you have complex subsystems (many services)
- ✅ When you want to simplify client code
- ✅ When you need consistent business logic

### Repository Pattern
- ✅ Always (good practice for data access)
- ✅ When you need to mock data access in tests
- ✅ When you might switch databases

### Template Method
- ✅ When you have similar algorithms (all platforms sync similarly)
- ✅ When you want to avoid code duplication
- ✅ When subclasses need to override specific steps

---

**Remember:** These patterns exist to solve real problems. Use them when they simplify your code, not as an exercise!
