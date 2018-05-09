Your new login password is 943675837542# LIST DOWNLOADED MACHINES
#https://docs.docker.com/engine/reference/run/#kernel-memory-constraints

# CHANGE DEFAULT 1CPU/2GB RAM:
docker-machine stop
VBoxManage modifyvm default --cpus 2
VBoxManage modifyvm default --memory 4096
docker-machine start

# OR CREATE NEW MACHINE (VMBOX)
docker-machine rm default
docker-machine create -d virtualbox --virtualbox-memory=4096 --virtualbox-cpu-count=2 --virtualbox-disk-size=50000 default


docker image ls
docker container ls # list RUNNING containers

docker container stop <name>
docker container rm <name1> <name2>

# interactive ubuntu session terminal
> docker run --interactive --tty ubuntu bash

#Pull and run a Dockerized nginx web server that we name, webserver:
> docker run --detach --publish 80:80 --name webserver nginx

# removing machine

docker-machine rm my-docker-machine

### CASSANDRA
# downlaoding cassandra node
docker run --name=n1 -d tobert/cassandra

docker exec -it <name> nodetool status  # those are cassandra's tools
docker exec -it <name> nodetool ring  # we can see list of vnodes and last token range associated with it
docker run -it --memory-reservation 1G <name> /bin/bash

docker exec -it n1 /bin/bash
vi /data/conf/cassandra.yaml      #num_tokens = 256 

#check IP of running container
docker inspect -f '{{ .NetworkSettings.IPAddress }}' <name>

# running second node
docker run --name=n2 -d tobert/cassandra -seeds <ip of running already>
