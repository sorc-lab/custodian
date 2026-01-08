#ifndef TASK_REPO_H
#define TASK_REPO_H

#include "task.h"
#include <stdio.h>

void task_save(task_t* task);
void task_delete_by_id(long target_id);
void task_set_is_done(long id);

static FILE* task_read_db();
static long task_gen_seq_id();
static void task_update(task_t* task);
static task_t* task_find_by_id(long target_id);

#endif
