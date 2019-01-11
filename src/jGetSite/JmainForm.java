
package jGetSite;

//<editor-fold desc="Imports - List ">
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import javax.swing.*;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions; 
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.awt.Image;
import java.awt.Desktop;
import java.io.IOException;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.table.TableRowSorter;
import java.net.URI;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
//</editor-fold>

//<editor-fold desc="WebSite Class - Class to hold Website Information">
class Website {
        private String lproto;
        private String lurl;
        private String ltitle;
        private URI luri;
        private int lID; 
        private Integer lResponseCode;
        private String lHTML;
        private String filename;
        private Boolean lactive;
        private File f;
        private Icon icon;
        private Image image;
        
        public Website (){
            
        }
        void setImageFile (File f){
            this.f = f;
        }
        File getImageFile() {
            return this.f;
        }
        void setIcon(Icon icon){
            this.icon = icon;
        }
        Icon getIcon() {
            return this.icon;
        }
        void setImage(Image image) {
            this.image = image;
        }
        Image getImage(){
            return this.image;
        }
        void setActive(Boolean active) {
          lactive = active;
        }
        Boolean getActive (){
            return lactive;
        }
        void setFilename(String fname) {
         filename = fname;
        }
        String getfilename () {
            return filename; 
        }
        void setHTML(String html){
            lHTML = html;
        }
        String getHTML (){
            return lHTML;
        }
        void seturl(String url){
           lurl = url; 
        }
        String geturl(){
            return lurl;
        }
        void setResponseCode(int code) {
            lResponseCode = code;
        }
        Integer getResponseCode () {
            return lResponseCode;
        }
        void setproto(String proto){
            lproto = proto;
        }
        String getproto(){
            return lproto;
        }
        void setTitle(String title){
            ltitle = title;
        }
        String getTitle(){
            return ltitle;
        }
        URI getURI(){
            return luri;
        }
        void setURI(URI uri){
            luri = uri;
        }
        void setID (int i){
            lID = i;
        }
        Integer getID(){
            return lID;
        }
        String getStringID(){
            return Integer.toString(lID);
        }
        Boolean isInTitle(String searchtext) {
          return ltitle.toLowerCase().contains(searchtext.toLowerCase());
           
            }
       
            
    }
//</editor-fold>

//<editor-fold desc="Class GroupButtonUtils  - Class to Handle the button Group">
class GroupButtonUtils {
    
    public String getSelectedButtonText(ButtonGroup buttonGroup) {
    
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
}
//</editor-fold>

//<editor-fold desc="Class ScreenShot -- Class for Thread">
class cScreenShot implements Callable<String>{
    
    String strSiteUrl;
    
    public cScreenShot(String url){
        strSiteUrl = url;
    }
    @Override 
    public String call() throws Exception{
    try {    
        
        String basefilename = strSiteUrl.replaceAll("[^a-zA-Z0-9]", "_");
        
        //ChromeOptions options = new ChromeOptions();  
        //options.addArguments("--headless", "--disable-gpu","--ignore-certificate-errors");  
        //WebDriver pDriver;
        //pDriver = new ChromeDriver(firefoxOptions);
        
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver pDriver = new FirefoxDriver(firefoxOptions);

        String fnScreenShot = "." + File.separator + "images" + File.separator + basefilename +  ".png";
    
        try {  
                  pDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                  pDriver.get(strSiteUrl);
                  File screenshot = ((TakesScreenshot)pDriver).getScreenshotAs(OutputType.FILE);           
                  FileUtils.copyFile(screenshot, new File(fnScreenShot));
                  System.out.println("Site " + pDriver.getTitle() + " screenshot saved to " + fnScreenShot + "\n");
                  //Made the table model public -- not sure best idea
                  JmainForm.tmodel.addRow(new String[] {strSiteUrl,pDriver.getTitle()});
                  String fnPageHTML = "." + File.separator + "html" + File.separator + basefilename +  ".html";
                  FileUtils.writeStringToFile(new File(fnPageHTML), pDriver.getPageSource(), "utf-8");
                  
        }catch (UnhandledAlertException f) {
                    Alert alert = pDriver.switchTo().alert();
                    String alertText = alert.getText();
                    System.out.println("Alert data: " + alertText);
                    alert.accept();
                
        }catch (RuntimeException g) {
                    System.out.printf("Could not connect to: %s\n",strSiteUrl + "\n"); 
                }           
         catch (IOException e){
                System.out.printf("Catch IOException %s\n",e);
                }
         finally {
                System.out.print("\nScreenshot Completed\n");
            }
                pDriver.quit();
        
        }catch (Exception e){
            System.out.println("Error " + e );
        }  
    return null;
}
}
//</editor-fold>

//<editor-fold desc="Class WebTitleMode - Table Model">
class WebTitleModel extends DefaultTableModel {
    //Set up tWebTitles Table       
    String[] columnNames = {"Address","Web Site Title","ID"};
    @Override
    public boolean isCellEditable(int row, int column)
    {
      return false;//This causes all cells to be not editable
    }
    @Override
    public void setColumnIdentifiers(Object[] os) {
        super.setColumnIdentifiers(os); //To change body of generated methods, choose Tools | Templates.
    }

