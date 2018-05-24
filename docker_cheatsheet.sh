# INSTALLATION
curl -sSL https://get.docker.com/ | sh
sudo groupadd docker
sudo gpasswd -a $USER docker    # adding self to group
sudo usermod -aG docker $USER   # adding self to group

tmux new -s my_docker # Detach the Tmux session by typing "Ctrl+b" and then "d".
docker run --rm -it -p 8888:8888 -p 50070:50070 -p 8088:8088 bigdatateam/hdfs-notebook # coursera
tmux a -t my_docker   # attach tmux session       tmux ls


# A container runs natively on Linux and shares the kernel of the host machine with other containers.
# It runs a discrete process, taking no more memory than any other executable, making it lightweight.

Your new login password is 943675837542# LIST DOWNLOADED MACHINES
#https://docs.docker.com/engine/reference/run/#kernel-memory-constraints
#Images can share layers to optimize disk usage, transfer times, and memory use.
#Image is a read-only filesystem
#A new image is created by stacking the new layer on top of the old image
#Container is copy of image

# CHANGE DEFAULT 1CPU/2GB RAM:
docker-machine stop
VBoxManage modifyvm default --cpus 2
VBoxManage modifyvm default --memory 4096
docker-machine start
docker run -it --memory-reservation 1G <name> /bin/bash

# OR CREATE NEW MACHINE (VMBOX)
docker-machine rm default
docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=2 --virtualbox-disk-size=50000 default



# ENABLE DOCKER @ BOOT
sudo systemctl enable docker
sudo chkconfig docker on

# ENABLE DOCKER TO LISTEN FOR CONNECTIONS
# edit   /etc/docker/daemon.json
{ "hosts": ["fd://", "tcp://0.0.0.0:2375"] }
{   "dns": ["your_dns_address", "8.8.8.8"] }  # if problems with pip,   set up own DNS
ps aux | grep docker   --> must be -H ...., if not then follow below steps:
sudo systemctl edit docker.service
ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:2375
# reload
sudo systemctl daemon-reload
sudo systemctl restart docker.service



#############################################################################################
# RUN CONTAINER
#############################################################################################
docker run busybox echo Hello
docker run -it --tty ubuntu bash    # interactive ubuntu session terminal
docker run -d jpetazzo/clock        # run container in background (detach)
docker run -d --name=<name> tobert/cassandra
docker run -d -p 80:80 --name=<name> nginx   #  -P is for --publish-all on RANDOM ports
docker run -it -p 80:80 --name=<name> nginx <overwrite command e.g. bash>

docker logs --tail 5 <container_ID>     # will display output/logs from background container
docker logs --tail 5 --follow
docker history <image>

docker ps           # currently running
docker ps -a        # history    -q   just ID

docker image ls
docker container ls # list RUNNING containers    -a  |  --all    list all

docker kill     # kills immediately
docker stop     # send TERM, waits 10 seconds
docker container stop <container_ID>

docker container stop <name>
docker container rm <name1> <name2>

docker-machine rm my-docker-machine
docker rm $(docker ps -a)                               # Remove all containers
docker container rm $(docker container ls -a -q)        # Remove all containers
docker rmi $(docker images)                             # Remove all images from this machine
docker image rm $(docker image ls -a -q)                # Remove all images from this machine

# DOWNLOAD IMAGE
docker pull debian:jessie
docker pull ubuntu  (default: latest)
# difference between container and base image
docker diff <container_ID>  # we must run at least one, latest   docker ps -l



#############################################################################################
# CONTAINTER NETWORKING
#############################################################################################
docker run -d --publish-all jpetazzo/web    # -P  publush all exposed ports to random ports

docker port <container_ID>  # will return all mappings
docker port <container_ID> <container_port> # will return host port for given container port

docker inspect --format '{{ .NetworkSettings.IPAddress }}' <container_ID>  # -f

docker-machine ip  # on Windows Docker Toolbox, that gives virtualbox IP, which is to be used instead of 0.0.0.0 or 127.0.0.1



