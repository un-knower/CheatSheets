git init
git init --bare			# you can NOT edit there, no working directory, treat as central repo
git status
git add app.py
git add docs/*.txt      # txt in docs dir
git add docs/           # all files in docs dir
git add "*.txt"         # all txt files in whole project
git add *.txt           # in current dir
git add --all           # adds all files
git add -A .            # adds everything recursively

git reset <file>        # remove from staging area ,   git reset -- <files>
git rm '*.txt'
git rm -r <folder>		# will remove folder(-r) file from git AND locally
git rm --cached <file>  	# to rm from staging area, keeping local
git rm --cached `git ls-files -i -X .gitignore`	# remove files from the repository based on your .gitignore without deleting them from the local file system
git ls-files -i -X .gitignore | xargs -I{} git rm --cached "{}"	# will include spaces in file names

git clone ssh://user@host/path/to/repo.git
git commit -am "comment"  # commits ALL (a)
git remote remove origin
git remote add origin  git@github.com:kklapec/test-repo.git #SSH (pub)
git push -u origin master  # --set-upstream
git push origin feature/feat1	# push branch to remote repo

git stash                   # save
git stash apply             # re-apply changes after pull
git diff --staged           # see what was staged
git diff --cached
git diff HEAD
git diff <source branch> <target branch>

git branch   feature/feat1      # both can be replaced with single 
git checkout feature/feat1      # checkout -b <new branch>

git checkout -- <file/target>   # discard changes in working directory, restore before changes, cancel all local changes

git checkout master  			# go back to master
git merge feature/feat1         # (while in master) bring changes from branch feat1 , also deleted files if in feat1

# To merge the latest changes from master into your branch, in this example named users/jamal/readme-fix:
git checkout users/jamal/readme-fix
git pull 					#download changes from remote repo
git pull origin <branch> --allow-unrelated-histories

fetch , which downloads the changes from your remote repo but does not apply them to your code (only for review)
merge , which applies changes taken from fetch to a branch on your local repo.
pull  , which is a combined command that = fetch + merge  (in Visual Studio - SYNC)

git fetch origin master         # 1. download latest code from repo (into hidden .git folder), drop all local changes and commits
git merge origin/master			# 2. merges into master branch what was stored in .git folder
git reset --hard origin/master  # (local branches only) reverts changes by moving a branch reference backwards in time to an older commit, like latest never happend
git reset --hard HEAD			# The --hard part of the command tells Git to reset the files to the state of the previous commit and discard any staged changes.
git reset HEAD~1

git revert <id>					# revert to given ID, undo changes made in commit ID
git commit 						# commit those reverted files

git branch -d feature/feat1     # DELETE branch locally
git push origin --delete feature/feat1 # DELETE branch remotely
git branch -d --force feature/feat1 # same as -D


git branch -f master HEAD~3		# moves (by force) the master branch to three parents behind HEAD.


git show HEAD
git log --summary
git log --pretty=oneline
git log --name-status
git log --graph --oneline --decorate --all
git log -p HEAD (exclude) .. FETCH_HEAD (include)

#### file  .gitignore # patterns to ignore
.DS_store
__pycache__/
*.pyc

git config --global user.name "NAME"
git config --global user.email "email@com"
git config --global color.ui true
git config --global --edit
git config format.pretty oneline
git config --get remote.origin.url
git config -l
git config --amend --reset-auth


1. git pull origin develop
2. git checkout develop
3. git merge some-feature
4. git push
5. git branch -d some-feature

# preparing for official release
1. git checkout -b release-0.1 develop
2. git checkout master
3. git merge release-0.1
4. git push
5. git checkout develop
6. git merge release-0.1
7. git push
8. git branch -d release-0.1

# to rebase
git pull
git checkout feature
git rebase master
git push -f origin feature # optional