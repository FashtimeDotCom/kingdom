/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.credential.manager.business.role;

import com.josue.credential.manager.auth.credential.Credential;
import com.josue.credential.manager.auth.role.Role;
import com.josue.credential.manager.auth.shiro.AccessLevelPermission;
import com.josue.credential.manager.auth.util.Current;
import static com.josue.credential.manager.rest.ResponseUtils.CONTENT_TYPE;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author Josue
 */
@ApplicationScoped
//TODO rename this class
//This is a subresource locator class... can be accessed throught /domains/123/roles
public class RoleSubRest {

    @Inject
    RoleRepository repository;

    @Inject
    @Current
    Credential credential;

    @GET
    @Produces(value = CONTENT_TYPE)
    public Response getSystemRolesForDomainCredential(@PathParam("domainUuid") String domainUuid) {
        List<Role> roles = repository.findAll(Role.class);
        for (Iterator<Role> iterator = roles.iterator(); iterator.hasNext();) {
            Role role = iterator.next();
            if (!SecurityUtils.getSubject().isPermitted(new AccessLevelPermission(domainUuid, role))) {
                iterator.remove();
            }
        }
        return Response.status(Response.Status.OK).entity(roles).build();
    }

}
