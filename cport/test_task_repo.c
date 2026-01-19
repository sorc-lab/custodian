#include "test_task_repo.h"
#include <stdio.h>
#include "assert.h"
#include "task.h"
#include "task_repo.h"

#define TEST_DB "test_tasks.tsv"

static void test_task_save_Success();
static char* file_to_str(const char* path);

void test_task_repo_all() {
    test_task_save_Success();
}

// TODO: Updated test_suite.md w/ enhancement ideas to test each line and testing the updated_at
//      timestamp within a threshold.
// NOTE: See if it makes sense to move whatever timestamp assert funcs into assert.h lib funcs.
static void test_task_save_Success() {
    // TODO: Add x2 more task_t structs.
    task_t* task_1 = task_init("test-desc", 7);

    task_save(task_1);

    char* contents = file_to_str(TEST_DB);
    printf("test_tasks.tsv CONTENTS:\n%s\n", contents);

    char* expected_task_1 = "1\ttest-desc\t7\tfalse\t1768844643";

    ASSERT_STR_CONTAINS(contents, expected_task_1);

    free(contents);

    // TODO: rm test_tasks.tsv
}

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
