# Gitlet

The code in this project is devided into two layers:

- the command layer (upper layer): run command and execute logical code.
- the repo layer (lower layer): provide CRUD data services for the command layer.

There are three packages in this project: commands, objects and repo.

## Commands Package

This package represents the command layer. It contains all command classes.

Every command class will do the following two things:

1. check operands of this command;
2. run logical code using the services provided by repo package.

## Objects Package

This package contains all object (entities) classes used in Gitlet.

## Repo Package

This package represents the repo layer. It contains all folder classes which represents different folders we will use in Gitlet. It also contains the `Repo` class as a connector between the commands package and the repo package.

If any code in the commands package wants to read or write data, either it should use methods in `Repo` , or it should access to a folder object in `Repo` and use methods from that folder object. 