#!/bin/bash
if [ "$(id -u)" != "0" ]; then        # if [[ $EUID -ne 0 ]]; then             if [[ `id -u` -ne 0 ]]; then 
   echo "This script must be run as root" 1>&2   
   exit 1
fi
echo "Press enter when ready"
read blabla

# configuring
echo "vm.swappiness = 0" >> /etc/sysctl.conf 
echo "net.ipv6.conf.all.disable_ipv6 = 1" >> /etc/sysctl.conf
echo "net.ipv6.conf.default.disable_ipv6 = 1" >> /etc/sysctl.conf
echo "net.ipv6.conf.lo.disable_ipv6 = 1" >> /etc/sysctl.conf
echo "NETWORKING_IPV6=no" >> /etc/sysconfig/network
echo "IPV6INIT=no" >> /etc/sysconfig/network
echo 0 > /proc/sys/vm/swappiness
grep NETWORKING=yes /etc/sysconfig/network || echo "NETWORKING=yes" >> /etc/sysconfig/network
# Verify	that	the	IP	address	is	static	(or	DHCP	but	statically	assigned):
#  (RHEL) /etc/sysconfig/network-scripts/ifcfg-eth*
#  (SLES) /etc/sysconfig/network/ifcfg-eth* 
# sysctl -p 
grep 'BOOTPROTO="static"' /etc/sysconfig/network-scripts/ifcfg-eth0 || echo "BOOTPROTO is not set to static"


# disable IP tables/firewall
service iptables stop
chkconfig iptables off
chkconfig ip6tables off


# disable SELinux
/usr/sbin/setenforce 0        #sestatus | grep -i mode
sed -i.old s/SELINUX=enforcing/SELINUX=disabled/ /etc/sysconfig/selinux   #cat /etc/selinux/config  | grep "SELINUX="
selinuxenabled || echo "selinux disabled"

#Disable	non-required	services,	if	any:	
chkconfig cups off
chkconfig postfix off

# set up hostname and validate settings
hostname NN
# uname -a          ## and verify if the output matches the hostname command
# /sbin/ifconfig    ##and note the inet addr in the eth0 entry
# host -v -t A $(hostname) ##make sure that hostname matches the output of the hostname command has the same IP addr

# DNS 
dig @<dnsserver> hostname
host <ip address>
dig -x <ip address>
nslookup <host name>
  
#  Make	sure	127.0.0.1	is	set	to	localhost,	not	the	host's	name:
#    cat /etc/hosts
#    ping localhost
#cat /etc/resolv.conf
#cat /etc/nsswitch.conf has	a	line	like	"hosts: files dns"
#cat /etc/host.conf has a line like "order hosts, bind"	

#Increase	the	nofile and	nproc ulimit	for	mapred	and	hdfs	users	to	at	least	32K.
echo hdfs - nofile 32768 >> /etc/security/limits.conf
echo mapred - nofile 32768 >> /etc/security/limits.conf
echo hbase - nofile 32768 >> /etc/security/limits.conf
echo hdfs - nproc 32768 >> /etc/security/limits.conf
echo mapred - nproc 32768 >> /etc/security/limits.conf
echo hbase - nproc 32768 >> /etc/security/limits.conf

# sync system clock by installing ntp services (http://www.cloudera.com/content/www/en-us/documentation/enterprise/latest/topics/install_cdh_enable_ntp.html)
yum install -y ntp
chkconfig ntpd on
service ntpd start
ntpdate -u 0.pool.ntp.org
hwclock --systohc  ## Synchronize the system clock (to prevent synchronization problems).   ntpq -p

