# Program 5: Implement a Mailing Service Using Apex

## Aim

To implement a mailing service using the Apex programming language of Salesforce.

## Requirements

- A Salesforce Developer Edition org (see [Program 4](../04-apex-hello))

## Procedure

### Step 1: Allow the org to send email

New Developer orgs may be set to send only system emails, in which case `sendEmail` fails.

1. Go to **Setup** and type `Deliverability` in the **Quick Find** box.
2. Set **Access level** to **All email** and click **Save**.

### Step 2: Create the Apex class

1. Open the **Developer Console** (gear icon → **Developer Console**).
2. Go to **File → New → Apex Class**, enter `EmailManager`, and click **OK**.
3. Replace the default class body with the code in [`EmailManager.cls`](EmailManager.cls).
4. Save with **File → Save**. The class can now be used to send email from other Apex classes or triggers.

### Step 3: Test the mailing service

1. Go to **Debug → Open Execute Anonymous Window**.
2. Enter the code from [`run.apex`](run.apex), replacing `your.email@example.com` with your email address.
3. Tick **Open Log** and click **Execute**.
4. Check your inbox (and spam folder) for the email.

## Program

[`EmailManager.cls`](EmailManager.cls)

```apex
public class EmailManager {

    // Public method
    public void sendMail(String address, String subject, String body) {
        // Create an email message object
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
        String[] toAddresses = new String[] {address};
        mail.setToAddresses(toAddresses);
        mail.setSubject(subject);
        mail.setPlainTextBody(body);

        // Pass this email message to the built-in sendEmail method
        // of the Messaging class
        Messaging.SendEmailResult[] results = Messaging.sendEmail(
                                 new Messaging.SingleEmailMessage[] { mail });

        // Call a helper method to inspect the returned results
        inspectResults(results);
    }

    // Helper method
    private static Boolean inspectResults(Messaging.SendEmailResult[] results) {
        Boolean sendResult = true;

        // sendEmail returns an array of result objects.
        // Iterate through the list to inspect results.
        // In this class, the methods send only one email,
        // so we should have only one result.
        for (Messaging.SendEmailResult res : results) {
            if (res.isSuccess()) {
                System.debug('Email sent successfully');
            } else {
                sendResult = false;
                System.debug('The following errors occurred: ' + res.getErrors());
            }
        }
        return sendResult;
    }
}
```

[`run.apex`](run.apex): executed in the Execute Anonymous window

```apex
EmailManager em = new EmailManager();
em.sendMail('your.email@example.com', 'Test mail from Apex', 'This email was sent using the EmailManager Apex class.');
```

## Output

Execution log (with **Debug Only** ticked):

```
USER_DEBUG [31]|DEBUG|Email sent successfully
```

The email arrives in the recipient's inbox with the subject *Test mail from Apex*.

> Developer orgs have a small daily limit on outgoing emails, so avoid sending many test mails.

## Result

A mailing service was implemented using Apex on Salesforce, and an email was sent successfully to the given address.
