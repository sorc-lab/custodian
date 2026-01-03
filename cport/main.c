// TODO: Go into each .c file and audit each #include, then do for .h files.
#include "cmd_handler.h"

// TODO: Move this to a formatted print output and replace the fprintf "Unknown command: "
/* API USAGE
    - add "description" 7 (add task w/ desc & num days til expires)
    - rm 12 (removes a task by its id)
    - ls (list all tasks)
    - view 12 (view full details of a specific task by id)
    - done 12 (marks a task (by id) as completed)
*/
// TODO: Task.c and Task.h are case insensitive in git diff. Fix it.
void main(int argc, char* argv[]) {
    cmd_handler(argc, argv);
}
