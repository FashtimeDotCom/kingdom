/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.util;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Josue
 */
@ApplicationScoped
public class KingdomUtils {

    public String generateBase64FromUuid() {
        UUID rand = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(rand.getMostSignificantBits());
        bb.putLong(rand.getLeastSignificantBits());
        String base64 = DatatypeConverter.printBase64Binary(bb.array());
        return base64.substring(0, base64.length() - 2).replace("/", "_").replace("+", "-");
    }

    public String obfuscateToken(String token, int lastShown) {
        final String defaultChar = "**********";
        if (lastShown >= token.length()) {
            return defaultChar;
        }
        return defaultChar + token.substring(lastShown);
    }

    //Create a new date for 'valid until' cases
    public Date addFutureDate(int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(field, amount);
        return calendar.getTime();
    }

}
