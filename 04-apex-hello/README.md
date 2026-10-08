# Program 4: Develop a Simple Application Using Apex

## Aim

To develop a simple custom application using the Apex programming language on the Salesforce cloud platform.

## Requirements

- A free Salesforce Developer Edition org: <https://developer.salesforce.com/signup>

## Procedure

1. **Sign up** for a Salesforce Developer Edition org and verify your email.
2. **Log in** to the org.
3. **Open the Developer Console:** click the gear icon (**Setup**) in the top-right corner and select **Developer Console**.
4. **Create a new Apex class:** in the Developer Console, go to **File → New → Apex Class**, enter `HelloWorldApp`, and click **OK**.
5. **Write the code:** replace the generated class body with the code below and save with **File → Save** (`Ctrl + S`).
6. **Execute the code:**
   1. Go to **Debug → Open Execute Anonymous Window** (`Ctrl + E`).
   2. Enter the code from [`run.apex`](run.apex).
   3. Tick **Open Log** and click **Execute**.
7. **View the output:** the execution log opens. Tick **Debug Only** at the bottom of the log to show only the `System.debug` output.

## Program

[`HelloWorldApp.cls`](HelloWorldApp.cls)

```apex
public class HelloWorldApp {
    public static void sayHello() {
        System.debug('WELCOME TO APEX PROGRAMMING');
    }
}
```

[`run.apex`](run.apex): executed in the Execute Anonymous window

```apex
HelloWorldApp.sayHello();
```

## Output

Execution log (with **Debug Only** ticked):

```
USER_DEBUG [3]|DEBUG|WELCOME TO APEX PROGRAMMING
```

## Result

A simple Apex application was developed and executed on the Salesforce cloud platform, and the message was displayed in the debug log.
