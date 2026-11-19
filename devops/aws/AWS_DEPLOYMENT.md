# AWS Deployment

## 1. Networking and database
1. Create a VPC with public subnets for EC2 and private subnets for RDS.
2. Create an RDS MySQL 8 instance. Add database `smart_city`, set a strong master password, and use a security group allowing MySQL port 3306 only from the EC2 security group.
3. Create EC2 security groups: inbound TCP 22 from your IP, TCP 80 and 443 from `0.0.0.0/0`, outbound allowed. Do not expose MySQL publicly.
4. Attach an Elastic IP to the EC2 instance for a stable address.

## 2. EC2 and application
1. Launch an Amazon Linux 2023 EC2 `t3.small` or larger with the EC2 security group.
2. Attach an IAM instance profile for S3 and CloudWatch permissions.
3. Run `user-data.sh` during launch or execute it after SSH.
4. Copy this repository and `.env.example` to `/opt/smart-city/.env`, set RDS endpoint, credentials, JWT secret, Gemini key, and S3 settings.
5. Set `SPRING_DATASOURCE_URL` and credentials in `.env` or an EC2 Compose override.
6. Run `docker compose up -d --build`.

## 3. S3 and IAM
Create an S3 bucket and enable block public access. Configure IAM permissions `s3:PutObject`, `s3:GetObject`, and `s3:DeleteObject` for the bucket ARN, and attach that policy to the EC2 instance role. Set `STORAGE_TYPE=s3`.

## 4. HTTPS and CloudWatch
Use an Application Load Balancer or Caddy/Nginx and ACM for HTTPS. Install the CloudWatch agent with `amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c ssm:/smart-city/cloudwatch -s`. Send Docker logs and EC2 metrics to CloudWatch; create alarms for CPU, status checks, disk, and application health at `/actuator/health`.
