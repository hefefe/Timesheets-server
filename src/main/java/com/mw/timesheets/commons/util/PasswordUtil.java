package com.mw.timesheets.commons.util;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    public static String generateTempPassword(){
        CharacterRule alphabets = new CharacterRule(EnglishCharacterData.Alphabetical);
        CharacterRule digits = new CharacterRule(EnglishCharacterData.Digit);

        PasswordGenerator passwordGenerator = new PasswordGenerator();
        return passwordGenerator.generatePassword(8, alphabets, digits);
    }
}
