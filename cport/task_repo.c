#include "task_repo.h"
#include <stdio.h>
#include <stdlib.h>

#define MAX_LINE_SIZE 1024

static const char DB[] = "tasks.tsv";

void task_save(task_t* task) {
    if (!task) return;

    FILE* fp = fopen(DB, "a");
    if (!fp) {
        fprintf(stderr, "Failed open Task database for append.\n");
        exit(EXIT_FAILURE);
    }

    fprintf(fp, "%ld\t%s\t%d\t%s\n",
        task_gen_seq_id(),
        (task->desc) ? task->desc : "",
        task->timer,
        (task->is_done) ? "true" : "false"
    );

    printf("Task %ld added: %s (%d days)\n", task->id, task->desc, task->timer);
    task_destroy(task);
    fclose(fp);
}

// generate sequential primary key id based on last generated
static long task_gen_seq_id() {
    FILE* fp = fopen(DB, "r");
    if (!fp) {
        fprintf(stderr, "Failed to open Task database for read.\n");
        exit(EXIT_FAILURE);
    }

    long last_id = 0;
    char line[MAX_LINE_SIZE];

    while (fgets(line, sizeof(line), fp)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1) {
            if (id > last_id) {
                last_id = id;
            }
        }
    }

    fclose(fp);
    return last_id + 1;
}
