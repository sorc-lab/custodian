#include "cmd_handler.h"
#include <stdio.h>
#include <stdbool.h>
#include "task_repo.h"
#include <string.h>

cmd_t cli_commands[] = {
    {"add", "add <description> <days>", cmd_add},
    {"rm", "rm <id>", cmd_rm},
    {"ls", "ls", cmd_ls},
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

    // TODO: Dump --help info here vs. unknown cmd msg. Move into cmd handler src file.
    fprintf(stderr, "Unknown command: %s\n", cmd_name);
    exit(EXIT_FAILURE);
}

static void cmd_add(int argc, char* argv[]) {
    if (argc < 3) {
        fprintf(stderr, "Usage: add <description> <days>\n");
        exit(EXIT_FAILURE);
    }
    task_save(task_init(argv[1], atoi(argv[2]), false));
}

static void cmd_rm(int argc, char* argv[]) {
    printf("rm not implemented yet\n");
}

static void cmd_ls(int argc, char* argv[]) {
    printf("ls not implemented yet\n");
}
