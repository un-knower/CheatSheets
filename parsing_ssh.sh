#The getopts command succeeds if there is an option left to be parsed and fails otherwise
#The OPTIND variable is set to the command line position of the next option as the options are parsed by getopts
#The OPTARG returns the expected option value from getopts

#!/bin/sh
while getopts abcdf:h OPTNAME
do
  case $OPTNAME in
  a)  echo Option a received
      ;;
  d)  set -x
      ;;
  f)  echo Option f received
      echo Optional argument is: $OPTARG
      ;;
  h | \?)  echo usage: $0 [-abc] [-f filename] parameter1 parameter2 ...
      echo Options are:
      echo "    -d  :  Turn debugging on"
      echo "    -h  :  Print help (this message)"
      exit 1
      ;;
  esac
done
shift `expr $OPTIND - 1`    # shift "$(( $OPTIND - 1 ))"
echo The number of parameters is: $#
echo The parameters are: $*


#### READING CONFIGURATION FILES
sample config.cfg:
## NAME: .myprogrc
default_dir=/path/to/data
temp_dir=/path/to/tmp
verbose=3
clean_up_tempdir=no

while IFS== read var val
do
  case $var in
    \#*) ;;  ## ignore commented lines
    *=*)  eval "$var=\$val" ;;          # tutal lepiej bez poczatkowej *=
  esac
done < "$configfile"
#eval "${ref}=\$value"  # Correct (curly braced PE used for clarity)
#eval "$ref"'=$value'   # Correct (equivalent)

######## SSH ###########
# http://stackoverflow.com/questions/7114990/pseudo-terminal-will-not-be-allocated-because-stdin-is-not-a-terminal
# ssh -t -t   (force)     ssh -T  (disable)
# ssh -T user@server <<EOT ...    or     ssh user@server /bin/bash <<EOT ...
# If <<EOF is not escaped or single-quoted (i. e. <<\EOT or <<'EOT') variables inside the here
# document will be expanded by the local shell before it is executing ssh ....
# The effect is that the variables inside the here document will remain empty because they are defined only in the remote shell.

### 1
ssh user@server /bin/bash <<'EOT'          # EOT is surrounded by single-quotes, so that bash recognizes the heredoc as a nowdoc
echo "These commands will be run on: $( uname -a )"
echo "They are executed by: $( whoami )"
EOT

### 2
cat <<'EOT' | ssh user@server /bin/bash
echo "These commands will be run on: $( uname -a )"
echo "They are executed by: $( whoami )"
EOT

### 3
ssh user@server "$( cat <<'EOT'
echo "These commands will be run on: $( uname -a )"
echo "They are executed by: $( whoami )"
EOT
)"

### 4
IFS='' read -r -d '' SSH_COMMAND <<'EOT'
echo "These commands will be run on: $( uname -a )"
echo "They are executed by: $( whoami )"
EOT
ssh user@server "${SSH_COMMAND}"

### 5 all commands in one big quoted paragraph
ssh user@server 'DEP_ROOT="/home/matthewr/releases"
datestamp=$(date +%Y%m%d%H%M%S)
REL_DIR=$DEP_ROOT"/"$datestamp
if [ ! -d "$DEP_ROOT" ]; then
    echo "creating the root directory"
    mkdir $DEP_ROOT
fi
mkdir $REL_DIR'

### 6  script name is a hostname, inside we keep all cmds to run e.g mvn clean install && echo OK && cd folder && run here something
for conf in /etc/myapp/*; do
    host=${conf##*/}
    ssh "$host" bash < "$conf"
