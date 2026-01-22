#include "test_task_repo.h"
#include <stdio.h>
#include "assert.h"
#include "task.h"
#include "task_repo.h"

#define TEST_DB "test_tasks.tsv"
#define TIME_TOLERANCE 2

static void task_save_Success(void);
static char*** tsv_split(const char* text);
static void tsv_free(char*** table);
static char* file_to_str(const char* path);

void test_task_repo_all() {
    task_save_Success();
}

// TODO: Updated test_suite.md w/ enhancement ideas to test each line and testing the updated_at
//      timestamp within a threshold.
// NOTE: See if it makes sense to move whatever timestamp assert funcs into assert.h lib funcs.
static void task_save_Success(void) {
    task_t* task_1 = task_init("test-desc-1", 7);
    task_t* task_2 = task_init("test-desc-2", 14);
    task_t* task_3 = task_init("test-desc-3", 30);
    task_save(task_1);
    task_save(task_2);
    task_save(task_3);

    char* contents = file_to_str(TEST_DB);
    char*** rows = tsv_split(contents);

    time_t curr_time = time(NULL);

    char* task_1_id = rows[0][0];
    char* task_1_desc = rows[0][1];
    char* task_1_timer_days = rows[0][2];
    char* task_1_is_done = rows[0][3];
    time_t task_1_updated_at = (time_t) strtoll(rows[0][4], NULL, 10);

    printf("LINE: %d\n", __LINE__);

    // TODO: Expand on this and do error checking for the full 2D array before all asserts.
    // NOTE: Keep pattern here for now and expand on it later.
    ASSERT_TRUE(rows != NULL);
    ASSERT_TRUE(rows[1] != NULL);
    ASSERT_TRUE(rows[1][0] != NULL);

    char* task_2_id = rows[1][0];
    char* task_2_desc = rows[1][1];
    char* task_2_timer_days = rows[1][2];
    char* task_2_is_done = rows[1][3];
    time_t task_2_updated_at = (time_t) strtoll(rows[1][4], NULL, 10);

    ASSERT_TRUE(strcmp(task_1_id, "1") == 0);
    ASSERT_TRUE(strcmp(task_1_desc, "test-desc-1") == 0);
    ASSERT_TRUE(strcmp(task_1_timer_days, "7") == 0);
    ASSERT_TRUE(strcmp(task_1_is_done, "false") == 0);

    printf("curr_time: %lld\n", curr_time);
    printf("task_1_updated_at: %lld\n", task_1_updated_at);
    ASSERT_TRUE(llabs(curr_time - task_1_updated_at) <= TIME_TOLERANCE);

    ASSERT_TRUE(strcmp(task_2_id, "2") == 0);
    ASSERT_TRUE(strcmp(task_2_desc, "test-desc-2") == 0);
    ASSERT_TRUE(strcmp(task_2_timer_days, "14") == 0);
    ASSERT_TRUE(strcmp(task_2_is_done, "false") == 0);
    ASSERT_TRUE(llabs(curr_time - task_2_updated_at) <= TIME_TOLERANCE);

    // TODO: Failed silently again. Did not print here and did not clear the DB file.
    printf("[SUCCESS] task_save_Success\n");

    // TODO: Assertion failures result in a failure to cleanup. Need better control flow.
    // NOTE: Need to store globals, like rows and file contents to be freed via private method
    //      after each or all test executions.
    // NOTE: Do that later. Strick w/ getting first test working w/ field parsers, etc.
    // TODO: Should rm DB also before the start of every test that uses it.
    tsv_free(rows);
    free(contents);
    if (remove(TEST_DB) != 0) {
        perror("remove");
    }
    //remove(TEST_DB);
}

// TODO: Grok this 100% and intimately understand the use of 'char***' type.
// NOTE: Would it be preferrable to use array idx notation vs. pointer stars?
static char*** tsv_split(const char* text) {
    char* copy = strdup(text);
    ASSERT_TRUE(copy != NULL);

    // Count rows
    size_t rows = 0;
    for (const char* p = text; *p; p++) {
        if (*p == '\n') rows++;
    }
    rows++; // last line

    char*** table = calloc(rows + 1, sizeof(char**));
    ASSERT_TRUE(table != NULL);

    size_t r = 0;
    char* save_line = NULL;
    char* line = strtok_r(copy, "\n", &save_line);

    while (line) {
        // Count columns
        size_t cols = 1;
        for (char* p = line; *p; p++) {
            if (*p == '\t') cols++;
        }

        table[r] = calloc(cols + 1, sizeof(char*));
        ASSERT_TRUE(table[r] != NULL);

        size_t c = 0;
        char* save_field = NULL;
        char* field = strtok_r(line, "\t", &save_field);

        while (field) {
            table[r][c++] = strdup(field);
            field = strtok_r(NULL, "\t", &save_field);
        }

        r++;
        line = strtok_r(NULL, "\n", &save_line);
    }

    free(copy);
    return table;
}


static void tsv_free(char*** table) {
    for (size_t r = 0; table[r]; r++) {
        for (size_t c = 0; table[r][c]; c++) {
            free(table[r][c]);
        }
        free(table[r]);
    }
    free(table);
}

// TODO: Consider moving all util methods into a single header file.
static char* file_to_str(const char* path) {
    FILE* fp = fopen(path, "r");
    ASSERT_TRUE(fp != NULL);

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);
    rewind(fp);

    char* buf = malloc(size + 1);
    ASSERT_TRUE(buf != NULL);

    fread(buf, 1, size, fp);
    buf[size] = '\0';

    fclose(fp);
    return buf;
}
