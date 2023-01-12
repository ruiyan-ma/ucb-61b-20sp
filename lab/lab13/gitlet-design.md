# Gitlet Design Document

**Author**: Shaw Ma

## Classes and Data Structures

### Bolbs
**This class stores files and the SHA code of each file.**

#### Fields
**1. File _file:** The file object stored in this bolb.  

**2. String _SHA:** The SHA code of this bolb.  

### Commit
**This class defines the components of a commit.**

#### Fields
**1. String _timestamp:** The timestamp of this commit.

**2. String _logMessage:** The log message of this commit.

**3. Commit _parent:** The reference to the primary parent of this commit.

**4. Commit _seParent:** The reference to the second parent of this commit. Only used when this commit is a merge of two parent commits.

**5. HashMap<String, String> _map:** The map of file names to bolb references.

**6. String _SHA:** The SHA code of this commit.

### Branch
**This class represents a branch, contains this branch's name and it's head pointer.**

#### Fields
**1. String _name:** The name of this branch.

**2. Commit _head:** The head pointer of this branch.

### Repository
**This class contains a map of branch names to the corresponding branch head pointer.**

#### Fields
**1. HashMap<String, Commit> _branchMap:** The map of branch names to commit references.

### Command
**This is the interface of commands.**

### InitCommand
**This is the init command class.**

### AddCommand
**This is the add command class.**

### CommitCommand
**This is the commit command class.**

### RmCommand
**This is the rm command class.**

### LogCommand
**This is the log command class.**

### GlogCommand
**This is the global-log command class.**

### FindCommand
**This is the find command class.**

### StatusCommand
**This is the status command.**

### CheckoutCommand
**This is the checkout command class.**

### BranchCommand
**This is the branch command class.**

### RmBranchCommand
**This is the rm-branch command class.**

### ResetCommand
**This is the reset command class.**

### MergeCommand
**This is the merge command class.**

### Dumpable

### DumpObj

### GitletException

### Utils
**This class contains some useful util functions for serialize and deserialize, IO read and write, etc.**

### Main
**This class is the main process of gitlet program. This class is responsible for analyzing the command and execute the corresponding method in each command class.**


## Algorithms

### Bolbs

### Commit

### Branch

### Repository

### Command

### InitCommand

### AddCommand

### CommitCommand

### RmCommand

### LogCommand

### GlogCommand

### FindCommand

### StatusCommand

### CheckoutCommand

### BranchCommand

### RmBranchCommand

### ResetCommand

### MergeCommand

### Dumpable

### DumpObj

### GitletException

### Utils
**This class contains some useful util functions for serialize and deserialize, IO read and write, etc.**

### Utils
##### SHA function
**1. sha1(Object... vals):** Returns the SHA-1 hash of the concatenation of VALS, which may be any mixture of byte arrays and Strings.

**2. sha1(List<Object> vals):** Returns the SHA-1 hash of the concatenation of the strings in VALS.

##### Delete file
**1. restrictedDelete(File file):** Deletes FILE if it exists and is not a directory.

**2. restrictedDelete(String file):** Deletes FILE if it exists and is not a directory.

##### Read and write contents to files
**1. readContents(File file):** Return the entire contents of FILE as a byte array.

**2. readContentsAsString(File file):** Return the entire contents of FILE as a String.

**3. writeContents(File file, Object... contents):** Write the result of concatenating the bytes in CONTENTS to FILE, creating or overwriting it as needed.

##### Read and write objects to files
**1. readObject(File file, Class<T> expectedClass):** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.

**2. writeObject(File file, Serilizable obj):** Write OBJ to FILE.

**3. plainFilenamesIn(File dir):** Returns a list of the names of all plain files in the directory DIR, in lexicographic order as Java Strings.

##### Other utils
**1. join(String first, String... others):** Return the concatenation of FIRST and OTHERS into a File designator.

**2. join(File file, String... other):** Return the concatenation of FIRST and OTHERS into a File designator.

**3. serialize(Serializable obj):** Returns a byte array containing the serialized contents of OBJ.

### Main
**This class is the main process of gitlet program. This class is responsible for analyzing the command and execute the corresponding method in each command class.**


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