# install mysql
yum install mysql-server
yum install mysql-connector-java
start mysql server       
service mysqld start
chkconfig mysqld on
chkconfig --list mysqld
# inna opcja
#cd /opt
#wget http://cdn.mysql.com/Downloads/MySQL-5.6/MySQL-client-5.6.29-1.el6.x86_64.rpm
#wget http://cdn.mysql.com/Downloads/MySQL-5.6/MySQL-server-5.6.29-1.el6.x86_64.rpm
#wget http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.36.zip
#yum remove mysql mysql-server mysql-libs compat-mysql51
#yum -y remove mysql-libs-5.1.71*
#rm -rf /var/lib/mysql
#rm -f /etc/my.cnf
#rpm -ivh  MySQL-server-5.6.29-1.el6.x86_64.rpm
#rpm -ivh  MySQL-client-5.6.29-1.el6.x86_64.rpm 
#chkconfig mysql on
#service mysql start
#service mysql status 
#cat /root/.mysql_secret 
#mysql>  set password for 'root'@'localhost'=password('hadoop');
#mysql> create database hive DEFAULT CHARSET utf8 COLLATE utf8_general_ci;  
#mysql>  create database amon DEFAULT CHARSET utf8 COLLATE utf8_general_ci;    
#mysql> create database hue DEFAULT CHARSET utf8 COLLATE utf8_general_ci;    
#mysql> create database Oozie DEFAULT CHARSET utf8 COLLATE utf8_general_ci;
#mysql> grant all privileges on *.* to 'root'@'ALYSZ101-129' identified by 'hadoop' with grant option;     
#mysql> flush privileges;


# install Oracle JDK
wget --no-check-certificate \
	--no-cookies \
	--header "Cookie: oraclelicense=accept-securebackup-cookie" \
	http://download.oracle.com/otn-pub/java/jdk/7u45-b18/jdk-7u45-linux-x64.rpm \
	-O jdk-7u45-linux-x64.rpm
rpm -ivh jdk-7u45-linux-x64.rpm
# update the installed java as the latest version using alternatives
#alternatives --install /usr/bin/java java /usr/java/jdk1.7.0_45/bin/java 200000 
# update-alternatives --config java

# adding users
useradd hadoop
echo "hadoop" | passwd hadoop --stdin
echo "hadoop ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers
# useradd --system --home=/opt/cm-5.6.0/run/cloudera-scm-server/ --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm

# install scalding UBUNTU
#echo "y" | sudo apt-get install scala
#echo "y" | sudo apt-get install git
#git clone https://github.com/twitter/scalding.git
#cd scalding
#./sbt update && ./sbt test && ./sbt assembly 



## INSTALLING CLOUDERA MANAGER
# links: http://www.cloudera.com/documentation/enterprise/release-notes/topics/cm_vd.html
cd /etc/yum.repos.d/
curl -o cloudera-cdh5.repo http://archive.cloudera.com/cdh5/redhat/6/x86_64/cdh/cloudera-cdh5.repo
curl -o cloudera-manager.repo http://archive.cloudera.com/cm5/redhat/6/x86_64/cm/cloudera-manager.repo
yum clean all


# download and install Latest CM
wget http://archive.cloudera.com/cm5/installer/latest/cloudera-manager-installer.bin
chmod u+x cloudera-manager-installer.bin
sudo ./cloudera-manager-installer.bin

# inna opcja instalacji
cd  /opt
wget http://archive-primary.cloudera.com/cm5/cm/5/cloudera-manager-el6-cm5.6.0_x86_64.tar.gz
tar xf cloudera-manager-el6-cm5.6.0_x86_64.tar.gz
unzip mysql-connector-java-5.1.36.zip
cp  mysql-connector-java-5.1.36/mysql-connector-java-5.1.36-bin.jar  /opt/cm-5.6.0/share/cmf/lib/
/opt/cm-5.6.0/share/cmf/schema/scm_prepare_database.sh mysql cm -hlocalhost -uroot -phadoop --scm-host localhost scm scm scm

vim /opt/cm-5.6.0/etc/cloudera-scm-agent/config.ini
server_host=ALYSZ101-129
scp -P 60088 -r cm-5.6.0/  root@ALYSZ65-240:/opt
scp -P 60088 -r cm-5.6.0/  root@ALYSZ101-205:/opt

cd /opt/cloudera/parcel-repo/
wget http://archive-primary.cloudera.com/cdh5/parcels/5.6.0/CDH-5.6.0-1.cdh5.6.0.p0.45-el6.parcel
wget http://archive-primary.cloudera.com/cdh5/parcels/5.6.0/CDH-5.6.0-1.cdh5.6.0.p0.45-el6.parcel.sha1
wget http://archive-primary.cloudera.com/cdh5/parcels/5.6.0/manifest.json
mv CDH-5.6.0-1.cdh5.6.0.p0.45-el6.parcel.sha1 CDH-5.6.0-1.cdh5.6.0.p0.45-el6.parcel.sha

