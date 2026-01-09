#include "task.h"
#include <stdlib.h>
#include <string.h>

task_t* task_init(const char* desc, int timer_days) {
    task_t* task = malloc(sizeof(task_t));
    task->desc = strdup(desc);
    task->timer_days = timer_days;

    // defaults
    task->is_done = false;
    task->updated_at = time(NULL); // current seconds since unix epoch
    return task;
}
