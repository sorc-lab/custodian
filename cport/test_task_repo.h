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
    size_t* cols; // cols[r] = number of columns in row r
    char*** data; // data[row][col]
} tsv_table_t;

tsv_table_t tsv_parse(const char* text);
void tsv_free(tsv_table_t* table);

void test_task_repo_all(void);

int task_save_Success(void);
char* file_to_str(const char* path);
void tsv_free(tsv_table_t* table);
tsv_table_t tsv_parse(const char* text);

#endif
