# Program 10: Deploy a Static Web Application Using S3 and Secure It with Signed URLs

## Aim

To deploy a static web application using Amazon S3 and secure its private content with signed URLs.

## Requirements

- An AWS account
- The website files in [`site/`](site): `index.html`, `style.css` (public) and `secret.html` (private)

## Procedure

### Step 1: Create an S3 bucket

1. In the AWS Console, open **S3** and click **Create bucket**.
2. Select an **AWS Region** (e.g. *Asia Pacific (Mumbai) ap-south-1*).
3. Enter a globally unique **Bucket name**, e.g. `cclab-static-site-<yourname>`.
4. Under **Block Public Access settings**, untick **Block all public access** and tick the acknowledgement.
5. Leave the rest as default and click **Create bucket**.

### Step 2: Enable static website hosting

1. Open the bucket → **Properties** → scroll to **Static website hosting** → **Edit**.
2. Select **Enable**, hosting type **Host a static website**.
3. **Index document:** `index.html`.
4. Click **Save changes**. Note the **Bucket website endpoint** shown at the bottom of the Properties tab.

### Step 3: Add the bucket policy

1. Open **Permissions** → **Bucket policy** → **Edit**.
2. Paste the policy below (from [`bucket-policy.json`](bucket-policy.json)), replace `BUCKET_NAME` with your bucket name, and click **Save changes**.

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadForWebsiteFiles",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": [
        "arn:aws:s3:::BUCKET_NAME/index.html",
        "arn:aws:s3:::BUCKET_NAME/style.css"
      ]
    }
  ]
}
```

This allows the public to **read** only the two website files. Every other object in the bucket, including `secret.html`, stays private.

> The lab manual's policy uses `"Action": "s3:*"` for everyone. That lets anyone on the internet delete or upload files in your bucket. Allow only `s3:GetObject`.

### Step 4: Upload the website files

1. Open the bucket and click **Upload**.
2. Drag and drop `index.html`, `style.css` and `secret.html` from the [`site/`](site) folder and click **Upload**.
3. Open the **Bucket website endpoint** in a browser (`http://<bucket>.s3-website.<region>.amazonaws.com`). The website loads.
4. Click the link to `secret.html` on the page. It returns **403 Forbidden** because the file is private.

### Step 5: Secure access with a signed URL

A signed (pre-signed) URL gives temporary access to a private object without making it public.

1. In the AWS Console, click the **CloudShell** icon (`>_`) in the top bar. A terminal opens with your credentials already set up.
2. Generate a signed URL valid for 120 seconds (use your bucket name and region):

   ```bash
   aws s3 presign s3://BUCKET_NAME/secret.html --expires-in 120 --region ap-south-1
   ```

3. Copy the printed URL and open it in a browser. The private page loads.
4. Wait for 2 minutes and reload the same URL. It now fails with **AccessDenied — Request has expired**.

### Step 6 (optional): Serve the site through CloudFront

To serve the website over HTTPS from edge locations with lower latency:

1. Open **CloudFront** and click **Create distribution**.
2. **Origin domain:** paste the S3 **bucket website endpoint** without `http://`. If prompted, choose **Use website endpoint**.
3. **Viewer protocol policy:** **Redirect HTTP to HTTPS**. **Allowed HTTP methods:** **GET, HEAD**.
4. Click **Create distribution** and wait for it to finish deploying.
5. Open `https://<distribution-domain>.cloudfront.net`. The website loads over HTTPS.

### Step 7: Clean up

Disable and delete the CloudFront distribution (if created), then empty and delete the S3 bucket.

## Output

- `http://<bucket>.s3-website.<region>.amazonaws.com` → website loads
- `http://<bucket>.s3-website.<region>.amazonaws.com/secret.html` → `403 Forbidden`
- Signed URL from `aws s3 presign`:

  ```
  https://BUCKET_NAME.s3.ap-south-1.amazonaws.com/secret.html?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=...&X-Amz-Date=...&X-Amz-Expires=120&X-Amz-SignedHeaders=host&X-Amz-Signature=...
  ```

  → private page loads; after 120 seconds → `AccessDenied: Request has expired`

## Result

A static website was deployed on Amazon S3 with only its public files readable by everyone. Private content was secured and shared with time-limited signed URLs.
