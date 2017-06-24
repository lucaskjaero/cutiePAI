package com.dapoppy.v001;

import javax.swing.*; // needed for Swing GUI classes
import java.awt.*; // needed for some GUI elements not covered by Swing
import java.awt.event.*; // needed for ActionListener interface
import java.io.IOException;
import java.util.ArrayList;

/**
 * This GUI class for Poppy extends the JFrame class. Its constructor
 * displays a simple window with a title. The application exits when
 * the user clicks the close button.
 */
public class Body extends JFrame {
    private JPanel appearance; // To reference a panel
    private JLabel expression; // To reference a label
    private JTextArea poppyChat, userInputField; // text area for user input
    private JButton okayButton, againButton, yesButton, noButton; // To reference a button
    private final int WINDOW_WIDTH = 350; // Window width
    private final int WINDOW_HEIGHT = 600; // Window height
    private String currentTopic = "";
    private String userOS = "win";
    private boolean isInChain = false; // If true, compare user input with reaction data instead of pattern
    private boolean start = true; // Used for "Again" button
    private boolean isConfirmingInput = false; // Used for "Yes" and "No" buttons' listeners & listenToUser()
    private int oReactionID = 0;
    private int confirmedInputID = 0; // Current order among the patterns similar to user inputs
    private String oInputVar = ""; // Origin Input Variable for origin user inputs with dynamic part(s)
    private ArrayList<String> confirmableInputs = new ArrayList<>(); // Array of patterns similar to user inputs
    private Idea oReaction; // To reference the original reaction of a chain reaction

    public Body(String operatingSystem) throws IOException {
        userOS = operatingSystem;
        // Create a window.
        JFrame window = new JFrame();
        // Set the title.
        window.setTitle("Poppy the Desktop Assistant");
        // Set the size of the window.
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        // Specify what happens when the close button is clicked.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set window as always on top even when other windows are clicked
        window.setAlwaysOnTop(true);
        // Set window as not resizable when dragging the border
        window.setResizable(false);
        // Build the panel and add it to the frame.
        poppy();
        // Add the panel to the frame's content pane.
        window.add(appearance);
        // Display the window.
        window.setVisible(true);
    }

