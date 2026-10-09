# Cloud Computing Lab

Lab programs for the Cloud Computing laboratory (2022 scheme). Each program has its own folder with a `README.md` (aim, procedure, code, output, result) and the code or configuration it needs.

| # | Program | Platform | Folder |
|---|---------|----------|--------|
| 1 | Install VirtualBox with different flavours of Linux / Windows OS | VirtualBox | [01-virtualbox-setup](01-virtualbox-setup) |
| 2 | Install a C compiler in a virtual machine and execute a simple program | VirtualBox + Ubuntu | [02-c-compiler-in-vm](02-c-compiler-in-vm) |
| 3 | Create an EC2 instance in AWS | AWS | [03-ec2-instance](03-ec2-instance) |
| 4 | Develop a simple application using Apex | Salesforce | [04-apex-hello](04-apex-hello) |
| 5 | Implement a mailing service using Apex | Salesforce | [05-apex-mailing](05-apex-mailing) |
| 6 | Simulate a cloud scenario in CloudSim with a scheduling algorithm not present in CloudSim | CloudSim 3.0.3 (Java) | [06-cloudsim-scheduling](06-cloudsim-scheduling) |
| 7 | Thread-based image processing application in Microsoft Azure | Azure Blob Storage (Python) | [07-azure-image-threads](07-azure-image-threads) |
| 8 | Deploy a web application on an EC2 instance | AWS | [08-ec2-web-app](08-ec2-web-app) |
| 9 | Use Google App Engine Launcher to launch web applications (deprecated; runs locally) | Google App Engine SDK 1.9.88 (Python 2.7) | [09-google-app-engine](09-google-app-engine) |
| 10 | Static website on S3 secured with signed URLs | AWS | [10-s3-static-site-signed-urls](10-s3-static-site-signed-urls) |
| 11 | Video streaming service using S3 and CloudFront | AWS | [11-video-streaming](11-video-streaming) |

## Accounts needed

- **AWS** (Programs 3, 8, 10, 11): new accounts get the AWS Free plan with credits. Use free-tier-eligible options only (e.g. `t3.micro`).
- **Salesforce** (Programs 4, 5): free Developer Edition org.
- **Microsoft Azure** (Program 7): Azure for Students gives free credits without a credit card.

## After each AWS / Azure program

Delete what you created (EC2 instances, S3 buckets, CloudFront distributions, Azure storage accounts) so it doesn't keep using credits.
