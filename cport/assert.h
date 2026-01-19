#ifndef ASSERT_H
#define ASSERT_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define ASSERT_TRUE(cond) do { \
    if (!(cond)) { \
        fprintf(stderr, "ASSERT FAILED: %s (%s:%d)\n", \
                #cond, __FILE__, __LINE__); \
        exit(EXIT_FAILURE); \
    } \
} while (0)

#define ASSERT_FALSE(cond) ASSERT_TRUE(!(cond))

#define ASSERT_STR_CONTAINS(haystack, needle) \
    ASSERT_TRUE(strstr((haystack), (needle)) != NULL)

#endif