    public void setColumnNames() {
       
    }            
    @Override
    public int getColumnCount() {
          return columnNames.length;
      }

    @Override
    public String getColumnName(int col) {
            return columnNames[col];
      }
    
    }
//</editor-fold>

public class JmainForm extends javax.swing.JFrame {

    // Private 
    private final List<String> urls = new ArrayList<>(); 
    private final List<Website> sitesgrabbed = new ArrayList<>();
    private final List<String> sitesmissed = new ArrayList<>();
   
    public  static WebTitleModel tmodel = new WebTitleModel();
    private TableRowSorter<WebTitleModel> sorter; 
    
    private final JFileChooser jc = new JFileChooser();
    private final GroupButtonUtils buttongroup = new GroupButtonUtils();

    private final MessageBox messagebox = new MessageBox(this,false);
    private final int MAX_SCREENSHOT_WORKER_THREAD = 10;
    
    private JmainForm.OpenFile readFile;
    private JmainForm.Screenshots ss;
    
    
    
    public JmainForm() {
        initComponents();
        setLocationRelativeTo(null);
    // Setup some things here
        formSetup();  
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
   
    private void formSetup() {
    /**
     * Reset form values  and also setup Trust Manager that allows
     * to connect to SSL sites without trusting the cert (I.E using an IP) <- Required
    */

        String[] columnNames = {"Address","Web Site Title","ID"};
        TableColumn column = tWebTitles.getColumnModel().getColumn(0); 
        column.setHeaderValue(columnNames[0]);
        column.setPreferredWidth(150);
        column = tWebTitles.getColumnModel().getColumn(1); 
        column.setHeaderValue(columnNames[1]);
        column.setPreferredWidth(300);
        column = tWebTitles.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);     
        rbHTTPs.setSelected(true);
        sitesgrabbed.removeAll(urls); 
        
        
        // Create a trust manager that does not validate certificate chains
        // Allows us to trust all the sites to grab the titles etc..
        TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(
            java.security.cert.X509Certificate[] certs, String authType) {
        }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (String hostname, SSLSession session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            } catch (Exception e) {
            }  
        
    }  
    
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        butgroupFileType = new javax.swing.ButtonGroup();
        lblStatusText = new javax.swing.JLabel();
        butGetSites = new javax.swing.JButton();
        butOpenFile = new javax.swing.JButton();
        butClose = new javax.swing.JButton();
        lblLoadFilePath = new javax.swing.JLabel();
        panelButGroup = new javax.swing.JPanel();
        rbHTTPs = new javax.swing.JRadioButton();
        rbHttp = new javax.swing.JRadioButton();
        rbRaw = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tWebTitles = new javax.swing.JTable();
        progressTask = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblStatusText.setText("Messages");

