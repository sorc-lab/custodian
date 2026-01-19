#include "task_repo.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <stdbool.h>

bool test_mode = false;

#define DB (test_mode ? "test_tasks.tsv" : "tasks.tsv")
#define TMP_DB (test_mode ? "test_tasks.tmp" : "tasks.tmp")

#define MAX_LINE_SIZE 1024

void task_save(task_t* task) {
    if (!task) return;

    FILE* db_appender = task_db_appender();
    long id = task_gen_seq_id();
    fprintf(db_appender, "%ld\t%s\t%d\t%s\t%ld\n",
        id,
        task->desc,
        task->timer_days,
        "false",
        (long) task->updated_at
    );

    printf("Task %ld added: %s (%d days)\n", id, task->desc, task->timer_days);
    fclose(db_appender);
}

// stream DB into TMP_DB, excluding the target_id record, then replicate orig. DB w/ tmp & cleanup
void task_delete_by_id(long target_id) {
    FILE* db_reader = task_db_reader();
    FILE* tmp_db_writer = task_tmp_db_writer(db_reader);

    char line[MAX_LINE_SIZE];
    bool found = false;

    while (fgets(line, sizeof(line), db_reader)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1 && id == target_id) {
            found = true;
            continue; // target to delete found, skip writing this record
        }
        fputs(line, tmp_db_writer);
    }

    task_close_db_access(db_reader, tmp_db_writer, found, target_id);
}

void task_set_is_done(long id) {
    task_t* task = task_find_by_id(id);
    task->is_done = true;
    task->updated_at = time(NULL);
    task_update(task);
}

static FILE* task_db_appender() {
    FILE* appender = fopen(DB, "a");
    if (!appender) {
        fprintf(stderr, "Failed open Task database for append.\n");
        exit(EXIT_FAILURE);
    }
    return appender;
}

// generate sequential primary key id based on last generated
static long task_gen_seq_id() {
    FILE* db_reader = task_db_reader();

    long last_id = 0;
    char line[MAX_LINE_SIZE];

    while (fgets(line, sizeof(line), db_reader)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1) {
            if (id > last_id) {
                last_id = id;
            }
        }
    }

    fclose(db_reader);
    return last_id + 1;
}

static FILE* task_db_reader() {
    FILE* reader = fopen(DB, "r");
    if (!reader) {
        fprintf(stderr, "Failed to open Task database for read.\n");
        exit(EXIT_FAILURE);
    }
    return reader;
}

// requires db_reader as it should handle closing its file pointer access upon fail to open tmp_db
static FILE* task_tmp_db_writer(FILE* db_reader) {
    FILE* writer = fopen(TMP_DB, "w");
    if (!writer) {
        fclose(db_reader);
        fprintf(stderr, "Failed to open TEMP Task database for write.\n");
        exit(EXIT_FAILURE);
    }
}

static void task_close_db_access(FILE* db_reader, FILE* tmp_db_writer, bool has_task_match, long task_id) {
    fclose(db_reader);
    fclose(tmp_db_writer);

    if (!has_task_match) {
        remove(TMP_DB);
        fprintf(stderr, "Failed to find matching record for Task ID: %ld.\n", task_id);
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
    FILE* db_reader = task_db_reader();
    char line[MAX_LINE_SIZE];

    while (fgets(line, sizeof(line), db_reader)) {
        long id = 0;
        int days = 0;
        char desc[256];
        char is_done_str[8];
        long updated_at;

        // `%255[^\t]` read description up to tab (safe!)
        int parsed = sscanf(line, "%ld\t%255[^\t]\t%d\t%7s\t%ld",
            &id,
            desc,
            &days,
            is_done_str,
            &updated_at
        );
        if (parsed == 5 && id == target_id) { // check if parsed 5 elements & matches target_id
            bool is_done = (strcmp(is_done_str, "true") == 0);
            fclose(db_reader);

            task_t* task = task_init(desc, days);
            task->id = target_id;
            task->is_done = is_done;
            task->updated_at = (time_t) updated_at;
            return task;
        }
    }

    fclose(db_reader);
    fprintf(stderr, "Failed to find Task by ID: %ld.\n", target_id);
    exit(EXIT_FAILURE);
}

static void task_update(task_t* task) {
    FILE* db_reader = task_db_reader();
    FILE* tmp_db_writer = task_tmp_db_writer(db_reader);

    char line[MAX_LINE_SIZE];
    bool found = false;

    while (fgets(line, sizeof(line), db_reader)) {
        long id = 0;
        if (sscanf(line, "%ld", &id) == 1 && id == task->id) {
            found = true;
            fprintf(tmp_db_writer, "%ld\t%s\t%d\t%s\t%ld\n",
                task->id,
                task->desc,
                task->timer_days,
                (task->is_done) ? "true" : "false",
                task->updated_at
            );
            continue; // writes updated data for this record and can skip to next line in file
        }
        fputs(line, tmp_db_writer);
    }

    task_close_db_access(db_reader, tmp_db_writer, found, task->id);
}

// TODO: Move out of task_repo.c. For display use only! Return str vs. void method.
static void timestamp(time_t epoch_time) {
    char buf[64];
    struct tm* tm = localtime(&epoch_time);
    strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", tm);
    printf("TIMESTAMP: %s\n", buf);
}
