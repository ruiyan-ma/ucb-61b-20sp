# Repository Package

This package provides search, read and write data services for all objects and commands in Gitlet.

## Folder

This class represents a folder which may contain files and sub-folders.

It provides the following services:

- get all files name in this folder.
- get a specific file.
- check whether a file exists.
- read content from a file.
- write content into a file.
- add a new file.
- delete a file.
- add a new sub-folder.

## WorkFolder

This class represents the current working directory. It extends the `Folder` class.

It provides the following services:

- get the UID of a file.
- checkout a file with a given commit.
- check whether we can checkout all files in the working directory.
- checkout all files with a given commit.

## BranchFolder

This class represents the `.gitlet/refs/heads` folder. It extends the `Folder` class.

It provides the following services:

- get all branches name.
- check whether we have a specific branch.
- get the HEAD UID of a branch.
- set the HEAD UID for a branch.
- delete a branch.
- get all history commits of a branch.

## BranchLatestFolder

This class represents the `.gitlet/branchesLatest` directory. It extends the `Folder` class.

It provides the following services:

-

## LogFolder

This class represents the `.gitlet/logs/refs/heads`  directory. It extends the `Folder` class.

It provides the following services:

- read log of a branch.
- write log to a branch.

## ObjectFolder

This class represents the current working directory. It extends the `Folder` class.

It provides the following services:

- get a object file using its UID.
- get bolb in a given commit.
- get commit using its UID.
- check whether we have a commit with the given UID.
- get all history commits of a commit.
- save a gitlet object.

## Repo

This class contains all folders, the current commit, the current branch and the stage.

It provides the folloing services using static methods:

- get/set the current commit.
- get/set the current branch.
- get the stage.
- get/set the head UID of the current branch.
- check if the current commit contains a file.
- check if a specific file in the current commit is the same with the file in the working directory.
- get all hisoty commits of the current commit.
- write log to the current branch.
- check if a specific file in the stage is the same with the file in the working directory. 