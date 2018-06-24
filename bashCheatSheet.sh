#!/bin/bash
# Basic Shell Programming.
read -t "$TIMEOUT" -s -n input             # awaits user input for 3 seconds, -s = nothing displayed, -n = same line. Echo -e allows \t\n
input=$(sed "s/[;\`\"\$\' ]//g" <<< $input)  # if you do not want quotes, then escape it
input=$(sed 's/[^0-9]*//g' <<< $input)       # For reading number, then you can escape other characters
varname=value                # defines a variable
varname=value command        # defines a variable to be in the environment of a particular subprocess
echo $varname                # checks a variable's value
     echo ${var}name         # dla var={1..3} result will be {1..3}name
eval echo ${var}name         # result will be 1name, 2name, 3name
echo AAA      BBB            # AAA BBB
echo "AAA       BBB"         # AAA      BBB
$sentence = "AAA      BBB"
echo $sentence               # AAA BBB
echo "$sentence"             # AAA      BBB
$$                  # process ID of the current shell
$!                  # process ID of the most recently invoked background job
$?                  # displays the exit status of the last command
$_                  # final arg of last executed foreground cmd
$0 - script name    # The function refers to passed arguments by position (as if they were positional parameters), that is, $1, $2, and so forth.
$@                  # expands to a list, is equal to "$1" "$2"... "$N",   for arg in "$@"   , which is = to   for arg do ; echo .... ; done
$*                  # expands to single string when quoted "$*"
$#                  # number of positional parameters, NOT including $0
$-                  # options given to shell
%%                  # current job (the most recently executed background pipeline qualifies for that), i.e.  cmd1 | cmd2 &   (both!!, $! gives only one last)

