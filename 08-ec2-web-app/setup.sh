#!/bin/bash
# Program 8: installs Apache (httpd) on Amazon Linux and deploys a website template.
# Run as root (sudo su -), or paste into EC2 "Advanced details -> User data" at launch.
set -e

TEMPLATE=templatemo_596_electric_xtra

yum update -y
yum install -y httpd wget unzip

mkdir -p /root/temp
cd /root/temp

# Download and extract the website template
wget -O "$TEMPLATE.zip" "https://templatemo.com/download/$TEMPLATE"
mkdir -p "${TEMPLATE}_unzipped"
unzip -o "$TEMPLATE.zip" -d "${TEMPLATE}_unzipped"

# Copy the site into Apache's document root
cp -r "${TEMPLATE}_unzipped/$TEMPLATE/"* /var/www/html/

# Start Apache now and on every boot
systemctl enable httpd
systemctl start httpd
systemctl status httpd --no-pager
