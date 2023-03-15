newgrp docker;
docker build -t mysql .;
docker run --name mysql -d -p 3306:3306 mysql;
