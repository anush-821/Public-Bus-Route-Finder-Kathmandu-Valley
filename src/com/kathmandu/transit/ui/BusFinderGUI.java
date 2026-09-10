package com.kathmandu.transit.ui;

import com.kathmandu.transit.data.KathmanduTransitDataLoader;
import com.kathmandu.transit.model.*;
import com.kathmandu.transit.service.FareCalculatorService;
import com.kathmandu.transit.service.RouteFinderService;
import com.kathmandu.transit.service.StationDirectoryService;
import com.kathmandu.transit.service.TransitGraph;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Desktop Graphical User Interface (Java Swing) for Kathmandu Valley Bus Route Finder.
 */
public class BusFinderGUI extends JFrame {

    /**
     * Devanagari is not included in Segoe UI.  Select a system font that can
     * render Nepali, while retaining Java's logical-font fallback on platforms
     * where none of the preferred fonts is installed.
     */
    private static final String NEPALI_SAMPLE = "नेपाली";
    private static final String UI_FONT_FAMILY = findUnicodeFontFamily();
    private static final Icon SWAP_ICON = new SwapIcon();

    private final TransitGraph graph;
    private final RouteFinderService routeFinder;

    // UI Components
    private JComboBox<Station> originCombo;
    private JComboBox<Station> destinationCombo;
    private JRadioButton fastestRadio;
    private JRadioButton shortestRadio;
    private JRadioButton fewestTransfersRadio;
    private JRadioButton directOnlyRadio;
    private JCheckBox studentConcessionCheck;
    private JPanel resultContainerPanel;
    private JLabel summaryLabel;
    private JTabbedPane tabbedPane;

    public BusFinderGUI() {
        this.graph = KathmanduTransitDataLoader.loadData();
        FareCalculatorService fareCalculator = new FareCalculatorService();
        this.routeFinder = new RouteFinderService(graph, fareCalculator);
        StationDirectoryService directory = new StationDirectoryService(graph);
        initUI(directory);
    }

    private static String findUnicodeFontFamily() {
        String[] candidates = {"Nirmala UI", "Noto Sans Devanagari", "Mangal", "Kokila", "Dialog"};
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String candidate : candidates) {
            Font font = new Font(candidate, Font.PLAIN, 12);
            if (font.canDisplayUpTo(NEPALI_SAMPLE) == -1) {
                return candidate;
            }
        }

