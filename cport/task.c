#include "task.h"
#include <stdlib.h>
#include <string.h>

task_t* task_init(const char* desc, int timer, bool is_done) {
    task_t* task = malloc(sizeof(task_t));
    task->desc = strdup(desc);
    task->timer = timer;
    task->is_done = is_done;

    return task;
}

void task_destroy(task_t* task) {
    if (!task) return;

    free(task->desc);
    free(task);
}
