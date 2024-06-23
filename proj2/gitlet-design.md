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

### gitlet add FILENAME

#### Objects involved:
- Blob
- Staging

### gitlet commit MESSAGE

#### Objects involved:
- Branch
- Commit
- Staging

### gitlet rm FILENAME

#### Objects involved:
- Commit
- Staging

### gitlet log

#### Objects involved:
- Commit
- Head

### gitlet global-log

#### Objects involved:
- Commit

### gitlet find MESSAGE

#### Objects involved:
- Commit

### gitlet status

- Branch
  - Branch
  - Head
- Staged Files
  - in Staging.additionalMap
- Removed Files
  - in Staging.removalSet
- Modifications Not Staged For Commit
  - Tracked in the current commit, changed in the working directory, but not staged; or
  - Staged for addition, but with different contents than in the working directory; or
  - Staged for addition, but deleted in the working directory; or
  - Not staged for removal, but tracked in the current commit and deleted from the working directory.
- Untracked Files
  - not in Staging
  - not tracked in the last commit

### gitlet checkout

#### gitlet checkoutFile

##### variables involved:
- Blob
- Commit

#### gitlet checkoutBranch
- Blob
- Branch
- Head

### gitlet branch

#### variables involved:
- Branch

### gitlet rm-branch

#### variables involved:
- Branch

### gitlet reset

#### variables involved:
- Branch
- Head
- Blob

### gitlet merge
1. only modified in given branch
    - changed to the version in the given branch
1. only modified in current branch 
    - remain
1. removed in both branch but in wd
    - absent
    - left alone
    - not tracked nor staged
1. tracked in both branch with same SHA 
    - left unchanged
1. tracked in both commit with different SHA
1. 
    