#############################################################################################
# VOLUMES
#############################################################################################
# Can be declared in 2 ways:
VOLUME /var/lib/postresql       # Dockerfile
docker run -d  --name=alpha -v /var/myvolume  training/postgresql bash
docker run -it --name=beta --volumes-from alpha training/postgresql bash
docker run -it -v [host-path]:[container-path]:[rw|ro]  #   -v "$(pwd)" :/opt/names

docker run -it -v /var/run/docker.sock:/docker.sock ubuntu bash # bad ideas a container gets root



#############################################################################################
# LINKING CONTAINERS (providing DNS entry...)
#############################################################################################
docker run -d --name=redisserver redis
docker run -it --name=redisclient1 --link redisserver:redissrv redis bash
cat /etc/hosts  # we can see server ip, and we can ping it
# we can now start redic command line on client and connect to redis srv
redis-cli -h redissrv # name from link...



#############################################################################################
# UPLOAD TO HUB, SHARE
#############################################################################################
docker login
docker tag image username/repository:tag
docker tag mywebsite <my_login>/mywebsite
odcker push <my_login>/mywebsite
dockier logout # drops credentials
# login to own registry
docker login registry.example.com



# CREATE NEW IMAGE MANUAL (from last container)
docker commit <existing container_ID> <tag>  # change tag by:     docker tag <image ID> <tag>

#############################################################################################
# CREATE NEW IMAGE AUTOMATIC
#############################################################################################
mkdir myimage && cd myimage
vim Dockerfile
FROM centos:6.6
RUN yum install -y wget
ADD wwwfolder /var/www/site     # copy from HOST to CONTAINER
EXPOSE 80   # expose port 80, to be reached by port 80
CMD wget -0- -q http://ifconfig.me/ip            # -0-  to console, only last CMD is executed
ENTRYPOINT ["wget", "-0-", "-q"]        # param from cmd will be appended
CMD default parameter for ENTRYPOINT # if used together, e.g. http://ifconfig.me/ip
if we type any command in command line, then we overwrite this CMD !!!

docker build -t <tag> .
docker build -t <dockerhubUsername>/web git://github.com/docker-training/staticweb.git

# https://www.docker.com/use-cases/cicd
docker build --pull=true -t dtr.mikegcoleman.com/hello-jenkins:$GIT_COMMIT .        # build docker image
docker run -i --rm dtr.mikegcoleman.com/hello-jenkins:$GIT_COMMIT /script/test      # test docker image
docker push  dtr.mikegcoleman.com/hello-jenkins:$GIT_COMMIT  # if tests successful, push image to docker trusted registry



#############################################################################################
# Dockerfile COMMANDS
#############################################################################################
FROM ubuntu
FROM ubuntu:12.04
FROM python:2.7-slim
FROM training/sinatra
FROM localhost:5000/funtoo

MAINTAINER Docker Education Team <education@docker.com>

RUN apt-get update          # with shell wrapping   /bin/sh -c
RUN ["apt-get", "update"]   # using exec method, for images without /bin/sh
RUN will not start daemons, use CMD and/or ENTRYPOINT

EXPOSE 8080 # all ports are private by default
docker run -P # all ports declared with EXPOSE become public

ADD /src/webapp /opt/webapp  # <source> <image>  , /src is relative to dir containing Dockerfile
ADD . /app                  # copy current dir into container's /app
ADD http://www.example.com/webapp /opt/  # will download file ,  zip/tar will be unzipped! , URL not
files are owned by root with 0755
COPY ....same as ADD

VOLUME [ "/opt/webapp/data" ]   # will create data volume mount point,  not captured by commit

WORKDIR /app                    # sets working dir for subsequent commands

ENV WEBAPP_PORT 8080    # specified env variables that should be set in launched containers
docker run -e WEBAPP_PORT=8080 -e WEBAPP_HOST=www.example.com

USER    # sets user name or UID

