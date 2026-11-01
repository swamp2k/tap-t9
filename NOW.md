# Session bookmark

Doing: Repository orientation completed; root instructions and this bookmark published in documentation-only PR #2 (https://github.com/swamp2k/tap-t9/pull/2), branch docs/session-bookmark-instructions. Awaiting user direction before implementation.

State:
- Reviewed master at 05b92fcacbfe79b5339a60a3d8ddb614001907e2, README.md, CONTRIBUTING.md, build configuration, core IME code, recent commits, and the only pre-existing open PR (#1).
- Root AGENTS.md, CLAUDE.md, HANDOVER.md, and NOW.md were absent; neither inspected branch contained nested files with those names. Created identical root instructions and this bookmark; no application code changed.
- Traditional T9 is an Android Java IME with predictive/ABC/numeric input, 50+ languages, physical and touchscreen input, and full/lite/premium build flavors. Build configuration uses Java 17 source compatibility, compile/target SDK 37, and min SDK 24; CI uses Java 21.
- Recent master commits cover Android Gradle Plugin 9.3.2, French uppercase suggestions, F-Droid revision metadata, and Latin frequencies.
- Draft PR #1, https://github.com/swamp2k/tap-t9/pull/1, targets master from feature/t9-glide-mvp at 3f1a03c452fae4ae8804a814780751d23b451eec. Latest commits add ranked dictionary suggestions, bounded repeated-key expansion, and word-mode gating. Its description still describes exact-only top-candidate commits and excludes repeated-key inference; it is stale relative to the code.
- PR #1 build check succeeded on 2026-09-27; no issue comments or submitted reviews were present at inspection. Device behavior has not been verified in this session. README still says swipe typing is outside upstream scope.

Next step: Subject to user direction, validate PR #1 on an Android device before expanding the classifier: ordinary taps/holds/per-key swipes, glide recognition, repeated-key words, candidate selection, cancellation, and delayed results after typing or switching input/language. Inspect request invalidation and mixed-sequence candidate handling during that review. Update PR scope documentation to match the verified implementation.

Blocked by: Further implementation awaits user direction. Device correctness is unverified. Local clone failed because the configured proxy at port 8080 was unreachable; GitHub connector reads/writes work. No local build or device tests were run; this session changes documentation only.

Updated: 2026-11-01
