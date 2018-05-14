git init
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

git commit -am "comment"  # commits ALL (a)
git remote remove origin
git remote add origin  git@github.com:kklapec/test-repo.git #SSH (pub)
git push -u origin master  # --set-upstream

git pull 		#download changes from remote repo

git stash                   # save
git stash apply             # re-apply changes after pull
git diff --staged           # see what was staged
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

git log --summary
git log --pretty=oneline
git log --name-status

#### file  .gitignore # patterns to ignore
.DS_store
__pycache__/
*.pyc

git config --global color.ui true
git config format.pretty oneline