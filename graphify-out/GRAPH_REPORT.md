# Graph Report - UTIS-cloud  (2026-10-30)

## Corpus Check
- 28 files · ~4,394 words
- Verdict: corpus is large enough that graph structure adds value.
- Unclassified: 63 file(s) not represented in the graph (top: .xml 55, (none) 2, .properties 2)

## Summary
- 196 nodes · 369 edges · 18 communities (4 shown, 14 thin omitted)
- Extraction: 98% EXTRACTED · 2% INFERRED · 0% AMBIGUOUS · INFERRED: 6 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9213b789`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- HomeActivity.java
- android.os.Bundle
- LocationTrackingService
- .onCreate
- AboutUs.java
- RouteSchedule
- ExampleInstrumentedTest.java
- android.view.View
- UTISApplication.java
- TempStopActivity
- .onCreate
- AGENTS.md
- CLAUDE.md
- GEMINI.md

## God Nodes (most connected - your core abstractions)
1. `HomeActivity` - 17 edges
2. `LocationTrackingService` - 15 edges
3. `RouteSchedule` - 14 edges
4. `TravelLogActivity` - 8 edges
5. `TempStopActivity` - 7 edges
6. `WebViewClient` - 6 edges
7. `TimeActivity` - 6 edges
8. `AboutUs` - 5 edges
9. `Admin` - 5 edges
10. `ExampleInstrumentedTest` - 4 edges

## Surprising Connections (you probably didn't know these)
- `HomeActivity` --references--> `RouteSchedule`  [EXTRACTED]
  app/src/main/java/com/example/utis/HomeActivity.java → app/src/main/java/com/example/utis/RouteSchedule.java

## Import Cycles
- None detected.

## Communities (18 total, 14 thin omitted)

### Community 1 - "android.os.Bundle"
Cohesion: 0.16
Nodes (6): Admin, ChatbotActivity, MainActivity, MapActivity, Other, TimeActivity

### Community 3 - ".onCreate"
Cohesion: 0.14
Nodes (4): OnBackPressedCallback, ValueEventListener, TravelLogActivity, ValueEventListener

### Community 4 - "AboutUs.java"
Cohesion: 0.18
Nodes (3): AboutUs, WebViewClient, View.OnTouchListener

### Community 7 - "android.view.View"
Cohesion: 0.24
Nodes (3): View.OnClickListener, WebViewClient, View.OnClickListener

## Knowledge Gaps
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `HomeActivity` connect `HomeActivity.java` to `android.os.Bundle`, `.onCreate`, `RouteSchedule`?**
  _High betweenness centrality (0.130) - this node is a cross-community bridge._
- **Should `HomeActivity.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07657657657657657 - nodes in this community are weakly interconnected._
- **Why does `RouteSchedule` connect `RouteSchedule` to `HomeActivity.java`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Should `.onCreate` be split into smaller, more focused modules?**
  _Cohesion score 0.14210526315789473 - nodes in this community are weakly interconnected._