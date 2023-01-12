# Gitlet Design Document

**Author**: Ryan Ma

## Classes and Data Structures

### Repo
**This class is responsible for all operations related to the repository. It's
also responsible for the lazy policy, read current branch, current commit and
stage only when needed.**

### Work
**This class is responsible for all operations related to the current working
directory.**

### Path
**This class is responsible for finding a file's absolute path, including find
path for files in .gitlet folder and files in current working directory. This
class needs to handle short UID issue.**

### GitletObject
**This class is the abstract class of bolbs and commits.
It's responsible for creating dirs and files, reading and writing operations,
and other collective functions which bole and commit will both have.**

### Bolbs
**This class stores the content of a file. The content of a file at a particular
time is called bolb. Bolbs objects are stored in .gitlet/objects directory.**

### CommitData
**This class defines the components of a commit. All pointers are instead with
UID.**

### Stage
**This class represents staging area in gitlet.
It contains an addition area and a removal area. This class object will stored
in .gitlet/index file.**

### Command
**This is the abstract class of all commands. It contains some collective
functions that all commands will have.**

#### Init
**This is the init command class.**

#### Add
**This is the add command class.**

#### Commit
**This is the commit command class.**

#### Rm
**This is the rm command class.**

#### Log
**This is the log command class.**

#### Global-log
**This is the global-log command class.**

#### Find
**This is the find command class.**

#### Status
**This is the status command.**

#### Checkout
**This is the checkout command class.**

#### Branch
**This is the branch command class.**

#### RmBranch
**This is the rm-branch command class.**

#### Reset
**This is the reset command class.**

#### Merge
**This is the merge command class.**

### Dumpable

### DumpObj

### GitletException

### Utils
**This class contains some useful util functions for serialize and deserialize,
IO read and write, etc.**
##### SHA function
**1. sha1(Object... vals):** Returns the SHA-1 hash of the concatenation of
VALS, which may be any mixture of byte arrays and Strings.
**2. sha1(List<Object> vals):** Returns the SHA-1 hash of the concatenation of
the strings in VALS.

##### Delete file
**1. restrictedDelete(File file):** Deletes FILE if it exists and is not a
directory.
**2. restrictedDelete(String file):** Deletes FILE if it exists and is not a
directory.

##### Read and write contents to files
**1. readContents(File file):** Return the entire contents of FILE as a byte
array.
**2. readContentsAsString(File file):** Return the entire contents of FILE as a
String.
**3. writeContents(File file, Object... contents):** Write the result of
concatenating the bytes in CONTENTS to FILE, creating or overwriting it as
needed.

##### Read and write objects to files
**1. readObject(File file, Class<T> expectedClass):** Return an object of type
T read from FILE, casting it to EXPECTEDCLASS.
**2. writeObject(File file, Serilizable obj):** Write OBJ to FILE.
**3. plainFilenamesIn(File dir):** Returns a list of the names of all plain
files in the directory DIR, in lexicographic order as Java Strings.
**4. plainFilenamesIn(String dir):** Returns a list of the names of all plain
files in the directory DIR, in lexicographic order as Java Strings.

##### Other utils
**1. join(String first, String... others):** Return the concatenation of FIRST
and OTHERS into a File designator.
**2. join(File file, String... other):** Return the concatenation of FIRST and
OTHERS into a File designator.
**3. serialize(Serializable obj):** Returns a byte array containing the
serialized contents of OBJ.

### Main
**This class is the main process of gitlet program. This class is responsible
for analyzing the command and execute the corresponding method in each command
class.**

## Persistence
**java gitlet.Main --command**
### Command excution steps
In order to maintain the consistency between two gitlet command, we need to retrieve the objects we stored in last command call, and save everything after executing the command. To do this:

1. First, search for the saved files in the working directory and read and deserialize the objects that we saved in them. This can be done with readObject method from Utils class.

2. Second, execute the command.

3. Finally, serialize commits, branches and repository objects into bytes and then write them to corresponding files. This can be done with writeObject method from the Utils class.
When something go wrong and the execution abort, exit the program without saving objects, so the files stores objects will not be changed.

### Other notes
Make sure that our commit, branch and repository class implement the Serializable interface.

Make "commits", "branches" and "repository" subdirectories in .gitlet directory to store corresponding objects respectively. I will use the SHA code of each object as their unique file names.

