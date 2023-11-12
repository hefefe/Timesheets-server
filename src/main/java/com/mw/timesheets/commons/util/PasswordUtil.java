package com.mw.timesheets.commons.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class PasswordUtil {

    private String generateTempPassword(){
        CharacterRule alphabets = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);
        CharacterRule special = new CharacterRule(EnglishCharacterData.Special);

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        return passwordGenerator.generatePassword(8, alphabets, digits, special);
    }
}
