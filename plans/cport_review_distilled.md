# Custodian Review & Proposed Changes

## Control Flow
### main.c Does Not Return Integer Values
- `main.c`
- Non‑standard `void main` signature; no return value.
- Change to `int main(int argc, char *argv[])` and `return 0;` on success.
### Database
- Database I/O (`task_repo.c`)
- Calls `exit(EXIT_FAILURE)` on file errors, terminating the whole program from deep library code.
- Replace `exit` with returning an error enum (e.g., `ERR_IO`). Let `cmd_handler` handle reporting and exit decisions.

## Validation
### cmd_handler.c Does Not Validate 'argc' Before Processing 'argv[idx]'
- `cmd_handler.c`
- Accesses `argv[1]` before validating `argc`.
- Move the `if (argc < 2)` guard **before** any use of `argv[1]`.
- Make sure to do this in other areas where it applies. New pattern w/ validation concept.
### Possible Overflow/Invalid Input
- `cmd_add` (in `cmd_handler.c`)
- Uses `atoi` without validation; possible overflow/invalid input.
- Replace with `strtol`, verify full conversion and range checks.
- Look into this further if this is a valid concern or overkill for this project.

## Requires Further Investigation
### Static Command Concern
- `cmd_handler.c`
- Static command table without `static` qualifier may cause multiple definitions.
- Add `static` to `cli_commands` or declare it `extern` in a header.
- Not sure what is being suggested here, or why. Look into this further.
### Magic Strings?
- Magic strings
- Hard‑coded

## Memory Management
### Memory Leak If Malloc Failure
- `task.c` – `task_init`
- No check for `malloc` failure; leaks if later steps fail.
- Verify allocation result, on failure print to `stderr` and return `NULL`. Propagate the check back to callers.
### Task Destroy Implementation
- `task.h`
- Declares `task_destroy` but no implementation.
- Implement:
```c
void task_destroy(task_t *t) {
    if (!t) return;
    free(t->desc);
    free(t);
}
```
### Memory Leak
- `task_set_is_done`
- Leaks the temporary `task_t*` returned by `task_find_by_id`.
- After updating, call `task_destroy(task)`.

## Database
### Task Save
- `task_save`
- Writes literal `"false"` for `is_done` regardless of actual state.
- Use `(task->is_done ? "true" : "false")` when formatting the line.
### Missing Return TMP DB Writer
- `task_tmp_db_writer`
- Missing `return writer;` on success, causing undefined behavior.
- Add `return writer;` before function end.

## Style
### Error Messages
- Error messages
- Inconsistent newline termination and missing context.
- Ensure all `fprintf(stderr, …)` end with `\n` and include operation description.

## Concurrency
- Concurrency
- No file‑locking; concurrent CLI runs could corrupt the TSV database.
- Implement a simple lock file (`tasks.lock`) around any write sequence (save, delete, update).

## 1️⃣  Architectural / Safety Concerns

