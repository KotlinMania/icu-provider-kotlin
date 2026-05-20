# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 8/18 (44.4%)
- **Function parity:** 61/180 matched (target 118) — 33.9%
- **Class/type parity:** 37/79 matched (target 53) — 46.8%
- **Combined symbol parity:** 98/259 matched (target 171) — 37.8%
- **Average inline-code cosine:** 0.44 (function body across 8 matched files)
- **Average documentation cosine:** 0.73 (doc text across 8 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 6 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. response

- **Target:** `icuprovider.Response`
- **Similarity:** 0.04
- **Dependents:** 0
- **Priority Score:** 344209.6
- **Functions:** 3/32 matched (target 4)
- **Missing functions:** `deref`, `unwrap_cart`, `fmt`, `clone`, `eq`, `test_clone_eq`, `from_owned`, `from_static_ref`, `with_mut`, `get_static`, `map_project`, `map_project_cloned`, `try_map_project`, `try_map_project_cloned`, `cast`, `cast_ref`, `dynamic_cast`, `dynamic_cast_mut`, `from_owned_buffer`, `from_yoked_buffer`, `from_static_buffer`, `default`, `from_payload`, `from_other`, `is_payload`, `into_inner`, `none`, `get_option`, `test_debug`
- **Types:** 5/10 matched (target 8)
- **Missing types:** `DataPayloadInner`, `DataPayloadOrInner`, `DataPayloadOrInnerInner`, `CartInner`, `Target`
- **Tests:** 0/2 matched

### 2. data_provider

- **Target:** `icuprovider.DataProvider`
- **Similarity:** 0.03
- **Dependents:** 0
- **Priority Score:** 213109.7
- **Functions:** 2/19 matched (target 2)
- **Missing functions:** `load`, `dry_load`, `load_data`, `dry_load_data`, `iter_ids_for_marker`, `new`, `from`, `get_warehouse`, `get_payload_v1`, `get_payload_alt`, `test_warehouse_owned`, `test_warehouse_owned_dyn_generic`, `test_provider2`, `test_provider2_dyn_generic`, `test_provider2_dyn_generic_alt`, `check_v1_v2`, `test_v1_v2_generic`
- **Types:** 8/12 matched (target 8)
- **Missing types:** `HelloAlt`, `HelloCombined`, `DataWarehouse`, `DataProvider2`
- **Tests:** 0/11 matched

### 3. marker

- **Target:** `icuprovider.Marker`
- **Similarity:** 0.46
- **Dependents:** 0
- **Priority Score:** 133505.4
- **Functions:** 15/21 matched (target 32)
- **Missing functions:** `bind`, `eq`, `cmp`, `partial_cmp`, `hash`, `fmt`
- **Types:** 7/14 matched (target 9)
- **Missing types:** `DataMarkerExt`, `DataStruct`, `Container`, `Slice`, `GetType`, `OwnedType`, `ULE`
- **Tests:** 4/4 matched

### 4. request

- **Target:** `icuprovider.Request`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 23404.0
- **Functions:** 26/26 matched (target 55)
- **Missing functions:** _none_
- **Types:** 6/8 matched (target 9)
- **Missing types:** `Target`, `Owned`
- **Tests:** 1/1 matched

### 5. error

- **Target:** `icuprovider.Error`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 1604.0
- **Functions:** 13/13 matched (target 22)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 11)
- **Missing types:** _none_

### 6. buf

- **Target:** `buf.Buf`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 504.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 7. fallback

- **Target:** `icuprovider.Fallback`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 307.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 8. baked

- **Target:** `baked.Baked`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 200.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

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
