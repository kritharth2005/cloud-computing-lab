"""Program 7: thread-based image processing on Microsoft Azure Blob Storage.

Downloads every image in the "images" container, resizes it to 200x200 using
a pool of worker threads, and uploads the result as processed_<name>.jpg.
"""

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
