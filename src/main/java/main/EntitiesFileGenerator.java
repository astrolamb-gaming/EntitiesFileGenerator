/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.walkertribe.ian.Context;
import com.walkertribe.ian.vesseldata.FilePathResolver;
import com.walkertribe.ian.vesseldata.PathResolver;
import com.walkertribe.ian.vesseldata.Vessel;
import com.walkertribe.ian.vesseldata.VesselData;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Matthew Holderbaum
 */
public class EntitiesFileGenerator {
    
    JFileChooser fileChooser = new JFileChooser();
    File file;
    File entitiesFile;
    File vesselDataFile;
    VesselData vesselData;
    Context context;
    PersistenceHandler ph;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                EntitiesFileGenerator efg = new EntitiesFileGenerator();
                efg.setup();
            }
        });
    }
    
    public EntitiesFileGenerator() {
        File file = new File(System.getProperty("user.home"),".artemisEntitiesGenerator");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(System.getProperty("user.home"),".artemisEntitiesGenerator/settings.dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ph = new PersistenceHandler(file);
    }
    
        public void setup() {
        JFrame frame = new JFrame("Artemis System Entites Extractor - v0.1b");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500,500));
        frame.setLayout(new BorderLayout());
        JPanel mid = new JPanel(new BorderLayout());
        JLabel doneLabel = new JLabel("Extraction Complete!");
        doneLabel.setVisible(false);
        
