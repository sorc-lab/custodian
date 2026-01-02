#ifndef TASK_H
#define TASK_H

#include <stdbool.h>

typedef struct task {
    long id;
    char* desc;  // dynamic len, but orig. was 80 chars
    int timer; // duration in days until exp.
    bool is_done;

    /*
        private LocalDateTime updatedAt;
        private LocalDateTime expirationDate;
    */
} task_t;

task_t* task_init(const char* desc, int timer, bool is_done);
void task_destroy(task_t* task);

#endif
