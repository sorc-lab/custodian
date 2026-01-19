#include "cmd_handler.h"
#include <stdio.h>
#include <stdbool.h>
#include "task_repo.h"
#include <string.h>
#include "test_task_repo.h"

cmd_t cli_commands[] = {
    {"add", "add <description> <days>", cmd_add},
    {"rm", "rm <id>", cmd_rm},
    {"done", "done <id>", cmd_done},
    {"ls", "ls", cmd_ls},
    {"help", "help", cmd_help},
    {"test", "test", cmd_test},
    {NULL, NULL, NULL}
};

void cmd_handler(int argc, char* argv[]) {
    const char* cmd_name = argv[1];

    if (argc < 2) {
        fprintf(stderr, "Usage: %s <command> [args]\n", argv[0]);
        exit(EXIT_FAILURE);
    }

    for (int i = 0; cli_commands[i].name != NULL; i++) {
        if (strcmp(cmd_name, cli_commands[i].name) == 0) {
            cli_commands[i].handler(argc - 1, argv + 1);
            exit(EXIT_SUCCESS);
        }
    }

    fprintf(stderr, "Unknown command: %s\n", cmd_name);
    cmd_help(argc, argv);
    exit(EXIT_FAILURE);
}

static void cmd_add(int argc, char* argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: add <description> <days>\n");
        exit(EXIT_FAILURE);
    }
    task_save(task_init(argv[1], atoi(argv[2])));
}

static void cmd_rm(int argc, char* argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: rm <id>\n");
        exit(EXIT_FAILURE);
    }
    task_delete_by_id(atoi(argv[1]));
}

static void cmd_done(int argc, char* argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: done <id>\n");
        exit(EXIT_FAILURE);
    }
    task_set_is_done(atoi(argv[1]));
}

static void cmd_ls(int argc, char* argv[]) {
    printf("ls not implemented yet\n");
}

static void cmd_help(int argc, char* argv[]) {
    (void) argc;
    (void) argv;

    printf(
        "custodian - task management CLI\n\n"
        "USAGE\n"
        "    custodian <command> [options]\n\n"
        "COMMANDS\n"
        "    add <description> <days>\n"
        "        Add a new task with the given description and number of days\n"
        "        until the task expires.\n\n"
        "    rm <id>\n"
        "        Remove a task by its numeric ID.\n\n"
        "    done <id>\n"
        "       Set task is_done to true.\n\n"
        "    ls\n"
        "        List all tasks.\n\n"
        "    view <id>\n"
        "        Display full details for a specific task.\n\n"
        "    test\n"
        "        Run full test suite to verify application integrity.\n\n"
        "EXAMPLES\n"
        "    custodian add \"Wipe kitchen floors\" 7\n"
        "    custodian ls\n"
        "    custodian view 3\n"
        "    custodian done 3\n"
        "    custodian rm 3\n"
        "    custodian test\n\n"
        "EXIT STATUS\n"
        "    0   Success\n"
        "    1   General error\n"
        "    2   Invalid usage\n"
    );
}

/* TODOS:
    1. Create assert.h w/ #define'd assert utils
    2. Import assert.h into test_task_repo.c & play with it. Look for good patterns.
    3. Once asert patterns established, start writing e2e tests exercizing ALL task_repo funcs & logic branches.
*/
static void cmd_test(int argc, char* argv[]) {
    test_task_repo_all();
}