set `echo $line`    # will split line for you to params --> $1, $2, $3
set -- 1 "2 two" "3 three tres"; echo $# ; set -- "$*"; echo "$#, $@")
set -- 1 "2 two" "3 three tres"; echo $# ; set -- "$@"; echo "$#, $@")
set -- 01234567890abcdefgh         # echo ${1:7:2}  =  78           
set -- foo bar baz       # echo "${@:(-1)}"  = baz      echo "${@:(-2):1}"  = second-to-last bar
set -- "$@" -f "$somefile"  ; ..... foocommand "$@"   # Add an option to our dynamically generated list of options
### ARRAYS
array[0]="val"               # several ways to define an array
array=([2]="val" [0]="val" [1]="val")
array("val" "val" "val")
photos=(~/"My Photos"/*.jpg)
files=(*)                          # avoid using ls,  na poczatku  shopt -s nullglob dotglob,  na koncu   shopt -u
unset 'nums[3]'         # removing element from array
files+=("$REPLY")       # adding element to array,   if index is in order:   arr[++i]="new item"    
for x in "${arr[@]}"; do echo "next element is '$x'" ; done            # quick dump:   printf "%s\n" "${arr[@]}"
"${array[i]}"           # displays array's value for this index. If no index is supplied, array element 0 is assumed
"${#array[i]}"          # to find out the length of any element in the array
"${#array[@]}"          # to find out how many values there are in the array, total number of elems for ((i=0; i<${#arr[@]}; i+=2)); do
array=("${array[@]}")   # This re-creates the indices, reindexing, then adding new item:    array=("${array[@]}" "new item")
"${#array[*]}"          # (number of?) elements in the array 
"${array[*]}"           # merge all elements in single string (one line), can separate with IFS   ( IFS=,; echo "Today's contestants are: ${names[*]}" )
"${array[@]}"           # new line after each element, expand all array elements
"${!arrayname[@]}"      # expands to a list of the indices of an array, for i in "${!first[@]}"; do echo "${first[$i]} ${last[$i]}"

declare -A fullNames    # ASSOCIATIVE ARRAYS, called in Python = DICTIONARY
fullNames=( ["lhunath"]="Maarten Billemont" ["greycat"]="Greg Wooledge" )
echo "Current user is: $USER.  Full name: ${fullNames[$USER]}." #for user in "${!fullNames[@]}" ; do echo "User: $user, full name: ${fullNames[$user]}."; done
mapfile -t lines <myfile      # replacement:    while IFS= read -r; do lines+=("$REPLY"); done <file ; [[ $REPLY ]] && lines+=("$REPLY")
mapfile -t lines < <(cmd)     # mapfile can't handle NUL-delimited files from find -print0, other GNU null separator:: sort -z, find -print0, xargs -0, grep accepts either -Z or --null, bash shell read command is -d '' 
declare -p <variable>   # list content of array
declare -a              # the variables are treaded as arrays
declare -f              # uses funtion names only
declare -F              # displays function names without definitions
declare -i              # the variables are treaded as integers
declare -r              # makes the variables read-only
declare -x              # marks the variables for export via the environment, it will be inherited by any child process

# http://www.gnu.org/software/bash/manual/bashref.html#Shell-Parameter-Expansion
${var:-word}          # if var exists and isn't null, return its value; otherwise return word
${var:=word}          # if var exists and isn't null, return its value; otherwise set it word and then return its value
${var:?message}       # if var exists and isn't null, return its value; otherwise print var, followed by message and abort the current command or script
${var:+word}          # if var exists and isn't null, return word; otherwise return null   grep -cP '([Ss]pecial|\|?characters*)$' ${1:+"$1"}
${var:offset:length}  # performs substring expansion. It returns the substring of $varname starting at offset and up to length characters

${variable#pattern}   # if the pattern matches the beginning of the variable's value, delete the shortest part that matches and return the rest
# m1=${x#???}         # Remove the first 3 characters, case ${1#-} in   .... remove leading hyphen
${variable##pattern}  # if the pattern matches the beginning of the variable's value, delete the longest part that matches and return the rest
#readonly progname=${0##*/}   # strip leading pattern that matches */ (be greedy)
${variable%pattern}   # if the pattern matches the end of the variable's value, delete the shortest part that matches and return the rest
${filename%.*}        # remove filename extension      for file in *.JPG *.jpeg ; do mv -- "$file" "${file%.*}.jpg"
# "Directory of file: ${path%/*}";                          strip trailing pattern matching /* (non-greedy)
${variable%%pattern}  # if the pattern matches the end of the variable's value, delete the longest part that matches and return the rest
# ex.   AR=${0%%\//} ## expands positional parameter 0, removing all text followingthe last '/'
${variable/pattern/string}   # the longest match to pattern in variable is replaced by string. Only the first match is replaced
${variable//pattern/string}  # the longest match to pattern in variable is replaced by string. All matches are replaced
${parameter/#pat/string}     # As above, but matched against the beginning. Useful for adding a common prefix with a null pattern: "${array[@]/#/prefix}".
${parameter/%pat/string}     # As above, but matched against the end. Useful for adding a common suffix with a null pattern. 

${#varname}                  # returns the length of the value of the variable as a character string

foobar=/accounts/facstaff/bhatias/scripts/hello.world
echo ${foobar##/*/}
echo ${foobar%%.*}
echo ${foobar/world/exe}
echo "${word#?}"   =  echo "${word:1}"    # if word=match, then it prints    atch
x=${x##+([[:space:]])}; x=${x%%+([[:space:]])} # To trim leading and trailing whitespace from a variable

# Extended Globals Globs, turned off by default, to turn on:    $ shopt -s extglob
*(patternlist)               # matches zero or more occurences of the given patterns
+(patternlist)               # matches one or more occurences of the given patterns
?(patternlist)               # matches zero or one occurence of the given patterns
@(patternlist)               # matches exactly one of the given patterns     elif [[ $LANG = @(C|POSIX) ]]; then
!(patternlist)               # matches anything except one of the given patterns
# Let’s say you want to process every file except files ending by a “V”, just type
for f in !(*V); do echo ":${f}:";done

# FUNCTIONS
functname() {
  shell commands
}
unset -f functname  # deletes a function definition
declare -f          # displays all defined functions in your login session

sum() {
    echo "$1 + $2 = $(($1 + $2))"
}
sum "$1" "$2"            #function using parameters inside script

# Flow Control
cmd1 && cmd2              # and operator, runs cmd2 if cmd1 successful, otherwise skip
echo true && echo false   # true, false
cmd1 || cmd2              # or operator, rund cmd2 if cmd1 not successful, otherwise skip
echo true || echo false   # true
cmd1 ; cmd2               # run cmd1, then cmd2
cmd1 & cmd2               # run cmd2 without waiting for cmd1 to finish
(cmd)                     # run cmd in subshell
ls -l $(which cp)     ==     ls -l `which cp`

test 5 -gt 6 ; echo $?   # 1 false, 0 true

!                         # not operator            if [ ! -w $filename ] then echo "ERROR: Cannot write to $filename"
-a                        # and operator inside a test conditional expression
-o                        # or operator inside a test conditional expression
# strings
str1=str2                 # str1 matches str2
str1!=str2                # str1 does not match str2
str1<str2                 # str1 is less than str2
str1>str2                 # str1 is greater than str2
-n str1                   # str1 is not null (has length greater than 0)
-z str1                   # str1 is null (has length 0)
# files/directories
-a file                   # file exists
-d file                   # file exists and is a directory
-e file                   # file exists; same -a
-f file                   # file exists and is a regular file (i.e., not a directory or other special type of file)
-L file                   # file is symbolic link
-r -w -x file             # you have read/write/execute permission
-s file                   # file exists and is not empty
-N file                   # file was modified since it was last read
-O file                   # you own file
-G file                   # file's group ID matches yours (or one of yours, if you are in multiple groups)
file1 -nt file2           # file1 is newer than file2
file1 -ot file2           # file1 is older than file2
# numbers
-lt                       # less than                   if [ $# -gt 1] then echo "ERROR, should have 0 or 1 command params"
-le                       # less than or equal
-eq                       # equal
-ge                       # greater than or equal
-gt                       # greater than
-ne                       # not equal

if [ -s file.txt ]      # if test \( -r $1 -a -r $2 \) -o \( -r $3 -a -r $4 \)
then
  statements
[elif condition
  then statements...]
[else
  statements]
fi

for x := 1 to 10 do        # for i in {1..10} do            # for i in `seq 1 10` do
begin
  statements             # let i--   same as   ((i--))         i=$(($i+1))
end

for name in $list  #  for name in `echo $list`    # for file in ./* ./.[!.]* ./..?* ; do ; if [ -e "$file" ] ; then  # includes hidden files
do
  statements that can use $name                        # e.g.   echo -n "$file "; wc -l < $file
done                                                   # last=$(wc -l < "$file") # count number of lines

for (( start = 1 ; start <= $max ; start++ ))
do
  statements...
done 2> errors

case expression in
  pattern1 ) statements ;;                        # [[:lower:]] | [[:upper:]] ) echo "You typed the letter $character"
  pattern2 ) statements ;;                        # while getopts ":af:z" option; do
  * ) statements ;;  # all the rest               #    case $option in
esac                                              # f) echo received -f with $OPTARG               :) echo "option -$OPTARG requires arg"

select name [in list]
do
  statements that can use $name
done

# READ PARAMETERS
while (( $# )) ; do                     # while :    (colon means infinite loop), must use break inside
     echo You gave me $1                # starts from $1, then $2, $3 etc
     shift                              # while sleep 60; do ; if who | grep -s $1 ; then ; echo $1 is logged; fi
done

# READ FILE LIST IN FOLDER
while read f; do              # FILES=$(ls -1)
     echo "file=${f}"         # for f in $FILES; do echo ${f}; done
done < <(ls -l /tmp)

# This is a safe way of parsing a command's output into strings
while read -r -d ''; do      # while IFS= read -r -d $'\0' file; do
    files+=("$REPLY")         # arr[i++]=$REPLY
done < <(find /foo -print0)   # produce NUL-delimited streams       printf "%s\0" "${myarray[@]}" > myfile

while IFS= read -r line; do
  printf '%s\n' "$line"        # avoid commented lines   [[ $line = \#* ]] && continue
done <<EOF                    # or   <<< "$var"
$var
EOF

while IFS= read -r -d '' file; do
echo $(basename "$file")
array+=${file}
done < <(find . -type f -print0)

find . -type f -print0 | while IFS="" read -r -d "" file ; do
   echo `basename "$file"`
done

for f in "$@"
  do
    script2.sh "$f"
  done
[ $# -eq 0 ] && ls | while read f
  do
    script.sh "$f"
  done

  
for i in 1 2 3 4 5
do
	head -`expr $i \* 20000` u.data | tail -20000 > tmp.$$
	sort -t"	" -k 1,1n -k 2,2n tmp.$$ > u$i.test
	head -`expr \( $i - 1 \) \* 20000` u.data > tmp.$$
	tail -`expr \( 5 - $i \) \* 20000` u.data >> tmp.$$
	sort -t"	" -k 1,1n -k 2,2n tmp.$$ > u$i.base
done


# READ FILE CONTENT
while read -r line;do echo "$line";done < file
while IFS=$'\n' read -rd $'\n' -a lines; do echo $lines; done < file


until condition; do    # e.g.   until who | grep -s $1 ; do ; sleep 60 ; done ;   if [ $? ] ; then; echo $1 is logged ; fi
  statements
done

# 3. Command-Line Processing Cycle.
# The default order for command lookup is functions, followed by built-ins, with scripts and executables last.
# There are three built-ins that you can use to override this order: `command`, `builtin` and `enable`.

command  # removes alias and function lookup. Only built-ins and commands found in the search path are executed
builtin  # looks up only built-in commands, ignoring functions and commands found in PATH
enable   # enables and disables shell built-ins
eval     # takes arguments and run them through the command-line processing steps all over again

# Input/Output Redirectors.
< file     # takes standard input from file  #e.g.     sort < file_list.txt > sorted_file_list.txt
cmd1 <(cmd2) # output of cmd2 as file input to cmd1
>|file     # forces standard output to file even if noclobber is set
n>|file    # forces output to file from file descriptor n even if noclobber is set
<> file    # uses file as both standard input and standard output
n<>file    # uses file as both input and output for file descriptor n
<<EOF      # here-document, e.g.   echo <<EOF , <then a lot of text> and last line  EOF, label
<<\EOF     # here but with no $, `...` substitution, same as <<'EOF'
EOF
cmd <<< string # same as echo string | cmd, but using one less process running,     { cmd1 <<<'my input'; cmd2; } >someFile
# Changing the the "<<" to "<<-" causes bash to ignore the leading tabs (but not spaces) in the here script
n>&        # duplicates standard output to file descriptor n
n<&        # duplicates standard input from file descriptor n                         echo "error to stderr" >&2
n>&m       # file descriptor n is made to be a copy of the output file descriptor,  2>&1   stderr to same place as stdout
n<&m       # file descriptor n is made to be a copy of the input file descriptor; Merge input from stream n with stream m
&>file     # directs standard output and standard error to file
cmd > x.out 2>&1  # same as  cmd &> x.out     # same as   cmd >& x.out
cmd >> x.out 2>&1 # append output and error to file
<&-        # closes the standard input      stdin <&-  
>&-        # closes the standard output     stderr 2>&-
n>&-       # closes the ouput from file descriptor n
n<&-       # closes the input from file descripor n
# Grouped command list
(cmd1 < input ; cmd2 < input)    > output &       # child/sub shell
{cmd1 < input ; cmd2 < input ; } > output &       # current shell
$ { echo "Starting at $(date)"; rsync -av . /backup; echo "Finishing at $(date)"; } >backup.log 2>&1    # bakup opened just once
$ [[ -f $CONFIGFILE ]] || { echo "Config file $CONFIGFILE not found" >&2; exit 1; }
lsof +f g -ap $BASHPID -d 0,1,2         # checking current settings

exec 9<file    # makes File Descriptor = 3 as input file
1) while read -u 9 line;do echo "$line"; read -p "Press any key" -n 1;done    # read file and ask user to press key after line
2) while read -r line <&9; do cat > ignoredfile ; echo $line ; done 9< "$file"  # or with -d '' parameter .... done 9< <(find . -print0)
exec 9<&-

#pipe version
mkfifo mypipe
find . -print0 > mypipe &
while IFS="" read -r -d "" file <&4 ; do
  COMMAND "$file" # Use quoted "$file", not $file, everywhere.
done 4< mypipe

mkfifo matrix
if [[ $1 == -c ]]
then
gzip < matrix > matrix.gz &
else
gunzip < matrix.gz > matrix &
fi
rm matrix

GNUPLOT_PIPE=/tmp/gnuplot_pipe
[[ -e $GNUPLOT_PIPE ]] || mkfifo $GNUPLOT_PIPE         # If the pipe doesn't exist, create it
cat > $GNUPLOT_PIPE << EOF &
  unset key
  plot '$DATA_FILE' using :1 with lines
replot '$DATA_FILE' using :2 with lines
EOF
gnuplot --persist < $GNUPLOT_PIPE       # Start gnuplot and pull stdin from the pipe
rm $GNUPLOT_PIPE                        # Clean up pipe on exit

# Process Handling.
# To suspend a job, type CTRL+Z or CTRL+Y  while it is running. You can also suspend a job with CTRL+Y, 
# this is slightly different from CTRL+Z in that the process is only stopped when it attempts to read input from terminal.
myCommand &  # runs job in the background and prompts back the shell
jobs         # lists all jobs (use with -l to see associated PID)
fg           # brings a background job into the foreground
fg %+        # brings most recently invoked background job
fg %-        # brings second most recently invoked background job
fg %N        # brings job number N
fg %string   # brings job whose command begins with string
fg %?string  # brings job whose command contains string
kill -l      # returns a list of all signals on the system, by name and number   # kill <PID> or   kill <%job>
kill -0 "$mypid" && echo "My process is still alive."   #sleep 10 & mypid=$!  http://mywiki.wooledge.org/ProcessManagement
kill    "$mypid" ;  echo "I just asked my process to shut down."
ps -u $USER -o ppid,pid,user,etime,pcpu,pmem,args,comm,size --forest --width=2000
wait                # waits until all background jobs have finished
if ps -ef | grep -v grep | grep -q foo; then
if ps -ef | grep -v grep | grep "$(basename "$0")" | wc -l > 1 ; then # this is bad to use!
if ps -ef | grep '[f]oo'; then

sleep 1000; CTRL+Z ; bg ;  disown <PID|JID>    # removes the process from the list of jobs
nohup sleep 1000 &
nohup ${SHELL} -c 'complex-command-line'
export -f func           # function execution
nohup ${SHELL} -c 'command line invoking func'
nohup command/script.sh &       # If you want the SIGQUIT signal to also be ignored, you can run nohup in the background
nohup bash -c "(time ./script arg1 arg2 > script.out) &> time_n_err.out" &
# stdout from the script gets written to script.out, while stderr and the output of time goes into time_n_err.out.
nohup sh -c 'wget "$0" && wget "$1"' "$url1" "$url2" > /dev/null &
ssh my_server "bash -c 'source load_module jdk; source load_module jabref; java_exe=\$(which java); jabref_exe=\$(which jabref); jabref_dir=\$(echo \${jabref_exe%/bin/jabref});eval \$(java -jar \$jabref_dir/jabref.jar $1)'" &

# script will run as daemon in the background when parameter specified
case "$1" in
    -d|--daemon)
        $0 < /dev/null &> /dev/null & disown
        exit 0
        ;;
    *)
        ;;
esac
# do stuff here, normal commands


If you now start your script with --daemon as an argument, it will restart itself detached from your current shell.

# screen for background stuff
screen -d -m -S mybackgroundjob /usr/local/bin/somescript.sh


watch -n 5 'ntpq -p'  # Issue the 'ntpq -p' command every 5 seconds and display output
watch -n.1 pstree -Uacp $$	# Display a changing process subtree
ps -e -o pid,args --forest	# List processes in a hierarchy
ps -e -o pcpu,cpu,nice,state,cputime,args --sort pcpu | sed '/^ 0.0 /d'	# List processes by % cpu usage
ps -e -orss=,args= | sort -b -k1,1n | pr -TW$COLUMNS	# List processes by mem (KB) usage. See also ps_mem.py
ps -C firefox-bin -L -o pid,tid,pcpu,state	          # List all threads for a particular process
ps -p 1,$$ -o etime=	# List elapsed wall time for particular process IDs

grep <what> <where>
grep -i  (case insensitive),  grep -r  (recursive),    grep -v  (inverted),     grep -o (show matched part of file only)
grep -n  (show line number),  grep -q  (quiet mode)    grep --color <word>
grep -A1 -B2 # show 1 line after and results and 2 lines before results.    -C3  = before and after 3 lines
grep '\bEXACT\b' file.txt   ==    grep -w EXACT file.txt

alias nocomment='grep -Ev '\''^(#|$)'\'''
alias szukaj='grep -i --color ' # WHAT /var/log/where.log'
alias ps2='ps -ef | grep -v $$ | grep -i '
alias psg='ps -Helf | grep -v $$ | grep -i -e WCHAN -e ' # with header
alias psme='ps -ef | grep $USER --color=always '

result = $[10+20]
result = `expr $1 \* $2 + $3`
echo $((2 + 2))   # number=$((2+2))
echo $(($((5**2)) * 3))  == echo $(((5**2) * 3))
number=$(echo "scale=4; 17/7" | bc)          # 2.4285
z=$(echo "scale = 3; $x/$y" | bc)            # 2.666 

alias today='date +"%A, %B %-d, %Y"'   Friday, January 6, 2017             $(date +"%x %r %Z")    01/06/2017 07:08:45 AM PST
date --date='@2147483647'                    # Convert seconds since the epoch (1970-01-01 UTC) to date
[ $(date -d '12:00 today +1 day' +%d) = '01' ] || exit	# exit a script unless it's the last day of the month
err() {   echo "[$(date +'%Y-%m-%dT%H:%M:%S%z')]: $@" >&2   }       # err "unable to do sth"

#measure script running time
SECONDS=0
sleep 10
duration=$SECONDS
echo "$(($duration / 60)) minutes and $(($duration % 60)) seconds elapsed."
ELAPSED="Elapsed: $(($SECONDS / 3600))hrs $((($SECONDS / 60) % 60))min $(($SECONDS % 60))sec"

START=$(date +%s.%N)
sleep 1
END=$(date +%s.%N)
DIFF=$(echo "$END - $START" | bc)

# to quickly go to a specific directory
cd; nano .bashrc
> shopt -s cdable_vars
> export websites="/Users/mac/Documents/websites"
source .bashrc
cd websites

# Debugging Shell Programs
tr -d '\r' < myscript > tmp && mv tmp myscript         # remove \r\n (windows carriage return) from end of lines
exec 2>myscript.errors                  # add at the beginning of script to redirect all command errors to file
exec > log.txt 2>&1                     # whole output and errors go to log.txt
trap '(read -p "[$BASH_SOURCE:$LINENO] $BASH_COMMAND?")' DEBUG   # asks before each cmd to proceed, put where you want to start debug
bash -n scriptname  # don't run commands; check for syntax errors only
set -o noexec       # alternative to above (set option in script)
bash -v scriptname  # echo commands before running them
set -o verbose      # alternative to above (set option in script)
bash -x scriptname  # tracing, echo commands after command-line processing        alt. #!/bin/bash -x
set -o xtrace       # alternative (set option in script)                          alt. set -x  (to turn ON), set +x to OFF
set -o nounset      # set -u     wyswietla error Unbound Variable      # set +u   nie wyswietla
set -o noclobber    # set -C     prevents pipelines e.g. echo > file, can be overridden with echo >| file
set +H or set +o histexpand  # disables history expansion (allows to execute    echo "hello !" )
set -e              # stopping on any error
set -m              # enable job control
set -f              # prevent glob expansion
#!/bin/bash -   (or --)   shell won't accept any options
oldSetOptions=$(set +o)             # Save shell option settings         then you can do set -f, etc.
eval "$oldSetOptions" 2> /dev/null  # Restore shell option settings

# TRAP, catching interruptions    http://mywiki.wooledge.org/SignalTrap
SIGKILL 9      SIGTERM 15 (kill, no params)     SIGINT 2 (CTRL+C)    SIGQUIT 3 (CTRL+\)    SIGSTP  (CTRL+Z)    SIGHUP 1 (CTRL+D)
trap cmd sig1 sig2  # executes a command when a signal is received by the script
trap "" sig1 sig2   # ignores that signals
trap - sig1 sig2    # resets the action taken when the signal is received to the default
trap 'echo $varname' EXIT  # useful when you want to print out the values of variables at the point that your script exits

function errtrap {
  exitstatus=$?
  echo "ERROR line $1: Command exited with status $exitstatus."
}
trap 'errtrap $LINENO' ERR  # is run whenever a command in the surrounding script or function exists with non-zero status 

function dbgtrap {
  echo "badvar is $badvar"
}
trap dbgtrap DEBUG  # causes the trap code to be executed before every statement in a function or script
# ...section of code in which the problem occurs...
trap - DEBUG  # turn off the DEBUG trap

function returntrap {
  echo "A return occured"
}
trap returntrap RETURN  # is executed each time a shell function or a script executed with the . or source commands finishes executing

TEMP_FILE=$TEMP_DIR/printfile.$$.$RANDOM
PROGNAME=$(basename $0)
trap "rm -f $TEMP_FILE; exit" SIGHUP SIGINT SIGTERM   # deletes file if stopped by user
trap `rm -f tmp.$$; exit 1` 1 2 15

tf=/tmp/tf.$$                      # DFOUT=/tmp/${0##*/}.$$.tmp 
cleanup() {  rm -f $tf  }
trap "cleanup" EXIT            # runs cleanup when exit,    signal 0 - exit normally


### Shortcuts
CTRL+A  # move to beginning of line               CTRL+E  # moves to end of line
CTRL+B  # moves backward one character            CTRL+F  # moves forward one character
CTRL+C  # halts the current command
CTRL+D  # deletes one character backward or logs out of current session, similar to exit
CTRL+G  # aborts the current editing command and ring the terminal bell
CTRL+J  CTRL+M  # RETURN
CTRL+K  # deletes forward to end of line          CTRL+U  # kills backward from point to the beginning of line
CTRL+L  # clears screen and redisplay the line
CTRL+N  # next line in command history            CTRL+P  # previous line in command history
CTRL+O  # RETURN, then displays next line in history file
CTRL+R  # searches backward                       CTRL+S  # searches forward
CTRL+T  # transposes two characters
CTRL+V  # makes the next character typed verbatim
CTRL+W  # kills the word behind the cursor
CTRL+X  # lists the possible filename completefions of the current word
CTRL+Y  # retrieves (yank) last item killed
CTRL+Z  # stops the current command, resume with fg in the foreground or bg in the background
!!      # repeats the last command
!abc    # repat last command starting with abc
!$  =  ALT + .   =   ESC + .    # arguments of last command
!^      # first argument of last command
!*      # all arguments of last command                # repeat last command with arguments  !!:*
!:2     # 2nd argument of last command  i.e. echo 111 222 333       echo !:2  = 222,   wich path echo Head: !$:h  Tail: !$:t
^abc^xxx  # run last command replacing first occurrence abc with xxx             !!:gs/ehco/echo/
^y        # removes y from previous command   e.g.  grep rooty /etc/passwd
^abc^xxx^:&  # run last command replacing all occurences of abc with xxx
!#:1      # reuse of second word (command is a 0),eg. mv report.txt $(date +%F)-!#:1  -->  mv report.txt $(date +%F)-report.txt

# File Commands.
ln -s <filename> <link>       # creates symbolic link to file
genscript                     # converts plain text files into postscript for printing and gives you some options for formatting

# SSH, System Info & Network Commands.
ssh user@host            # connects to host as user
ssh -p <port> user@host  # connects to host on specified port as user
ssh-copy-id user@host    # adds your ssh key to host for user to enable a keyed or passwordless login

quota -v                 # shows what your disk quota is
finger <user>            # displays information about user
uname -a                 # shows kernel information
last <yourUsername>      # lists your last logins
killall <processname>    # kill all processes with the name
whois <domain>           # gets whois information for domain
dig <domain>             # gets DNS information for domain
dig -x <host>            # reverses lookup host
wget -r -np -nH --cut-dirs=3 -R index.html http://hostname/aaa/bbb/ccc/ddd/
wget -r -np -nH --cut-dirs=6 https://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/5.9.1/

alias path='echo -e ${PATH//:/\\n}'
for SCRIPT in $( ls -1 $HOME/scripts/login/*-${OS} )  do  . ${SCRIPT}  done
find /tmp -name core -type f -print0 | xargs -0 /bin/rm -f  # ==  find /tmp -depth -name core -type f -delete  (ta szybsza)
find /data -name "*.odf" -exec cp {} /backup/ \;   # error??? jako ze {} musza byc ostatnie
find /data -name "*.odf" -ok rm {} \;                       # executed command after confirmation on every found file
find . -type f -exec sh -c 'cp -- "$@" /target' _ {} +
find /foo -name '*.bar' -exec bash -c 'mv "$1" "${1%.bar}.jpg"' -- {} \;   # sh -c 'command foo "$1" bar' -- "$@"
# invoking functions from file or already sourced in 
find . -type f -exec bash -c 'source /my/bash/function; my_bash_function "$@"' _ {} +     # invoke own function stored in file
function someFunc { : } ; bash -s #<<E_O_F $(typeset -f someFunc) someFunc E_O_F               # importing func to script

find . -type f -name "*.py[co]" -delete -or -type d -name "__pycache__" -delete
find . -user [you] ! -group [grp] -exec chgrp[yourgroup] {} \; 
find . -user [you] -perm /u+r ! -perm /g+r -exec chmod g+r {} \; 
find . \( -path '*/.*' -prune -o ! -name '.*' \) -a -name '*.[ch]'  # finding all but hidden   ## + hidden:    find . -name '.?*' -prune -o ....
find . -type f -printf '%Tc %p\n' | sort -nr | head  # newest files
find . -type f -printf '%k %p\n' | sort -nr | head     # biggest
find . -type d -empty -printf "%Tc %p\n"               #empty dir
find /usr/bin -type f -perm -04000  # sticky 1755 (/tmp),  setgid 2755 (group owns all created in),  setuid 4755 (/usr/bin/passwd) - user execute as root
find ${DIR} -name "${REG_EXP}" -print | while read FILE
do ; echo ${FILE} ; head -20 ${FILE} | tail -5 ; done       # displays lines 15 to 20 from files that meets criteria

# run programs in parallel (apart from    cmd1 & cmd2 & cmd3  )
find . -print0 | xargs -0 -n 1 -P 5 "echo XXX"       # -n  co ile wynikow/linii ma sie uaktywniac   http://mywiki.wooledge.org/ProcessManagement
# The xargs -i% will read a line of input at a time, and replace the % at the end of the command with the input. 
almost any example above | xargs -i% wget $LOCATION/%  # wget "$prefix$(printf %03d $i).jpg"  #leading zeros printf -v n %03d $i ; wget "$prefix$n.jpg"

alias listusers='cut -d: -f1 < /etc/passwd | sort | xargs echo'
alias listusers='awk -F: '{ print $1}' /etc/passwd'
alias bigfiles="find . -type f 2>/dev/null | du -a 2>/dev/null | awk '{ if ( \$1 > 5000) print \$0 }'"
alias fastping='ping -c 100 -s.2'
bu() { cp "$1" "$1".backup-`date +%y%m%d`; }
bu() { cp $@ $@.backup-`date +%y%m%d`; }
bu() { cp $@ $@.backup-`date +%y%m%d`; echo "`date +%Y-%m-%d` backed up $PWD/$@" >> ~/.backups.log; }
# rozne wersje
if cat /proc/version | grep -i -e ubuntu -e debian -e raspbian > /dev/null 2>&1 ; then
    alias update="sudo apt-get update && sudo apt-get upgrade";
elif cat /proc/version | grep -i -e centos -e redhatenterpriseserver -e fedora > /dev/null 2>&1 ; then
    alias update="sudo yum update";
fi

diff <(ssh server1 'rpm -qa | sort') <(ssh server2 'rpm -qa | sort')  # transient named pipe
operator '> >(sort)'  is equvalent to   pipe   '| sort'
while IFS= read -r line; do du -hs "$line"; done < <(ls-1)   # double quotes
find "$imgFolder" -print0 | while IFS= read -r -d '' image; do echo "$image" ; done  # browse or ...  mv "$file" "${file// /_}"
while IFS=$'\t \n' read -r field1 field2 field3 others 
do ; echo ${field2}$'\t'${field3}$'\t'${field1}  ; done < file_for_sorting.txt 

alias webshare='python -c "import SimpleHTTPServer;SimpleHTTPServer.test()"'
history | awk 'BEGIN {FS="[ \t]+|\\|"} {print $3}' | sort | uniq -c | sort -nr | head
export HISTTIMEFORMAT="%Y-%m-%d %H:%M:%S : "      # export HISTTIMEFORMAT='%F %T '      - row, time, command
cp /home/foo/realllylongname.cpp{,-old}           # //Renaming/moving files with suffixes quickly:   
for f in *.txt; do mv $f ${f/txt/doc}; done
mv 's/text_to_find/been_renamed/' *.txt
###
alias watchtail='watch -n .5 tail -n 20'
alias watchdir='watch -n .5 ls -la'
alias watchsize='watch -n .5 du -h –max-depth=1'
alias pastebin='curl -F "clbin=<-" "https://clbin.com"'
hili() { e="$1"; shift; grep --col=always -Eih "$e|$" "$@"; }	highlight occurences of expr. (e.g: • env | hili $USER)
alias removeblanks="egrep -v '(^[[:space:]]*#|^$|^[[:space:]]*//)'"

# RSYNCH   --dry-run option for testing
alias backupstuff='rsync -avhpr --delete-delay /some/location/foo/bar /media/your/remote/location'
rsync -P rsync://rsync.server.com/path/to/file file    # oly get diffs. Do multiple times for troublesome downloads
rsync --bwlimit=1000 fromfile tofile                   # locally copy with rate limit. It's like nice for I/O
rsync -az -e ssh --delete ~/public_html/ remote.com:'~/public_html'  # Mirror web site (using compression and encryption)
rsync -auz -e ssh remote:/dir/ . && rsync -auz -e ssh . remote:/dir/ # Synchronize current directory with remote one

# checks if USB is attached
myusb () { usb_array=();while read -r -d $'\n'; do usb_array+=("$REPLY"); done < <(find /dev/disk/by-path/ -type
l -iname \*usb\*scsi\* -not -iname \*usb\*scsi\*part* -print0 | xargs -0 -iD readlink -f D | cut -c 8) && for usb in "${usb_array[@]}";
do echo "USB drive assigned to sd$usb"; done; }

#traversing dirs
find ... -type d -print0 | while IFS= read -r -d '' subdir; do
   (cd "$subdir" || exit; whatever; ...)
done
netstat -lnt | awk '$6 == "LISTEN" && $4 ~ "80$"'  # netstat -tulpn | less      # ss -tupl	List internet services on a system
ss -tup	List active connections to/from system
echo 'a[$(echo injection >&2)]' | bash -c 'read num; echo $((num+1))' #always validate content as it allows injections
rpm -q -a --qf '%10{SIZE}\t%{NAME}\n' | sort -k1,1n              # list rpm packages sorted by size
echo "DISPLAY=$DISPLAY xmessage cooker" | at "NOW +30min"
echo "mail -s 'go home' P@draigBrady.com < /dev/null" | at 17:30

# checks return code from all parts of pipe
tar -cf - ./* | ( cd "${dir}" && tar -xf - )
if [[ "${PIPESTATUS[0]}" -ne 0 || "${PIPESTATUS[1]}" -ne 0 ]]; then
  echo "Unable to tar files to ${dir}" >&2
fi
# checks return code from particular element of pipe
tar -cf - ./* | ( cd "${DIR}" && tar -xf - )
return_codes=(${PIPESTATUS[*]})
if [[ "${return_codes[0]}" -ne 0 ]]; then
  do_something
fi
if [[ "${return_codes[1]}" -ne 0 ]]; then
  do_something_else
fi
# leading zeros (adding)
printf '%03d\n' {1..300}   eval "printf '%03d\n' {$start..$end}"  for i in {000{0..9},00{10..34}}  seq -w 1 10   seq -f "%03g" 1 10   printf "%03d\n" $(seq 300)
############
IFS=$'\n\t' ###### add after shebang IFS="$(printf '\n\t')"      IFS="`printf '\n\t'`"
http://www.cs.umsl.edu/~sanjiv/classes/cs2750/lectures/shell.pdf
http://mywiki.wooledge.org/BashSheet#Arrays
(screen with Ctrl-A d and tmux with Ctrl-B d). You can reattach (as long as you didnt reboot the server) with screen -x to screen and with tmux attach to tmux.
# Quickly resume a screen session or start one
alias dr='screen -dr || screen'


curl -H @{"Metadata"="true"} -Method POST -Body '{"DocumentIncarnation":"5", "StartRequests": [{"EventId": "f020ba2e-3bc0-4c40-a10b-86575a9eabd5"}]}' -Uri http://169.254.169.254/metadata/scheduledevents?api-version=2017-08-01

# loop through our file of common last names and send it over a local tcp socket
{ while :; do cat names.txt; sleep 0.05; done; } | netcat -l -p 8088