| # | Issue | Why it matters | How to fix / improve |
|---|-------|----------------|----------------------|
| **A** | **All file‑system operations are “fire‑and‑forget” and abort the whole program with `exit()`** | A library routine should never terminate the process on its own; the caller (your CLI main) usually decides whether to bail out, retry, or report a user‑friendly error.  Using `exit()` also makes unit‑testing impossible because the test harness is killed as soon as any file error occurs. | Return an error code (`int`, `enum errcode`) or propagate `errno` up the call chain.  In the CLI front‑end you can translate that into a message and `return EXIT_FAILURE;`. |
| **B** | **No file locking / concurrency protection** | If two instances of your program run at the same time they can corrupt the TSV file (e.g., both generate the same ID, both truncate/rename).  Even a single‑process multithreaded version would be unsafe. | Use an advisory lock (`flock()` on POSIX, `LockFileEx` on Windows) around any sequence that reads‑modifies‑writes the DB, or switch to a real embedded database (SQLite is only ~500 KB). |
| **C** | **Two‑pass ID generation (`task_gen_seq_id`) scans the whole file each time you add a record** | O(N) work for every insert becomes painful as the task list grows; also race‑prone (two processes could read the same “last id” and both write duplicate IDs). | Keep a separate “sequence” file that stores the last used number, update it atomically (`fopen(...,"r+")`, `flock()`, `fprintf("%ld\n", ++seq)`).  Or, better yet, let SQLite auto‑increment. |
| **D** | **`task_find_by_id` allocates a new `task_t` with `task_init` and never frees it** (the caller must free, but the API does not document that). | Memory leaks accumulate each time you call `task_set_is_done`, `task_update`, etc. | Either return the task by value (`task_t task;`) or clearly document ownership: “caller owns the returned pointer and must `task_free()` it”.  Provide a matching destructor. |
| **E** | **`task_tmp_db_writer` does not return the opened `FILE*`** (the function is declared to return `FILE*`, but there is no `return writer;`). The compiler will warn, but if warnings are ignored you get undefined behaviour. | Undefined behaviour → crash or random data loss. | Add `return writer;`. |
| **F** | **All I/O uses the “text” mode with platform‑dependent line endings** (`\n` on *nix, `\r\n` on Windows).  The TSV format is not portable across OSes unless you explicitly open in binary mode and normalise line endings. | On Windows a file opened with `"r"` will translate `\r\n` → `\n`; the same file opened later with `"w"` will write native `\r\n`.  If you ever move the file to another platform the parsing code may mis‑behave. | Open files in binary mode (`"rb"`, `"ab"`, `"wb"`), and handle line‑ending conversion yourself (or use a proper CSV/TSV library). |
| **G** | **No validation of user‑supplied strings** – `task->desc` is printed with `%s` directly into the DB, and later read back with a fixed 255‑byte buffer. If `desc` contains a tab (`\t`) or newline it will break the TSV layout. | Corrupt database, security issue (injection of extra fields). | Escape tabs/newlines before writing (e.g., replace them with space or an escape sequence) and unescape on read; or reject such input outright. |
| **H** | **`bool` is used without including `<stdbool.h>`** (some compilers accept it as a GNU extension, but it’s not ISO‑C). | Non‑portable code. | `#include <stdbool.h>` at the top of the file (or use an enum/int). |
| **I** | **Time handling – casting `time_t` to `long` and back** (`(long) task->updated_at`, later `(time_t) updated_at`).  `time_t` is not guaranteed to fit in a `long`. | On 64‑bit platforms where `time_t` is 64 bits and `long` is 32 bits you lose data, causing wrong timestamps after the year 2038. | Store `int64_t` (`#include <stdint.h>`) or simply write `%lld` with `(long long)task->updated_at`. |
| **J** | **No error checking for `fprintf`, `fputs`, `fflush`, `rename`, `remove`** – they can fail (disk full, permission denied). | Silent data loss. | Check the return value; on failure clean up and propagate an error. |
| **K** | **`task_set_is_done` does not verify that `task_find_by_id` succeeded** – it dereferences a possibly `NULL` pointer if the find routine were changed to return NULL instead of exiting. | Potential segmentation fault. | Either keep the “exit on failure” contract (documented) *or* make `task_find_by_id` return `NULL` and test for it. |
| **L** | **The whole “repository” lives in a single source file (`task_repo.c`) but you expose many static helpers that are only useful internally** – the public API is not clearly defined. | Hard to maintain, unit‑test or replace parts of the implementation. | Put the public functions (e.g., `task_save`, `task_delete_by_id`, `task_set_is_done`, `task_find_by_id`) in a header (`task_repo.h`).  Keep all helpers static *or* move them to a separate “private” file (`task_repo_priv.c`). |
| **M** | **`MAX_LINE_SIZE` is fixed at 1024 bytes** – if any description exceeds that, the line will be truncated when reading, causing malformed parsing. | Data loss / mis‑identification of records. | Either dynamically allocate a buffer (use `getline()` which grows as needed) or enforce a hard limit on description length and store that limit in the schema (`#define DESC_MAX 255`). |
| **N** | **`task_update` rewrites the whole file for a single record change** – O(N) I/O for each edit. | Acceptable for tiny data sets, but becomes a performance bottleneck as soon as you have hundreds or thousands of tasks. | Keep an index (e.g., store byte offset per ID) or switch to a real DB engine that supports in‑place updates. |
| **O** | **`task_close_db_access` removes the original DB *before* renaming the temporary file** – if `rename()` fails after the removal, you end up with no database at all. | Catastrophic data loss. | Rename first (POSIX guarantees that rename over an existing file is atomic). If rename succeeds, then remove the old file; otherwise keep the original untouched. |

---

## 2️⃣  C‑Style / API Issues

| # | Issue | Explanation | Suggested fix |
|---|-------|-------------|---------------|
| **1** | Mixing `bool` (C99) with `<stdbool.h>` omission, and using `true/false` literals without the header. | Not portable. | Add `#include <stdbool.h>`. |
| **2** | Use of magic string `"false"` in `task_save`; later you compare against `"true"` – the two‑state representation is fragile. | If you ever need a third state (e.g., “in progress”) you must touch many places. | Store a single character (`'0'`/`'1'`) or better, store an integer flag (`0` / `1`). |
| **3** | All public functions take raw pointers (`task_t* task`) but never check for `NULL`. | Defensive programming. | Add `if (!task) return;` (or error). |
| **4** | Function names are all prefixed with `task_`, which is good, but the *module* name (`task_repo.c`) suggests a “repository” layer. Yet you also have UI‑related code (`timestamp()`). | Mixing concerns. | Move UI helpers to a separate file (e.g., `task_view.c`). |
| **5** | The signature of `task_save` returns `void`.  It would be more useful to return the newly allocated ID or an error code, so the caller can act on it. | You already print the ID inside; callers cannot programmatically know it. | Change to `int task_save(task_t *task, long *out_id);` |
| **6** | Inconsistent use of `size_t` vs. `int` for counts (`timer_days`).  If you ever support >2 147 483 647 days you’ll overflow a signed int. | Not a practical issue now, but using the appropriate unsigned type is good practice. | Use `uint32_t` or `int64_t`. |
| **7** | The helper `timestamp()` prints directly to stdout – not something a repository layer should do. | Separation of concerns. | Return a string (`char *`) that the caller can format, or make it part of a UI module. |
| **8** | No const‑correctness: functions that only read from a `task_t` (e.g., `task_save`) should accept `const task_t *`. | Allows passing temporaries and signals intent. | Add `const` where appropriate. |
| **9** | The macro names (`DB`, `TMP_DB`, `MAX_LINE_SIZE`) are all‑caps but not scoped; they could clash with other headers. | Global macros pollute the namespace. | Either give them a module prefix (`TASK_DB_PATH`) or make them `static const char *` variables inside the file. |
| **10** | The header `<time.h>` is included, yet you use `localtime()` without checking its return (it can be NULL on error). | Rare but possible failure. | Check for `NULL`. |

---

## 3️⃣  Minor Style / Readability Points

* **Indentation & Bracing** – Your code is already fairly clean; just keep a consistent style (K&R vs. Allman) throughout the project.
* **Error messages** – Include `strerror(errno)` to give the user more context, e.g.:
  ```c
  fprintf(stderr, "Failed to open %s for read: %s\n", DB, strerror(errno));

