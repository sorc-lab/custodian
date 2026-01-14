# CPort Code Review & Fix Plan

## Overview
This document outlines the audit findings for the `cport/` source files and provides concrete remediation actions to bring the project in line with modern C best‑practice standards, improve safety, and eliminate memory‑leak and error‑handling issues.

---
### Identified Issues & Fixes
| File | Issue | Recommended Change |
|------|-------|--------------------|
| `cport/main.c` | Non‑standard `void main` signature; no return value. | Change to `int main(int argc, char *argv[])` and `return 0;` on success. |
| `cport/cmd_handler.c` | Accesses `argv[1]` before validating `argc`. | Move the `if (argc < 2)` guard **before** any use of `argv[1]`. |
| `cport/cmd_handler.c` | Static command table without `static` qualifier may cause multiple definitions. | Add `static` to `cli_commands` or declare it `extern` in a header. |
| `cmd_add` (in `cmd_handler.c`) | Uses `atoi` without validation; possible overflow/invalid input. | Replace with `strtol`, verify full conversion and range checks. |
| `cport/task.c` – `task_init` | No check for `malloc` failure; leaks if later steps fail. | Verify allocation result, on failure print to `stderr` and return `NULL`. Propagate the check back to callers. |
| `cport/task.h` | Declares `task_destroy` but no implementation. | Implement:
```c
void task_destroy(task_t *t) {
    if (!t) return;
    free(t->desc);
    free(t);
}
```

- Database I/O (`task_repo.c`)
    - Calls `exit(EXIT_FAILURE)` on file errors, terminating the whole program from deep library code.
    - Replace `exit` with returning an error enum (e.g., `ERR_IO`). Let `cmd_handler` handle reporting and exit decisions.

- `task_save`
  - Writes literal `"false"` for `is_done` regardless of actual state.
  - Use `(task->is_done ? "true" : "false")` when formatting the line.

- `task_set_is_done`
  - Leaks the temporary `task_t*` returned by `task_find_by_id`.
  - After updating, call `task_destroy(task)`.

- `task_tmp_db_writer`
  - Missing `return writer;` on success, causing undefined behavior.
  - Add `return writer;` before function end.

- Error messages
  - Inconsistent newline termination and missing context.
  - Ensure all `fprintf(stderr, …)` end with `\n` and include operation description.

- Concurrency
  - No file‑locking; concurrent CLI runs could corrupt the TSV database.
  - Implement a simple lock file (`tasks.lock`) around any write sequence (save, delete, update).

- Magic strings
  - Hard‑coded
