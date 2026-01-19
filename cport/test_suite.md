You’re right to stop asserting the *exact* timestamp — that’s a classic brittle-test trap 👍
What you want is **“close enough to now”**, with a clear tolerance.

I’ll show you a **clean, test-friendly pattern** that:

1. Captures “now” safely
2. Extracts the timestamp from the TSV
3. Asserts it’s within a tolerance window
4. Deletes the test DB file reliably

All without overengineering or concurrency concerns.

---

## 1️⃣ Capture “now” in a test-safe way

In C, for your needs, **`time_t` + `time(NULL)` is exactly correct**.

```c
#include <time.h>

time_t before = time(NULL);

/* call function under test */

time_t after = time(NULL);
```

This gives you a **time window** in which the timestamp *must* fall.

---

## 2️⃣ Do NOT assert the whole TSV line as a string

This part is important:

```c
ASSERT_STR_CONTAINS(contents, expected_task_1);
```

This is what makes the test fragile.

Instead:

* Assert stable fields via string checks
* Parse the timestamp as a number
* Assert numerically

---

## 3️⃣ Extract the timestamp from the TSV line

Your TSV format:

```
id  desc  timer  is_done  updated_at
```

We can parse the last column.

### Minimal parsing approach (safe and simple)

```c
#include <stdlib.h>
#include <string.h>

static time_t extract_updated_at(const char* tsv)
{
    // duplicate because strtok modifies input
    char* copy = strdup(tsv);
    char* line = strtok(copy, "\n");

    // skip to last field
    char* token = NULL;
    for (int i = 0; i < 5; i++) {
        token = strtok(i == 0 ? line : NULL, "\t");
    }

    time_t ts = (time_t)strtoll(token, NULL, 10);
    free(copy);
    return ts;
}
```

This assumes:

* One task line (fine for this test)
* Format stability (reasonable for unit tests)

---

## 4️⃣ Assert timestamp is “close enough”

Define a tolerance (in seconds):

```c
#define TIME_TOLERANCE 2
```

Then assert:

```c
ASSERT_TRUE(updated_at >= before - TIME_TOLERANCE);
ASSERT_TRUE(updated_at <= after  + TIME_TOLERANCE);
```

This:

* Handles slow CI
* Handles clock granularity
* Avoids flaky failures

---

## 5️⃣ Full revised test (clean & robust)

```c
static void test_task_save_Success(void)
{
    task_t* task = task_init("test-desc", 7);

    time_t before = time(NULL);
    task_save(task);
    time_t after = time(NULL);

    char* contents = file_to_str(TEST_DB);

    ASSERT_STR_CONTAINS(contents, "1\ttest-desc\t7\tfalse\t");

    time_t updated_at = extract_updated_at(contents);

    ASSERT_TRUE(updated_at >= before - 2);
    ASSERT_TRUE(updated_at <= after  + 2);

    free(contents);
    remove(TEST_DB);
}
```

---

## 6️⃣ Deleting the `.tsv` file (portable & correct)

Use **`remove()`**, not `unlink()`:

```c
#include <stdio.h>

remove(TEST_DB);
```

✔ Portable
✔ Works on Windows + Linux
✔ Fails silently if file doesn’t exist (or check return value if you want)

Optional assert:

```c
ASSERT_EQ(0, remove(TEST_DB));
```

---

## 7️⃣ Why this approach is “the right C way”

* ✅ Tests logic, not formatting
* ✅ Avoids mocking time (overkill here)
* ✅ Works without global state
* ✅ No dependency injection required
* ✅ CI-safe

This is exactly how time-sensitive C code is tested in real-world codebases.

---

## 🔥 Bonus: one improvement for later

When you’re ready, the **gold standard** is:

```c
time_t (*now_fn)(void) = time;
```

Then override `now_fn` in tests.

But for your current scope?
**What you’re doing now is perfect.**

---

If you want, next we can:

* Refactor parsing to avoid `strtok`
* Add multi-task tests
* Write a helper assertion like `ASSERT_TIME_CLOSE()`
* Discuss how to test expiration logic cleanly

You’re doing *excellent* C engineering work here.
