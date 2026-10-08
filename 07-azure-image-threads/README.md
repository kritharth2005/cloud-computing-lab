# Program 7: Thread-Based Image Processing Application in Microsoft Azure

## Aim

To implement a thread-based image processing application in Microsoft Azure.

## Requirements

- A Microsoft Azure account (Azure for Students works)
- Python 3.8 or later
- A few sample images (`.jpg` / `.png`)

## Procedure

### Step 1: Create an Azure account

1. Go to <https://portal.azure.com>.
2. Sign in, or create an account (students can activate **Azure for Students**).

### Step 2: Create a storage account

1. In the Azure Portal, search for **Storage accounts** and click **Create**.
2. Fill in the details:
   - **Resource group:** click *Create new*, e.g. `image-lab-rg`
   - **Storage account name:** a globally unique lowercase name, e.g. `imagestorage<yourinitials>123`
   - **Region:** the nearest one, e.g. *Central India*
   - **Redundancy:** *Locally-redundant storage (LRS)*
3. Click **Review + create → Create**, then **Go to resource**.

### Step 3: Create a blob container

1. In the storage account, open **Data storage → Containers**.
2. Click **+ Container**, name it `images`, keep access level **Private**, and click **Create**.

### Step 4: Upload sample images

1. Open the `images` container and click **Upload**.
2. Select several image files and click **Upload**.

### Step 5: Get the connection string

1. In the storage account, open **Security + networking → Access keys**.
2. Click **Show** next to **Connection string** under *key1* and copy it.

### Step 6: Install the required libraries

```bash
pip install -r requirements.txt
```

### Step 7: Write the multithreaded program

Save the program below as [`app.py`](app.py). It uses a `ThreadPoolExecutor` so several images are downloaded, resized, and uploaded at the same time by different threads.

### Step 8: Run the application

Either paste the connection string into `CONNECTION_STRING` in `app.py`, or set it as an environment variable:

```bash
# Linux / macOS
export AZURE_STORAGE_CONNECTION_STRING="<your connection string>"
# Windows PowerShell
$env:AZURE_STORAGE_CONNECTION_STRING="<your connection string>"

python app.py
```

### Step 9: Verify the output

1. In the Azure Portal, open the `images` container again.
2. New blobs named `processed_<name>.jpg` are present. Click one and choose **Download** to check that it is 200×200.

### Step 10: Clean up

Delete the resource group (`image-lab-rg`) to remove the storage account and its data.

## Program — [`app.py`](app.py)

```python
import io
import os
import threading
from concurrent.futures import ThreadPoolExecutor

from azure.storage.blob import BlobServiceClient
from PIL import Image

# Paste the connection string from Storage Account -> Access keys,
# or set the AZURE_STORAGE_CONNECTION_STRING environment variable.
CONNECTION_STRING = os.environ.get("AZURE_STORAGE_CONNECTION_STRING", "YOUR_CONNECTION_STRING")
CONTAINER_NAME = "images"
OUTPUT_PREFIX = "processed_"
MAX_THREADS = 8

blob_service_client = BlobServiceClient.from_connection_string(CONNECTION_STRING)
container_client = blob_service_client.get_container_client(CONTAINER_NAME)


def process_image(blob_name):
    # Download the image
    data = container_client.download_blob(blob_name).readall()

    # Resize; convert to RGB so PNGs with transparency can be saved as JPEG
    img = Image.open(io.BytesIO(data)).convert("RGB")
    img = img.resize((200, 200))

    output = io.BytesIO()
    img.save(output, format="JPEG")
    output.seek(0)

    # Upload the processed image
    new_name = OUTPUT_PREFIX + os.path.splitext(blob_name)[0] + ".jpg"
    container_client.upload_blob(new_name, output, overwrite=True)
    print(f"[{threading.current_thread().name}] {blob_name} -> {new_name}")


def main():
    # Skip images this script already produced, so a second run doesn't re-process them
    blob_names = [b.name for b in container_client.list_blobs()
                  if not b.name.startswith(OUTPUT_PREFIX)]

    # A fixed pool of threads instead of one thread per image
    with ThreadPoolExecutor(max_workers=MAX_THREADS, thread_name_prefix="worker") as pool:
        list(pool.map(process_image, blob_names))

    print(f"All {len(blob_names)} images processed successfully")


if __name__ == "__main__":
    main()
```

## Output

```
[worker_3] photo.jpeg -> processed_photo.jpg
[worker_1] chart.png -> processed_chart.jpg
[worker_0] cat.jpg -> processed_cat.jpg
[worker_2] logo.png -> processed_logo.jpg
All 4 images processed successfully
```

The thread names show different images being handled by different worker threads at the same time. The container now holds the original images and their 200×200 `processed_` copies.

## Result

A multithreaded image processing application was implemented using Azure Blob Storage. Images were downloaded, resized in parallel threads, and uploaded back to the cloud successfully.
