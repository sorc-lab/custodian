#include "test_task_repo.h"
#include <stdio.h>
#include "assert.h"
#include "task.h"
#include "task_repo.h"

#define TEST_DB "test_tasks.tsv"
#define TIME_TOLERANCE 2

static int task_save_Success(void);
static int task_delete_by_id_Success(void);
static int task_set_is_done_Success(void);

static char* file_to_str(const char* path);
static tsv_table_t tsv_parse(const char* text);
static void tsv_free(tsv_table_t* table);

void test_task_repo_run(void) {
    remove(TEST_DB);
    task_save_Success();

    remove(TEST_DB);
    task_delete_by_id_Success();

    remove(TEST_DB);
    task_set_is_done_Success();
}

static int task_save_Success(void) {
    task_t* task_1 = task_init("test-desc-1", 7);
    task_t* task_2 = task_init("test-desc-2", 14);
    task_t* task_3 = task_init("test-desc-3", 30);
    task_save(task_1);
    task_save(task_2);
    task_save(task_3);

    char* contents = file_to_str(TEST_DB);
    tsv_table_t table = tsv_parse(contents);

    ASSERT_TRUE(table.rows == 4);
    ASSERT_TRUE(table.data[0] != NULL);
    ASSERT_TRUE(table.data[1] != NULL);
    ASSERT_TRUE(table.data[2] != NULL);

    time_t curr_time = time(NULL);

    char* task_1_id = table.data[0][0];
    char* task_1_desc = table.data[0][1];
    char* task_1_timer_days = table.data[0][2];
    char* task_1_is_done = table.data[0][3];
    time_t task_1_updated_at = (time_t) strtoll(table.data[0][4], NULL, 10);

    char* task_2_id = table.data[1][0];
    char* task_2_desc = table.data[1][1];
    char* task_2_timer_days = table.data[1][2];
    char* task_2_is_done = table.data[1][3];
    time_t task_2_updated_at = (time_t) strtoll(table.data[1][4], NULL, 10);

    char* task_3_id = table.data[2][0];
    char* task_3_desc = table.data[2][1];
    char* task_3_timer_days = table.data[2][2];
    char* task_3_is_done = table.data[2][3];
    time_t task_3_updated_at = (time_t) strtoll(table.data[2][4], NULL, 10);

    ASSERT_TRUE(strcmp(task_1_id, "1") == 0);
    ASSERT_TRUE(strcmp(task_1_desc, "test-desc-1") == 0);
    ASSERT_TRUE(strcmp(task_1_timer_days, "7") == 0);
    ASSERT_TRUE(strcmp(task_1_is_done, "false") == 0);
    ASSERT_TRUE(llabs(curr_time - task_1_updated_at) <= TIME_TOLERANCE);

    ASSERT_TRUE(strcmp(task_2_id, "2") == 0);
    ASSERT_TRUE(strcmp(task_2_desc, "test-desc-2") == 0);
    ASSERT_TRUE(strcmp(task_2_timer_days, "14") == 0);
    ASSERT_TRUE(strcmp(task_2_is_done, "false") == 0);
    ASSERT_TRUE(llabs(curr_time - task_2_updated_at) <= TIME_TOLERANCE);

    ASSERT_TRUE(strcmp(task_3_id, "3") == 0);
    ASSERT_TRUE(strcmp(task_3_desc, "test-desc-3") == 0);
    ASSERT_TRUE(strcmp(task_3_timer_days, "30") == 0);
    ASSERT_TRUE(strcmp(task_3_is_done, "false") == 0);
    ASSERT_TRUE(llabs(curr_time - task_3_updated_at) <= TIME_TOLERANCE);

    tsv_free(&table);
    free(contents);
    remove(TEST_DB);
    fflush(stdout);

    printf("[SUCCESS] task_save_Success\n");
    return 0;
}

static int task_delete_by_id_Success(void) {
    task_t* task_1 = task_init("test-desc-1", 7);
    task_t* task_2 = task_init("test-desc-2", 14);
    task_save(task_1);
    task_save(task_2);
    task_delete_by_id(1);

    char* contents = file_to_str(TEST_DB);
    tsv_table_t table = tsv_parse(contents);

    ASSERT_TRUE(table.rows == 2);
    ASSERT_TRUE(table.data[0] != NULL);

    char* task_id = table.data[0][0];
    char* task_desc = table.data[0][1];
    char* task_timer_days = table.data[0][2];
    char* task_is_done = table.data[0][3];
    time_t task_updated_at = (time_t) strtoll(table.data[0][4], NULL, 10);
    time_t curr_time = time(NULL);

    ASSERT_TRUE(strcmp(task_id, "2") == 0);
    ASSERT_TRUE(strcmp(task_desc, "test-desc-2") == 0);
    ASSERT_TRUE(strcmp(task_timer_days, "14") == 0);
    ASSERT_TRUE(strcmp(task_is_done, "false") == 0);
    ASSERT_TRUE(llabs(curr_time - task_updated_at) <= TIME_TOLERANCE);

    tsv_free(&table);
    free(contents);
    remove(TEST_DB);
    fflush(stdout);

    printf("[SUCCESS] task_delete_by_id_Success\n");
    return 0;
}

static int task_set_is_done_Success(void) {
    task_t* task = task_init("test-desc", 7);
    task_save(task);
    task_set_is_done(1);

    char* contents = file_to_str(TEST_DB);
    tsv_table_t table = tsv_parse(contents);

    ASSERT_TRUE(table.rows == 2);
    ASSERT_TRUE(table.data[0] != NULL);

    char* task_is_done = table.data[0][3];
    ASSERT_TRUE(strcmp(task_is_done, "true") == 0);

    tsv_free(&table);
    free(contents);
    remove(TEST_DB);
    fflush(stdout);

    printf("[SUCCESS] task_set_is_done_Success\n");
    return 0;
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

static tsv_table_t tsv_parse(const char* text) {
    ASSERT_TRUE(text != NULL);

    tsv_table_t table = {0};

    char* copy = strdup(text);
    ASSERT_TRUE(copy != NULL);

    // count rows
    for (const char* p = text; *p; p++) {
        if (*p == '\n') table.rows++;
    }
    table.rows++;

    table.data = calloc(table.rows, sizeof(char**));
    table.cols = calloc(table.rows, sizeof(size_t));
    ASSERT_TRUE(table.data != NULL);
    ASSERT_TRUE(table.cols != NULL);

    size_t r = 0;
    char* save_line = NULL;
    char* line = strtok_r(copy, "\n", &save_line);

    while (line && r < table.rows) {

        size_t col_count = 1;
        for (char* p = line; *p; p++) {
            if (*p == '\t') col_count++;
        }

        table.cols[r] = col_count;
        table.data[r] = calloc(col_count, sizeof(char*));
        ASSERT_TRUE(table.data[r] != NULL);

        size_t c = 0;
        char* save_field = NULL;
        char* field = strtok_r(line, "\t", &save_field);

        while (field && c < col_count) {
            table.data[r][c++] = strdup(field);
            field = strtok_r(NULL, "\t", &save_field);
        }

        r++;
        line = strtok_r(NULL, "\n", &save_line);
    }

    free(copy);
    return table;
}

static void tsv_free(tsv_table_t* table) {
    if (!table || !table->data) return;

    for (size_t r = 0; r < table->rows; r++) {
        for (size_t c = 0; c < table->cols[r]; c++) {
            free(table->data[r][c]);
        }
        free(table->data[r]);
    }

    free(table->data);
    free(table->cols);

    table->data = NULL;
    table->cols = NULL;
    table->rows = 0;
}
