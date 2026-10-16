# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 16/18 (88.9%)
- **Function parity:** 122/169 matched (target 204) — 72.2%
- **Class/type parity:** 58/79 matched (target 86) — 73.4%
- **Combined symbol parity:** 180/248 matched (target 290) — 72.6%
- **Average inline-code cosine:** 0.36 (function body across 15 matched files)
- **Average documentation cosine:** 0.58 (doc text across 15 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 14 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. export.payload

- **Target:** `icuprovider.ExportTest`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 182608.8
- **Functions:** 5/20 matched (target 7)
- **Missing functions:** `bake_yoke`, `serialize_yoke`, `maybe_bake_varule_encoded`, `eq`, `fmt`, `upcast`, `serialize`, `tokenize`, `tokenize_encoded_seq`, `postcard_size`, `baked_size`, `hash`, `hash_and_postcard_size`, `try_push`, `finalize`
- **Types:** 3/6 matched (target 4)
- **Missing types:** `HashFlavor`, `Output`, `DataStruct`
- **Tests:** 2/2 matched

### 2. marker

- **Target:** `icuprovider.Marker`
- **Similarity:** 0.49
- **Dependents:** 0
- **Priority Score:** 123505.1
- **Functions:** 16/21 matched (target 33)
- **Missing functions:** `eq`, `cmp`, `partial_cmp`, `hash`, `fmt`
- **Types:** 7/14 matched (target 9)
- **Missing types:** `DataMarkerExt`, `DataStruct`, `Container`, `Slice`, `GetType`, `OwnedType`, `ULE`
- **Tests:** 4/4 matched

### 3. response

- **Target:** `icuprovider.Response`
- **Similarity:** 0.29
- **Dependents:** 0
- **Priority Score:** 104207.1
- **Functions:** 27/32 matched (target 38)
- **Missing functions:** `deref`, `fmt`, `clone`, `eq`, `test_debug`
- **Types:** 5/10 matched (target 9)
- **Missing types:** `DataPayloadInner`, `DataPayloadOrInner`, `DataPayloadOrInnerInner`, `CartInner`, `Target`
- **Tests:** 1/2 matched

### 4. data_provider

- **Target:** `icuprovider.DataProvider`
- **Similarity:** 0.21
- **Dependents:** 0
- **Priority Score:** 103107.9
- **Functions:** 9/19 matched (target 14)
- **Missing functions:** `dry_load`, `load_data`, `dry_load_data`, `iter_ids_for_marker`, `new`, `from`, `get_warehouse`, `get_payload_v1`, `get_payload_alt`, `check_v1_v2`
- **Types:** 12/12 matched (target 14)
- **Missing types:** _none_
- **Tests:** 6/11 matched

### 5. buf.serde

- **Target:** `buf.Serde`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 51007.3
- **Functions:** 4/8 matched (target 5)
- **Missing functions:** `deserialize_impl`, `load`, `dry_load`, `from`
- **Types:** 1/2 matched
- **Missing types:** `AsDeserializingBufferProvider`

### 6. hello_world

- **Target:** `icuprovider.HelloWorld`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 42104.9
- **Functions:** 12/16 matched (target 17)
- **Missing functions:** `from_static_str`, `write_to`, `writeable_borrow`, `writeable_length_hint`
- **Types:** 5/5 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 7. baked.zerotrie

- **Target:** `baked.Zerotrie`
- **Similarity:** 0.17
- **Dependents:** 0
- **Priority Score:** 40908.3
- **Functions:** 2/5 matched (target 7)
- **Missing functions:** `get_index`, `from_trie_and_values_unchecked`, `from_trie_and_refs_unchecked`
- **Types:** 3/4 matched (target 3)
- **Missing types:** `IterReturn`

### 8. request

- **Target:** `icuprovider.Request`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 23404.0
- **Functions:** 26/26 matched (target 49)
- **Missing functions:** _none_
- **Types:** 6/8 matched (target 7)
- **Missing types:** `Target`, `Owned`
- **Tests:** 1/1 matched

### 9. varule_traits

- **Target:** `icuprovider.VaruleTraits [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20410.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `maybe_encode_as_varule`
- **Types:** 2/3 matched (target 2)
- **Missing types:** `EncodedStruct`

### 10. serde_borrow_de_utils

- **Target:** `icuprovider.SerdeBorrowDeUtils`
- **Similarity:** 0.38
- **Dependents:** 0
- **Priority Score:** 10906.2
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 6)
- **Missing types:** `Demo`
- **Tests:** 3/3 matched

### 11. error

- **Target:** `icuprovider.Error`
- **Similarity:** 0.63
- **Dependents:** 0
- **Priority Score:** 1603.7
- **Functions:** 13/13 matched (target 22)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 11)
- **Missing types:** _none_

### 12. buf

- **Target:** `buf.Buf`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 504.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 13. fallback

- **Target:** `icuprovider.Fallback`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 307.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 14. baked

- **Target:** `baked.Baked`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 200.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 15. dynutil

- **Target:** `icuprovider.Dynutil [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 16. constructors

- **Target:** `icuprovider.Constructors [STUB]`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 0.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 1)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

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

