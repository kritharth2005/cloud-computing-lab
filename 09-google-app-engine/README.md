# Program 9: Use Google App Engine Launcher to Launch Web Applications

## Aim

To use the Google App Engine Launcher (Google App Engine SDK) to launch a web application locally.

> **Status:** Google has retired the App Engine SDK, the Launcher and the Python 2.7 runtime, so apps can no longer be **deployed** to Google's servers this way. Running an app **locally** with the SDK, which is all this program does, still works. The steps below were tested with SDK 1.9.88 and Python 2.7.18.

## Requirements

- **Python 2.7.** The SDK does not run on Python 3.
  - Windows: [python-2.7.18.amd64.msi](https://www.python.org/ftp/python/2.7.18/python-2.7.18.amd64.msi) from python.org
  - Linux: install Python 2.7 from your distribution (e.g. the `python2` package from the AUR on Arch)
- **Google App Engine SDK for Python**, one of:
  - **SDK archive (recommended, works on any OS):** `google_appengine_1.9.88.zip` from this repository's [Releases page](https://github.com/kritharth2005/cloud-computing-lab/releases/tag/gae-sdk-1.9.88). It contains `dev_appserver.py`, the tool that the Launcher runs behind the scenes.
  - **Windows installer with the Launcher GUI:** Google's last Windows installer was `GoogleAppEngine-1.9.90.msi` at `https://storage.googleapis.com/appengine-sdks/featured/GoogleAppEngine-1.9.90.msi` (SHA-256 `8dc6037f9833dbfa776f0d8363e82cabcd15ba8896e6bd48230b5b88cc99ac8e`). Google may have removed it; if the link fails, use the SDK archive.
- The application in [`ae-01-trivial/`](ae-01-trivial)

## Procedure

### Step 1: Create the application folder

Create a folder called `apps`, and inside it a folder called `ae-01-trivial`.

### Step 2: Create `app.yaml`

In `ae-01-trivial`, create [`app.yaml`](ae-01-trivial/app.yaml) with the following contents:

```yaml
application: ae-01-trivial
version: 1
runtime: python27
api_version: 1
threadsafe: false

handlers:
- url: /.*
  script: index.py
```

### Step 3: Create `index.py`

In the same folder, create [`index.py`](ae-01-trivial/index.py) with three lines:

```python
print 'Content-Type: text/plain'
print ''
print 'Hello there Chuck'
```

The first line is the HTTP header, the empty line ends the headers, and the last line is the page body.

### Step 4: Run the application

**Option A: with the App Engine Launcher (Windows installer).**

1. Install Python 2.7, then install `GoogleAppEngine-1.9.90.msi`.
2. Start **Google App Engine Launcher** from the Start menu or desktop shortcut. If the shortcut is missing, run `C:\Program Files\Google\google_appengine\launcher\GoogleAppEngineLauncher.exe`.
3. Use **File → Add Existing Application**, browse to the `apps` folder, and select `ae-01-trivial`.
4. Select the application in the Launcher and click **Run**. After a few moments a green icon appears next to it.
5. Click **Browse** to open the application at <http://localhost:8080/>.

**Option B: with the SDK archive (any OS).**

1. Download `google_appengine_1.9.88.zip` from the [Releases page](https://github.com/kritharth2005/cloud-computing-lab/releases/tag/gae-sdk-1.9.88) and unzip it, e.g. into your home folder or `C:\`.
2. From the folder that contains `ae-01-trivial`, run:

   ```bash
   # Linux / macOS
   python2.7 ~/google_appengine/dev_appserver.py ae-01-trivial

   # Windows
   C:\Python27\python.exe C:\google_appengine\dev_appserver.py ae-01-trivial
   ```

3. When the terminal shows `Starting module "default" running at: http://localhost:8080`, open <http://localhost:8080/> in a browser.

### Step 5: Edit the application

Edit `index.py` and change `Chuck` to your own name. Save the file and press **Refresh** in the browser. The page shows the new name without restarting the server, because the development server detects file changes.

### Step 6: Watch the log

- **Launcher:** select the application and click **Logs**.
- **SDK archive:** the log is printed in the terminal.

Each refresh in the browser adds a `GET` request line to the log, for example:

```
INFO     2026-10-09 03:41:31,080 module.py:865] default: "GET / HTTP/1.1" 200 22
```

### Step 7: Dealing with errors

There are two kinds of errors:

- **Error in `app.yaml`:** the application does not start. In the Launcher the icon turns yellow; in the terminal `dev_appserver.py` exits. The log shows where the error is. For example, removing the `:` after `handlers` gives:

  ```
  google.appengine.api.yaml_errors.EventListenerYAMLError: while scanning a simple key
    in "ae-01-trivial/app.yaml", line 7, column 1
  could not find expected ':'
  ```

- **Error in `index.py`:** the server keeps running, the browser shows an error page, and the Python traceback appears in the log.

Stop the server with **Stop** in the Launcher, or `Ctrl + C` in the terminal.

## Output

Browser at `http://localhost:8080/`:

```
Hello there Chuck
```

Terminal or Launcher log:

```
INFO     devappserver2.py:289] Skipping SDK update check.
INFO     api_server.py:282] Starting API server at: http://localhost:8001
INFO     dispatcher.py:267] Starting module "default" running at: http://localhost:8080
INFO     admin_server.py:150] Starting admin server at: http://localhost:8000
INFO     module.py:865] default: "GET / HTTP/1.1" 200 18
```

After changing the name in `index.py` and refreshing:

```
Hello there Kritharth
```

## Notes on the lab manual

- The manual's `app.yaml` uses `runtime: python` (Python 2.5). The SDK no longer supports it and falls back to `python27` with a warning, so this version uses `runtime: python27` with `threadsafe: false`, which is required for CGI-style scripts like `index.py`.
- The manual's `index.py` prints `' '` (a space) after the header. That is not an empty line, so the header is not ended correctly and the page is served as `text/html`. Printing `''` fixes it, and the page is served as `text/plain`.

## SDK archive details

| | |
|---|---|
| File | `google_appengine_1.9.88.zip` (Releases page) |
| Version | App Engine SDK for Python 1.9.88 |
| SHA-256 | `3ce93f48ecd1e6a67e78a10cd8a2aa749f80bd673bee9ffcf741a5ab6fddbffe` |
| Source | Repackaged from the `appengine-sdk` 1.9.88 package on PyPI, which bundles Google's SDK |
| License | Apache License 2.0 (see `google_appengine/LICENSE` inside the archive) |

The archive contains the command-line SDK only. The Launcher GUI was never part of the zip; Google shipped it only in the Windows `.msi` and Mac `.dmg` installers.

## Result

A web application was created with the Google App Engine SDK and launched locally on the App Engine development server. Changes to the code were reflected on refresh, and requests and errors were observed in the log.