CMD nginx -g "daemon off;"              # executed in a shell
CMD [ "nginx", "-g", "daemon off;" ]    # dxecuted directly without shell processing

ENTRYPOINT [ "/bin/ls"]     # arguments given are appended to entry point
docker run -it --entrypoint bash <image ID> # this will overwrite ENTRYPOINT in Dockerfile

ONBUILD # it's a trigger, sets instructions that will be executed when another image
# is build from the image being built, useful for binding images used as a base
ONBUILD ADD . /app/src


# 1. RUBY
FROM ruby
RUN apt-get update -qq && apt-get install -y build-essential libpq-dev
RUN mkdir /myapp
WORKDIR /myapp
ADD Gemfile /myapp/Gemfile
RUN bundle install -j8
EXPOSE 3000
ADD . /myapp
ADD ./webapp/requirements.txt /tmp/requirements.txt # first copy
RUN pip install -qr /tmp/requirements.txt           # then install updated
CMD ["bundle", "exec", "rails", "s"]

# AMBASSADORS - portability

# AUTOMATED - Every time you make a commit to git repo a new version of the image will be built
# EXAMPLE
# Dockerfile    https://github.com/docker-training/webapp
FROM ubuntu:14.04
MAINTAINER Docker Education Team <education@docker.com>
RUN apt-get update
RUN DEBIAN_FRONTEND=noninteractive apt-get install -y -q python-all python-pip
ADD ./webapp/requirements.txt /tmp/requirements.txt
RUN pip install -qr /tmp/requirements.txt
ADD ./webapp /opt/webapp/
WORKDIR /opt/webapp
EXPOSE 5000
CMD ["python", "app.py"]

# RUN JENKINS (Dockerfile used to build this image https://github.com/docker-training/jenkins/blob/master/Dockerfile)
$ docker run -d --name=jenkins -p 8080:8080 \
-v /var/run/docker.sock:/var/run/docker.sock \
-e DOCKERHUB_ID=<Your Docker Hub ID> \
-e DOCKERHUB_EMAIL=<Your Docker Hub Email> \
-e GITHUB_ID=<Your GitHub ID> \
nathanleclaire/jenkin

### CASSANDRA
docker exec -it <name> nodetool status  # those are cassandra's tools
docker exec -it <name> nodetool ring  # we can see list of vnodes and last token range associated with it
docker exec -it n1 /bin/bash
vi /data/conf/cassandra.yaml      #num_tokens = 256 
# running second node
docker run --name=n2 -d tobert/cassandra -seeds <ip of running already>



#############################################################################################
# FIG
#############################################################################################
# with fig we define set of containers to boot up and their runtime properties in YAML
# INSTALL 1
curl -L https://github.com/docker/fig/releases/download/0.5.2/linux \
> /usr/local/bin/fig
chmod +x /usr/local/bin/fig
# INSTALL ALTERNATIVE
sudo pip install -U fig
# CLONE SOURCE CODE
cd
git clone https://github.com/docker-training/simplefig
cd simplefig
# CREATE DOCKERFILE
FROM python:2.7
ADD requirements.txt /code/requirements.txt
WORKDIR /code
RUN pip install --trusted-host pypi.python.org -r requirements.txt
ADD . /code
# CREATE fig.yml
web:
  build: .
  command: python app.py
  ports:
   - "5000:5000"
  volumes:
   - .:/code
  links:
   - redis
redis:
  image: orchardup/redis
# FIG
fig build        # rebuild
fig up -d       # run in background
fig ps          # see currently runnig services
fig rm          # remove existing services



#############################################################################################
# DOCKER API
#############################################################################################
# The API binds locally to unix:///var/run/docker.sock but can also be bound to a network interface.
# • Not authenticated by default.
# • Securable with certificates.

# CREATING CONTAINER, api will return containerID
$ curl -X POST -H 'Content-Type: application/json' \
http://localhost:2375/containers/create \
-d '{
"Cmd":["echo", "hello world"],
"Image":"busybox"
}'
{"Id":"<yourContainerID>","Warnings":null}

