# My First Wallet #Bankathon16

This is the prototype developed during the "Bankathon16" hackathon hosted by [INSO](https://www.inso.tuwien.ac.at/home/) (Technical University of Vienna). 
It was a 2 day event, 3 developer worked on the project. This project achieved 2nd place in the ranking.

![Banner](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/my_first_wallet.png?raw=true)

## Use Cases

### Adding children
An envisioned app for the parents contains an administrative UI where a child can be registrated.
To do this the the parents mobile phone communicates through NFC with the child's app, automatically
registering it to the parent.

![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/00_main.png?raw=true)
![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/03_registration.png?raw=true)

### Sending money
Parents can send money and set restrictions on how the money can be spend (e.g. max daily amount, specific
shops, etc.)

![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/01_send_money.png?raw=true)
![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/02_detail1.png?raw=true)
![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/02_detail2.png?raw=true)

### Pay with children's App
The app can be used to pay at NFC payment terminals. It is implemented to work with simple NFC tags
altough HCE is a possible real world solution for this.

![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/01_main.png?raw=true)
![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/02_payment.png?raw=true)

### Save money
To learn financial literacy, a child can set a saving goal to set a certain amount of money aside
whenever it feels like it. This amount wont be able to be spent through the payment function. A parent
has to release the money.

![Screenshot](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/03_saving.png?raw=true)

## Architecture

![Diagram](https://github.com/patrickfav/bankathon16-inso/blob/master/docs/sm/tech_diagram_eng.png?raw=true)

### Noteable Technical Details

* Update App through GCM push
* App-to-App and payment with `android.nfc.action.NDEF_DISCOVERED` nfc communication
* Server uses Spring Bootstrap & Postgres as 

## Build & Run

# App 
Use Android Studio and fill with the GCM sender id

    buildConfigField "String", "GCM_PROJECT_ID", "\"FILL_ME\""
    
in the build.gradle.    

# Server

Fill server GCM Api key in class `NotificationService.java`
Use gradle task `bootRun` to start the server.