//        JMenuBar menuBar = new JMenuBar();
//        JMenu menu = new JMenu("Connection");
//        menuBar.add(menu);
//        JMenuItem connectMenuItem = new JMenuItem("Choose File");
//        
//        connectMenuItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                
//                
//                
//                mid.setVisible(false);
//            }
//        }); 
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel gridPanel = new JPanel(new GridLayout(3,1));
        mainPanel.add(gridPanel,BorderLayout.PAGE_START);
        
        gridPanel.add(panel);
        
        
        JTextField fileLoc = new JTextField();
        final File f = new File(ph.options.getOrDefault("currentDir", "C:\\"));
        file = f;
        fileLoc.setText(f.getPath()); 
        JLabel fileLabel = new JLabel("Mission XML File: ");
        JButton chooseButton = new JButton("Choose File");
        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(f);
                fileChooser.showOpenDialog(frame);
                file = fileChooser.getSelectedFile();
                ph.options.put("currentDir", file.getPath());
                ph.saveOptions();
                fileLoc.setText(file.getPath()); 
                doneLabel.setVisible(false);
            }
        });
        
        panel.add(fileLabel,BorderLayout.LINE_START);
        panel.add(fileLoc,BorderLayout.CENTER);
        panel.add(chooseButton,BorderLayout.LINE_END);
        
       
        mainPanel.add(mid,BorderLayout.PAGE_END);
        
        JPanel panel2 = new JPanel(new BorderLayout());
        mainPanel.add(panel2,BorderLayout.NORTH);
        JTextField entityFileLoc = new JTextField();
        final File f2 = new File(ph.options.getOrDefault("entityDir", "C:\\"));
        entitiesFile = f2;
        entityFileLoc.setText(f2.getPath());
        JLabel entityLabel = new JLabel("Entities File: ");
        JButton chooseEntity = new JButton("Choose File: ");
        chooseEntity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
                fileChooser.setCurrentDirectory(f2);
                fileChooser.showOpenDialog(frame);
                entitiesFile = fileChooser.getSelectedFile();
                ph.options.put("entityDir", entitiesFile.getPath());
                ph.saveOptions();
                entityFileLoc.setText(entitiesFile.getPath()); 
                doneLabel.setVisible(false);
            }
        });
        panel2.add(entityLabel,BorderLayout.LINE_START);
        panel2.add(entityFileLoc,BorderLayout.CENTER);
        panel2.add(chooseEntity,BorderLayout.LINE_END);
        gridPanel.add(panel2);
        
        
        
        JPanel panel3 = new JPanel(new BorderLayout());
        mainPanel.add(panel3,BorderLayout.NORTH);
        JTextField vesselDataFileLoc = new JTextField();
        final File f3 = new File(ph.options.getOrDefault("vesselDataDir", "C:\\"));
        vesselDataFile = f3;
        vesselDataFileLoc.setText(f3.getPath());
        loadVesselData(f3);
        JLabel vesselDataLabel = new JLabel("VesselData Directory: ");
        JButton chooseVesselData = new JButton("Choose Directory: ");
        chooseVesselData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
                fileChooser.setCurrentDirectory(f3);
                fileChooser.showOpenDialog(frame);
                vesselDataFile = fileChooser.getSelectedFile();
                ph.options.put("vesselDataDir", vesselDataFile.getPath());
                ph.saveOptions();
                vesselDataFileLoc.setText(vesselDataFile.getPath()); 
                doneLabel.setVisible(false);
                loadVesselData(vesselDataFile);
            }
        });
        panel3.add(vesselDataLabel,BorderLayout.LINE_START);
        panel3.add(vesselDataFileLoc,BorderLayout.CENTER);
        panel3.add(chooseVesselData,BorderLayout.LINE_END);
        gridPanel.add(panel3);
        
        
        mainPanel.add(doneLabel, BorderLayout.CENTER);
        
        JButton convert = new JButton("Extract Entities");
        convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    extractEntities(file, entitiesFile);
                    doneLabel.setVisible(true);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(EntitiesFileGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        mid.add(convert);
        
        frame.add(mainPanel);
        frame.pack();
    }
        
    public String extractEntities(File file, File entitiesFile) throws FileNotFoundException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
        if (!entitiesFile.exists()) {
            throw new FileNotFoundException();
        }
        StringBuilder sb = new StringBuilder();
        try(BufferedReader br = new BufferedReader( new InputStreamReader(fis))) {
            HashMap<String,String> map = new HashMap<>();
            String line;
            int sector = 1;
            boolean foundObjects = false;
            while(( line = br.readLine()) != null ) {
                if (line.contains("Named Objects")) {
                    foundObjects = true;
                    continue;
                }
                if (line.contains("Destroy Objects")) {
                    foundObjects = false;
                    continue;
                }
                if (foundObjects) {
                    System.out.println(line);
                    String name = "";
                    int hullID = 0;
                    if (line.contains("name")) {
                        String[] n = line.split("name=\"");
                        String[] n2 = n[1].split("\"");
                        System.out.println(n2[0]);
                        name = n2[0];
                        System.out.println(name);
                    }
                    if (line.contains("hullID")) {
                        String[] n = line.split("hullID=\"");
                        System.out.println(n[1]);
                        String[] n2 = n[1].split("\"");
                        hullID = Integer.parseInt(n2[0]);
                    }
                    
                    //String[] attr = line.split(" ");
                    //String name = "";
                    //int hullID = 0;
//                    for (String s : attr) {
//                        
//                        if (s.contains("name")) {
//                            name = s.replace("name=\"", "").replace("\"", "");
//                            System.out.println(name);
//                            //sb.append(name);
//                        }
//                        if (s.contains("hullID")) {
//                            hullID = Integer.parseInt(s.replace("hullID=\"", "").replace("\"", ""));
//                            System.out.println(hullID);
//                        }
//                    }
                    String type = this.getTypeFromName(name);
                    if (type.equals("")) {
                        type = this.getTypeFromVesselID(hullID);
                    }
                    sb.append(name + "," + type + "," + sector);
                    sb.append("\n");
                    continue;
                }
                int i = sector + 1;
                if (line.contains("Sector " + i)) {
                    System.out.println("Sector: " + i);
                    System.out.println(line);
                    sector = i;
                } else {
                    //sb.append(line);
                }
                
            }
            fis.close();
            br.close();
            
            FileWriter f2 = new FileWriter(entitiesFile, false);
            f2.write(sb.toString());
            f2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    public String getTypeFromName(String name) {
        //String s = "";
        if (name.startsWith("WP")) {
            return "W";
        } else if (name.contains("Military") || name.contains("Command")) {
            return "C";
        } else if (name.startsWith("SY-") || name.contains("Ship Yard") || name.contains("Shipyard")) {
            return "SY";
        } else if (name.contains("Market")) {
            return "V";
        } else if (name.contains("Industrial")) {
            return "I";
        } else if (name.contains("Civilian")) {
            return "V";
        } else if (name.contains("Shipyard")) {
            return "Y";
        } else if (name.contains("Mining")) {
            return "M";
        } else if (name.contains("Science")) {
            return "X";
        } else if (name.contains("Research")) {
            return "R";
        } else if (name.contains("Refinery")) {
            return "F";
        } else if (name.contains("Skaraan")) {
            return "SKN-B";
        } else if (name.contains("Comms")) {
            return "CR";
        } else if (name.contains("Sensor")) {
            return "SB";
        } else if (name.contains("Defense Platform")) {
            return "DP";
        } else if (name.contains("Defense")) {
            return "D";
        } else if (name.contains("Planet")) {
            return "P";
        } else if (name.contains("Gate")) {
            return "G";
        } else {
            return "";
        }
    }
    public String getTypeFromVesselID(int id) {
        System.out.println(id);
        //vesselData.
        String name = vesselData.getVessel(id).getName();
        if (name.contains("Industrial")) {
            return "I";
        } else if (name.contains("Military") || name.contains("Command")) {
            return "C";
        } else if (name.contains("Civilian")) {
            return "V";
        } else if (name.contains("Shipyard")|| name.contains("Ship Yard")) {
            return "M";
        } else if (name.contains("Science")) {
            return "X";
        } else if (name.contains("Research")) {
            return "R";
        } else if (name.contains("Refinery")) {
            return "F";
        } else if (name.contains("Skaraan")) {
            return "SKN-B";
        } else if (name.contains("Comms")) {
            return "CR";
        } else if (name.contains("Sensor")) {
            return "SB";
        } else if (name.contains("Defense Platform")) {
            return "DP";
        } else if (name.contains("Defense")) {
            return "D";
        } else if (name.contains("Planet")) {
            return "P";
        } else if (name.contains("Gate")) {
            return "G";
        } else {
            return getTypeFromVesselDescription(vesselData.getVessel(id).getDescription());
        }
    }
    
    public String getTypeFromVesselDescription(String name) {
        if (name.contains("Industrial")) {
            return "I";
        } else if (name.contains("Military") || name.contains("Command")) {
            return "C";
        } else if (name.contains("Civilian")) {
            return "V";
        } else if (name.contains("Shipyard") || name.contains("Ship Yard")) {
            return "Y";
        } else if (name.contains("Mining")) {
            return "M";
        } else if (name.contains("Science")) {
            return "X";
        } else if (name.contains("Research")) {
            return "R";
        } else if (name.contains("Refinery")) {
            return "F";
        } else if (name.contains("Skaraan")) {
            return "SKN-B";
        } else if (name.contains("Comms")) {
            return "CR";
        } else if (name.contains("Sensor")) {
            return "SB";
        } else if (name.contains("Defense Platform")) {
            return "DP";
        } else if (name.contains("Defense")) {
            return "D";
        } else if (name.contains("Planet")) {
            return "P";
        } else if (name.contains("Gate")) {
            return "G";
        } else {
            return "";
        }
    }
    
    public void loadVesselData(File file) {
        try {
            PathResolver r = new FilePathResolver(file); 
            context = new Context(r);

            vesselData = context.getVesselData();
            vesselData.vesselIterator().forEachRemaining(vessel -> {
                System.out.println(vessel.getName());
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //loadVesselData(file.getParentFile());
        }
        
    }
    
    
    
}
