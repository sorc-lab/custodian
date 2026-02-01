#ifndef TASK_REPO_H
#define TASK_REPO_H

#include "task.h"
#include <stdio.h>

extern bool test_mode;

void task_save(task_t* task);
void task_delete_by_id(long target_id);
void task_set_is_done(long id);

#endif