        butGetSites.setBackground(new java.awt.Color(0, 153, 51));
        butGetSites.setText("Go");
        butGetSites.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGetSitesActionPerformed(evt);
            }
        });

        butOpenFile.setText("Load File");
        butOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOpenFileActionPerformed(evt);
            }
        });

        butClose.setText("Close");
        butClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCloseActionPerformed(evt);
            }
        });

        lblLoadFilePath.setForeground(new java.awt.Color(51, 51, 51));
        lblLoadFilePath.setText("File Path");

        panelButGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("URL Type"));

        butgroupFileType.add(rbHTTPs);
        rbHTTPs.setText("HTTPS");
        rbHTTPs.setToolTipText("Get https://");

        butgroupFileType.add(rbHttp);
        rbHttp.setText("HTTP");

        butgroupFileType.add(rbRaw);
        rbRaw.setText("RAW");

        javax.swing.GroupLayout panelButGroupLayout = new javax.swing.GroupLayout(panelButGroup);
        panelButGroup.setLayout(panelButGroupLayout);
        panelButGroupLayout.setHorizontalGroup(
            panelButGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButGroupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbHTTPs)
                    .addComponent(rbHttp)
                    .addComponent(rbRaw))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelButGroupLayout.setVerticalGroup(
            panelButGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButGroupLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbHTTPs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbHttp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbRaw)
                .addContainerGap())
        );

        tWebTitles.setModel(tmodel);
        jScrollPane1.setViewportView(tWebTitles);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblLoadFilePath, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butClose, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatusText, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressTask, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(butGetSites, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(butOpenFile, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 546, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(panelButGroup, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(552, 552, 552))))
                .addGap(12, 12, 12))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butGetSites, butOpenFile});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLoadFilePath)
                    .addComponent(butClose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelButGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butOpenFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butGetSites, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblStatusText)
                    .addComponent(progressTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butGetSites, butOpenFile});

        butGetSites.getAccessibleContext().setAccessibleName("GO");
        butGetSites.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
