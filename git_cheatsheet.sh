# git

git init
git status
git add app.py
git rm --cached <file>    # to rm from staging area
git commit -m "comment" -a  # commits ALL (a)
git remote remove origin
git remote add origin  git@github.com:kklapec/test-repo.git #SSH (pub)
git push -u origin master  # git push --set-upstream origin master

git pull #download changes from remote repo

#### file  .gitignore # patterns to ignore
.DS_store
__pycache__/
*.pyc

git branch feature/feat1
git checkout feature/feat1


git checkout master  # go back to master
git merge feature/feat1  # bring changes from branch feat1

git branch -d feature/feat1  # DELETE branch
