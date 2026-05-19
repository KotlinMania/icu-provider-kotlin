# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/18 (11.1%)
- **Function parity:** 14/218 matched (target 33) — 6.4%
- **Class/type parity:** 4/79 matched (target 7) — 5.1%
- **Combined symbol parity:** 18/297 matched (target 40) — 6.1%
- **Average inline-code cosine:** 0.14 (function body across 2 matched files)
- **Average documentation cosine:** 0.54 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. request

- **Target:** `icuprovider.Request`
- **Similarity:** 0.06
- **Dependents:** 0
- **Priority Score:** 273409.3
- **Functions:** 5/26 matched (target 12)
- **Missing functions:** `fmt`, `for_locale`, `for_marker_attributes`, `for_marker_attributes_and_locale`, `into_owned`, `as_cow`, `partial_cmp`, `cmp`, `as_borrowed`, `from_locale`, `from_marker_attributes`, `from_marker_attributes_owned`, `from_owned`, `from_borrowed_and_owned`, `is_unknown`, `default`, `deref`, `try_from_str`, `from_str_or_panic`, `as_str`, `to_owned`
- **Types:** 2/8 matched (target 3)
- **Missing types:** `DataRequest`, `DataRequestMetadata`, `DataIdentifierBorrowed`, `DataIdentifierCow`, `Target`, `Owned`
- **Tests:** 1/1 matched

### 2. marker

- **Target:** `icuprovider.Marker`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 243507.9
- **Functions:** 9/21 matched
- **Missing functions:** `bind`, `make_locale`, `to_unaligned`, `from_unaligned`, `eq`, `cmp`, `partial_cmp`, `hash`, `name`, `from_id`, `match_marker`, `fmt`
- **Types:** 2/14 matched (target 4)
- **Missing types:** `DynamicDataMarker`, `DataMarker`, `DataMarkerExt`, `NeverMarker`, `DataStruct`, `Container`, `Slice`, `GetType`, `OwnedType`, `ULE`, `DataMarkerInfo`, `ErasedMarker`
- **Tests:** 4/4 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/icu_provider/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/icuprovider kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
## Reexport / Wiring Modules

These files match `reexport_modules` patterns in `.ast_distance_config.json`. They are filtered out of
normal priority and missing-file ladders because they are wiring
modules, not direct logic ports. Consult them for call-site routing;
do not treat them as the next implementation target by default.

### Missing

| Source | Expected target | Deps | Source path | Expected path |
|--------|-----------------|------|-------------|---------------|
| `export.mod` | `export.Mod` | 0 | `export/mod.rs` | `export/Mod.kt` |
| `lib` | `Lib` | 0 | `lib.rs` | `Lib.kt` |
