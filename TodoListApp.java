import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class TodoListApp extends JFrame implements ActionListener {

    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskField;
    private JButton addButton, removeButton, clearButton, darkModeButton, editButton;
    private boolean darkMode = false;
    private final String FILE_NAME = "tasks.txt";

    public TodoListApp() {
        setTitle("To-Do List");
        setSize(450, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255)); // light background

        taskField = new JTextField();
        taskField.setFont(new Font("Arial", Font.PLAIN, 14));
        add(taskField, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 16));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);
        add(scrollPane, BorderLayout.CENTER);

        // Double-click to toggle completed
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = taskList.locationToIndex(evt.getPoint());
                    if (index != -1) {
                        String task = taskListModel.getElementAt(index);
                        if (task.startsWith("[✓] ")) {
                            task = task.substring(4); // Remove completed mark
                        } else {
                            task = "[✓] " + task;
                        }
                        taskListModel.setElementAt(task, index);
                    }
                }
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255));

        addButton = createButton("Add", new Color(144, 238, 144));
        removeButton = createButton("Remove", new Color(255, 160, 122));
        clearButton = createButton("Clear All", new Color(173, 216, 230));
        darkModeButton = createButton("Dark Mode", new Color(211, 211, 211));
        editButton = createButton("Edit", new Color(255, 228, 181));

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(editButton);
        buttonPanel.add(darkModeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enter key to add task
        taskField.addActionListener(e -> addTask());

        loadTasks();

        setLocationRelativeTo(null);
        setVisible(true);

        // Save tasks on close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(110, 40));
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(this);
        return button;
    }

    private void addTask() {
        String task = taskField.getText().trim();
        if (!task.isEmpty()) {
            taskListModel.addElement(task);
            taskField.setText("");
        }
    }

    private void removeTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            taskListModel.remove(index);
        }
    }

    private void clearTasks() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all tasks?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            taskListModel.clear();
        }
    }

    private void editTask() {
        int index = taskList.getSelectedIndex();
        if (index != -1) {
            String currentTask = taskListModel.getElementAt(index).replace("[✓] ", "");
            String newTask = JOptionPane.showInputDialog(this, "Edit Task:", currentTask);
            if (newTask != null && !newTask.trim().isEmpty()) {
                if (taskListModel.getElementAt(index).startsWith("[✓] ")) {
                    newTask = "[✓] " + newTask;
                }
                taskListModel.setElementAt(newTask, index);
            }
        }
    }

    private void toggleDarkMode() {
        darkMode = !darkMode;
        Color bg = darkMode ? new Color(40, 44, 52) : new Color(240, 248, 255);
        Color fg = darkMode ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bg);
        taskList.setBackground(bg);
        taskList.setForeground(fg);
        taskField.setBackground(bg);
        taskField.setForeground(fg);

        darkModeButton.setText(darkMode ? "Light Mode" : "Dark Mode");
    }

    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                writer.println(taskListModel.getElementAt(i));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tasks.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    taskListModel.addElement(line);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading tasks.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addTask();
        } else if (e.getSource() == removeButton) {
            removeTask();
        } else if (e.getSource() == clearButton) {
            clearTasks();
        } else if (e.getSource() == darkModeButton) {
            toggleDarkMode();
        } else if (e.getSource() == editButton) {
            editTask();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TodoListApp::new);
    }
}

