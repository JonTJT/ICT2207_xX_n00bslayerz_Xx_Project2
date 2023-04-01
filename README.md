# ICT2207_xX_n00bslayerz_Xx_

## Project Members:
Meng Rong - https://github.com/GMengRong<br>
Wesley - https://github.com/wesleychiau<br>
Jon - https://github.com/JonTJT<br>
Keefe - https://github.com/keefelee<br>
Min Yao - https://github.com/IUshiii<br>
Lynette - https://github.com/Bas1lSage<br>

## Installation Guide:
1. Clone the repository into your preferred working directory.
2. Import into android studio: File > New > Import Project > MyNotes-master<br>
![image](https://user-images.githubusercontent.com/23615745/229261117-070e1969-f011-4575-a4a5-657360fa6e42.png)<br>
(Note make sure to target the "MyNotes-master" folder)
3. Build and run the application.

## Security Features:
On top of the original application, these security features have been added:
1. Added password functionality to prompt users for a password on first launch and subsequently ensures that the same password is entered before granting access
2. Added database encryption to encrypt all of the notes in the event that the database is compromised
3. Added client server functionality to authenticate the credentials

## Known bugs
- Accessing a note crashes the program
  - Fixed by commenting out makeGone function in CreateNoteFragment.kt
- Unable to see all the notes after creating them unless using the search bar

