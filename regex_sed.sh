#REGULAR EXPRESSIONS in vi and everywhere
. (dot) #	Any single character except newline
*	      # zero or more occurances of any character
[...]	  # Any single character specified in the set
[^...]	# Any single character not specified in the set
^	      # Anchor - beginning of the line
$	      # Anchor - end of line
\<    	# Anchor - begining of word
\>	    # Anchor - end of word
\(...\)	# Grouping - usually used to group conditions
\n	    # Contents of nth grouping

[...] - Set Examples [A-Z]	The SET from Capital A to Capital Z
[a-z]	The SET from lowercase a to lowercase z
[0-9]	The SET from 0 to 9 (All numerals)
[./=+]	The SET containing . (dot), / (slash), =, and +
[-A-F]	The SET from Capital A to Capital F and the dash (dashes must be specified first)
[0-9 A-Z]	The SET containing all capital letters and digits and a space
[A-Z][a-zA-Z]	In the first position, the SET from Capital A to Capital Z. In the second character position, the SET containing all letters

Regular Expression Examples /Hello/	Matches if the line contains the value Hello
/^TEST$/	Matches if the line contains TEST by itself
/^[a-zA-Z]/	Matches if the line starts with any letter
/^[a-z].*/	Matches if the first character of the line is a-z and there is at least one more of any character following it
/2134$/	Matches if line ends with 2134
/\(21|35\)/	Matches is the line contains 21 or 35
Note the use of ( ) with the pipe symbol to specify the 'or' condition
/[0-9]*/	Matches if there are zero or more numbers in the line
/^[^#]/	Matches if the first character is not a # in the line
:%s/ˆ[> ]*//    finding > or space at the beginning of line and deleting them
/(.*)/          is used to indicate a set of characters enclosed in parentheses, e.g  (this) and (that)
/([ˆ)]*)/       searches for ( then anything except )    , so it will give   (this)
/a\{5\}/        aaaaa  (5 a)
s/[0-9]\.[0-9][0-9]/\$&/    -->  allows to put $ before e.g.  8.50  -->  $8.50   (& does that)
s/\([ˆ,]*\), \(.*\)/\2 \1/  --> last name, initial first name  --> changes into --> first-name initial last-name

############ SED  http://www.cs.umsl.edu/~sanjiv/classes/cs2750/lectures/re.pdf   page 8
printf '%s\n' ',s/^/XXX/' w q | ed -s file  # prepend XXX at each line  (use ED, not sed), same as $>   { command; cat file; } > tempfile && mv tempfile file
sed -i -e 's@^ONBOOT="no@ONBOOT="yes@' ifcfg-eth0          # -i   oznacza podmiane na miejscu
sed 's/from/to/g'                     # global
sed 's/DEL/d'                         # delete line containging DEL   also   sed '/expr/d' 
sed 's:FROM:TO:'                            # any separator possible
sed 's/$/\  /'                              # from single space to double space
sed 's/[ \t][ \t]*/\/g' file                # creating list of words used in document
sed '1,/ˆ$/d' in_file > out_file            # removing header (up to first empty line)
sed 's/ˆ/\t/'file                           # inserts tab at the beginning of every line, including empty lines
sed '/./s/ˆ/\t/' file                       # as above, but skipping empty lines
sed '/ˆ$/!s/ˆ/\t/' file                     # as above (another method)
sed -n '/ˆ$/,/ˆend/p/'
sed 's/\([a-z][a-z]*\) \([a-z][a-z]*\)/\2 \1/'  # switch 2 words
sed -r 's/([a-z]+) ([a-z]+)/\2 \1/'             # switch 2 words - using Extended Regular Expr (ERE), no need to \( for brackets

sed 's/\(.*\)1/\12/g'           #	Modify anystring1 to anystring2
sed '/^ *#/d; /^ *$/d'	        # Remove comments and blank lines
sed ':a; /\\$/N; s/\\\n//; ta'	# Concatenate lines with trailing \
sed 's/[ \t]*$//'	              # Remove trailing spaces from lines
sed 's/\([`"$\]\)/\\\1/g'	      # Escape shell metacharacters active within double quotes
seq 10 | sed "s/^/      /; s/ *\(.\{7,\}\)/\1/"	  # Right align numbers
seq 10 | sed p | paste - -	    # Duplicate a column
sed -n '1000{p;q}'	            # Print 1000th line
sed -n '10,20p;20q'	            # Print lines 10 to 20
sed 10q                         # Print lines 1-10 and then quit.
sed '1,5d; 10q'                 # Print just lines 6-10 by filtering the first 5 then quitting after 10.
sed -n 's/.*<title>\(.*\)<\/title>.*/\1/ip;T;q'	    # Extract title from HTML web page
sed -i 42d ~/.ssh/known_hosts	    # Delete a particular line