        for (String candidate : environment.getAvailableFontFamilyNames()) {
            Font font = new Font(candidate, Font.PLAIN, 12);
            if (font.canDisplayUpTo(NEPALI_SAMPLE) == -1) {
                return candidate;
            }
        }
        return "Dialog";
    }

    private static Font uiFont(int style, int size) {
        //noinspection MagicConstant
        return new Font(UI_FONT_FAMILY, style, size);
    }

    private void initUI(StationDirectoryService directory) {
        setTitle("Kathmandu Valley Public Bus Route Finder | काठमाडौं उपत्यका बस सेवा");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 720);
        setMinimumSize(new Dimension(880, 600));
        setLocationRelativeTo(null);

        // Apply clean look and feel if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(new Color(245, 247, 250));

        // Header Panel
        mainContent.add(createHeaderPanel(), BorderLayout.NORTH);

        // Center split layout: Left Control / Search Panel + Right Result Tabbed Panel
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        centerPanel.setBackground(new Color(245, 247, 250));

        JPanel leftControlPanel = createControlPanel(directory);
        leftControlPanel.setPreferredSize(new Dimension(380, 600));
        centerPanel.add(leftControlPanel, BorderLayout.WEST);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(uiFont(Font.PLAIN, 14));
        tabbedPane.addTab("Route & Directions", createDirectionsTab());
        tabbedPane.addTab("Valley Transit Lines", createRoutesExplorerTab());
        tabbedPane.addTab("Official Fare Slabs", createFareInfoTab());

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainContent);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 43, 73));
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel("Kathmandu Valley Public Bus Route Finder");
        titleLabel.setFont(uiFont(Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        JLabel subTitleLabel = new JLabel("Kathmandu - Lalitpur - Bhaktapur Transit");
        subTitleLabel.setFont(uiFont(Font.PLAIN, 13));
        subTitleLabel.setForeground(new Color(186, 214, 255));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setOpaque(false);
        titleBox.add(titleLabel);
        titleBox.add(subTitleLabel);

        JLabel statsLabel = new JLabel("<html><div style='text-align: right; color: #E2E8F0; font-family: " + UI_FONT_FAMILY + ";'>"
                + "<b>47</b> Stops &bull; <b>10</b> Main Lines<br><span style='color: #6EE7B7;'>Live DoTM Fare Rules</span></div></html>");
        statsLabel.setFont(uiFont(Font.PLAIN, 12));

        header.add(titleBox, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createControlPanel(StationDirectoryService directory) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel cardTitle = new JLabel("Trip Planner");
        cardTitle.setFont(uiFont(Font.BOLD, 17));
        cardTitle.setForeground(new Color(15, 23, 42));
        cardTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cardTitle);
        panel.add(Box.createVerticalStrut(15));

        // Origin selection
        JLabel originLabel = new JLabel("Origin Bus Stop:");
        originLabel.setFont(uiFont(Font.BOLD, 13));
        originLabel.setForeground(new Color(51, 65, 85));
        originLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(originLabel);
        panel.add(Box.createVerticalStrut(5));

        List<Station> allStations = directory.getAllStationsSorted();
        originCombo = new JComboBox<>(allStations.toArray(new Station[0]));
        originCombo.setFont(uiFont(Font.PLAIN, 13));
        originCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        originCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(originCombo);

        // Full-width row guarantees that the compact control is visually centered.
        panel.add(Box.createVerticalStrut(8));
        JButton swapBtn = new JButton("Swap stops", SWAP_ICON);
        swapBtn.setFont(uiFont(Font.PLAIN, 12));
        swapBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        swapBtn.setIconTextGap(8);
        swapBtn.setHorizontalAlignment(SwingConstants.CENTER);
        swapBtn.setPreferredSize(new Dimension(150, 34));
        swapBtn.addActionListener(e -> swapStations());

        JPanel swapButtonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        swapButtonRow.setOpaque(false);
        swapButtonRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        swapButtonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        swapButtonRow.add(swapBtn);
        panel.add(swapButtonRow);
        panel.add(Box.createVerticalStrut(8));

        // Destination selection
        JLabel destLabel = new JLabel("Destination Bus Stop:");
        destLabel.setFont(uiFont(Font.BOLD, 13));
        destLabel.setForeground(new Color(51, 65, 85));
        destLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(destLabel);
        panel.add(Box.createVerticalStrut(5));

        destinationCombo = new JComboBox<>(allStations.toArray(new Station[0]));
        destinationCombo.setFont(uiFont(Font.PLAIN, 13));
        destinationCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        destinationCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Default destination to a different stop
        if (allStations.size() > 5) {
            destinationCombo.setSelectedIndex(5);
        }
        panel.add(destinationCombo);
        panel.add(Box.createVerticalStrut(15));

        // Routing Preferences
        JLabel prefLabel = new JLabel("Route Priority:");
        prefLabel.setFont(uiFont(Font.BOLD, 13));
        prefLabel.setForeground(new Color(51, 65, 85));
        prefLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(prefLabel);
        panel.add(Box.createVerticalStrut(4));

        fastestRadio = new JRadioButton("Fastest", true);
        shortestRadio = new JRadioButton("Shortest Distance");
        fewestTransfersRadio = new JRadioButton("Fewest Transfers");
        directOnlyRadio = new JRadioButton("Direct Routes Only");

        ButtonGroup bg = new ButtonGroup();
        bg.add(fastestRadio);
        bg.add(shortestRadio);
        bg.add(fewestTransfersRadio);
        bg.add(directOnlyRadio);

        for (JRadioButton rb : new JRadioButton[]{fastestRadio, shortestRadio, fewestTransfersRadio, directOnlyRadio}) {
            rb.setFont(uiFont(Font.PLAIN, 12));
            rb.setBackground(Color.WHITE);
            rb.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(rb);
        }

        panel.add(Box.createVerticalStrut(10));

        // Student Concession Checkbox
        studentConcessionCheck = new JCheckBox("Apply Student / Senior Concession (45% Off)");
        studentConcessionCheck.setFont(uiFont(Font.PLAIN, 12));
        studentConcessionCheck.setBackground(Color.WHITE);
        studentConcessionCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(studentConcessionCheck);

        panel.add(Box.createVerticalStrut(15));

        // Search Action Button
        JButton searchBtn = new JButton("Find Best Bus Route");
        searchBtn.setFont(uiFont(Font.BOLD, 14));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setBackground(new Color(16, 149, 93)); // Green
        searchBtn.setFocusPainted(false);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchBtn.addActionListener(e -> executeRouteSearch());
        panel.add(searchBtn);

        panel.add(Box.createVerticalStrut(15));

        // Quick shortcut routes
        JLabel quickLabel = new JLabel("Popular Commute Routes:");
        quickLabel.setFont(uiFont(Font.BOLD, 12));
        quickLabel.setForeground(new Color(100, 116, 139));
        quickLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(quickLabel);
        panel.add(Box.createVerticalStrut(5));

        addQuickRouteButton(panel, "Kalanki -> Suryabinayak (East-West)", "KALANKI", "SURYABINAYAK");
        addQuickRouteButton(panel, "Lagankhel -> Budhanilkantha (North-South)", "LAGANKHEL", "BUDHANILKANTHA");
        addQuickRouteButton(panel, "Ratnapark -> Kamalbinayak (Bhaktapur)", "RATNAPARK", "KAMALBINAYAK");
        addQuickRouteButton(panel, "Balkhu -> Jorpati Corridor", "BALKHU", "JORPATI");

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addQuickRouteButton(JPanel panel, String title, String origId, String destId) {
        JButton btn = new JButton("• " + title);
        btn.setFont(uiFont(Font.PLAIN, 11));
        btn.setForeground(new Color(2, 100, 200));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> {
            Station o = graph.getStationById(origId);
            Station d = graph.getStationById(destId);
            if (o != null && d != null) {
                originCombo.setSelectedItem(o);
                destinationCombo.setSelectedItem(d);
                executeRouteSearch();
            }
        });
        panel.add(btn);
    }

    private void swapStations() {
        Object orig = originCombo.getSelectedItem();
        Object dest = destinationCombo.getSelectedItem();
        originCombo.setSelectedItem(dest);
        destinationCombo.setSelectedItem(orig);
    }

    /** A font-independent icon representing an exchange of the two selected stops. */
    private static final class SwapIcon implements Icon {
        private static final int WIDTH = 22;
        private static final int HEIGHT = 18;

        @Override public int getIconWidth() { return WIDTH; }
        @Override public int getIconHeight() { return HEIGHT; }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setColor(new Color(30, 64, 175));
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(x + 2, y + 5, x + 17, y + 5);
                g.drawLine(x + 17, y + 5, x + 13, y + 1);
                g.drawLine(x + 17, y + 5, x + 13, y + 9);
                g.drawLine(x + 20, y + 13, x + 5, y + 13);
                g.drawLine(x + 5, y + 13, x + 9, y + 9);
                g.drawLine(x + 5, y + 13, x + 9, y + 17);
            } finally { g.dispose(); }
        }
    }

    private JPanel createDirectionsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));

        // Summary Bar on Top
        summaryLabel = new JLabel("Select an origin and destination to view recommended public bus routes.", SwingConstants.CENTER);
        summaryLabel.setFont(uiFont(Font.PLAIN, 14));
        summaryLabel.setOpaque(true);
        summaryLabel.setBackground(Color.WHITE);
        summaryLabel.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));
        panel.add(summaryLabel, BorderLayout.NORTH);

        // Scrollable Result Container
        resultContainerPanel = new JPanel();
        resultContainerPanel.setLayout(new BoxLayout(resultContainerPanel, BoxLayout.Y_AXIS));
        resultContainerPanel.setBackground(new Color(245, 247, 250));

        JScrollPane scrollPane = new JScrollPane(resultContainerPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void executeRouteSearch() {
        tabbedPane.setSelectedIndex(0);
        Station origin = (Station) originCombo.getSelectedItem();
        Station destination = (Station) destinationCombo.getSelectedItem();

        if (origin == null || destination == null) {
            JOptionPane.showMessageDialog(this, "Please select both Origin and Destination stations.",
                    "Input Needed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origin.equals(destination)) {
            JOptionPane.showMessageDialog(this, "Origin and Destination are identical (" + origin.getName() + ").",
                    "Same Station Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        RouteFinderService.Preference pref = RouteFinderService.Preference.FASTEST;
        if (shortestRadio.isSelected()) {
            pref = RouteFinderService.Preference.SHORTEST;
        } else if (fewestTransfersRadio.isSelected()) {
            pref = RouteFinderService.Preference.FEWEST_TRANSFERS;
        } else if (directOnlyRadio.isSelected()) {
            pref = RouteFinderService.Preference.DIRECT_ONLY;
        }

        Optional<JourneyPlan> planOpt = routeFinder.findRoute(origin, destination, pref);

        resultContainerPanel.removeAll();

        if (planOpt.isEmpty() || planOpt.get().getLegs().isEmpty()) {
            summaryLabel.setText("No routes found connecting " + origin.getName() + " and " + destination.getName() + " with the selected criteria.");
            summaryLabel.setForeground(new Color(185, 28, 28));

            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            emptyPanel.add(new JLabel("Try switching to 'Fastest' or 'Fewest Transfers' mode to explore connecting routes."));
            resultContainerPanel.add(emptyPanel);
        } else {
            JourneyPlan plan = planOpt.get();
            boolean student = studentConcessionCheck.isSelected();
            int finalFare = student ? plan.getTotalStudentFareNpr() : plan.getTotalFareNpr();

            String summaryText = String.format(
                    "<html><b>Trip:</b> %s &rarr; %s &nbsp;|&nbsp; <b>Time:</b> ~%d mins &nbsp;|&nbsp; <b>Distance:</b> %.1f km &nbsp;|&nbsp; <b>Transfers:</b> %d &nbsp;|&nbsp; <span style='color: #059669;'><b>Fare:</b> Rs. %d %s</span></html>",
                    plan.getOrigin().getName(), plan.getDestination().getName(),
                    plan.getTotalDurationMinutes(), plan.getTotalDistanceKm(),
                    plan.getTransferCount(), finalFare, (student ? "(Student Rate)" : "NPR")
            );
            summaryLabel.setText(summaryText);
            summaryLabel.setForeground(new Color(15, 23, 42));

            // Populate Leg Cards
            for (int i = 0; i < plan.getLegs().size(); i++) {
                JourneyLeg leg = plan.getLegs().get(i);
                if (i > 0) {
                    resultContainerPanel.add(createTransferBanner(leg.getBoardingStation()));
                }
                resultContainerPanel.add(createLegCard(leg, i + 1, student));
            }
        }

        resultContainerPanel.revalidate();
        resultContainerPanel.repaint();
    }

    private JPanel createTransferBanner(Station transferStation) {
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        banner.setBackground(new Color(254, 243, 199)); // Amber light
        banner.setBorder(new CompoundBorder(
                new LineBorder(new Color(245, 158, 11), 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JLabel label = new JLabel("🔄 Interchange at " + transferStation.getDisplayLabel() + ": Switch bus (~7 min walk & wait)");
        label.setFont(uiFont(Font.BOLD, 12));
        label.setForeground(new Color(146, 64, 14));
        banner.add(label);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(8, 0, 8, 0));
        wrapper.add(banner, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createLegCard(JourneyLeg leg, int legNumber, boolean student) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        BusRoute route = leg.getRoute();
        int legFare = student ? leg.getStudentFareNpr() : leg.getFareNpr();

        // Top info bar: Leg number, Vehicle Tag, Route Name, Fare badge
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel routeTitle = new JLabel(String.format("Leg %d: %s %s (%s)",
                legNumber, route.getVehicleType().getTag(), route.getName(), route.getOperator()));
        routeTitle.setFont(uiFont(Font.BOLD, 14));
        routeTitle.setForeground(new Color(15, 23, 42));

        JLabel fareBadge = new JLabel(String.format(" Fare: Rs. %d ", legFare));
        fareBadge.setFont(uiFont(Font.BOLD, 13));
        fareBadge.setOpaque(true);
        fareBadge.setBackground(new Color(220, 252, 231));
        fareBadge.setForeground(new Color(21, 128, 61));
        fareBadge.setBorder(new EmptyBorder(3, 8, 3, 8));

        topRow.add(routeTitle, BorderLayout.WEST);
        topRow.add(fareBadge, BorderLayout.EAST);
        card.add(topRow, BorderLayout.NORTH);

        // Center: Boarding & Alighting stops details
        JPanel centerRow = new JPanel(new GridLayout(2, 1, 4, 4));
        centerRow.setOpaque(false);

        JLabel boardLabel = new JLabel("● Board at: " + leg.getBoardingStation().getDisplayLabel() + 
                "  [" + leg.getBoardingStation().getArea() + "]");
        boardLabel.setFont(uiFont(Font.BOLD, 13));
        boardLabel.setForeground(new Color(30, 64, 175));

        JLabel alightLabel = new JLabel("◎ Alight at: " + leg.getAlightingStation().getDisplayLabel() + 
                "  [" + leg.getAlightingStation().getArea() + "]");
        alightLabel.setFont(uiFont(Font.BOLD, 13));
        alightLabel.setForeground(new Color(185, 28, 28));

        centerRow.add(boardLabel);
        centerRow.add(alightLabel);
        card.add(centerRow, BorderLayout.CENTER);

        // Bottom stats: Intermediate stops summary, duration, distance
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);

        String stopsSummary = leg.getIntermediateStops().isEmpty() ? "Direct non-stop link" :
                "Passing " + leg.getIntermediateStops().size() + " stops (" + formatStopsList(leg.getIntermediateStops()) + ")";
        JLabel stopsLabel = new JLabel(stopsSummary);
        stopsLabel.setFont(uiFont(Font.ITALIC, 11));
        stopsLabel.setForeground(new Color(100, 116, 139));

        JLabel stats = new JLabel(String.format("~%d min &bull; %.1f km &bull; Freq: every %d min",
                leg.getDurationMinutes(), leg.getDistanceKm(), route.getFrequencyMinutes()));
        stats.setFont(uiFont(Font.PLAIN, 12));
        stats.setForeground(new Color(71, 85, 105));

        bottomRow.add(stopsLabel, BorderLayout.WEST);
        bottomRow.add(stats, BorderLayout.EAST);
        card.add(bottomRow, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(6, 0, 6, 0));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private String formatStopsList(List<Station> stops) {
        StringBuilder sb = new StringBuilder();
        int max = Math.min(3, stops.size());
        for (int i = 0; i < max; i++) {
            sb.append(stops.get(i).getName());
            if (i < max - 1) sb.append(", ");
        }
        if (stops.size() > 3) {
            sb.append("...");
        }
        return sb.toString();
    }

    private JPanel createRoutesExplorerTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        Collection<BusRoute> routes = graph.getAllRoutes();
        JPanel routesList = new JPanel();
        routesList.setLayout(new BoxLayout(routesList, BoxLayout.Y_AXIS));
        routesList.setBackground(new Color(245, 247, 250));

        for (BusRoute r : routes) {
            JPanel card = new JPanel(new BorderLayout(8, 8));
            card.setBackground(Color.WHITE);
            card.setBorder(new CompoundBorder(
                    new LineBorder(new Color(226, 232, 240), 1, true),
                    new EmptyBorder(12, 14, 12, 14)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            JLabel title = new JLabel("[" + r.getCode() + "] " + r.getName() + " (" + r.getOperator() + ")");
            title.setFont(uiFont(Font.BOLD, 14));
            title.setForeground(new Color(15, 23, 42));

            JLabel sub = new JLabel(r.getVehicleType().getDisplayName() + " | Operating: " + 
                    r.getOperatingHours() + " | Frequency: ~" + r.getFrequencyMinutes() + " mins");
            sub.setFont(uiFont(Font.PLAIN, 12));
            sub.setForeground(new Color(71, 85, 105));

            StringBuilder stopsStr = new StringBuilder("Stops: ");
            for (int i = 0; i < r.getStations().size(); i++) {
                stopsStr.append(r.getStations().get(i).getName());
                if (i < r.getStations().size() - 1) stopsStr.append(" -> ");
            }
            JLabel stopsLbl = new JLabel("<html><body style='width: 550px; font-family: " + UI_FONT_FAMILY + "; font-size: 11px; color: #64748B;'>" 
                    + stopsStr + "</body></html>");

            card.add(title, BorderLayout.NORTH);
            card.add(sub, BorderLayout.CENTER);
            card.add(stopsLbl, BorderLayout.SOUTH);

            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(new EmptyBorder(4, 0, 4, 0));
            wrapper.add(card, BorderLayout.CENTER);
            routesList.add(wrapper);
        }

        JScrollPane sp = new JScrollPane(routesList);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFareInfoTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        String html = "<html><body style='font-family: " + UI_FONT_FAMILY + "; color: #1E293B;'>"
                + "<h2 style='color: #0F172A; margin-bottom: 4px;'>Official Public Transport Fare Rules (Kathmandu Valley)</h2>"
                + "<p style='color: #64748B; margin-top: 0;'>Regulated by the Department of Transport Management (DoTM), Nepal.</p>"
                + "<br>"
                + "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse; border-color: #CBD5E1; width: 100%; font-size: 13px;'>"
                + "<tr style='background-color: #F1F5F9; font-weight: bold;'>"
                + "  <td>Distance Travelled</td>"
                + "  <td>Standard Fare (NPR)</td>"
                + "  <td>Student / Senior Concession (45% Discount)</td>"
                + "</tr>"
                + "<tr><td>0 to 5 km (Base fare)</td><td><b>Rs. 20</b></td><td>Rs. 11</td></tr>"
                + "<tr><td>5 to 10 km</td><td><b>Rs. 27</b></td><td>Rs. 15</td></tr>"
                + "<tr><td>10 to 15 km</td><td><b>Rs. 32</b></td><td>Rs. 18</td></tr>"
                + "<tr><td>15 to 20 km</td><td><b>Rs. 35</b></td><td>Rs. 19</td></tr>"
                + "<tr><td>Above 20 km</td><td><b>Rs. 40</b></td><td>Rs. 22</td></tr>"
                + "</table>"
                + "<br><br>"
                + "<h4 style='color: #0F172A;'>Commuter Guidelines:</h4>"
                + "<ul style='line-height: 1.6; font-size: 13px; color: #334155;'>"
                + "  <li><b>Concession Verification:</b> Students must carry and present a valid educational institution ID card to the conductor to avail the 45% discount.</li>"
                + "  <li><b>Separate Fare per Bus:</b> When transferring between two different bus lines, fare is payable per vehicle leg according to the distance traveled on that line.</li>"
                + "  <li><b>Ring Road Circular:</b> Ring Road buses run both clockwise and anti-clockwise loops. Choose the direction corresponding to the shortest half-circle to save time.</li>"
                + "  <li><b>Peak Traffic Hours:</b> Major choke points include Kalanki, Koteshwor, Maitighar, and Chabahil during peak morning (9:00 AM - 11:00 AM) and evening (4:30 PM - 7:00 PM) hours.</li>"
                + "</ul>"
                + "</body></html>";

        JLabel content = new JLabel(html);
        content.setVerticalAlignment(SwingConstants.TOP);

        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }
}
