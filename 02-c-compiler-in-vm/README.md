# Program 2: Install a C Compiler in a Virtual Machine and Execute a Simple Program

## Aim

To install a C compiler in a virtual machine created using VirtualBox and execute a simple program.

## Requirements

- VirtualBox (see [Program 1](../01-virtualbox-setup))
- An Ubuntu virtual machine: either the lab's `.ova` file or a VM created in Program 1

## Procedure

### Step 1: Import the Ubuntu VM (skip if you already have one from Program 1)

1. Open VirtualBox.
2. Go to **File → Import Appliance**.
3. Browse to the lab's Ubuntu `.ova` file and click **Next → Import**.
4. Open the VM's **Settings → USB** and select **USB 1.1 (OHCI) Controller**.
5. Start the VM.

### Step 2: Install the C compiler

Open a terminal (`Ctrl + Alt + T`) and check whether `gcc` is already installed:

```bash
gcc --version
```

If it is not found, install it:

```bash
sudo apt update
sudo apt install -y build-essential
```

### Step 3: Write and run the program

```bash
gedit hello.c          # type the program below and save
gcc hello.c -o hello   # compile
./hello                # run
```

## Program — [`hello.c`](hello.c)

```c
#include <stdio.h>

int main(void)
{
    int a = 10, b = 20;

    printf("Hello from the virtual machine!\n");
    printf("Sum of %d and %d is %d\n", a, b, a + b);
    return 0;
}
```

## Output

```
Hello from the virtual machine!
Sum of 10 and 20 is 30
```

## Result

The GCC C compiler was installed in the Ubuntu virtual machine and a simple C program was compiled and executed successfully.
