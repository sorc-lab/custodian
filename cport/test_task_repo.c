#include "test_task_repo.h"
#include <stdio.h>
#include "assert.h"
#include "task.h"
#include "task_repo.h"

#define TEST_DB "test_tasks.tsv"

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

    ASSERT_TRUE(strcmp(rows[0][0], "1") == 0);
    ASSERT_TRUE(strcmp(rows[0][1], "test-desc-1") == 0);
    ASSERT_TRUE(strcmp(rows[0][2], "7") == 0);
    ASSERT_TRUE(strcmp(rows[0][3], "false") == 0);

    time_t updated_at = strtoll(rows[0][4], NULL, 10);
    time_t now = time(NULL);
    ASSERT_TRUE(llabs(now - updated_at) <= 2);

    tsv_free(rows);
    free(contents);

    // TODO: Remove after testing!
    if (remove(TEST_DB) != 0) {
        perror("remove");
    }
    remove(TEST_DB);
}

// TODO: Grok this 100% and intimately understand the use of 'char***' type.
// NOTE: Would it be preferrable to use array idx notation vs. pointer stars?
static char*** tsv_split(const char* text) {
    char* copy = strdup(text);

    // Count rows
    size_t rows = 0;
    for (const char* p = text; *p; p++) {
        if (*p == '\n') rows++;
    }
    rows++; // last line

    char*** table = calloc(rows + 1, sizeof(char**));

    size_t r = 0;
    char* line = strtok(copy, "\n");

    while (line) {
        // Count columns
        size_t cols = 1;
        for (char* p = line; *p; p++) {
            if (*p == '\t') cols++;
        }

        table[r] = calloc(cols + 1, sizeof(char*));

        size_t c = 0;
        char* field = strtok(line, "\t");
        while (field) {
            table[r][c++] = strdup(field);
            field = strtok(NULL, "\t");
        }

        r++;
        line = strtok(NULL, "\n");
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
