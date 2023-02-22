package Panels;

import Main.MazeGame;
import Database.MazeRepository;

import Models.Exceptions.DatabaseException;
import Models.Maze;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


import static Utils.Utils.*;

public class MazeDatabase extends JPanel implements ActionListener {
    private final JPanel contentPane;
    private JTable mazeTable;
    private JButton printBtn;
    private JButton exportBtn;
    private JButton mazeEditBtn;
    private JButton mainMenuBtn;
    private JButton deleteBtn;
    private JButton clearBtn;
    private JCheckBox solutionBox;
    private DefaultTableModel model;

    /**
     * Implements: Creates a panel that shows the information from the maze database and facilitates interacting with it
     * Pre-condition:
     * Post-condition:
     * @param contentPane The contentPane of the main app window
     */
    public MazeDatabase(JPanel contentPane) {
        this.contentPane = contentPane;
        InitUI();
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitUI() {
        setSize(20 * MazeDisplay.getCellSize() + 200, 20 * MazeDisplay.getCellSize() + 75);

        InitTable();
        InitButton();

        JPanel dataPanel = new JPanel(new GridBagLayout());
        dataPanel.setLayout(new GridBagLayout());

        add(dataPanel, BorderLayout.WEST);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitTable() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.weightx = 100;
        constraints.weighty = 1;

        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setLayout(new GridBagLayout());

        String[] columns = {"ID", "Title", "Author", "Created Date", "Last Edited", "Dead Ends"};
        model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<Maze> mazes;
        try {
            mazes = MazeRepository.getInstance().GetAllMazes();
        } catch(DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                    "There was a problem retrieving maze information from the database when initialising the table" + "\n" + e.getMessage(),
                    "Maze retrieval error", JOptionPane.ERROR_MESSAGE);
            mazes = new ArrayList<>();
        }
        Object[] data = new Object[6];
        for (Maze maze : mazes) {
            data[0] = maze.getId();
            data[1] = maze.getName();
            data[2] = maze.getAuthor();
            data[3] = maze.getCreationDate();
            data[4] = maze.getLastEdited();
            data[5] = maze.deadEnds();
            model.addRow(data);
        }

        mazeTable = new JTable(model);
        mazeTable.setBounds(10, 20, 100, 200);
        mazeTable.setRowHeight(30);
        mazeTable.setGridColor(Color.black);
        mazeTable.setShowGrid(true);
        mazeTable.setAutoCreateRowSorter(true);
        mazeTable.setCellSelectionEnabled(false);
        mazeTable.setRowSelectionAllowed(true);
        mazeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        MatteBorder tableBorder = new MatteBorder(1, 1, 1, 1, Color.black);
        mazeTable.setBorder(tableBorder);

        TableColumnModel mazeTableModel = mazeTable.getColumnModel();
        mazeTableModel.getColumn(0).setPreferredWidth(35);
        mazeTableModel.getColumn(3).setPreferredWidth(180);
        mazeTableModel.getColumn(4).setPreferredWidth(180);

        JScrollPane tableContainer = new JScrollPane(mazeTable);

        addToPanel(tablePanel, tableContainer, constraints, 0, 0, 1, 1);
        add(tablePanel, BorderLayout.WEST);
        mazeTable.setVisible(true);

        mazeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() > 0) {
                    printBtn.setEnabled(true);
                    exportBtn.setEnabled(true);
                    mazeEditBtn.setEnabled(true);
                    deleteBtn.setEnabled(true);
                }
            }
        });

    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void InitButton() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 100;
        constraints.weighty = 0;

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        printBtn = new JButton("Print");
        printBtn.addActionListener(this);
        printBtn.setEnabled(false);

        exportBtn = new JButton("Export");
        exportBtn.addActionListener(this);
        exportBtn.setEnabled(false);

        mazeEditBtn = new JButton("Maze Editor");
        mazeEditBtn.addActionListener(this);
        mazeEditBtn.setEnabled(false);

        deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(this);
        deleteBtn.setEnabled(false);

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(this);

        mainMenuBtn = new JButton("Main Menu");
        mainMenuBtn.addActionListener(this);

        solutionBox = new JCheckBox("Solution");
        solutionBox.setToolTipText("If selected when exporting mazes the solution will be exported along with the maze in a separate file suffixed with _solution\n" +
                "When printing if selected the printed maze will include the solution");
        solutionBox.addActionListener(this);

        addToPanel(buttonPanel, printBtn, constraints, 0, 0, 1, 1);
        addToPanel(buttonPanel, exportBtn, constraints, 1, 0, 1, 1);
        addToPanel(buttonPanel, solutionBox, constraints, 1, 1, 1, 1);
        addToPanel(buttonPanel, mazeEditBtn, constraints, 2, 0, 1, 1);
        addToPanel(buttonPanel, mainMenuBtn, constraints, 5, 0, 1, 1);
        addToPanel(buttonPanel, deleteBtn, constraints, 3, 0, 1, 1);
        addToPanel(buttonPanel, clearBtn, constraints, 4, 0, 1, 1);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == printBtn) {
            PrintPressed();
        }
        if (e.getSource() == exportBtn) {
            try {
                ExportPressed();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getSource() == mazeEditBtn) {
            try {
                mazeEditPressed();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        if (e.getSource() == mainMenuBtn) {
            mainMenuPressed();
        }
        if (e.getSource() == deleteBtn) {
            deletePressed();
        }
        if (e.getSource() == clearBtn) {
            clearPressed();
        }
        if(e.getSource() == solutionBox)  {
            solutionPressed();
        }
    }

    private void solutionPressed() {

    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void PrintPressed() {
        ArrayList<Maze> mazes = getSelectedMazes();
        for (Maze maze : mazes) {
            maze.myPrint(solutionBox.isSelected());
        }
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     * @return
     */
    private ArrayList<Maze> getSelectedMazes() {
        MazeRepository repository = MazeRepository.getInstance();
        int[] rows = mazeTable.getSelectedRows();
        ArrayList<Maze> mazes = new ArrayList<>();
        for (int row : rows) {
            int id = (int) model.getDataVector().elementAt(row).elementAt(0);
            Maze maze;
            try {
                maze = repository.GetMazeById(id);
            } catch(DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Maze retrieval error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            mazes.add(maze);
        }
        return mazes;
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void ExportPressed() throws IOException {
        ArrayList<Maze> mazes = getSelectedMazes();
        String path;
        // Exporting one maze
        if (mazes.size() == 1) {
            path = pickImage("Choose where to save your maze", true);
            if (path == null)
                return;
            if(solutionBox.isSelected()) {
                int dotIndex = path.lastIndexOf('.');
                String solPath = path.substring(0, dotIndex) + "_solution" + path.substring(dotIndex);
                mazes.get(0).exportAsImage(solPath, true);
            }
            mazes.get(0).exportAsImage(path, false);
            ExportMazeNotify();

        }
        // Exporting multiple mazes
        else if (mazes.size() > 1) {
            String folder = pickFolder("Choose where to save your mazes", true);
            if (folder == null)
                return;
            ArrayList<String> exportedFiles = new ArrayList<>();
            for (Maze maze : mazes) {
                File file = new File(folder, maze.getName() + ".png");
                if (file.exists()) {
                    // Don't ask user if we should overwrite if the problem is that one of the other mazes being exported has the same name
                    if (exportedFiles.contains(file.getAbsolutePath())) {
                        path = createUniqueFile(file).getAbsolutePath();
                    } else {
                        // Check if we should overwrite, rename or skip the maze
                        int result = JOptionPane.showOptionDialog(this, file.getName() + " already exists.\nDo you want to replace it?", "Confirm Export",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                                new String[]{"Overwrite", "Rename", "Skip"}, "Overwrite");
                        if (result == 0) {
                            path = file.getAbsolutePath();
                        } else if (result == 1) {
                            path = createUniqueFile(file).getAbsolutePath();
                        } else
                            continue;
                    }
                } else
                    path = file.getAbsolutePath();
                exportedFiles.add(path);
                if(solutionBox.isSelected()) {
                    int dotIndex = path.lastIndexOf('.');
                    String solPath = path.substring(0, dotIndex) + "_solution" + path.substring(dotIndex);
                    maze.exportAsImage(solPath, true);
                }
                maze.exportAsImage(path, false);
            }
            ExportMazeNotify();
        }

    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void deletePressed() {
        if(getSelectedMazes().size() == 0)
            return;
        boolean plural = getSelectedMazes().size() > 1;
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " +
                        (plural ? "these" : "this") + " maze" + (plural ? "s" : "") + "?\nThis cannot be undone!",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
            return;
        for(Maze maze : getSelectedMazes()) {
            try {
                MazeRepository.getInstance().DeleteMaze(maze);
                MazeDatabase databaseUpdate = new MazeDatabase(contentPane);
                contentPane.add(databaseUpdate);
                CardLayout layout = (CardLayout) contentPane.getLayout();
                layout.next(contentPane);
                JOptionPane.showMessageDialog(this,
                        "Maze deleted successfully", "Deleting successful", JOptionPane.INFORMATION_MESSAGE);
            } catch(DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(), "Deleting error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void clearPressed() {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all mazes?\nThis cannot be undone!",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION)
            return;
        ArrayList<Maze> mazes;
        try {
            mazes = MazeRepository.getInstance().GetAllMazes();
        } catch(DatabaseException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(), "Delete error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (MazeRepository.getInstance().ClearAll(mazes)) {
            MazeDatabase databaseUpdate = new MazeDatabase(contentPane);
            contentPane.add(databaseUpdate);
            CardLayout layout = (CardLayout) contentPane.getLayout();
            layout.next(contentPane);
            JOptionPane.showMessageDialog(this,
                    "All Mazes deleted successfully", "Delete successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "There was a problem deleting all the maze", "Delete error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void mazeEditPressed() throws IOException {
        ArrayList<Maze> mazes = getSelectedMazes();
        if(mazes.size() == 0) {
            JOptionPane.showMessageDialog(this, "You must select a maze before you can begin editing", "Select a maze", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Need to implement to be able to edit maze that was selected
        EditMazeDisplay editMaze = new EditMazeDisplay(contentPane, mazes.get(0), false);
        contentPane.add(editMaze);
        CardLayout layout = (CardLayout) contentPane.getLayout();
        layout.next(contentPane);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void mainMenuPressed() {
        MainMenu mainMenu = new MainMenu(contentPane);
        contentPane.add(mainMenu);
        CardLayout layout = (CardLayout) contentPane.getLayout();
        layout.next(contentPane);
    }

    /**
     * Implements:
     * Pre-condition:
     * Post-condition:
     */
    private void ExportMazeNotify() {
        JOptionPane.showMessageDialog(this, "Maze exported successfully", "Exported successfully", JOptionPane.INFORMATION_MESSAGE);
    }
}
