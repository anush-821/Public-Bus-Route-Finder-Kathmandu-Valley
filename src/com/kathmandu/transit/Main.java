package com.kathmandu.transit;

import com.kathmandu.transit.test.TransitAppTest;
import com.kathmandu.transit.ui.BusFinderGUI;
import com.kathmandu.transit.ui.ConsoleApp;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

/**
 * Main application entry point for Kathmandu Valley Public Bus Route Finder.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            switch (arg) {
                case "--cli":
                case "-c":
                    launchCLI();
                    return;
                case "--gui":
                case "-g":
                    launchGUI();
                    return;
                case "--test":
                case "-t":
                    TransitAppTest.main(new String[0]);
                    return;
                case "--help":
                case "-h":
                    printHelp();
                    return;
                default:
                    System.out.println("Unknown option: " + args[0]);
                    printHelp();
                    return;
            }
        }

        // Default behavior: launch GUI if desktop display is available, otherwise CLI
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                launchGUI();
            } catch (Throwable t) {
                System.out.println("Notice: Could not initialize Graphical UI. Falling back to Console CLI...");
                launchCLI();
            }
        } else {
            launchCLI();
        }
    }

    private static void launchGUI() {
        SwingUtilities.invokeLater(() -> {
            BusFinderGUI gui = new BusFinderGUI();
            gui.setVisible(true);
        });
    }

    private static void launchCLI() {
        ConsoleApp app = new ConsoleApp();
        app.start();
    }

    private static void printHelp() {
        System.out.println("Kathmandu Valley Public Bus Route Finder");
        System.out.println("Usage: java -cp bin com.kathmandu.transit.Main [OPTION]");
        System.out.println("Options:");
        System.out.println("  --gui,  -g    Launch the desktop Graphical User Interface (Swing)");
        System.out.println("  --cli,  -c    Launch the interactive Terminal / Console Interface");
        System.out.println("  --test, -t    Run the automated transit routing test suite");
        System.out.println("  --help, -h    Display this help message and exit");
    }
}