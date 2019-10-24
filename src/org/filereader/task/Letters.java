package org.filereader.task;

public enum Letters {
    LETTER_A_G("answerA_G.txt", 'A', 'G'),
    LETTER_H_N("answerH_N.txt", 'H', 'N'),
    LETTER_O_U("answerO_U.txt", 'O', 'U'),
    LETTER_V_Z("answerV_Z.txt", 'V', 'Z');

    String fileName;
    char startChar;
    char finishChar;

    Letters(String fileName, char startChar, char finishChar) {
        this.fileName = fileName;
        this.startChar = startChar;
        this.finishChar = finishChar;
    }
}
