# Objects Package

This package contains all object classes (except folders) in Gitlet.

## GitletObject

The parent class of `Bolb` and `CommitData`.

## Bolb

This class stores the content of a file.

It provides the following services:

- get the UID of this bolb.
- get the content of this bolb.

## CommitData

This class stores all the data in a commit.

It provides the following services:

- check whether this commit contains a specific file.
- get the UID of a file in this commit.
- get the parent commit.
- get the second parent commit.
- get commit meesage.
- get all files name in this commit.
- get log message for this commit.
- get the UID of this commit.
- compare file using UID.
- compare file with another commit.

## Stage

This class stores all the new added  files and removed files for the next commit.

It uses a treemap to store the new added files and their UID.

It uses a set to store the removed files. 