# START new container
$ curl -X POST -H 'Content-Type: application/json' \
http://localhost:2375/containers/<yourContainerID>/start \
-d '{}'

# INSPECT CONTAINER
$ curl --silent -X GET http://localhost:2375/containers/<yourContainerID>/json | python -mjson.tool

# WAIT IF TAKES LONGER
# But for containers running for a longer period of time, we can call the wait endpoint.
# The wait endpoint also gives the exit status of the container.
$ curl --silent -X POST http://localhost:2375/containers/<yourContainerID>/wait
{"StatusCode":0}

# VIEW CONTAINER OUTPUT LOGS, we can simulare tail -f ...
$ curl --silent http://localhost:2375/containers/<yourContainerID>/logs?stdout=1

# STOPPING (success: code 204)
$ curl --silent -X POST http://localhost:2375/containers/<yourContainerID>/stop

# RETURNS A HASH OF ALL IMAGES
$ curl -X GET http://localhost:2375/images/json?all=0
# SEARCHING 
$ curl -X GET http://localhost:2375/images/search?term=training
# ADD IMAGE TO DOCKER HOST
$ curl -i -v -X POST \
http://localhost:2375/images/create?fromImage=training/namer
{"status":"Pulling repository training/namer"}





#############################################################################################
# SECURING DOCKER
#############################################################################################
# 1. initialize the CA serial file and generate CA private and public keys:
$ echo 01 > ca.srl
$ openssl genrsa -des3 -out ca-key.pem 2048
$ openssl req -new -x509 -days 365 -key ca-key.pem -out ca.pem
# We will use the ca.pem file to sign all of the other keys later.

# 2. Create and Sign the Server Key
# Now that we have a CA, we can create a server key and certificate signing request. Make
# sure that CN matches the hostname you run the Docker daemon on:
$ openssl genrsa -des3 -out server-key.pem 2048
$ openssl req -subj '/CN=**<Your Hostname Here>**' -new -key server-key.pem -out server.csr
$ openssl rsa -in server-key.pem -out server-key.pem

# 3. Next we're going to sign the key with our CA:
$ openssl x509 -req -days 365 -in server.csr -CA ca.pem -CAkey ca-key.pem -out server-cert.pem

# 4. Create and Sign the Client Key
$ openssl genrsa -des3 -out client-key.pem 2048
$ openssl req -subj '/CN=client' -new -key client-key.pem -out client.csr
$ openssl rsa -in client-key.pem -out client-key.pem
# To make the key suitable for client authentication, create a extensions config file:
$ echo extendedKeyUsage = clientAuth > extfile.cnf
# Now sign the key:
$ openssl x509 -req -days 365 -in client.csr -CA ca.pem -CAkey ca-key.pem \
-out client-cert.pem -extfile extfile.cnf

# 5. Configuring the Docker Daemon for TLS
# • By default, Docker does not listen on the network at all.
# • To enable remote connections, use the -H flag.
# • The assigned port for Docker over TLS is 2376.
$ sudo docker -d --tlsverify
--tlscacert=ca.pem --tlscert=server-cert.pem
--tlskey=server-key.pem -H=0.0.0.0:2376
# Note: You will need to modify the startup scripts on your server for this to be
# permanent! The keys should be placed in a secure system directory, such as /etc/docker


# 6. Configuring the Docker Client for TLS
# If you want to secure your Docker client connections by default, you can move the key
# files to the .docker directory in your home directory. Set the DOCKER_HOST variable as well.
$ cp ca.pem ~/.docker/ca.pem
$ cp client-cert.pem ~/.docker/cert.pem
$ cp client-key.pem ~/.docker/key.pem
$ export DOCKER_HOST=tcp://:2376
# Then you can run docker with the --tlsverify option.
$ docker --tlsverify ps


