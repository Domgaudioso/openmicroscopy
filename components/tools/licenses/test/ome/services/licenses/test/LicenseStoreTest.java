/*
 *   $Id$
 *
 *   Copyright 2007 Glencoe Software, Inc. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */

package ome.services.licenses.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;
import ome.services.licenses.LicenseException;
import ome.services.licenses.LicenseStore;
import ome.services.licenses.Store;
import ome.services.licenses.TokenInfo;
import ome.system.Principal;

import org.testng.annotations.Test;

@Test(groups = "unit")
public class LicenseStoreTest extends TestCase {

    LicenseStore store;
    Principal p;
    byte[] dummy = new byte[] { (byte) 1 };

    @Test
    public void testInitialValues() throws Exception {

        store = new Store();
        ((Store) store).setLicenseCount(0);

        assertTrue("Should be 0", store.getAvailableLicenseCount() == 0);
        assertTrue("Should be 0", store.getTotalLicenseCount() == 0);
        expectLicenseException(store, LicenseStore.class
                .getMethod("acquireLicense"));
        assertFalse(store.releaseLicense(null));
        expectLicenseException(store, LicenseStore.class.getMethod(
                "enterMethod", byte[].class, Principal.class), dummy, null);
        expectLicenseException(store, LicenseStore.class.getMethod(
                "exitMethod", byte[].class, Principal.class), dummy, null);

    }

    @Test
    public void testSetValues() throws Exception {

        store = new Store();
        ((Store) store).setLicenseCount(10);

        assertTrue("Should be 10", store.getAvailableLicenseCount() == 10);
        assertTrue("Should be 10", store.getTotalLicenseCount() == 10);

        ((Store) store).setUsedLicenses(5);

        assertTrue("Should be 5", store.getAvailableLicenseCount() == 5);
        assertTrue("Should be 10", store.getTotalLicenseCount() == 10);

        store = new Store(100);

        assertTrue("Should be 100", store.getAvailableLicenseCount() == 100);
        assertTrue("Should be 100", store.getTotalLicenseCount() == 100);

        store = new Store(100, 10);

        assertTrue("Should be 90", store.getAvailableLicenseCount() == 90);
        assertTrue("Should be 100", store.getTotalLicenseCount() == 100);

    }

    @Test
    public void testAcquireAndRelease() throws Exception {

        store = new Store(10);

        byte[] token = store.acquireLicense();

        assertTrue("Should be 9", store.getAvailableLicenseCount() == 9);
        assertTrue("Should still be 10", store.getTotalLicenseCount() == 10);

        store.releaseLicense(token);

        assertTrue("Should be 10 again", store.getAvailableLicenseCount() == 10);
        assertTrue("Should still be 10", store.getTotalLicenseCount() == 10);

        byte[][] tokens = new byte[10][];

        for (int i = 0; i < tokens.length; i++) {
            assertTrue(store.getAvailableLicenseCount() == 10 - i);
            tokens[i] = store.acquireLicense();
            assertTrue(store.getAvailableLicenseCount() == 9 - i);
        }

        for (int i = 0; i < tokens.length; i++) {
            assertTrue(store.getAvailableLicenseCount() == i);
            store.releaseLicense(tokens[9 - i]);
            assertTrue(store.getAvailableLicenseCount() == i + 1);
        }

    }

    @Test
    public void testEnterAndExit() throws Exception {

        store = new Store(1);

        byte[] token = store.acquireLicense();
        TokenInfo info = ((Store) store).getToken(token);

        store.enterMethod(token, p);
        assertTrue("Timeout should be disabled.", info.time == -1);
        assertTrue("Should be one method entered", info.count == 1);

        store.exitMethod(token, p);
        assertTrue("Timeout should be reset.", info.time > 0);
        assertTrue("Should be no method entered", info.count == 0);

        store.releaseLicense(token);
    }

    @Test
    public void testNoAvailableLicenses() throws Exception {

        store = new Store(1);

        store.acquireLicense();
        expectLicenseException(store, LicenseStore.class
                .getMethod("acquireLicense"));
    }

    @Test
    public void testResetLicenses() throws Exception {

        store = new Store(10);

        byte[] token = store.acquireLicense();

        assertTrue(store.hasLicense(token));
        assertTrue(store.getAvailableLicenseCount() == 9);

        store.resetLicenses();

        assertTrue(store.getAvailableLicenseCount() == 10);
        assertFalse(store.hasLicense(token));
    }

    // ~ Helpers
    // =========================================================================

    private void expectLicenseException(LicenseStore s, Method m, Object... o) {
        try {
            m.invoke(s, o);
            fail(String.format("Method %s should have thrown exception.", m));
        } catch (LicenseException le) {
            // ok.
        } catch (InvocationTargetException ite) {
            if (LicenseException.class.isAssignableFrom(ite.getCause()
                    .getClass())) {
                // ok
            } else {
                throw new RuntimeException(ite);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}