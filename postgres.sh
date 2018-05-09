# SSH SETUP
vi /etc/ssh/sshd_config
service sshd reload

PermitRootLogin  no
AllowUsers kris   # last line, kris can connect SSH, root no



# POSTRESQL
apt-get install postgresql postgresql-contrib


sudo -i -u postgres
createuser kris -P   # as/on postgresql, must be same as user
createdb krisdb   # dropdb  to delete
exit

# we need to make sure postgresql is asking for password to log in
sudo vi /etc/postgresql/9.5/main/pg_hba.conf
# change peer to md5 at the bottom for local .... line




psql  # will log in as current user
postgres=# \conninfo
\q    # exit



# NGINX - reverse proxy, mutliple flask apps on server etc.
sudo apt-get update  # UBUNTU
sudo apt-get install nginx
# let nginx have access by firewall, so it's not blocked
sudo ufw status
sudo ufw enable
sudo ufw allow 'Nginx HTTP'
sudo ufw status  # we see it's allowed
sudo ufw allow ssh   # !!!!!!
systemctl status nginx
systemctl stop nginx  #(Start/restart)
# add config into nginx config
sudo vi /etc/ngingx/sites-available/rest.conf
server {
listen:80;
real_ip_header X-Forwarded-For;  #forward from requesttor to flask
set_rea_ip_from 127.0.0.1;
server_name localhost;

location / {
include uwsgi_params;
uwsgi_pass unix:/var/www/html/items-rest/socket.sock;   #conn point
uwsgi_modifier1 30;
}

error_page 404 /404.html;
location = /404.html {
root /usr/share/nginx/html;
}

error_page 500 502 503 504 /50x.html
location = /50x.html {
root /usr/share/nginx/html;
}
}

sudo ln -s /etc/nginx/sites-available/items-rest.conf /etc/nginx/sites-enabled/
sudo mkdir /var/www/html/items-rest
sudo chown kris:kris /var/www/html/items-rest
cd /var/www/html/items-rest
git clone https://github.com/schoolofcode-me/stores-rest-api.git .
mkdir log
# installing flask and our application
sudo apt-get install python-pip python3-dev libpq-dev
pip install virtualenv
virtualenv venv --python=python3.5
source venv/bin/activate
pip install -r requirements.txt


#create ubuntu service , descriptor of program, run, restart, etc...
sudo vi /etc/systemd/system/uwsgi_items_rest.service
[Unit]
Description=uWSGI items rest

[Service]
Environment=DATABASE_URL=postgres://kris:1234@localhost:5432/krisdb
ExecStart=/var/html/items-rest/venv/bin/uwsgi--master --emperor /var/www/html/items-rest/uwsgi.ini --die-on-term --uid kris --gid kris --logto /var/www/html/items-rest/log/emperor.log
Restart=always
KillSignal=SIGQUIT
Type=notify
NotifyAccess=all

[Install]
WantedBy=multi-user.target   #bootstrap



## edit  vi /var/www/html/items-rest/uwsgi.ini
[uwsgi]
base = /var/www/html/items-rest
app = run
module = %(app)
home = %(base)/venv
pythonpath = %(base)
socket = %(base)/socket.sock
chmod-socket = 777
processes = 8
threads = 8
harakiri = 15  #seconds to destroy thread and restart
callable = app
logto = /var/www/html/items-rest/log/%n.log    # %n = uwsgi


# start service!
sudo sustemcrl start uwsgi_items_rest
vi log/uwsgi.log

# delete default config
sudo rm /etc/nginx/sites-enabled/default
sudo rm /etc/nginx/sites-available/default
sudo systemctl reload nginx
sudo systemctl restart nginx
sudo systemctl start uwsgi_items_rest


# Set up SSL NGINX
sudo mkdir /var/www/ssl
sudo touch /var/www/ssl/rest-api-course.trial.com.pem
sudo touch /var/www/ssl/rest-api-course.trial.com.key
# copy origin > pem,    private key -> key
sudo vi  /etc/nginx/sites-enabled/items-rest.conf
listen 443 default_server  # change from 80
server_name rest-api-course-trial.com
ssl on;
ssl_certificate /var/www/ssl/rest-api-course-trial.com.pem
ssl_certificate_key /var/www/ssl/rest-api-course-trial.com.key
#after closing braket for server block }

server {  # usun server_name localhost z gory
  listen 80;
  server_name rest-api-course-trial.com ;
  rewrite ^/(.*) https://rest-api-course-trial.com/$1 permanent;
}

sudo ufw allow https
sudo ufw reload


# digital ocean
https://m.do.co/c/d54c088544ed   , 2 free months

# register domain
www.namecheap.com

www.cloudflare.com
