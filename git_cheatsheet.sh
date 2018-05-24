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
git rm -r <folder>
git rm --cached <file>    # to rm from staging area

git clone ssh://user@host/path/to/repo.git
git commit -am "comment"  # commits ALL (a)
git remote remove origin
git remote add origin  git@github.com:kklapec/test-repo.git #SSH (pub)
git push -u origin master  # --set-upstream

git review master

git pull 					#download changes from remote repo
git pull origin <branch>

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

git fetch origin                # drop all local changed and commits
git reset --hard origin/master  # fetch latest history from server

git branch -d feature/feat1     # DELETE branch
git branch -d --force feature/feat1 # same as -D

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
