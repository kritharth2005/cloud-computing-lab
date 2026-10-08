# Program 8: Deploy a Web Application on an EC2 Instance in AWS

## Aim

To deploy a web application on an EC2 instance in AWS.

## Requirements

- An AWS account

## Procedure

### Step 1: Launch an instance

1. Log in to the AWS Console, type **EC2** in the search box, open EC2, and click **Launch instance**.
2. **Name and tags:** enter `web-server`.
3. **Application and OS Images:** select **Amazon Linux** (Amazon Linux 2023, free tier eligible).
4. **Instance type:** select **t3.micro**.
5. **Key pair:** click **Create new key pair**, name it `kyp`, and click **Create key pair**. The file `kyp.pem` downloads.
6. **Network settings:** make sure all three are ticked:
   - **Allow SSH traffic from** (Anywhere)
   - **Allow HTTPS traffic from the internet**
   - **Allow HTTP traffic from the internet**
7. **Configure storage** and **Advanced details:** keep as they are.
8. Click **Launch instance**, then **View all instances**, and wait until the state is *Running*.

### Step 2: Connect to the instance

1. Select the checkbox of `web-server` and click **Connect**.
2. Choose the **EC2 Instance Connect** tab and click **Connect**. A terminal opens in the browser.

### Step 3: Install the web server and deploy the website

Type the following commands. (Shortcut: paste [`setup.sh`](setup.sh), which contains the same steps, into **Advanced details → User data** while launching the instance, and the instance sets itself up on first boot.)

```bash
sudo su -                          # switch to root user
yum update -y                      # update packages
yum install -y httpd wget unzip    # install Apache web server

mkdir temp
cd temp

# download the website template files
wget -O templatemo_596_electric_xtra.zip https://templatemo.com/download/templatemo_596_electric_xtra
ls -lrt

# extract the template
mkdir templatemo_596_electric_xtra_unzipped
unzip templatemo_596_electric_xtra.zip -d templatemo_596_electric_xtra_unzipped
cd templatemo_596_electric_xtra_unzipped/templatemo_596_electric_xtra
ls -lrt

# copy the extracted files to Apache's document root
mv * /var/www/html/
cd /var/www/html/
ls -lrt

systemctl enable httpd             # start httpd on every boot
systemctl start httpd              # start httpd now
systemctl status httpd             # check that it is active (running); press q to exit
```

### Step 4: Open the website

1. Go back to **Instances**, select `web-server`, and copy the **Public IPv4 address**.
2. In a browser, open `http://<public-ip>`. Type `http://` explicitly; the server has no HTTPS certificate, so browsers that auto-switch to `https://` will fail to load the page.
3. The deployed website opens.

### Step 5: Clean up

Select the instance → **Instance state → Terminate instance**.

## Output

`systemctl status httpd` shows:

```
● httpd.service - The Apache HTTP Server
     Loaded: loaded (/usr/lib/systemd/system/httpd.service; enabled; preset: disabled)
     Active: active (running)
```

Opening `http://<public-ip>` in the browser shows the deployed *Electric Xtra* website.

## Result

A web application was deployed on an EC2 instance using the Apache HTTP server and accessed through the instance's public IP address.
