# Program 11: Create a Video Streaming Service Using S3 and CloudFront

## Aim

To create a video streaming service using Amazon S3 for storage and Amazon CloudFront for secure content delivery.

## Requirements

- An AWS account
- A short test video in `.mp4` format (H.264), e.g. `sample.mp4`
- [`player.html`](player.html) from this folder

## Procedure

### Step 1: Set up the S3 bucket

The bucket stays private, so videos can't be downloaded directly from S3 and must be accessed through CloudFront.

1. Log in to the AWS Console and go to **Services → S3**.
2. Click **Create bucket**.
   - Enter a globally unique name, e.g. `my-video-streaming-bucket-2026`.
   - Select a region close to you, e.g. *ap-south-1* (Mumbai).
3. Configure settings:
   - **Block Public Access:** keep **Block all public access** ticked.
   - **Bucket Versioning:** **Enable**.
   - **Default encryption:** **SSE-S3**.
4. Click **Create bucket**.
5. Open the bucket, click **Upload**, add `sample.mp4` and `player.html` (if your video has a different name, change `sample.mp4` inside `player.html` first), and click **Upload**.

### Step 2: Create a CloudFront distribution

1. Open the **CloudFront** console and click **Create distribution**.
2. **Origin settings:**
   - **Origin domain:** select the S3 bucket created in Step 1.
   - **Origin access:** select **Origin access control settings (recommended)**.
   - **Origin access control:** click **Create control setting**, keep the defaults, and click **Create**.
3. **Default cache behavior:**
   - **Viewer protocol policy:** **Redirect HTTP to HTTPS**.
   - **Allowed HTTP methods:** **GET, HEAD**.
   - **Cache policy:** **CachingOptimized**.
4. **Web Application Firewall:** **Do not enable security protections**.
5. Click **Create distribution**.

### Step 3: Allow CloudFront to read the private bucket

1. After the distribution is created, a banner at the top shows **Copy policy**. Click it.
2. Go to **S3 → your bucket → Permissions → Bucket policy → Edit**.
3. Paste the copied policy and click **Save changes**.

The copied policy looks like [`bucket-policy.json`](bucket-policy.json), with your bucket name, account ID and distribution ID filled in:

```json
{
  "Version": "2008-10-17",
  "Id": "PolicyForCloudFrontPrivateContent",
  "Statement": [
    {
      "Sid": "AllowCloudFrontServicePrincipal",
      "Effect": "Allow",
      "Principal": {
        "Service": "cloudfront.amazonaws.com"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::BUCKET_NAME/*",
      "Condition": {
        "StringEquals": {
          "AWS:SourceArn": "arn:aws:cloudfront::ACCOUNT_ID:distribution/DISTRIBUTION_ID"
        }
      }
    }
  ]
}
```

Keep the `Condition` block. It limits access to **your** distribution only; without it, any CloudFront distribution in any AWS account could read your bucket.

### Step 4: Test the streaming service

1. On the CloudFront **Distributions** page, wait until **Last modified** shows a date instead of *Deploying*.
2. Copy the **Distribution domain name**, e.g. `d111111abcdef8.cloudfront.net`.
3. Open the video directly: `https://d111111abcdef8.cloudfront.net/sample.mp4`. It plays in the browser.
4. Open the player page: `https://d111111abcdef8.cloudfront.net/player.html`. The video plays inside the page.
5. To confirm the bucket is private, open the S3 object URL (`https://<bucket>.s3.<region>.amazonaws.com/sample.mp4`). It returns **AccessDenied**.

### Step 5: Clean up

Disable and then delete the CloudFront distribution, then empty and delete the S3 bucket.

## Output

- `https://<distribution>.cloudfront.net/sample.mp4` → video streams in the browser
- `https://<distribution>.cloudfront.net/player.html` → video plays in the player page
- `https://<bucket>.s3.<region>.amazonaws.com/sample.mp4` → `AccessDenied` (direct S3 access blocked)

## Result

A video streaming service was created in which videos are stored in a private S3 bucket and delivered to users securely over HTTPS through the CloudFront CDN.
