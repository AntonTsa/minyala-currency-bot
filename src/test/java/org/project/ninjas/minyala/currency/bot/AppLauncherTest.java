package org.project.ninjas.minyala.currency.bot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AppLauncherTest {
    @Test
    void appHasAGreeting() {
        AppLauncher classUnderTest = new AppLauncher();
        assertNotNull(classUnderTest.getGreeting());
    }
}
