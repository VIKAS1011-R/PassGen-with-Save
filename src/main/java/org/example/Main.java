package org.example;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.*;

public class Main extends JFrame implements ActionListener, ChangeListener {
    // Define character sets for password generation
    @Serial
    private static final long serialVersionUID = 1L;
    private static final String CHARACTERS_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHARACTERS_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHARACTERS_DIGITS = "0123456789";
    private static final String CHARACTERS_SPECIAL = "!@#$%^&*()_-+=<>";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 20;

    // User interface components
    private final JCheckBox upperCaseCheckbox;
    private final JCheckBox lowerCaseCheckbox;
    private final JCheckBox digitsCheckbox;
    private final JCheckBox specialCheckbox;
    private final JSlider lengthSlider;
    private final JTextField lengthTextField;
    private final JButton generateButton;
    private final JButton copyButton;
    private final JButton copyandsave;
    private final JButton Findsavedpassword;
    private final JLabel passwordLabel;

    public Main() {
        setTitle("Password Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        // Initialize user interface components
        upperCaseCheckbox = new JCheckBox("Include Uppercase Letters");
        lowerCaseCheckbox = new JCheckBox("Include Lowercase Letters");
        digitsCheckbox = new JCheckBox("Include Digits");
        specialCheckbox = new JCheckBox("Include Special Characters");
        upperCaseCheckbox.setSelected(true);
        lowerCaseCheckbox.setSelected(true);
        digitsCheckbox.setSelected(true);

        lengthSlider = new JSlider(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH);
        lengthSlider.setMajorTickSpacing(2);
        lengthSlider.setPaintTicks(true);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setValue(8);
        lengthSlider.addChangeListener(this);

        lengthTextField = new JTextField(2);
        lengthTextField.setText(Integer.toString(lengthSlider.getValue()));

        generateButton = new JButton("Generate Password");
        generateButton.addActionListener(this);

        copyButton = new JButton("Copy to Clipboard");
        copyButton.addActionListener(this);

        copyandsave = new JButton("Copy and Save Password");
        copyandsave.addActionListener(this);

        Findsavedpassword = new JButton("Find Password");
        Findsavedpassword.addActionListener(this);

        passwordLabel = new JLabel();

        // Add components to the panel
        panel.add(upperCaseCheckbox);
        panel.add(lowerCaseCheckbox);
        panel.add(digitsCheckbox);
        panel.add(specialCheckbox);
        panel.add(new JLabel("Password Length:"));
        panel.add(lengthSlider);
        panel.add(lengthTextField);
        panel.add(generateButton);
        panel.add(copyButton);
        panel.add(copyandsave);
        panel.add(Findsavedpassword);
        panel.add(passwordLabel);

        add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setSize(300, 400);
        setResizable(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            // Get the desired password length from the user
            int passwordLength = Integer.parseInt(lengthTextField.getText());
            // Check if the password length is within the allowed range
            if (passwordLength < MIN_PASSWORD_LENGTH || passwordLength > MAX_PASSWORD_LENGTH) {
                JOptionPane.showMessageDialog(this, "Invalid password length. Length should be between " +
                        MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + ".", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Determine which character sets to include in the password
            boolean includeUpperCase = upperCaseCheckbox.isSelected();
            boolean includeLowerCase = lowerCaseCheckbox.isSelected();
            boolean includeDigits = digitsCheckbox.isSelected();
            boolean includeSpecial = specialCheckbox.isSelected();

            if (!includeUpperCase && !includeLowerCase && !includeDigits && !includeSpecial) {
                JOptionPane.showMessageDialog(this, "Please select at least one character set.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Generate the password based on user preferences
            String password = generatePassword(passwordLength, includeUpperCase, includeLowerCase,            includeDigits, includeSpecial);
            // Display the generated password
            passwordLabel.setText("Generated Password: " + password);
        } else if (e.getSource() == copyButton) {

            // Copy the generated password to the system clipboard
            String password = passwordLabel.getText().replace("Generated Password: ", "");
            if (!password.isEmpty()) {
                copyToClipboard(password);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == copyandsave) {
            String password = passwordLabel.getText().replace("Generated Password: ", "");
            if (!password.isEmpty()) {
                savetofile(password);
            }
        }else if (e.getSource() == Findsavedpassword) {
            findPass();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == lengthSlider) {
            // Update the password length text field based on the slider value
            int length = lengthSlider.getValue();
            lengthTextField.setText(Integer.toString(length));
        }
    }

    private String generatePassword(int length, boolean includeUpperCase, boolean includeLowerCase,
                                    boolean includeDigits, boolean includeSpecial) {
        StringBuilder characters = new StringBuilder();
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        // Build the character set based on user preferences
        if (includeUpperCase) {
            characters.append(CHARACTERS_UPPER);
        }
        if (includeLowerCase) {
            characters.append(CHARACTERS_LOWER);
        }
        if (includeDigits) {
            characters.append(CHARACTERS_DIGITS);
        }
        if (includeSpecial) {
            characters.append(CHARACTERS_SPECIAL);
        }

        // Generate the password by selecting random characters from the character set
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private void copyToClipboard(String text) {
        // Copy the given text to the system clipboard
        StringSelection selection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
    }

    private void savetofile(String text) {
        String FilePath = "PasswordSaves.txt";
        try {
            File check = new File(FilePath);
            if (!check.exists()) {
                check.createNewFile();
            }
            String forAcc = JOptionPane.showInputDialog("For which Service Are You Copying the Password For?");
            String FinalText = forAcc+" = "+text;
            try {
                FileWriter fw = new FileWriter(FilePath,true);
                fw.write(FinalText);
                fw.append("\n");
                fw.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Password Saved", "Success", JOptionPane.INFORMATION_MESSAGE);

        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null, "File not found . ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void findPass(){
        String FilePath = "PasswordSaves.txt";
        try {
            String fileContents = Files.readString(Path.of(FilePath));
            String[] rawData = fileContents.split("\n");
            int count = Integer.parseInt(String.valueOf(Files.lines(Path.of(FilePath)).count()));
            String[] Accvalues = new String[count];
            String[] Passwords = new String[count];
            for (int i = 0; i < count; i++) {
                String[] parts = rawData[i].split(" = ");
                Accvalues[i] = parts[0];
                Passwords[i] = parts[1];
            }
            for (String l : Accvalues) {
                System.out.println(l);
            }
            for (String p : Passwords) {
                System.out.println(p);
            }
            String Choice = JOptionPane.showInputDialog("Enter 1 For Searching \nEnter 2 for displaying the list \nEnter 3 for Exit. ");
            switch (Choice) {
                case "1":Search(count,Accvalues,Passwords);
                         break;
                case "2":Display(Accvalues);
                         break;
                case "3": break;
                default: JOptionPane.showMessageDialog(null, "Invalid Choice", "Error", JOptionPane.ERROR_MESSAGE);
                findPass();
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void Search(int count,String[] Accvalues,String[] Passwords){
        String textACC = JOptionPane.showInputDialog("Enter the Account for  which the Password u wanna  know !");
        for (int i = 0; i < count; i++) {
            if(textACC.equalsIgnoreCase(Accvalues[i])){
                copyToClipboard(Passwords[i]);
                JOptionPane.showMessageDialog(this, "Copied to clipboard", "Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
    }

    public void Display(String[] ACC){
        JOptionPane.showMessageDialog(null, ACC, "Success", JOptionPane.INFORMATION_MESSAGE);
        findPass();
    }

    public static void main(String[] args) {
        // Create an instance of the PasswordGenerator class
        Main passwordGene = new Main();
    }
}