# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 18/18 (100.0%)
- **Function parity:** 111/177 matched (target 198) — 62.7%
- **Class/type parity:** 63/84 matched (target 95) — 75.0%
- **Combined symbol parity:** 174/261 matched (target 293) — 66.7%
- **Average inline-code cosine:** 0.34 (function body across 15 matched files)
- **Average documentation cosine:** 0.58 (doc text across 15 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 16 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. response

- **Target:** `icuprovider.Response`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 234208.7
- **Functions:** 14/32 matched (target 23)
- **Missing functions:** `deref`, `fmt`, `clone`, `eq`, `from_owned`, `from_static_ref`, `map_project`, `map_project_cloned`, `try_map_project`, `try_map_project_cloned`, `cast`, `cast_ref`, `dynamic_cast`, `dynamic_cast_mut`, `from_payload`, `from_other`, `none`, `test_debug`
- **Types:** 5/10 matched (target 9)
- **Missing types:** `DataPayloadInner`, `DataPayloadOrInner`, `DataPayloadOrInnerInner`, `CartInner`, `Target`
- **Tests:** 1/2 matched

### 2. export.payload

- **Target:** `icuprovider.ExportTest`
- **Similarity:** 0.13
- **Dependents:** 0
- **Priority Score:** 182608.8
- **Functions:** 5/20 matched (target 7)
- **Missing functions:** `bake_yoke`, `serialize_yoke`, `maybe_bake_varule_encoded`, `eq`, `fmt`, `upcast`, `serialize`, `tokenize`, `tokenize_encoded_seq`, `postcard_size`, `baked_size`, `hash`, `hash_and_postcard_size`, `try_push`, `finalize`
- **Types:** 3/6 matched (target 4)
- **Missing types:** `HashFlavor`, `Output`, `DataStruct`
- **Tests:** 2/2 matched

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
- **Similarity:** 0.12
- **Dependents:** 0
- **Priority Score:** 71008.8
- **Functions:** 2/8 matched (target 2)
- **Missing functions:** `as_deserializing`, `deserialize_impl`, `into_deserialized`, `load`, `dry_load`, `from`
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
- **Functions:** 2/5 matched (target 6)
- **Missing functions:** `get_index`, `from_trie_and_values_unchecked`, `from_trie_and_refs_unchecked`
- **Types:** 3/4 matched (target 3)
- **Missing types:** `IterReturn`

### 8. export.mod

- **Target:** `export.Mod [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31210.0
- **Functions:** 4/7 matched
- **Missing functions:** `supported_markers`, `new`, `fmt`
- **Types:** 5/5 matched
- **Missing types:** _none_

### 9. request

- **Target:** `icuprovider.Request`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 23404.0
- **Functions:** 26/26 matched (target 55)
- **Missing functions:** _none_
- **Types:** 6/8 matched (target 9)
- **Missing types:** `Target`, `Owned`
- **Tests:** 1/1 matched

### 10. varule_traits

- **Target:** `icuprovider.VaruleTraits [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20410.0
- **Functions:** 0/1 matched (target 0)
- **Missing functions:** `maybe_encode_as_varule`
- **Types:** 2/3 matched (target 2)
- **Missing types:** `EncodedStruct`

### 11. serde_borrow_de_utils

- **Target:** `icuprovider.SerdeBorrowDeUtils`
- **Similarity:** 0.38
- **Dependents:** 0
- **Priority Score:** 10906.2
- **Functions:** 6/6 matched (target 8)
- **Missing functions:** _none_
- **Types:** 2/3 matched (target 6)
- **Missing types:** `Demo`
- **Tests:** 3/3 matched

### 12. error

- **Target:** `icuprovider.Error`
- **Similarity:** 0.60
- **Dependents:** 0
- **Priority Score:** 1604.0
- **Functions:** 13/13 matched (target 22)
- **Missing functions:** _none_
- **Types:** 3/3 matched (target 11)
- **Missing types:** _none_

### 13. buf

- **Target:** `buf.Buf`
- **Similarity:** 0.51
- **Dependents:** 0
- **Priority Score:** 504.9
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 4/4 matched
- **Missing types:** _none_

### 14. fallback

- **Target:** `icuprovider.Fallback`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 307.3
- **Functions:** 1/1 matched (target 2)
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 15. baked

- **Target:** `baked.Baked`
- **Similarity:** 1.00
- **Dependents:** 0
- **Priority Score:** 200.0
- **Functions:** 0/0 matched
- **Missing functions:** _none_
- **Types:** 2/2 matched
- **Missing types:** _none_

### 16. dynutil

- **Target:** `icuprovider.Dynutil [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 110.0
- **Functions:** 0/0 matched (target 1)
- **Missing functions:** _none_
- **Types:** 1/1 matched (target 2)
- **Missing types:** _none_

### 17. lib

- **Target:** `icuprovider.Lib [STUB]`
- **Similarity:** 0.27
- **Dependents:** 0
- **Priority Score:** 107.3
- **Functions:** 1/1 matched
- **Missing functions:** _none_
- **Types:** 0/0 matched (target 2)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 18. constructors

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

