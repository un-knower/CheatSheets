ansible <group> -i hosts -u <user> -m <module>
# install apache server httpd
ansible <group> -i hosts -u <user> -m yum -a "name=httpd state=latest" -b   # b = become root!
state=absent  --> to check if something is installed

# check if apache server has started.
ansible <group> -i hosts -u <user> -m service -a "name=httpd state=started" -b

ansible ...... --check      # DRY-RUN
ansible ...... -vvvv         # DEBUG

# run commands locally , no need to specify user
ansible localhost -m setup
ansible localhost -m command -a "uptime"

# help
ansible-doc l | grep ec2

# PLAYBOOKS , YAML
# start with ---
---
- name: install and configure mariadb
  hosts: testServer
  # we can define it in command line --extra-vars
  hosts: "{{myHosts}}"
  remote_user: ec2-user
  become: yes   # root needed?
  
  connection: local
  gather_facts: false
  
  # option 1 - we can use vars directly in file
  vars:
    mysql_port: 3306
    log_path: "/var/log"
    listenport: 8888
    
  # option 2 - we can replace vars with file containing variables defined
  vars_files:
  - my_variables.yml
    
  tasks:
    - block:
      - name: install mariadb
        yum: name=mariadb-server state=latest
        # possible to loop over items
        yum: name={{item}} state=installed
        yum: name={{item}} state=present
        with_items:
        - mariadb-server
        - MySQL-python
        - libselinux-python
        
      - name: create mysql conf file, copy from A to B
        template: src=my.cnd.j2 dest=/etc/my.cnf
        # template: src=iptables.j2 dest=/etc/sysconfig/iptables
        notify: restart mariadb
        # if notify used, then "handlers" is needed!!
        # notify: restart iptables, etc... SEE HANDLERS below
        
      - name: create mariadb log file
        file: path={{log_path}}/mysqld.log state-touch owner=mysql group=mysel mode=0775
        
      - name: start mariadb service
        service: name=mariadb state=started enabled=yes
    
    when: ansible_os_family=="RedHat"
    become: yes
        
  handlers:
  - name: restart mariadb
    service: name=mariadb state=restarted

# another task    
- name: test to see if selinux is running
  command: getenforce
  
- name: restart machine
  shell: sleep 2 && shutdown -r now "ansible updates have happened
  async: 1      # this means that another tasks can continue in background
  poll: 0       # fire and forget
  ignore_errors: True
  
- name: waiting for server to come background
  wait_for: host={{inventory_hostname}} state=started delay=30 timeout=300
  become: no
  delegate_to: 127.0.0.1
  # this is same as
  # local_action: wait for host=.....  (we don't need delegate_to)

- name: testing delegate facts
  ...
 
  tasks:
  - name: gather local facts
    setup:
    delegate_to: 127.0.0.1
    delegate_facts: true  # will delegate facts from target machine NOT local    
  
    
# cat my.cnf.j2
[mysqld]
datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.socket
user=mysql
# disabling symbolic links is recommended to prevent assorted security risks
symbolic-links=0
port={{ mysql_port }}   # variable from yaml file

[mysql_safe]
log-error=/var/log/mysqld.log
pid-file=/var/run/mariadb/mysql.pid


# Running playbook
ansible-playbook -i hosts <name.yml>
ansible-playbook -i hosts <name.yml> --extra-vars "hosts=testServer"
ansible-playbook -i hosts <name.yml> --start-at-task="quick echo"
ansible-playbook -i hosts <name.yml> --step   # will ask  y/n/c  c= yes to all

# my_variables.yml
mysql_port: 3306
log_path: "/var/log"


# we can pick output of commands
# REGISTERED VARIABLES
# var_cases.yml
---
- name: testing variable stuff
  hosts: testServer
  remote_user: ec2_user
  
  tasks:
  - name: get date on the server
    shell: date
    register: output
    
  - debug: msg="the date is {{output.stdout}}
  
  - debug: var=ansible_distribution_version
  
  - name: group some machines temporarily
    group_by: key=rhel_{{ansible_distribution_version}}
    register: group_result
    
  - debug: var=group_result
  
  - name: quick echo
    shell: echo $PATH
    register: result
    changed_when: false  # it will display OK instead of CHANGED
    
  - debug: msg="stop running playbook if the play failed"
    failed_when: result|failed
    
  - name: echo failed
    shell: echo I failed
    register: output
    
  - debug: msg="Okay, really stop playbook"
    failed_when: output.stdout.find('failed')!=-1
  
  
# run
ansible-playbook -i hosts var_cases.yml


#######
####### CONDITIONALS
#######
# conditionals.yml
---
- name: testing conditionals
  hosts: testServer
  remote_user: ec2-user
  become: yes
  
  vars:
    unicorn: true
    
  tasks:
  - name: don't install on debian machines
    yum: name=httpd state=latest
    when: (ansible_os_family="RedHat" and ansible_distribution_major_version=="6")
    
  - name: are unicorns real or fake
    shell: echo "unicorns are fake"
    when: not unicorn
    
  - fail: msg="unicorns require variable rainbow"
    when: rainbow is undefined
    
  - name: test to see if selinux is runinng
    shell: getenforce
    register: sestatus
    
  - name: configure selinux if not enforcing
    seboolean: name=mysql_connect_any state=true persistent=yes
    when: sestatus.rc != 0     # rc = return code!
    
  - name: checking systemd
    shell: cat /var/log/messages
    register: log_output
    
  - name: next task
    shell: echo "systemd know when we're doing ansible stuff"
    when: log_output.stdout.find('ansible') != 0
    register: shell_echo
    
  - debug: var=shell_echo
  