// <editor-fold defaultstate="collapsed" desc="Form Button Actions">
    private void butCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCloseActionPerformed
    // this will hide and dispose the frame, so that the application quits by
    // itself if there is nothing else around. 
    setVisible(false);
    dispose();
    // if you have other similar frames around, you should dispose them, too.

    // finally, call this to really exit. 
    // i/o libraries such as WiiRemoteJ need this. 
    // also, this is what swing does for JFrame.EXIT_ON_CLOSE
    System.exit(0);
    }//GEN-LAST:event_butCloseActionPerformed

    private void butOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOpenFileActionPerformed
       String path;  
    
        jc.setDialogTitle("Choose URL File");
        jc.setDialogType(JFileChooser.FILES_ONLY);
        String lastJFCDirectory = System.getProperty("user.dir", "");
        jc.setCurrentDirectory(new File(lastJFCDirectory));
        
        int rtnValue = jc.showOpenDialog(JmainForm.this);
        if (rtnValue  == JFileChooser.APPROVE_OPTION){   
           
            File fileUrls = jc.getSelectedFile();
            path = fileUrls.getPath();
            urls.clear();
            (readFile = new OpenFile(path)).execute();
               
           
         } 
       lblStatusText.setText("Loaded Sites");
        
        
    }//GEN-LAST:event_butOpenFileActionPerformed

    private void butGetSitesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGetSitesActionPerformed
        if (!urls.isEmpty()) {
        //Chrome Usage
        //System.setProperty("webdriver.chrome.driver", "/usr/local/bin/Chromedriver");
        //System.out.println(System.getProperty("webdriver.chrome.driver"));
        
        //Firefox 
        System.setProperty("webdriver.gecko.driver", "/usr/local/Cellar/geckodriver/0.23.0/bin/geckodriver");
        //
        setTitle("Getting Screenshots -- Please Wait...");
        //Clear the Table of Website(s) 
        tmodel.setRowCount(0);
        tWebTitles.removeAll();
        //progressTask.setIndeterminate(true);
        new Screenshots().execute();
        } else {
            JOptionPane.showMessageDialog(null, "Please Load URLs First","Error Collecting Screenshots",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butGetSitesActionPerformed
   // </editor-fold>   
    
//<editor-fold desc="Swing Worker Classes">

    private class OpenFile extends SwingWorker<List<String>,Integer>{
      String filepath;
       int count = 0;

            public OpenFile(String path){
                filepath = path;   
                butOpenFile.setEnabled(false);
                
            }
            @Override 
            protected List doInBackground(){ 
            
            try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
                    String line;
                    while ((line = reader.readLine()) != null)
                        {
                         publish(count); 
                         urls.add(line);
                         
                         count ++;
                          
                         
                        }
              reader.close();
            }catch (Exception e)
                    {
                     System.out.println(e.toString());
                     setTitle ("Error Reading File");
                    }
                return urls;
            }
            
            @Override
            protected void process(List<Integer> chunks){
               
                
            }
            
            @Override 
            protected void done() {
              butOpenFile.setEnabled(true);
              lblLoadFilePath.setText("File Loaded: " + filepath );  
              JOptionPane.showMessageDialog(null,"Loaded " + urls.size() + " site(s)","File Loaded",JOptionPane.INFORMATION_MESSAGE);
              lblStatusText.setText("File loaded " + count + " sites");
              butOpenFile.setText("Loaded: " + count + " site(s)");
              progressTask.setMaximum(count);
            }
            
            
    }   

    private class Screenshots extends SwingWorker<Void,Void>{
    
    /* Screeshots is a swingworker to grab screenshots */

    long startTime = System.currentTimeMillis();
    String fullURI;
    int count = 0;
    ExecutorService executorService = Executors.newFixedThreadPool(MAX_SCREENSHOT_WORKER_THREAD);
   
    
    @Override 
    
    protected Void doInBackground(){
        
            lblStatusText.setText("Trying to Capture " + Integer.toString(urls.size()) + " site(s)");
            butOpenFile.setEnabled(false);
            butGetSites.setEnabled(false);
            
            long completedCount; 
            String urlPrefix = (buttongroup.getSelectedButtonText(butgroupFileType));
            
       try {
        for (String url: urls){
             switch (urlPrefix) {
                case "RAW": fullURI = url;
                break;
                case "HTTP": fullURI = "http://" + url;
                break;
                case "HTTPS": fullURI = "https://" + url; 
                break;    
            }
             // Important! This submits jobs to Executor Service 
             executorService.submit(new cScreenShot(fullURI));
            }
        
        
         // This area keeps an eye on the count of sites processed 
         //int activecount;
          
         if (executorService instanceof ThreadPoolExecutor) {
             
             while (((ThreadPoolExecutor)executorService).getActiveCount() > 0) {
              //activecount = ((ThreadPoolExecutor)executorService).getActiveCount();
              completedCount = ((ThreadPoolExecutor)executorService).getCompletedTaskCount();
              lblStatusText.setText("Working: Completed " + Long.toString(completedCount) + " sites out of " + urls.size());
              progressTask.setValue(count);
             }
         }
         executorService.shutdown();
         executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
         
       }catch (NullPointerException | InterruptedException e){
          
          JOptionPane.showMessageDialog(null, "Screenshots error: " + e);
          
          
         }
         return null; 
        }
    
    @Override
    protected void done(){
            
            long endTime = System.currentTimeMillis();
            long millis = endTime - startTime; 
            lblStatusText.setText(String.format("Finished ScreenShots %02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(millis), TimeUnit.MILLISECONDS.toSeconds(millis) - 
                   TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
            setTitle("Screen Capture Complete");
            JOptionPane.showMessageDialog(null, "Screen Capture Complete");
            //progressTask.setIndeterminate(false);
            butGetSites.setEnabled(true);
            butOpenFile.setEnabled(true);
            
        }
    } 
   
//</editor-fold>    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JmainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JmainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JmainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JmainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JmainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClose;
    private javax.swing.JButton butGetSites;
    private javax.swing.JButton butOpenFile;
    private javax.swing.ButtonGroup butgroupFileType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblLoadFilePath;
    private javax.swing.JLabel lblStatusText;
    private javax.swing.JPanel panelButGroup;
    private javax.swing.JProgressBar progressTask;
    private javax.swing.JRadioButton rbHTTPs;
    private javax.swing.JRadioButton rbHttp;
    private javax.swing.JRadioButton rbRaw;
    private javax.swing.JTable tWebTitles;
    // End of variables declaration//GEN-END:variables
}
