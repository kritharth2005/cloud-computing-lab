# Program 1: Install VirtualBox with Different Flavours of Linux or Windows OS

## Aim

To install VirtualBox and create virtual machines running different flavours of Linux or Windows.

## Requirements

- Host machine running Windows 10/11 (or Linux / macOS), with virtualization (VT-x / AMD-V) enabled in BIOS
- VirtualBox installer from <https://www.virtualbox.org/wiki/Downloads>
- An OS installation image (`.iso`), e.g. Ubuntu Desktop from <https://ubuntu.com/download/desktop>

## Procedure

### Part A: Install VirtualBox

1. Download the VirtualBox installer for your host OS and double-click the `.exe`.
2. Click **Next** on the welcome screen.
3. Keep the default features and install location, click **Next**.
4. A warning says network interfaces will be reset briefly. Click **Yes**.
5. Click **Install** and allow the installer to make changes when prompted.
6. Click **Finish**. The VirtualBox icon appears on the desktop.

### Part B: Create a virtual machine with a guest OS

1. Open VirtualBox and click **New**.
2. Enter a name (e.g. `Ubuntu-VM`) and select the downloaded `.iso` as the ISO image. The type and version are detected automatically.
3. Set memory to **2048 MB** (or more) and processors to **2**.
4. Create a virtual hard disk of **25 GB** (dynamically allocated).
5. Click **Finish**, select the new VM, and click **Start**.
6. Follow the OS installer inside the VM window (language, keyboard, user name, password).
7. After installation, the VM restarts into the installed OS.

Repeat Part B with a different `.iso` (e.g. Fedora, Linux Mint, or a Windows evaluation image) to run other OS flavours side by side.

## Output

VirtualBox Manager lists the created virtual machines, and each one boots into its own guest OS.

## Result

VirtualBox was installed successfully and virtual machines with different operating systems were created and run.
