#ifndef TASK_H
#define TASK_H

#include <stdbool.h>
#include <time.h>

// TODO: Set static length 80 on 'char* desc' def.
// TOOD: "task" in typedef not necessary w/ alias task_t declared afterwards.
// TODO: Need validation to ensure max char limit of 80.
typedef struct task {
    long id;
    char* desc; // dynamic len, but orig. was 80 chars
    int timer_days;
    bool is_done;
    time_t updated_at;
} task_t;

task_t* task_init(const char* desc, int timer_days);
void task_destroy(task_t* task); // TODO: Implement this. Not sure how this compiles.

#endif
