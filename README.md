# Sportify
Sportify is an Android application whose goal is to connect
users who are interested in engaging in outdoor activities 
with other users who share the same interest. 
Unlike other competing Android applications like 
Meet-Up or social networking websites like Facebook, 
Sportify intends to simplify the communication process 
between users and allow users of the application to join and 
host outdoor activities with ease.

## Table of Contents
- [Requirements](#requirements)
- [Installation](#installation)
- [Test Suites](#test-suites)
- [Dummy Accounts](#dummy-accounts)
- [Source Code](#source-code)
- [Authors](#authors)
- [Point of Contact](#point-of-contact)

## Requirements
In terms of testing, two Android smartphones with internet access and
access to downloading content on the Google Play Store are required. 
(Users of these mobile devices will need their own accounts for downloading.)
(Installation should be performed for both devices.) Both Android devices 
must have operating systems of Android 4.1 and above. (API level 15+)

## Installation
To install the Sportify App:
* (Ensure that the current user is logged into the Google Play store and 
  is logged in Gmail from their mobile device's web browser.)
* On the mobile device's web browser, go to:
  https://play.google.com/apps/testing/gspot.com.sportify.
* Tap on the button labeled **BECOME A TESTER**.
* Tap the clickable text, "Download the Sportify app on Google Play".
* Open the respective Google Play page through the Play Store app.
* Install the Sportify application and follow Google Play's installation
   instructions.

In the case that the installation instructions do not work, please
use the following alternative method of installation:
* Ensure that the the phone's USB Debug mode is turned on.
* On the mobile device's web browser, go to:
   https://app.box.com/s/9zo8fjvclec84et9wosx3aqivtxwg2wa
* Tap on the button labeled **Download** and open it using the current
   web browser.
* The Sportify APK should be downloaded. Install the application
   and follow the installation instructions prompted by the mobile device.

Alternatively, files on Sportify's GitHub page (see below) can be downloaded into Android
Studio and installed directly on the User's device (or opened on an emulator) after following
the instructions on CSE 110's lab 5. 

**Result**: The Sportify App is installed onto the device.

## Test Suites
As presented in the Test Cases document, most test cases require 
dependency cases to be tested prior to testing the current case.
For the convenience of the tester, we have grouped these test cases
into test suites. To perform a specific test case, please ensure that
prior test cases that are in the same suite have already been tested.
Unless otherwise specified, use the Player and Host accounts for testing
and the Backup account as the Player or Host account in case of log in
failure. For all test suites, ensure that UA1 is performed on the
Player account and that UA2 has been performed for both the Player and
the Host accounts.

**Test Suite #1 - Forgot Password**
* UA3 - User Forgot Password

**Test Suite #2 - Logout**
* UA4 - User Logout

**Test Suite #3 - Create Profile and Edit Basic Profile Information**
* PB1 - Create Default Profile
* PB2 - Basic Profile Information

**Test Suite #4 - Create Profile and Edit Profile Picture**
* PB1 - Create Default Profile
* PB7 - Edit Profile Picture

**Test Suite #5 - Create Profile, Add and Delete Sport**
* PB1 - Create Default Profile
* PB4 - Add Sport
* PB5 - Delete Sport

**Test Suite #6 - Create Profile, Add and Edit Sport**
* PB1 - Create Default Profile
* PB4 - Add Sport
* PB6 - Edit Sport

**Test Suite #7 - Create Profile, Set Availability to Filters**
* PB1 - Create Default Profile
* PB3 - Set Availability
* FG1 - Set Filter Settings
* FG2 - View Find Events with Filters

**Test Suite #8 - Host Creates and Edits Gathering**
* HA1 - Create Gathering
* HA2 - Edit Gathering

**Test Suite #9 - Host Creates and Deletes Gathering**
* HA1 - Create Gathering
* HA3 - Delete Gathering

**Test Suite #10 - Host Creates Gathering and Player Joins Public Gathering and Views Players**
* HA1 - Create Gathering
* PA1 - View and Join a Gathering
* PA3 - View Players

**Test Suite #11 - Host Creates Gathering and Player Joins and Leaves Closed Gathering**
* HA1 - Create Gathering
* PA1 - View and Join a Gathering
* HA4 - Accept Request for Closed Gatherings
* PA2 - Leave a Gathering



## Dummy Accounts
For the purposes of testing, please log in using the following dummy account for all test cases with the exception of "User Sign Up":

**Player Account:** (Follow UA1 to set up this account)
* **Name:** Player
* **Email:** gspot.host110@gmail.com
* **Email Password:** password110

**Host Account:** 
* **Name:** Host
* **Email:** gspot.player110@gmail.com
* **Email Password:** password110
* **App Password:** password110

**Backup Account**
* **Name:** Player
* **Email:** gspot.backup110@gmail.com
* **Email Password:** password110
* **App Password:** password110

Note: These accounts are used to log into https://mail.google.com in
      order to access the app's features, and into the Sportify application.
      Please note that the Player account has NOT yet been created. 
      It is highly encouraged that this account is used for the tester
      to sign up. In the case that either the Host account or the Player
      account cannot be used to log into the application, do use 
      the Backup account. Follow test case UA2 for specific instructions
      for logging in.

## Source Code
To access the code used for this project please visit our private
Githib repository. To do so, use the following account below, and go to
https://github.com/andrewyang95/SportsApp or access the SportsApp repository.

* **Login**: cse110.gspot@gmail.com
* **Password**: SanDiego110


## Authors

* Patrick Hayes - Project Manager
* Danny K Chan - Business Analyst
* Amir Assadollahzadeh - Algorithm Specialist
* Armin Ahmadi - Algorithm Specialst
* Don Le Vo - Quality Assurance Lead
* Aaron Benjamin Tom - Database Specialist
* Massoud Maher - Senior System Analyst
* Harsh Patel - User Interface Specialist
* Yunfan (Andrew Yang) - Software Development Lead
* Anshul Bhaskar Chandan - Software Architect

## Point of Contact
In the case that  technical difficulty arises in the installation of 
Sportify, feel free to call us at (424) 209 - 5335.
