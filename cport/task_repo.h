#ifndef TASK_REPO_H
#define TASK_REPO_H

#include "task.h"

void task_save(task_t* task);
static long task_gen_seq_id();

#endif
