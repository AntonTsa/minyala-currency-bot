package org.project.ninjas.minyala.currency.bot;

/**
 * Entry point for the Minyala Currency Bot demo application.
 * Currently prints a simple greeting to STDOUT.
 *
 * @author Anton Tsarenko
 * @since 0.1.0
 */
public class AppLauncher {

  /**
     * Returns a greeting message.
     *
     * @return the string {@code "Hello World!"}
     */
  public String getGreeting() {
    return "Hello World!";
  }

  /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
  public static void main(String[] args) {
    System.out.println(new AppLauncher().getGreeting());
  }
}
