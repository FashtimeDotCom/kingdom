/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import com.josue.credential.manager.account.Manager;
import com.josue.credential.manager.auth.Role;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Josue
 */
public abstract class InstanceHelper {

    public static Date mysqlMilliSafeTimestamp() {

        //TIP: http://www.coderanch.com/t/530003/java/java/Comparing-Date-Timestamp-unexpected-result
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 2);
        cal.set(Calendar.MILLISECOND, 0); //reset milliseconds

        return cal.getTime();
    }

    public static Role createRole() {
        Role role = new Role();
        role.setDescription("Role description");
        role.setLevel(new Random().nextInt());
        role.setName("ADMIN");

        return role;
    }

    public static Manager createManager() {
        Manager manager = new Manager();
        manager.setEmail("josue@gmail.com");
        manager.setFirstName("josue");
        manager.setLastName("Eduardo");
//        manager.set

        return manager;
    }

//    public static ManagerCredential createManagerCredential() {
//        ManagerCredential credential = new ManagerCredential();
//        credential.setLogin("login123");
////        credential.se
//    }
//    public static Domain creteDomain() {
//        return creteDomain(cre)
//    }
//
//    public static Domain creteDomain(ManagerCredential owner) {
//        Domain domain = new Domain();
//        domain.setName("DEFAULT-DOMAIN");
//        domain.setDescription("A default domain");
//        domain.setOwner(null);
//    }
}
