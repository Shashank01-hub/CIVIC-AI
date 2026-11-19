#!/bin/bash
set -e
yum update -y
yum install -y docker git amazon-cloudwatch-agent
systemctl enable --now docker
usermod -aG docker ec2-user
mkdir -p /usr/local/lib/docker/cli-plugins
curl -SL "https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64" -o /usr/local/lib/docker/cli-plugins/docker-compose
chmod +x /usr/local/lib/docker/cli-plugins/docker-compose
mkdir -p /opt/smart-city
chown ec2-user:ec2-user /opt/smart-city
