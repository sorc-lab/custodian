#include "test_task_repo.h"
#include <stdio.h>
#include "assert.h"

void test_task_repo_all() {
    ASSERT_TRUE(3 < 4);
    ASSERT_FALSE(4 < 3);

    char str[] = "This is a test";
    ASSERT_STR_CONTAINS(str, "blah blah");
}
