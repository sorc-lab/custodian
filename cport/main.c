#include "cmd_handler.h"
#include <stdbool.h>
#include "task_repo.h"
#include <string.h>

static void set_test_mode(int argc, char* argv[]);

// TODO: Look into removing .vscode from outer root dir of project. .vscode only needed inside cport.
int main(int argc, char* argv[]) {
    set_test_mode(argc, argv);
    cmd_handler(argc, argv);
    return 0; // TODO: Control to be fixed as a separate issue.
}

// global test_mode defined in 'task_repo.h'
static void set_test_mode(int argc, char* argv[]) {
    for (int i = 0; i < argc; i++) {
        if (strcmp(argv[i], "test") == 0) {
            test_mode = true;
            return;
        }
    }
    test_mode = false;
}
