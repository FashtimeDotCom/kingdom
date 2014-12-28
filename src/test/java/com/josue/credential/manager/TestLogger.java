/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.junit.Test;

/**
 *
 * @author Josue
 */
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)//Enable interceptor, on CDI 1.1 onwards
public class TestLogger {

    private static final Logger LOG = Logger.getLogger(TestLogger.class.getName());

    @AroundInvoke
    public Object logMethodEntry(InvocationContext ctx) throws Exception {
        if (ctx.getMethod().isAnnotationPresent(Test.class)) {
            LOG.log(Level.INFO, "***** TESTING {0} *****", ctx.getMethod().getName());
        }
        return ctx.proceed();
    }

}
