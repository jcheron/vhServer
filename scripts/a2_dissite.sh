#!/bin/bash
A2DISSITE=/usr/sbin/a2dissite
${A2DISSITE} $1
service apache2 reload
