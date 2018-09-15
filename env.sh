#!/usr/bin/env bash


# config table schema :  (id , _key ,_value , desc)


# mysql database user (required)
export CONF_DB_USER=mysql

# mysql password for user (required)
export CONF_DB_PASSWORD=mysql

# mysql databses host ,port , database (if not set, CONF_DB_URL instead)
export CONF_DB_HOST=172.20.31.108
export CONF_DB_PORT=3306
export CONF_DB_DATABASE=information

# mysql database jdbcurl( if not set ,(CONF_DB_HOST, CONF_DB_PORT,CONF_DB_DATABASE) instead )

# export CONF_DB_DATABASE = jdbc:mysql://host:port/database

# mysql config table name (defaut config)
export CONF_DB_TABLE=config	

