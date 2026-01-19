#ifndef TASK_REPO_H
#define TASK_REPO_H

#include "task.h"
#include <stdio.h>

extern bool test_mode;

void task_save(task_t* task);
void task_delete_by_id(long target_id);
void task_set_is_done(long id);

static FILE* task_db_appender();
static long task_gen_seq_id();
static FILE* task_db_reader();
static FILE* task_tmp_db_writer(FILE* db_reader);
static void task_close_db_access(FILE* db_reader, FILE* tmp_db_writer, bool has_task_match, long task_id);
static task_t* task_find_by_id(long target_id);
static void task_update(task_t* task);
static void timestamp(time_t epoch_time);

#endif