/opt/cm-5.6.0/etc/init.d/cloudera-scm-server start

# CM AGENT
/opt/cm-5.6.0/etc/init.d/cloudera-scm-agent start
cp /opt/mysql-connector-java-5.1.36-bin.jar /opt/cloudera/parcels/CDH-5.6.0-1.cdh5.6.0.p0.45/lib/hive/lib/
cp /opt/mysql-connector-java-5.1.36-bin.jar /var/lib/oozie

################################### COMPLETING INSTALLATION ###
# Format the NameNode
sudo -u hdfs hdfs namenode -format
# Start HDFS
for x in `cd /etc/init.d ; ls hadoop-hdfs-*` ; do sudo service $x start ; done
# Create the /tmp Directory
sudo -u hdfs hadoop fs -mkdir /tmp 
sudo -u hdfs hadoop fs -chmod -R 1777 /tmp
# Create MapReduce system directories:
sudo -u hdfs hadoop fs -mkdir -p /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chmod 1777 /var/lib/hadoop-hdfs/cache/mapred/mapred/staging
sudo -u hdfs hadoop fs -chown -R mapred /var/lib/hadoop-hdfs/cache/mapred
# Verify HDFS File Structure
sudo -u hdfs hadoop fs -ls -R /
# Start MapReduce
for x in `cd /etc/init.d ; ls hadoop-mapreduce-*` ; do sudo service $x start ; done
# Start Yarn
for x in `cd /etc/init.d ; ls hadoop-yarn-*` ; do sudo service $x start ; done
# Create a Hadoop User directories
export HDUSER=$USER
sudo -u hdfs hadoop fs -mkdir /user/$HDUSER
sudo -u hdfs hadoop fs -chown $HDUSER /user/$HDUSER
# Test Hadoop
hadoop fs -mkdir input
hadoop fs -put /etc/hadoop/conf/*.xml input
hadoop fs -ls input
/usr/bin/hadoop jar /usr/lib/hadoop-0.20-mapreduce/hadoop-examples.jar grep input output 'dfs[a-z.]+'
hadoop fs -ls
hadoop fs -ls output
hadoop fs -cat output/part-00000 | head



# ssh -f -N -L 50030:localhost:50030 user@digitalocean
#https://gist.github.com/krishna209

############################ UPGRADING JAVA on CM
# stop CM, stop server (from GUI)
service cloudera-scm-agent stop		# stop agent on ALL NODES !!!
service cloudera-scm-server stop	# on CM server only

# download JAVA easiers from CM
rpm -ivh "http://archive.cloudera.com/director/redhat/7/x86_64/director/2.6.0/RPMS/x86_64/oracle-j2sdk1.8-1.8.0+update121-1.x86_64.rpm"
yum localinstall -y oracle-j2sdk1.....rpm

vi /etc/default/cloudera-scm-server # add this line at the end
export JAVA_HOME=/usr/java/latest
ln -s /usr/java/jdk1.8.0-121-cloudera

# remove previous java
yum remove -y oracle-j2sdk1.7.x86_64

# create auto run shell
vi /etc/profile.d/java.sh   # add
export JAVA_HOME=/user/java/latest

service cloudera-scm-server start
service cloudera-scm-agent start  # ALL NODES

# UI --> HOSTS --> ALL HOSTS --> CONFIGURATION --> java home dir

### INSTALLINA SPARK 2.2 on CDH
# download descriptor (jar)  (e.g.   SPARK2_ON_YARN-2.2.0.cloudera1.jar)
# ADMINISTRATORS --> Settings --> Custom Service Descriptors  (path is   /opt/cloudera/csd)
cd /opt/cloudera/csd
chown cloudera-scm:cloudera-scm /opt/cloudera/csd/SPARK2_ON_YARN-2...
chmod 644 /opt/cloudera/csd/SPARK2_ON_YARN-2.....
service cloudera-scm-server restart
+ restart CM in UI
# we add repo of spark 2 i nParcels REMOTE URL
https://archive.cloudera.com/spark2/parcels/2.2.0.cloudera1
# available to download.  --> distribute --> activate
ADD SERVICE --> you will see SPARK 2
now you can use    spark2-shell  # remember 2 !!