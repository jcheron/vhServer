#!/bin/bash
A2ENSITE=/usr/sbin/a2ensite
${A2ENSITE} $1
service apache2 reload
