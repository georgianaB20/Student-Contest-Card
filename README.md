# Student-Contest-Card
Student: Boghiu Georgiana-Viorica

Smart Cards and Applications Course Project (Faculty of Computer Science, year 3, semester 2)

## Components
The application consists of the Card and Terminal.

### Card
Requirements to run the Card component
1. eclipse (PHOTON)
2. Java Card Development Kit 3.1 with JDK 11 (64 bit)
  
  -> Simulator (java_card_simulator-3_1_0-u4-win-bin-do-b_112-06_jan_2021.msi): https://www.oracle.com/java/technologies/javacard-sdk-downloads.html
    
    Create the environment variable JC_HOME_SIMULATOR and set it's value to the root directory of Java Card development Kit Simulator (C:\Program Files (x86)\Oracle\Java Card Development Kit Simulator 3.1.0)
  
  -> Tools (java_card_tools-win-bin-b_108-06_jan_2021.zip): https://www.oracle.com/java/technologies/javacard-sdk-downloads.html
    
    Unarchive the archive you downloaded in the Oracle\Java Card Tools 3.1.0 directory
    Create the environment variable JC_HOME_TOOLS and set it's value to the root directory of Java Card Tools (C:\Program Files (x86)\Oracle\Java Card Tools 3.1.0)
4. GCC compiler - Minimal GNU for Windows (MinGW)
5. Apache ANT

#### Setup eclipse
1. Open the eclipse executable 
2. Help->Install New Software->Add->Archive
3. JC_HOME_SIMULATOR\eclipse-plugin\jcdk-repository_yyyymmddxxxx.zip
4. In window "Add Repository" add in the field Name: Java Card SDK
5. In window "Available Software" select: Java Card 3 Platform Development Kit
6. "Group items by category" must NOT be checked.
7. Restart eclipse

#### Test eclipse
1. If you don't see "Java Card View" window in eclipse: Windows->Perspective ->Reset Perspective for Java Card View
2. Import a sample wallet: File->Import->Existing Projects into Workspace and choose …Java Card Development Kit Simulator 3.1.0\samples\classic_applets\Wallet\
3. Right click on "Wallet" (on Package) and check if you see the "Java Card" field (with Runtime Setting and CAP File Settings options). If you can see this field go to step 4. Otherwise, retake all the previous steps.
4. Window->Preferences->Java Card Tools Path (here you should write the path to the Java Card Tools directory -> the one from Card->Tools section)
5. Java Card View-> right click on Sample Device->Start (starting the simulator)
6. Package Explorer view, click Wallet Java -> right click -> Java Card->CAP Files Settings , select "Wallet"-> Edit-> Compact CAP File->Next
7. ScriptGen tab and check Suppress "PowerDown;" APDU command at the end of CAP script
8. Finish -> Apply -> Close
9. Restart the simulator
10. Console->Scripts(an icon with 3 horizontal blue lines and a green arrow)-> run scripts 1 (cap-Wallet) and then 4

### Terminal
In order to run the Terminal component, you'll need:

-> java IDE (I used Intellij)
-> setup done for the Card component
