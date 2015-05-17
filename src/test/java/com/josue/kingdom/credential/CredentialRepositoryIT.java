package com.josue.kingdom.credential;

import com.josue.kingdom.credential.entity.APICredential;
import com.josue.kingdom.credential.entity.LoginAttempt;
import com.josue.kingdom.credential.entity.Manager;
import com.josue.kingdom.credential.entity.PasswordChangeEvent;
import com.josue.kingdom.domain.entity.ManagerMembership;
import com.josue.kingdom.testutils.ArquillianTestBase;
import com.josue.kingdom.testutils.InstanceHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Josue
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.ROLLBACK)
public class CredentialRepositoryIT {

    @Inject
    CredentialRepository repository;

    private static final Integer DEFAULT_LIMIT = 100;
    private static final Integer DEFAULT_OFFSET = 0;

    @Deployment
    @TargetsContainer("wildfly-managed")
    public static WebArchive createDeployment() {
        return ArquillianTestBase.createDefaultDeployment();
    }

    @Test
    public void testGetAPICredentialsByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        List<APICredential> foundAPICredentials = repository.getAPICredentials(InstanceHelper.APP_ID, membership.getDomain().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundAPICredentials.size());
        assertEquals(apiCredential, foundAPICredentials.get(0));
    }

    @Test
    public void testGetAPICredentialsByDomainAndManager() {

        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        List<APICredential> foundAPICredentials = repository.getAPICredentials(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundAPICredentials.size());
        assertEquals(apiCredential, foundAPICredentials.get(0));

    }

    @Test
    public void testGetAPICredential() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential);

        APICredential foundAPICredential = repository.getAPICredential(InstanceHelper.APP_ID, apiCredential.getUuid());
        assertNotNull(foundAPICredential);
        assertEquals(apiCredential, foundAPICredential);
    }

    @Test
    public void testCountAPICredentialByDomainAndManager() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential1 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential1);

        APICredential apiCredential2 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential2);

        long count = repository.countAPICredential(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertEquals(2, count);

    }

    @Test
    public void testCountAPICredentialByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);

        APICredential apiCredential1 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential1);

        APICredential apiCredential2 = InstanceHelper.createAPICredential(membership);
        repository.create(apiCredential2);

        long count = repository.countAPICredential(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertEquals(2, count);
    }

    @Test
    public void testGetManagers() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<Manager> foundManagers = repository.getManagers(InstanceHelper.APP_ID, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertTrue(foundManagers.size() >= 1);//App level managers

        boolean hasManager = false;
        for (Manager manager : foundManagers) {
            if (membership.getManager().equals(manager)) {
                hasManager = true;
            }
        }
        assertTrue(hasManager);
    }

    @Test
    public void testGetManagerByEmail() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Manager foundManager = repository.getManagerByEmail(InstanceHelper.APP_ID, membership.getManager().getEmail());
        assertNotNull(foundManager);
        assertEquals(membership.getManager(), foundManager);
    }

    @Test
    public void testGetManagerByUsername() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        Manager foundManager = repository.getManagerByUsername(InstanceHelper.APP_ID, membership.getManager().getUsername());
        assertNotNull(foundManager);
        assertEquals(membership.getManager(), foundManager);
    }

    @Test
    public void testGetManagerMembership() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        ManagerMembership foundMembership = repository.getManagerMembership(InstanceHelper.APP_ID, membership.getDomain().getUuid(), membership.getManager().getUuid());
        assertNotNull(foundMembership);
        assertEquals(membership, foundMembership);
    }

    @Test
    public void testGetManagerMembershipByManager() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<ManagerMembership> foundMemberships = repository.getManagerMembershipByManager(InstanceHelper.APP_ID, membership.getManager().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundMemberships.size());
        assertEquals(membership, foundMemberships.get(0));
    }

    @Test
    public void testGetManagerMembershipByDomain() {
        ManagerMembership membership = InstanceHelper.createFullManagerMembership(repository);
        List<ManagerMembership> foundMemberships = repository.getManagerMembershipByDomain(InstanceHelper.APP_ID, membership.getDomain().getUuid(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundMemberships.size());
        assertEquals(membership, foundMemberships.get(0));
    }

    @Test
    public void testGetPasswordResetEvent() {
        Manager targetManager = InstanceHelper.createManager();
        repository.create(targetManager);

        PasswordChangeEvent event = InstanceHelper.createPasswordChangeEvent(targetManager);
        repository.create(event);

        PasswordChangeEvent foundEvent = repository.getPasswordResetEvent(InstanceHelper.APP_ID, event.getToken());
        assertEquals(event, foundEvent);
    }

    @Test
    public void testGetPasswordResetEvents() {
        Manager targetManager = InstanceHelper.createManager();
        repository.create(targetManager);

        PasswordChangeEvent event1 = InstanceHelper.createPasswordChangeEvent(targetManager);
        repository.create(event1);
        PasswordChangeEvent event2 = InstanceHelper.createPasswordChangeEvent(targetManager);
        repository.create(event2);

        List<PasswordChangeEvent> events = repository.getPasswordResetEvents(InstanceHelper.APP_ID, targetManager.getUuid());
        assertEquals(2, events.size());
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
    }

    @Test
    public void testGetLoginAttempts() {
        String login = "manager1username123";

        LoginAttempt successfulAttempt = InstanceHelper.createLoginAttempt(login, LoginAttempt.LoginStatus.SUCCESSFUL);
        repository.create(successfulAttempt);
        LoginAttempt failedAttempt = InstanceHelper.createLoginAttempt(login, LoginAttempt.LoginStatus.FAILED);
        repository.create(failedAttempt);

        List<LoginAttempt> foundSuccededAttempts = repository.getLoginAttempts(InstanceHelper.APP_ID, login, LoginAttempt.LoginStatus.SUCCESSFUL, null, null, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundSuccededAttempts.size());
        assertEquals(successfulAttempt, foundSuccededAttempts.get(0));
        List<LoginAttempt> foundFailedAttempts = repository.getLoginAttempts(InstanceHelper.APP_ID, login, LoginAttempt.LoginStatus.FAILED, null, null, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundFailedAttempts.size());
        assertEquals(failedAttempt, foundFailedAttempts.get(0));

        //Any status for a given user
        List<LoginAttempt> allStatusAttempts = repository.getLoginAttempts(InstanceHelper.APP_ID, login, null, null, null, DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(2, allStatusAttempts.size());
        assertTrue(allStatusAttempts.contains(successfulAttempt));
        assertTrue(allStatusAttempts.contains(failedAttempt));

        //find by date
        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.HOUR_OF_DAY, -2);

        //Added 1 hour due date error caused by mysql rounding
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date());
        endDate.add(Calendar.HOUR_OF_DAY, +1);

        List<LoginAttempt> foundByDate = repository.getLoginAttempts(InstanceHelper.APP_ID, login, null, startDate.getTime(), endDate.getTime(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(2, foundByDate.size());
        assertTrue(foundByDate.contains(successfulAttempt));
        assertTrue(foundByDate.contains(failedAttempt));

        List<LoginAttempt> foundByDateAndStatus = repository.getLoginAttempts(InstanceHelper.APP_ID, login, LoginAttempt.LoginStatus.SUCCESSFUL, startDate.getTime(), endDate.getTime(), DEFAULT_LIMIT, DEFAULT_OFFSET);
        assertEquals(1, foundByDateAndStatus.size());
        assertTrue(foundByDateAndStatus.contains(successfulAttempt));

    }

    @Test
    public void testCountLoginAttempts() {
        String login = "ausername123456";

        LoginAttempt successfulAttempt = InstanceHelper.createLoginAttempt(login, LoginAttempt.LoginStatus.SUCCESSFUL);
        repository.create(successfulAttempt);
        LoginAttempt failedAttempt = InstanceHelper.createLoginAttempt(login, LoginAttempt.LoginStatus.FAILED);
        repository.create(failedAttempt);

        Long foundSuccededAttempts = repository.countLoginAttempts(InstanceHelper.APP_ID, login, LoginAttempt.LoginStatus.SUCCESSFUL, null, null);
        assertEquals(Long.valueOf(1L), foundSuccededAttempts);

        //Any status for a given user
        Long allStatusAttempts = repository.countLoginAttempts(InstanceHelper.APP_ID, login, null, null, null);
        assertEquals(Long.valueOf(2L), allStatusAttempts);

        Calendar startDate = Calendar.getInstance();
        startDate.setTime(new Date());
        startDate.add(Calendar.HOUR_OF_DAY, -2);

        //Added 1 hour due date error caused by mysql rounding
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(new Date());
        endDate.add(Calendar.HOUR_OF_DAY, +1);

        Long foundByDate = repository.countLoginAttempts(InstanceHelper.APP_ID, login, null, startDate.getTime(), endDate.getTime());
        assertEquals(Long.valueOf(2L), foundByDate);

        Long foundByDateAndStatus = repository.countLoginAttempts(InstanceHelper.APP_ID, login, LoginAttempt.LoginStatus.SUCCESSFUL, startDate.getTime(), endDate.getTime());
        assertEquals(Long.valueOf(1L), foundByDateAndStatus);

    }
}
