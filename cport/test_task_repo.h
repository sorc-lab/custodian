#ifndef TEST_TASK_REPO_H
#define TEST_TASK_REPO_H

#include <stddef.h>

/*
char*** data
   |
   +--> char** row 0
   |       |
   |       +--> char* "1"
   |       +--> char* "test-desc-1"
   |
   +--> char** row 1
           |
           +--> char* "2"
*/
typedef struct {
    size_t rows;
    size_t cols;      // max columns in any row (optional but useful)
    char*** data;     // data[row][col]
} tsv_table_t;

void test_task_repo_all();

#endif
