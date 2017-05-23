#!/bin/bash
# for debugging - set -x
if [ "$(id -u)" != "0" ]; then        # if [[ $EUID -ne 0 ]]; then             if [[ `id -u` -ne 0 ]]; then 
   echo "This script must be run as root" 1>&2   
   exit 1
fi
echo Press enter when ready
read blabla
# installing ntp to synchronize time
# on KDC host:
yum -y install ntp && chkconfig ntpd on && /etc/init.d/ntpd start
# on all client hosts
## yum -y install ntp && chkconfig ntpd on && /etc/init.d/ntpd start

#installing keberos
yum install krb5-server krb5-workstation openldap-clients

sed -i.orig 's/EXAMPLE.COM/CLOUDERA/g' /etc/krb5.conf
sed -i.bak1 's/kerberos.example.com/quickstart.cloudera/g' /etc/krb5.conf
sed -i.bak2 's/example.com/cloudera/g' /etc/krb5.conf

# download UnlimitedJCEPolicyJDK7.zip from Oracle into the /root directory
mkdir jce && cd jce
unzip ../UnlimitedJCEPolicyJDK7.zip 
cp /usr/java/jdk1.7.0_67-cloudera/jre/lib/security/local_policy.jar local_policy.jar.orig
cp /usr/java/jdk1.7.0_67-cloudera/jre/lib/security/US_export_policy.jar US_export_policy.jar.orig
cp /root/jce/UnlimitedJCEPolicy/local_policy.jar /usr/java/jdk1.7.0_67-cloudera/jre/lib/security/local_policy.jar
cp /root/jce/UnlimitedJCEPolicy/US_export_policy.jar /usr/java/jdk1.7.0_67-cloudera/jre/lib/security/US_export_policy.jar
# now create the kerberos database
kdb5_util create -s     # type in cloudera at the password prompt
# update the kdc.conf file
sed -i.orig 's/EXAMPLE.COM/CLOUDERA/g' /var/kerberos/krb5kdc/kdc.conf
sed -i.m1 '/dict_file/a max_life = 1d' /var/kerberos/krb5kdc/kdc.conf
sed -i.m2 '/dict_file/a max_renewable_life = 7d' /var/kerberos/krb5kdc/kdc.conf
sed -i.m3 's/^max_/  max_/' /var/kerberos/krb5kdc/kdc.conf  # indent 2 spaces to the two new lines in the file
sed -i 's/EXAMPLE.COM/CLOUDERA/' /var/kerberos/krb5kdc/kadm5.acl
sed -i.m3 '/supported_enctypes/a default_principal_flags = +renewable, +forwardable' /var/kerberos/krb5kdc/kdc.conf
sed -i.m4 's/^default_principal_flags/  default_principal_flags/' /var/kerberos/krb5kdc/kdc.conf   #identing
service krb5kdc start
service kadmin start

kadmin.local <<eoj
modprinc -maxrenewlife 1week krbtgt/CLOUDERA@CLOUDERA
addprinc -pw cloudera cloudera-scm/admin@CLOUDERA
modprinc -maxrenewlife 1week cloudera-scm/admin@CLOUDERA
addprinc -pw cloudera cloudera@CLOUDERA
eoj

#test
kinit cloudera-scm/admin@CLOUDERA
klist -e
kinit cloudera@CLOUDERA
hadoop jar /usr/lib/hadoop-0.20-mapreduce/hadoop-examples.jar pi 10 10000

# kerberos services autostart
chkconfig kadmin on
chkconfig krb5kdc on

# Cloudera Manager will input a destination path
DEST="$1"

# Cloudera Manager will input the principal name in the format: <service>/<fqdn>@REALM
PRINC="$2"

# Assuming the '<service>_<fqdn>@REALM.keytab' naming convention for keytab files
IN=$(echo $PRINC | sed -e 's/\//_/')
SRC="/keytabs/${IN}.keytab"

# Copy the keytab to the destination input by Cloudera Manager
cp -v $SRC $DEST
