package org.project.ninjas.minyala.currency.bot.banks;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.project.ninjas.minyala.currency.bot.banks.model.CurrencyRate;
import org.project.ninjas.minyala.currency.bot.banks.service.BankAggregatorServiceImpl;
import org.project.ninjas.minyala.currency.bot.banks.service.BankRateService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.BankAggregatorService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.MonobankService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.NbuService;
import org.project.ninjas.minyala.currency.bot.banks.service.impl.PrivatBankService;
import org.project.ninjas.minyala.currency.bot.banks.util.CurrencyFormatter;

/**
 * Demonstrates how to use the bank services and aggregator
 * through a simple command-line interface.
 */
public final class ApiDemo {

  private ApiDemo() {
    // Prevent instantiation
  }

  /**
   * Entry point for the API demo.
   *
   * @param args command-line arguments (not used)
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("=== Currency Rates Demo ===");
    System.out.println("Choose bank(s) to fetch data from:");
    System.out.println("1 - Monobank");
    System.out.println("2 - PrivatBank");
    System.out.println("3 - NBU");
    System.out.println("4 - All banks");
    System.out.print("Your choice: ");
    int choice = scanner.nextInt();

    System.out.print("Enter number of digits after comma (e.g., 2): ");
    final int digits = scanner.nextInt();
    System.out.println();

    List<BankRateService> selectedBanks = new ArrayList<>();

    switch (choice) {
      case 1:
        selectedBanks.add(new MonobankService());
        break;
      case 2:
        selectedBanks.add(new PrivatBankService());
        break;
      case 3:
        selectedBanks.add(new NbuService());
        break;
      case 4:
        selectedBanks.add(new MonobankService());
        selectedBanks.add(new PrivatBankService());
        selectedBanks.add(new NbuService());
        break;
      default:
        System.out.println("⚠ Invalid choice. Exiting.");
        scanner.close();
        return;
    }

    BankAggregatorService aggregator = new BankAggregatorServiceImpl(selectedBanks);

    System.out.println("Fetching currency data...");
    List<CurrencyRate> rates = aggregator.getAllRates();

    if (rates.isEmpty()) {
      System.out.println("No data available (check API or implementation).");
    } else {
      System.out.println("\n Results:");
      for (CurrencyRate rate : rates) {
        printFormattedRate(rate, digits);
      }
    }

    System.out.println("\nDone.");
    scanner.close();
  }

  /**
   * Prints a formatted currency rate according to user precision.
   *
   * @param rate   the currency rate to print
   * @param digits number of digits after the decimal point
   */
  private static void printFormattedRate(CurrencyRate rate, int digits) {
    String buy = rate.getBuy() > 0 ? CurrencyFormatter.format(rate.getBuy(), digits) : "-";
    String sell = rate.getSell() > 0 ? CurrencyFormatter.format(rate.getSell(), digits) : "-";
    String official = rate.getRate() > 0 ? CurrencyFormatter.format(rate.getRate(), digits) : "-";

    System.out.printf("%s | %s | Buy: %s | Sell: %s | Rate: %s%n",
        rate.getBankName(), rate.getCurrency(), buy, sell, official);
  }
}
