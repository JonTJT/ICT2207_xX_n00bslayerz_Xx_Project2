# ICT2207_xX_n00bslayerz_Xx_
This project is a modification of the existing application "MyNotes" by adding new security features to enhance the security of the existing application. The links to the original application can be found in the links below:<br>
GitHub Repository: https://github.com/akshatbhuhagal/MyNotes<br>
Google Play Store: https://play.google.com/store/apps/details?id=com.akshatbhuhagal.mynotes

## :family_man_girl_boy:Project Members:family_man_boy_boy::
Meng Rong - https://github.com/GMengRong<br>
Wesley - https://github.com/wesleychiau<br>
Jon - https://github.com/JonTJT<br>
Keefe - https://github.com/keefelee<br>
Min Yao - https://github.com/IUshiii<br>
Lynette - https://github.com/Bas1lSage<br>

## :electric_plug:Installation Guide:electric_plug::
1. Clone the repository into your preferred working directory.
2. Import into android studio: File > New > Import Project > MyNotes-master<br>
![image](https://user-images.githubusercontent.com/23615745/229261117-070e1969-f011-4575-a4a5-657360fa6e42.png)<br>
(Note make sure to target the "MyNotes-master" folder)
3. Build and run the application.

## :shield:Security Features:shield::
On top of the original application, these security features have been added:
1. Added password functionality to prompt users for a password on first launch and subsequently ensures that the same password is entered before granting access
2. Added database encryption to encrypt all of the notes in the event that the database is compromised
3. Added client server functionality to authenticate the credentials

## :space_invader:Known bugs from the original application:space_invader:: 
- Accessing a note crashes the program
  - Fixed by commenting out makeGone function in CreateNoteFragment.kt
- Https link appears in note despite user not providing a link
  - Fixed by removing the auto-showing of links in a note in CreateNote
- Unable to see all the notes when the application starts up

## Known common issues:
- Threading error is sometimes caused by the "Enter password" title.
- Database error when updating from the original application to the modified one. Application will crash when attempting to access notes.
  ![image](https://user-images.githubusercontent.com/38094040/229277295-249fa612-a88a-445a-9065-f5626064e238.png)
  - To fix, uninstall the original application first, then install the modified application. This is caused by the original database being created without a key, but is not updated to be encrypted when the application is updated. 