ansible-playbook -i hosts conditionals.yml


##########
########## LOOPING LOOPS
##########
---
- name: testing loops
  hosts: testServer
  remote_user
  
  tasks:
  - name: looping over environment facts
    debug: msg={{item.key}}={{item.value}}
    with_dict: ansible_env
    
  - name: looping over files and then copy
    copy: src={{item}} dest=/tmp/loops
    with_fileglob: "/tmp/*.conf"
    
  - name: do until condition is met
    shell: echo hello
    register: output
    retries: 5
    delay: 5
    until: output.stdout.find('hello') != -1
    
    
    
    
############
############   BLOCK  +   INCLUSE
############
# block.yml
---
- name: testing blocks
  hosts: testServer
  remote_user: ec2-user
  become: yes
  
  tasks:
    - include: selinux.yml   
    # error handling
    - block:
      - name: copying in a block
        copy: src=/tmp/test1.txt dest=/tmp/loops
      rescue:
      - debug: msg="Stop. Error time"
      always:
      - debug: msg="This message will always appear"
    
    # nesting
    - block:
      - block:
        - block:
          - debug: msg="nesting some block"
          
          
          
          
############
############   TEMPLATES  jinja
############
# iptables.j2
# {{ ansible_managed }}
*filter
:INPUT ACCEPT [0:0]
:FORWARD ACCEPT [0:0]
:OUTPUT ACCEPT [0:0]

{% if (inventory_hostname in groups['webservers']) or (inventory_hostname in groups['monitoring']) %}
-A INPUT -p tcp --dport 80 -j ACCEPT
{% endif %}

{% if inventory_hostname in groups['dbservers'] %}
-A INPUT -p tcp --dport 3306 -j ACCEPT  # albo   {{ listenport }}
{% endif %}

{% for host in groups['monitoring'] %}   # groups wziete z pliku hosts
-A INPUT -p tcp -s {{ hostvars[host].ansible_default_ipv4.address }} --dport 5666..
{% endfor %}

-A INPUT -m state --state ESTABLISHED,RELATED -j ACCEPT
-A INPUT -p icmp -j ACCEPT
-A INPUT -i lo -j ACCEPT


# vi filters.yml
---
- name: test filter options
  # if variable is not set, then default value will be used
  hosts: "{{database_host | default('testServer')}}"
  remote_user: ec2-user
  become: yes
  
  vars:
    cheese: ['american', 'cheddar', 'cheddar', 'cheddar']
    pizza: ['mozarella', 'pepperoni']
    
  tasks:
  - name: quick echo
    shell: echo $PATH
    register: result
    
  - debug: msg="The play failed"
    when: result|failed
    
  - debug: msg="The play changed"
    when: result|changed
   
  - debug: msg="The play succeeded"
    when: result|success
    
  - debug: msg="The play skipped"
    when: result|skipped
    
  - debug: msg="{{ cheese | unique }}"
  
  - debug: msg="{{ cheese | difference(pizza)}}"
  
  - debug: msg="{{ cheese | union(pizza)}}"
  
  
  
# hosts   , sample
[east-webservers]
east1.example.com

[west-webservers]
west1.example.com

[east-dbservers]
...

[webservers:children]
east-webservers
west-webservers


# mozna trzymac zmienne w folderze np.  ../ansible_project/group_vars
# i nazwa je np. all   albo    dbservers   i tam wrzucic zmienne.

############## ROLES
# folder structure   /roles/mariadb/vars/main.yml
#                    /roles/mariadb/tasks/main.yml
#                    /roles/mariadb/templates/my.cnf.j2
#                    /roles/mariadb/handlers/main.yml
# w kazdym pliku robimy fragmenty tego co powyej


############## COMMON  ##### in  /home/vagrant/myProject/roles#...
ansible-galaxy init common
# mozna wtedy wchodzic do /roles/common/tasks/ntp.yml

############## DEPENDENCIES
---
dependencies:
  - {role: apache, when: "ansible_os_family=='RedHat'"}
  
  
#  /roles/apache/tasks/main.yml
- name: install httpd and firewalld
  yum: name={{item}} state=present
  with_items:
  - httpd
  - firewalld

- name: start firewalld
  service: name=firewalld state=started enabled=yes
  
- name: insert firewalld rule for httpd
  firewalld: port={{httpd_port}}/tcp permanent=true state=enabled immediate=yes
  
- name: start httpd
  service: name=httpd state=started enabled=yes
  

# encryption
ansible-vault encrypt something.yml
# decypt
ansible-vault edit something.yml
# run encrypted
ansible-playbook playbook.yml --ask-vault-pass