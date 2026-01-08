#include "task_repo.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

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

/*
public void completeTaskById(long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No task found for id: " + id));

        task.setComplete(true);
        task.setUpdatedAt(LocalDateTime.now());
        task.setExpirationDate(task.getUpdatedAt().plusDays(task.getTimerDurationDays()));

        taskRepo.save(task);
    }
*/
void task_set_is_done(long id) {
    task_t* task = task_find_by_id(id);

    // TODO: After update prototype working, set updated_at & set expiration etc, here.
    // NOTE: These fields will need to be set w/ defaults in the task_add and display code, etc.
    task->is_done = true;
    task_update(task);


    // TODO: Time in seconds since UNIX epoch.
    time_t now = time(NULL);
    printf("Current time: %d\n", now);
}

static void task_update(task_t* task) {
    // TODO: REMOVE AFTER PROTOTYPING
    printf("ID: %ld\n", task->id);
    printf("desc: %s\n", task->desc);
    printf("timer: %d\n", task->timer);
    printf("is_done: %s\n", (task->is_done) ? "true" : "false");

    // TODO: Introduces duplicate code in task_delete_by_id func.
    // NOTE: Many lines here are duplicated. Figure out a good func handler for this boilerplate.
    // NOTE: Pattern might not make sense bc it would need to return a struct basically for db update setup.
    // NOTE: May be best to just create func for returning file handler for tmp_db write access. task_write_tmp_db().
    //          FILE* db_reader, FILE* tmp_db_writer vars holding the file handles.
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
        if (sscanf(line, "%ld", &id) == 1 && id == task->id) {
            found = true;
            fprintf(tmp_db, "%ld\t%s\t%d\t%s\n",
                task->id,
                task->desc,
                task->timer,
                (task->is_done) ? "true" : "false"
            );
            continue; // writes updated data for this record and can skip to next line in file
        }
        fputs(line, tmp_db);
    }

    // TODO: Again, this entire cleanup process is duplicate code w/ task_delete_by_id func.
    fclose(db);
    fclose(tmp_db);

    if (!found) {
        remove(TMP_DB);
        fprintf(stderr, "Failed to delete. Could not find Task ID: %ld.\n", task->id);
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

static task_t* task_find_by_id(long target_id) {
    FILE* db = task_read_db();
    char line[MAX_LINE_SIZE];

    while (fgets(line, sizeof(line), db)) {
        long id = 0;
        int days = 0;
        char desc[256];
        char is_done_str[8];

        // `%255[^\t]` read description up to tab (safe!)
        int parsed = sscanf(line, "%ld\t%255[^\t]\t%d\t%7s", &id, desc, &days, is_done_str);
        if (parsed == 4 && id == target_id) {
            bool is_done = (strcmp(is_done_str, "true") == 0);
            fclose(db);

            task_t* task = task_init(desc, days, is_done);
            task->id = target_id;
            return task;
        }
    }

    fclose(db);
    fprintf(stderr, "Failed to find Task by ID: %ld.\n", target_id);
    exit(EXIT_FAILURE);
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