    private void poppy() throws IOException {
        // Add the image
        ImageIcon facial = new ImageIcon("mem/img/love.gif");
        expression = new JLabel(facial, JLabel.CENTER);
        // Create a label to display instructions.
        poppyChat = new JTextArea(9, 28);
        poppyChat.setLineWrap(true);
        poppyChat.setWrapStyleWord(true);
        poppyChat.setEditable(false);
        poppyChat.setBackground(new Color(238, 238, 238, 255));
        poppyChat.setText(Lip.sayHello() + " " + Lip.sayGreeting());
        // Create a text area for user input
        userInputField = new JTextArea(9, 28);
        userInputField.setLineWrap(true);
        userInputField.setWrapStyleWord(true);
        // Create a listener for when user press Enter
        userInputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    // Do try-catch here instead of throwing IOException after parent override method
                    try {
                        if (isConfirmingInput == true && confirmedInputID <= (confirmableInputs.size() - 1)) {
                            customReact("Simply press the Yes or No button to answer, and " +
                                    "Okay button to do something else. Did you mean \""
                                    + confirmableInputs.get(confirmedInputID) + "\" ?", "money-choice");
                        }else{
                            clearInputSuggestion(); // Clear close match suggestion parameters to avoid bugs
                            listenToUser(""); // Call the most important method of Poppy
                        }
                    } catch (IOException ex) {
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
        // Create 2 buttons, 'Okay' for yielding chat turn to Poppy, 'Again' for repeating last action
        okayButton = new JButton("Okay");
        // Shorter text label for Again button in Mac
        againButton = (userOS == "mac") ? new JButton("Again") : new JButton("Do it again");
        yesButton = new JButton("Yes");
        noButton = new JButton("No");
        // Add action listeners to the buttons.
        okayButton.addActionListener(new okayButtonListener());
        againButton.addActionListener(new againButtonListener());
        yesButton.addActionListener(new yesButtonListener());
        noButton.addActionListener(new noButtonListener());
        // Create a JPanel object and let the panel field reference it.
        appearance = new JPanel();
        // Add all components (in this order) to the 'appearance' panel.
        appearance.add(expression, BorderLayout.CENTER);
        appearance.add(poppyChat);
        appearance.add(userInputField);
        appearance.add(Box.createRigidArea(new Dimension(350, 10)));
        appearance.add(okayButton);
        appearance.add(againButton);
        appearance.add(yesButton);
        appearance.add(noButton);
        userInputField.requestFocus();
    }

    private class okayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                clearInputSuggestion(); // Clear close match parameter suggestions, just in case
                customReact("...", "love");
            } catch (IOException ae) {}
        }
    }

    private class againButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (start) { // Default reaction if there is no previous reaction to repeat
                String defaultChat = "We're just getting started. I don't know what to repeat again.";
                String defaultGif = "turning";
                expression.setIcon(new ImageIcon(new ImageIcon("mem/img/" + defaultGif + ".gif").getImage().getScaledInstance(350, 197, Image.SCALE_DEFAULT)));
                poppyChat.setText(defaultChat); // Change the chat response
            } else {
                try { // Do this to catch IOException within an ActionListener
                    reactToUser(oReaction);
                } catch (IOException ex) {
                }
            }
        }
    }

    private class yesButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (isConfirmingInput == true) {
                    listenToUser(confirmableInputs.get(confirmedInputID));
                    clearInputSuggestion(); // Clear close match parameter suggestions
                }
            }catch (IOException ea) {}
        }
    }

    private class noButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                if (isConfirmingInput == true) {
                    confirmedInputID++; // Increment the order of suggested input pattern
                    if (confirmedInputID > (confirmableInputs.size() - 1)) {
                        customReact("Oh, ok...", "casual-looking");
                    } else {
                        customReact("Did you mean \"" + confirmableInputs.get(confirmedInputID)
                                + "\" ?", "money-choice");
                    }
                }
            }catch (IOException ea) {}
        }
    }

    /**
     * Main method for all functions to happen when user press Enter
     *
     * @throws IOException
     */
    private void listenToUser(String uContext) throws IOException {
        // If empty, get uContext from user input text area & prep it
        uContext = uContext.equals("") ? Ear.clearChat(userInputField.getText()) : uContext;
        // Create new Idea object for pattern-reaction matching
        Idea currentThought = new Idea(uContext, currentTopic);

        if (start) { // Skips if not 1st reaction
            start = false; // Change the 'start' flag to false right after 1st reaction
        }

        // If there is no exact pattern match, try again using Levenshtein distance
        if (currentThought.originReactionID() == -1) {
            confirmableInputs = (ArrayList) Head.getSimilarPatterns(uContext).clone(); // Cloning values

            // If there is more than 1 element in the array list, there exists similar patterns
            // besides the place holder one.
            if (confirmableInputs.size() > 1) {
                isConfirmingInput = true; // Pressing Yes or No button switches the flag back to False
                confirmedInputID++; // Increment the order of suggested input pattern
                customReact("Did you mean \"" + confirmableInputs.get(confirmedInputID)
                        + "\" ?", "money-choice");
            } else {
                reactToUser(currentThought); // Show confused reaction
            }
        } else {
            // Check if a same chain reaction still going. If not, change related variables.
            if (oReactionID != currentThought.originReactionID() || (!oInputVar.equals(currentThought.getInputVar()))) {
                oReaction = new Idea(currentThought); // Copy currentThought object to oReaction field
                oReactionID = currentThought.originReactionID(); // Copy ID of original reaction to shorten things
                oInputVar = currentThought.getInputVar();
            }
            reactToUser(currentThought);
        }
    }

    // Pass an Idea object and carry out related reaction
    private void reactToUser(Idea reaction) throws IOException {
        // Change the gif response according to user input
        expression.setIcon(new ImageIcon(new ImageIcon("mem/img/" + reaction.getGifResponses()
                + ".gif").getImage().getScaledInstance(350, 197, Image.SCALE_DEFAULT)));
        poppyChat.setText(reaction.getChatResponse()); // Change the chat response
        userInputField.setText(""); // Reset user input text area
        userInputField.requestFocus(); // Redirect mouse cursor to text area
        reaction.takeAction(); // take action accordingly
    }

    /**
     * Customize reaction to fit a certain purpose
     * @param output The text response
     * @param outlook The image response
     * @throws IOException
     */
    private void customReact(String output, String outlook) throws IOException {
        // Change the gif response according to user input
        expression.setIcon(new ImageIcon(new ImageIcon("mem/" + "img/" + outlook
                + ".gif").getImage().getScaledInstance(350, 197, Image.SCALE_DEFAULT)));
        poppyChat.setText(output); // Change the chat response
        userInputField.setText(""); // Reset user input text area
        userInputField.requestFocus(); // Redirect mouse cursor to text area
    }

    // Clear all variables related to close match suggestions to user input
    private void clearInputSuggestion() {
        confirmedInputID = 0;
        confirmableInputs.clear();
        isConfirmingInput = false;
    }
}