#############################################################################################
# SERVICES
#############################################################################################
# docker-compose.yml
version: "3"
services:
  web:
    # replace username/repo:tag with your name and image details
    image: username/repo:tag
    deploy:
      replicas: 5
      resources:
        limits:
          cpus: "0.1"       # each of 5 services to use at most 10% CPU across all cores
          memory: 50M
      restart_policy:
        condition: on-failure
    ports:
      - "80:80"
    networks:
      - webnet
##### addition
  visualizer:
    image: dockersamples/visualizer:stable
    ports:
      - "8080:8080"
    volumes:
      - "/var/run/docker.sock:/var/run/docker.sock"
    deploy:
      placement:
        constraints: [node.role == manager]               # ensuring that this service only ever runs on a swarm manager -- never a worker.
    networks:
      - webnet
####  
  redis:
    image: redis
    ports:
      - "6379:6379"
    volumes:
      - "/home/docker/data:/data"           # remember to create ./data on manager !!   docker-machine ssh myvm1 "mkdir ./data"
    deploy:
      placement:
        constraints: [node.role == manager]
    command: redis-server --appendonly yes
    networks:
      - webnet
##### addition END
networks:
  webnet:       # load balancing!

# enable swarm mode and make your machine swarm manager
docker swarm init
# start single service stack (or RESTART...no need to kill etc, just run same command)
docker stack deploy -c docker-compose.yml getstartedlab
docker stack deploy --with-registry-auth -c docker-compose.yml getstartedlab    # if private registry used
# get running service info
docker stack ls
docker service ls   # list running services associated with app
# service is called   <name>_<service> , e.g.: getstartedlab_web
# single container running service is called TASK, listing tasks of service:
docker service ps getstartedlab_web
# tasks also show up in all containers list
docker container ls -q -a
# take down app
docker stack rm getstartedlab
# take down swarm
docker swarm leave --force        # leave on worker    --force  on manager


#############################################################################################
# SWARM (is a group of machines that are running Docker and joined into a cluster)
#############################################################################################
# CREATE COUPLE OF VM
docker-machine create --driver virtualbox myvm1         # manager
docker-machine create --driver virtualbox myvm2         # worker
# send commands to machine
docker-machine ssh myvm1 "docker swarm init --advertise-addr <myvm1 ip>" # if issues use native ssh:    docker-machine --native-ssh ssh myvm1 ...
# IP mozna zdobyc   docker-machine ip myvm1          albo             docker-machine inspect --format '{{ .Driver.IPAddress }}' myvm1
# to add worker to this swarm run command:              # to add manager run :      'docker swarm join-token manager'
docker swarm join --token <token> <myvm1 ip>:2377
# so we run it on our second VM, to join it to the swarm
docker-machine ssh myvm2 "docker swarm join --token <token> <ip>:2377"
# view nodes in the swatm 
docker-machine ssh myvm1 "docker node ls"     docker node inspect <node>           docker swarm join-token -q worker
# leave swarm
docker swarm leave

# we can also configure local shell to talk to VM, this is "better" as allows copy/use local file to deploy
# check where are we connected
docker-machine ls   (or docker-machine active)
# this command will make a selected VM the active/default one!
eval $(docker-machine env myvm1)
# next just run command and it's deployed on swarm cluster, it must be executed on manager!
docker stack deploy -c docker-compose.yml getstartedlab
# check how tasks have been distributed between myvm1 and myvm2
docker stack ps getstartedlab

# to copy files across machines we can use
docker-machine scp <file> <machine>:~           # Copy file to node's home dir (only required if you use ssh to connect to manager and deploy the app)
docker-machine scp docker-compose.yml myvm1:~   # Copy file to node's home dir (only required if you use ssh to connect to manager and deploy the app)

# unsetting docker-machine shell variable setting
eval $(docker-machine env -u)      # none VM is active/default, we can use native docker commands again

docker-machine stop $(docker-machine ls -q)               # Stop all running VMs
docker-machine rm $(docker-machine ls -q) # Delete all VMs and their disk images




https://youtu.be/DQwyIpDcLAk?t=2051



docker pull bigdatateam/spark-course2
https://www.coursera.org/learn/big-data-analysis/home/week/6