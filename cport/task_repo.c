#include "task_repo.h"
#include <stdio.h>
#include <stdlib.h>

#define DB "tasks.tsv"
#define TMP_DB "tasks.tmp"

#define MAX_LINE_SIZE 1024

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

// stream DB into TMP_DB, excluding the target_id record, then replicate orig. DB w/ tmp & cleanup
void task_delete_by_id(long target_id) {
    FILE* db = task_read_db();
    FILE* tmp_db = fopen(TMP_DB, "w");
    if (!tmp_db) {
        fclose(db);
        fprintf(stderr, "Failed to open TEMP Task database for write.\n");
        exit(EXIT_FAILURE);
    }

    char line[MAX_LINE_SIZE];
    bool found = false;

    while (fgets(line, sizeof(line), db)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1 && id == target_id) {
            found = true;
            continue; // target to delete found, skip writing this record
        }
        fputs(line, tmp_db);
    }

    fclose(db);
    fclose(tmp_db);

    if (!found) {
        remove(TMP_DB);
        fprintf(stderr, "Failed to delete. Could not find Task ID: %ld.\n", target_id);
        exit(EXIT_FAILURE);
    }

    if (remove(DB) != 0) {
        fprintf(stderr, "Failed to remove original Task database.\n");
        exit(EXIT_FAILURE);
    }

    if (rename(TMP_DB, DB) != 0) {
        fprintf(stderr, "Failed to replicate Task database.\n");
        exit(EXIT_FAILURE);
    }
}

static FILE* task_read_db() {
    FILE* fp = fopen(DB, "r");
    if (!fp) {
        fprintf(stderr, "Failed to open Task database for read.\n");
        exit(EXIT_FAILURE);
    }

    return fp;
}

// generate sequential primary key id based on last generated
static long task_gen_seq_id() {
    FILE* db = task_read_db();

    long last_id = 0;
    char line[MAX_LINE_SIZE];

    while (fgets(line, sizeof(line), db)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1) {
            if (id > last_id) {
                last_id = id;
            }
        }
    }

    fclose(db);
    return last_id + 1;
}
