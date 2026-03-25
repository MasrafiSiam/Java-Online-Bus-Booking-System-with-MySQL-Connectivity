package view;

import controller.AuthController;
import util.Theme;

import javax.swing.*;
import java.awt.*;

public class RegisterGUI extends BaseFrame {

    public RegisterGUI() {
        super("Create Account", 920, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        int fx = 270, fw = 380;

        // Back arrow
        JButton back = Theme.secondaryButton("← Back to Login");
        back.setBounds(30, 25, 160, 36);
        root.add(back);

        // Header
        JLabel icon = new JLabel("🚌");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        icon.setBounds(fx, 55, 60, 50);
        root.add(icon);

        JLabel title = new JLabel("Create your account");
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setFont(Theme.FONT_TITLE);
        title.setBounds(fx, 100, fw, 40);
        root.add(title);

        JLabel sub = Theme.label("Book your first trip in minutes", Theme.TEXT_MUTED, Theme.FONT_BODY);
        sub.setBounds(fx, 140, fw, 25);
        root.add(sub);

        addRule(fx, 172, 340);

        // Full Name
        addLabel("Full Name", fx, 192, fw, 20, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        JTextField nameF = Theme.styledField("");
        nameF.setBounds(fx, 214, fw, 40);
        root.add(nameF);

        // Email
        addLabel("Email Address", fx, 268, fw, 20, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        JTextField emailF = Theme.styledField("");
        emailF.setBounds(fx, 290, fw, 40);
        root.add(emailF);

        // Password
        addLabel("Password", fx, 344, fw, 20, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        JPasswordField passF = Theme.styledPassField();
        passF.setBounds(fx, 366, fw, 40);
        root.add(passF);

        // Confirm Password
        addLabel("Confirm Password", fx, 420, fw, 20, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        JPasswordField passF2 = Theme.styledPassField();
        passF2.setBounds(fx, 442, fw, 40);
        root.add(passF2);

        // Password strength bar
        JPanel strengthBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BORDER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                String pwd = new String(passF.getPassword());
                int strength = getStrength(pwd);
                if (strength > 0) {
                    Color c = strength == 1 ? Theme.DANGER : strength == 2 ? Theme.WARN : Theme.SUCCESS;
                    g2.setColor(c);
                    g2.fillRoundRect(0, 0, getWidth() * strength / 3, getHeight(), 4, 4);
                }
                g2.dispose();
            }
            int getStrength(String p) {
                if (p.isEmpty()) return 0;
                int s = 0;
                if (p.length() >= 6) s++;
                if (p.matches(".*[A-Z].*") || p.matches(".*[0-9].*")) s++;
                if (p.matches(".*[^a-zA-Z0-9].*")) s++;
                return Math.max(1, s);
            }
        };
        strengthBar.setOpaque(false);
        strengthBar.setBounds(fx, 490, fw, 4);
        root.add(strengthBar);
        passF.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                strengthBar.repaint();
            }
        });

        JLabel strengthLabel = Theme.label("Password strength", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        strengthLabel.setBounds(fx, 498, fw, 18);
        root.add(strengthLabel);

        // Status
        JLabel status = Theme.label("", Theme.DANGER, Theme.FONT_SMALL);
        status.setBounds(fx, 520, fw, 20);
        root.add(status);

        // Register button
        JButton regBtn = Theme.primaryButton("Create Account");
        regBtn.setBounds(fx, 546, fw, 46);
        root.add(regBtn);

        // ── Actions ────────────────────────────────────────────────────────
        AuthController ctrl = new AuthController();

        regBtn.addActionListener(e -> {
            String name  = nameF.getText().trim();
            String email = emailF.getText().trim();
            String pass  = new String(passF.getPassword());
            String pass2 = new String(passF2.getPassword());

            if (name.isBlank() || email.isBlank() || pass.isBlank()) {
                status.setForeground(Theme.DANGER);
                status.setText("All fields are required."); return;
            }
            if (!email.contains("@")) {
                status.setForeground(Theme.DANGER);
                status.setText("Please enter a valid email."); return;
            }
            if (pass.length() < 4) {
                status.setForeground(Theme.DANGER);
                status.setText("Password must be at least 4 characters."); return;
            }
            if (!pass.equals(pass2)) {
                status.setForeground(Theme.DANGER);
                status.setText("Passwords do not match."); return;
            }

            if (ctrl.register(name, email, pass)) {
                JOptionPane.showMessageDialog(this,
                        "Account created! Please log in.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginGUI(); dispose();
            } else {
                status.setForeground(Theme.DANGER);
                status.setText("Email already registered or error occurred.");
            }
        });

        back.addActionListener(e -> { new LoginGUI(); dispose(); });
    }
}
