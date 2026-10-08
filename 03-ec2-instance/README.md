# Program 3: Create an EC2 Instance in AWS

## Aim

To create an EC2 instance in Amazon Web Services and connect to it using SSH.

## Requirements

- An AWS account

## Procedure

### Part A: Launch the instance

1. Log in to the AWS Management Console. In the top-right corner, select a region close to you (e.g. **Asia Pacific (Mumbai)**).
2. Search for **EC2** in the search bar and open it. Click **Launch instance**.
3. **Name and tags:** enter a name, e.g. `my-first-instance`.
4. **Application and OS Images (AMI):** select **Amazon Linux** (or Ubuntu). Make sure it is marked *Free tier eligible*.
5. **Instance type:** select **t3.micro** (free tier eligible). Do not pick other types, as they are billed.
6. **Key pair (login):** click **Create new key pair**, name it (e.g. `kyp`), keep type *RSA* and format *.pem*, and click **Create key pair**. The file `kyp.pem` downloads. Keep it safe; it cannot be downloaded again.
7. **Network settings:** keep the defaults, with **Allow SSH traffic from** enabled.
8. **Configure storage:** keep the default (8 GB gp3). Free tier allows up to 30 GB.
9. Check the **Summary** panel and click **Launch instance**.
10. Click **View all instances** and wait until **Instance state** is *Running* and **Status check** shows *2/2 checks passed*.

### Part B: Connect using the SSH key

1. Select the instance and click **Connect** → **SSH client** tab. Note the example command and the **Public IPv4 address**.
2. Open a terminal (Linux/macOS) or PowerShell (Windows 10/11) in the folder where `kyp.pem` was downloaded.
3. Restrict the key's permissions (Linux/macOS only; SSH refuses keys readable by others):

   ```bash
   chmod 400 kyp.pem
   ```

4. Connect, replacing the IP with your instance's public IP:

   ```bash
   ssh -i kyp.pem ec2-user@<public-ip>
   ```

   Use `ubuntu@<public-ip>` instead if you chose an Ubuntu AMI. Type `yes` when asked to confirm the host fingerprint.

5. Once connected, verify:

   ```bash
   whoami
   uname -a
   ```

> **Alternative:** in the **Connect** page, the **EC2 Instance Connect** tab opens a terminal in the browser without needing the `.pem` file.

### Part C: Clean up

Select the instance → **Instance state → Terminate instance** when you are done.

## Output

```
   ,     #_
   ~\_  ####_        Amazon Linux 2023
  ~~  \_#####\
  ~~     \###|
  ~~       \#/ ___
   ~~       V~' '->
    ~~~         /
      ~~._.   _/
         _/ _/
       _/m/'
[ec2-user@ip-172-31-xx-xx ~]$ whoami
ec2-user
```

## Result

An EC2 instance was created in AWS and accessed from a local terminal using SSH with a key pair.
