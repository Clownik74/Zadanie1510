import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GeneratorRaportow extends JFrame {

    private JTextArea textAreaRaport;
    private JButton btnGeneruj;
    private JProgressBar progressBar;

    public GeneratorRaportow() {
        setTitle("INF.04 - Generator Raportów");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuPlik = new JMenu("Plik");

        JMenuItem itemZapisz = new JMenuItem("Zapisz Raport");
        JMenuItem itemWyjscie = new JMenuItem("Wyście");

        itemZapisz.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

        itemZapisz.addActionListener(e -> zapiszRaportDoPliku());
        itemWyjscie.addActionListener(e -> System.exit(0));

        menuPlik.add(itemZapisz);
        menuPlik.addSeparator();
        menuPlik.add(itemWyjscie);
        menuBar.add(menuPlik);
        setJMenuBar(menuBar);

        textAreaRaport = new JTextArea();
        textAreaRaport.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(textAreaRaport);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelDolny = new JPanel(new BorderLayout(5, 5));
        panelDolny.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        btnGeneruj = new JButton("Generuj Raport w Tle");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        panelDolny.add(btnGeneruj, BorderLayout.WEST);
        panelDolny.add(progressBar, BorderLayout.CENTER);
        add(panelDolny, BorderLayout.SOUTH);

        btnGeneruj.addActionListener(e -> uruchomGenerowanieRaportu());
    }

    private void uruchomGenerowanieRaportu() {
        textAreaRaport.setText("");

        SwingWorker<Void, String> worker = new SwingWorker<>() {

            @Override
            protected Void doInBackground() throws Exception {
                SwingUtilities.invokeLater(() -> btnGeneruj.setEnabled(false));

                for (int i = 1; i <= 100; i += 10) {
                    Thread.sleep(300);
                    String nowaLinia = "Wpis raportu nr " + (i / 10 + 1) + " [Status OK]\n";
                    publish(nowaLinia);
                    setProgress(i);
                }

                setProgress(100);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String linia : chunks) {
                    textAreaRaport.append(linia);
                }
                progressBar.setValue(getProgress());
            }

            @Override
            protected void done() {
                btnGeneruj.setEnabled(true);
                JOptionPane.showMessageDialog(GeneratorRaportow.this,
                        "Raport został pomyślnie wygenerowany!",
                        "Informacja",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };

        worker.execute();
    }

    private void zapiszRaportDoPliku() {
        String tekstRaportu = textAreaRaport.getText().trim();

        if (tekstRaportu.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pole raportu jest puste. Brak danych do zapisania!",
                    "Ostrzeżenie",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Wybierz miejsce zapisu raportu");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File plikDoZapisu = fileChooser.getSelectedFile();

            if (!plikDoZapisu.getAbsolutePath().toLowerCase().endsWith(".txt")) {
                plikDoZapisu = new File(plikDoZapisu.getAbsolutePath() + ".txt");
            }

            try (FileWriter writer = new FileWriter(plikDoZapisu)) {
                writer.write(tekstRaportu);
                JOptionPane.showMessageDialog(this,
                        "Zapisano pomyślnie do: " + plikDoZapisu.getName(),
                        "Sukces",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Błąd podczas zapisu pliku: " + ex.getMessage(),
                        "Błąd",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GeneratorRaportow().setVisible(true);
        });
    }
}