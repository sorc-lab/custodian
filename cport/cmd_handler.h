#ifndef CMD_HANDLER_H
#define CMD_HANDLER_H

#include <stdlib.h>

typedef void (*cmd_func)(int argc, char* argv[]);

typedef struct cli_command {
    const char* name;
    const char* usage;
    cmd_func handler;
} cmd_t;

void cmd_handler(int argc, char* argv[]);

static void cmd_add(int argc, char* argv[]);
static void cmd_rm(int argc, char* argv[]);
static void cmd_ls(int argc, char* argv[]);
static void cmd_help(int argc, char* argv[]);

#endif
