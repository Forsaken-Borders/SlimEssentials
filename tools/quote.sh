#!/bin/bash

# https://unix.stackexchange.com/a/244556
quote () {
    local quoted=${1//\'/\'\\\'\'};
    printf "'%s'" "$quoted"
}