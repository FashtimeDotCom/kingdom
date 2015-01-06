/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.testutils;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Josue
 */
@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggingInterceptor implements Serializable {

    private Logger logger;

    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext)
            throws Exception {
        logger = Logger.getLogger(invocationContext.getMethod().getDeclaringClass().getName());

        String methodName = invocationContext.getMethod().getName();
        String className = invocationContext.getMethod().getDeclaringClass().getName();

        logger.log(Level.INFO, "STARTED TEST:  {0}.{1}", new Object[]{className, methodName});
        Object response = invocationContext.proceed();
        logger.log(Level.INFO, "FINISHED TEST:  {0}.{1}", new Object[]{className, methodName});
        return response;
    }

}