done
parallel ssh {/} bash "<" {} ::: /etc/myapp/*   # easy to parallelize

### 7 using function
{
    declare -f my_bash_function
    echo "my_bash_function foo 'bar bar'"
} | ssh -T user@host bash

### 8 from file
ssh user@server 'bash -s' < /path/script.sh

### 9 using parameters/variables
ssh user@host ARG1=$ARG1 ARG2=$ARG2 'bash -s' <<'ENDSSH'
  # commands to run on remote host
  echo $ARG1 $ARG2
ENDSSH
If any of the env var values contain spaces, use: ssh user@host "ARG1=\"$ARG1\" ARG2=\"$ARG2\"" 'bash -s' <<'ENDSSH'

######### COLORING ###############
echo_failure() {
  [ "$BOOTUP" = "color" ] && $MOVE_TO_COL
  echo -n "["
  [ "$BOOTUP" = "color" ] && $SETCOLOR_FAILURE
  echo -n $"FAILED"
  [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
  echo -n "]"
  echo -e "\r"
  return 1
}

echo_passed() {
  [ "$BOOTUP" = "color" ] && $MOVE_TO_COL
  echo -n "["
  [ "$BOOTUP" = "color" ] && $SETCOLOR_SUCCESS
  echo -n $"PASSED"
  [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
  echo -n "]"
  echo -e "\r"
  return 1
}

echo_warning() {
  [ "$BOOTUP" = "color" ] && $MOVE_TO_COL
  echo -n "["
  [ "$BOOTUP" = "color" ] && $SETCOLOR_WARNING
  echo -n $"WARNING"
  [ "$BOOTUP" = "color" ] && $SETCOLOR_NORMAL
  echo -n "]"
  echo -e "\r"
  return 1
}

BOOTUP=color
RES_COL=60
MOVE_TO_COL="echo -en \\033[${RES_COL}G"
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_WARNING="echo -en \\033[1;33m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"
echo -n "failure test" && echo_failure
echo -n "warn test   " && echo_warning
echo -n "ok test     " && echo_passed

############################################# ANOTHER OPTION
NORMAL=$(tput sgr0)
GREEN=$(tput setaf 2; tput bold)
YELLOW=$(tput setaf 3)
RED=$(tput setaf 1)
function red()    { echo -e "$RED$*$NORMAL" }
function green()  { echo -e "$GREEN$*$NORMAL" }
function yellow() { echo -e "$YELLOW$*$NORMAL" }
green "Task has been completed"
red "The configuration file does not exist"
yellow "You have to use higher version."
############################################## ANOTHER OPTION 2
ESC_SEQ="\x1b["
COL_RESET=$ESC_SEQ"39;49;00m"
COL_RED=$ESC_SEQ"31;01m"
COL_GREEN=$ESC_SEQ"32;01m"
COL_YELLOW=$ESC_SEQ"33;01m"
COL_BLUE=$ESC_SEQ"34;01m"
COL_MAGENTA=$ESC_SEQ"35;01m"
COL_CYAN=$ESC_SEQ"36;01m"
echo -e "$COL_RED This is red $COL_RESET"
echo -e "$COL_BLUE This is blue $COL_RESET"
echo -e "$COL_YELLOW This is yellow $COL_RESET"

########################################## COLOR PROMPT
RED='\[\033[01;31m\]'
WHITE='\[\033[01;00m\]'
GREEN='\[\033[01;32m\]'
BLUE='\[\033[01;34m\]'
export PS1="${debian_chroot:+($debian_chroot)}$GREEN\u$WHITE@$BLUE\h$WHITE\w\$ "
########################################## ERRORS IN COLOR
whatever_command 2> >(while read line; do echo -e "\e[01;31m$line\e[0m" >&2; done)
printf '\e[1m%s\e[0m\n' "Hello, world"   # no ! usage
################# PROMPT_COMMAND http://www.ccs.neu.edu/research/gpc/MSim/vona/terminal/VT100_Escape_Codes.html
#                                http://wiki.bash-hackers.org/scripting/terminalcodes
PROMPT_COMMAND="printf '%.0s-' {1..20};printf '\n'"          # printf "%s\b\b\b" -{001..25} 
PROMPT_COMMAND="echo $'\e7\e[2;800H\e[16D\e[7m' $(date +"%Y/%m/%d %H:%M")$'\e8'" # clock in top right corner