who | sed 's/ˆ\([ˆ ]*\).*\([0-9][0-9]:[0-9][0-9]\).*/\1\t\2/' # leaving just user name and login time from who command
echo Sunday     | sed 's/Sun/&&/'           # SunSunday     && doubles found string
echo Sunday     | sed 's_\(Sun\)_\1ny_'     # Sunnyday
echo Sunday     | sed 's_\(Sun\)_\1ny \1_'  # Sunny Sunday
echo 2014-04-01 | sed 's/....-..-../YYYY-MM-DD/'  # YYYY-MM-DD                  . means any char
echo 2014-04-01 | sed 's/\(....\)-\(..\)-\(..\)/\3+\2+\1/'  # 01+04+2014
cat list2 | sed 's/ooo\?/A/'  # third o is optional \?
cat list2 | sed 's/o\{3\}/A/' # exactly 3 times

alias tree="find . -print | sed -e 's;[^/]*/;|____;g;s;____|; |;g'"
alias dirtree="ls -R | grep :*/ | grep ":$" | sed -e 's/:$//' -e 's/[^-][^\/]*\//--/g' -e 's/^/   /' -e 's/-/|/'"

sed 's/\r/\xFF/g' notepad.txt | grep -P --color -n "\xFF"        # FIND NON-PRINTING characters
grep --color -P -n "[\x80-\xFF]" unicode.txt                     # find UNICODE characters
sed 's/\t/\xFF/g' tab_delimit.txt | grep -P --color -n "\xFF"    # even tabs

sed -f cmdfile in_file    # read sed script from <cmdfile>      # same with awk -f <scriptfile> infile

############ AWK  
# BEGIN actions are performed before the first input line has been read (used to initialize variables, print headings, and like)
awk 'BEGIN { FS = "\t" } ; {print $3FS$2FS$1}' file_for_sorting.txt   
awk '{sum += $2} END {print sum/NR}' file_for_sorting.txt 
awk 'BEGIN {print "Hello World"}'                               # working on empty file
awk '/regular expression/ { print }' filename                   # same as grep
awk ’/ˆ$/ { print "Encountered a blank line" }’ filename        # print message for every blank line
awk ’{ print }’ filename                                        # Omitting pattern performs the action on every line
awk ’/regular expression/’ filename                             # Omitting action prints matched lines
awk '$3 == "nfs" { print $2 " maps to " $1 }' /proc/mounts

# PRINTING
awk -F: ’{print $1"\t"$5}’ /etc/passwd                          # : to be separator, print user name and real name
command who has 6 columns, and are called $1, $2, ..., $NF      # NF = number of fields,    $NF = content of last field
                                                                # NR = current line, record $0  = entire input record
awk ’{print NR, $0}’ filename                                   # add line numbers to each line
awk ’{ printf "%4d %s\n", NR, $0 }’ filename                    # printf
echo 'Insert completed. 100 rows added ' | awk '{print $(NF-2)}'   #prints 100

# splitting lines by 10
awk -v range=10 '{print > FILENAME "." (int((NR -1)/ range)+1)}' file

# PATTERNS
awk -F: ’$2 == ""’ /etc/passwd            # people who have no password (2nd field empty, blank)
awk -F: ’$2 == "*"’ /etc/passwd           # people who have password locked
awk -F: ’!( $2 == "" )’ filename          # negate match
$2 ~ /ˆ$/                                 # 2nd field matches regex empty string
$2 !~ /./                                 # 2nd field does not match regex any character
length($2) == 0                           # length of 2nd field is zero       printing long lines   awk ’length($0) > 72’ filename
awk ’(length($0) > 72) { print "Line", NR, "too long: ", substr($0,1,50)}’ filename
date | awk ’{ print substr ( $4, 1, 5 ) }’    # gets HH:MM

# ARITHMETICS
{ s = s + $1 }
END    { print s, s/NR }             # calculates sum of 1st column and print sum and avg at the end
--------------------------------
$ awk ’{ nc += length ( $0 ) + 1     # number of chars, 1 for \n
         nw += NF                    # number of words
}
END  { print NR, nw, nc }’ filename  # replicating wc function

# leading zeros
# count variable contains an integer
awk -v count="$count" 'BEGIN {for (i=1;i<=count;i++) {printf("%03d\n",i)} }'
# Bourne, with Solaris's decrepit and useless awk:
awk "BEGIN {for (i=1;i<=$count;i++) {printf(\"%03d\\n\",i)} }"
