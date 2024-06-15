# Gitlet Design Document

## Objects

### Blob

Represents a Binary Large Object. Its name is the SHA1 of the file, and content is the content of file after being serialized.

#### variables:
- File blobFile : the blob file `.gitlet/objects/[sha1]`.
- File storedFile : the file stored in the blob file.
- String sha1 : the sha of the stored file, as the name of blob file.

### Branch

Represents a branch in the project.

#### variables:
- File branchFile : The file storing a branch in .gitlet/heads. Its content is the id of the head commit of this branch.
- String branchName : The name of this branch, as the file name of branch file.
- String headCommitId : The head commit id of this branch, as the content of branch file.

### Commit

Represents a gitlet commit object.

#### variables:
- String message
- Date timestamp
- Commit[] parents : Length can be 0, 1, 2. This commit is initial commit if length is 0.
- id : The sha1 code of this commit, same as the commit id of this Commit, decided by its other variables.
- HashMap<String, String> tracked : Casts the tracked files' name to their sha1

### Head

Represents the HEAD of the project, storing the branch name of the head.

#### variables:

### Staging

Represents the Staging Area, storing files being added and removed.

#### variables:
- HashMap<String, String> additional : Casting the filename of additional files in staging area to their SHA1 code.
- HashSet<String> removal : The set of the filename of removal files in staging area

## Operations

### gitlet init

#### Objects involved:
- Branch
- Commit
- Head

### gitlet add

#### Objects involved:
- Blob
- Staging

### gitlet commit

#### Objects involved:
- Branch
- Commit
- Staging