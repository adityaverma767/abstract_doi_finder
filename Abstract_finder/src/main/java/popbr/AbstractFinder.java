package popbr;
// mvn exec:java -Dexec.mainClass="popbr.AbstractFinder"

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.ArrayList;

import java.sql.*;

import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

/*
// Those classes clash with Jsoup's classes, 
// I am commenting them out for now,
// as well as the methods using them.
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
*/

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AbstractFinder {
    public static void main(String[] args) throws Exception { 

    System.out.println("Welcome to Abstract Finder.");

    
    ArrayList<String> searchList = Read_From_Excel(); // will return a searchList that has the author's name and all of the titles for our search query
    
    ArrayList<String> abstractList = RetrieveAbstract(searchList); //takes a few minutes to accomplish due to having to search on the Internet    

    Write_To_Excel(abstractList); // Currently only does one sheet at a time and needs to be manually updated

    System.out.println("Thanks for coming! Your abstracts should be in your Excel file now");
    
    /*
        Class.forName("com.mysql.cj.jdbc.Driver");

        //This method returns the base filepath for the program, like C:\User\JohnS\desktop\
        //With this, the program can keep track of itself, the files it creates, and looks for necessary files 
        String BasePath = EstablishFilePath();

        //This creates a path to the example database in the downloads folder
        String TestFile = BasePath + File.separator + "target" + File.separator + "downloads" + File.separator + "test.xml";

        //This creates a path to the login information folder, then goes and retrieves login information for SQL
        String LoginPath = BasePath + File.separator + "target" + File.separator;
        String[] SQLLogin = DetermineSQLLogin(LoginPath);

        System.out.println("Attempting to connect to the Test Data file: " + Connect_to_File(TestFile));

        System.out.println("Attempting to Connect, create, and insert to an SQL database: " + Connect_to_SQL(SQLLogin));
        System.out.println("Attempting to Connect to and output from an SQL database: " + Output_from_SQL(SQLLogin));

        System.out.println("Attempting to Create and insert into an Excel: " + Connect_to_Excel(SQLLogin, BasePath));
    */
        System.exit(0);
    }
    
    public static ArrayList<String> RetrieveAbstract(ArrayList<String> searchFor){
    
    String abstracttext = "error"; // Will be overwritten by the abstract if we succeed.
    
    // Documentation at 
    // https://jsoup.org/apidocs/org/jsoup/nodes/Document.html
    Document doc;

    ArrayList<String> abstractList = new ArrayList<String>();


    try {
        // Warning, my searchstring is very "poor", as it is *only the title* of the article.
        // You may need to improve the search criteria.
        // You can read the doc, e.g. at
        // https://pubmed.ncbi.nlm.nih.gov/help/#citation-matcher-auto-search
        // to get some idea on how to add e.g., author's names to that query.
        // String searchstring = "Characterization of ribonuclease NU cleavage sites in a bacteriophage phi80-induced ribonucleic acid";
        // We retrieve that webpage as a document:

        String searchString = "";     

        for(int i = 1; i < searchFor.size(); i++)
        {
           try {

           searchString = searchFor.get(0) + " " + searchFor.get(i);

           doc = Jsoup.connect("https://pubmed.ncbi.nlm.nih.gov/?term=" + searchString).get();

           // By looking at the source code of 
           // https://pubmed.ncbi.nlm.nih.gov/1089660/
           // which is where I land when I enter the 
           // https://pubmed.ncbi.nlm.nih.gov/?term=Characterization%20of%20ribonuclease%20NU%20cleavage%20sites%20in%20a%20bacteriophage%20phi80-induced%20ribonucleic%20acid
           // url, I can see that the part that interest us is as follows:
       
           /*
           <div class="abstract" id="abstract">
    
           <h2 class="title">
           Abstract
        
           </h2>
      
        
          
               <div class="abstract-content selected" id="eng-abstract">
              
                


  
           <p>
      
           Ribonuclease NU, an endoribonuclease isolated from human KB tissue culture cells, can cleave a bacteriophage phi80-induced RNA at four distinct sites. Nucleotide sequence analysis of the eight cleavage products has shown that the enzyme produces oligonucleotides terminating in 3'-phosphate groups, and that the four cleavage sites are in the only nonhydrogen-bonded region of the substrate. Various aspects of the cleavage reaction with this RNA and with other substrates are discussed.
          </p>
  

  


              
            </div>
          
        
      

      
    

    

    

            </div>*/
        
        
           // Warning, "abstract" is a keyword in java ;-)
        
           // Warning: we do something dangerous here, which is that we select the first element
           // with id "abstract". If the page contains more than one result, this may be problematic.
        
           // Refer to 
           // https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#selectFirst(java.lang.String)
           // for the documentation of that class, and an indication of other methods that may be best suited.
        
           // For the syntax of the argument of selectFirst, refer to 
           // https://jsoup.org/cookbook/extracting-data/selector-syntax
           // We are selecting the content of the "p" element under the id "abstract"

           // The 
        
           Element abstractelement = doc.selectFirst("#abstract p");
           // At this point, element contains
           // <p>Ribonuclease NU, an endoribonuclease isolated from human KB tissue culture cells, can cleave a bacteriophage phi80-induced RNA at four distinct sites. Nucleotide sequence analysis of the eight cleavage products has shown that the enzyme produces oligonucleotides terminating in 3'-phosphate groups, and that the four cleavage sites are in the only nonhydrogen-bonded region of the substrate. Various aspects of the cleavage reaction with this RNA and with other substrates are discussed.</p>
           // The following line remove the <p> … </p>, cf.
           // https://jsoup.org/apidocs/org/jsoup/nodes/Element.html#text(java.lang.String)
           abstracttext = abstractelement.text();
           abstractList.add(abstracttext);
           }
           catch (NullPointerException npe) {
              abstracttext = "no abstract";
              abstractList.add(abstracttext);
           }
           catch (MalformedURLException mue) {
              abstracttext = "error";
              abstractList.add(abstracttext);
           }
       }
        
    } catch (IOException e) {
        e.printStackTrace();
    }

    return abstractList;
    
    }

    public static ArrayList<String> Read_From_Excel() throws IOException{
       
       ArrayList<String> searchList = new ArrayList<String>();
       
       FileInputStream fins = new FileInputStream(new File("C:\\Users\\reyno\\Downloads\\Abstracts_Copy.xlsx"));

       XSSFWorkbook wb = new XSSFWorkbook(fins);
       
       // Currently manually inputting the sheet index
       // Starting at 2 which would be:
       // "Bothwell, A Pub Abstracts"
       XSSFSheet sheet = wb.getSheetAt(2);

       int rows = sheet.getLastRowNum(); // gets number of rows
       int cols = sheet.getRow(0).getLastCellNum(); // gets the number of columns

       XSSFRow row = sheet.getRow(0); // starting the row at 0 for sheet 2

       for (int i = 0; i < cols; i++)
       {
          XSSFCell cell = row.getCell(i);
          if (cell == null)
             continue;
          if (cell.getCellType() == CellType.STRING)
          {
             String cellValue = cell.getStringCellValue();
             if (cellValue.toLowerCase().equals("researcher"))
             {
                XSSFRow tempRow = sheet.getRow(1);
                XSSFCell tempCell = tempRow.getCell(i);
                cellValue = tempCell.getStringCellValue();
                searchList.add(cellValue);
             }
             if (cellValue.toLowerCase().equals("title"))
             {
                for (int j = 1; j <= rows; j++)
                {
                   row = sheet.getRow(j);
                   cell = row.getCell(i);
                   cellValue = cell.getStringCellValue();
                   searchList.add(cellValue);
                }
             }
          }

       }
       fins.close(); //closes the inputstream
       // the author's name will always be the first index followed by the titles
       return searchList;
    }

    public static void Write_To_Excel(ArrayList<String> writingList) throws Exception {
        try {

           FileInputStream fins = new FileInputStream(new File("C:\\Users\\reyno\\Downloads\\Abstacts.xlsx")); // misspelled abstracts

           XSSFWorkbook wb = new XSSFWorkbook(fins);

           // Currently manually inputting the sheet index
           // Starting at 2 which would be:
           // "Bothwell, A Pub Abstracts"
           XSSFSheet sheet = wb.getSheetAt(2);

           //int rows = sheet.getLastRowNum(); //gets the number of rows in the sheet
           int rows = writingList.size(); // SocketTimeoutException causing it to not work
           int cols = sheet.getRow(1).getLastCellNum(); //gets the number of columns in the sheet

           XSSFRow row = sheet.getRow(0);

           for(int i = 0; i < cols; i++)
           {
              XSSFCell cell = row.getCell(i);
              if (cell == null) // if the cell is null for whatever reason, it will throw an error when trying to get the cell type
                 continue;
              if (cell.getCellType() == CellType.STRING)
              {
                 String valueOfCell = cell.getStringCellValue();
                 if (valueOfCell.toLowerCase().equals("abstract"));
                 {
                    for (int j = 1; j <= rows; j++)
                    {
                       int abIndex = j - 1; // allows us to access the correct abstract in our list
                       row = sheet.getRow(j);
                       row.createCell(10, CellType.STRING).setCellValue(writingList.get(abIndex)); // 10 is number of the column that contains "Abstract"
                    }
                 }
              }
           }
           FileOutputStream out = new FileOutputStream(new File("C:\\Users\\reyno\\Downloads\\abstract2.xlsx"));
           wb.write(out);
           out.close();
           fins.close();
       }
       catch (Exception e) {
          e.printStackTrace();
       }
       
    }

    public static void Write_To_ExcelAlt() throws Exception
    {
       try {
          XSSFWorkbook wb = new XSSFWorkbook();
          XSSFSheet sheet = wb.createSheet(" Testing Sheet ");
          for (int j = 0; j < 10; j++)
          {
             XSSFRow row = sheet.createRow(j);
             for (int i = 0; i < 10; i++)
             {
                System.out.println("i: " + i); //aka the cell number or col
                System.out.println("j: " + j); //aka the row
                XSSFCell cell = row.createCell(i);
                cell.setCellValue("Testing works");
             }
          }
          FileOutputStream out = new FileOutputStream(new File("C:\\Users\\reyno\\Downloads\\test.xlsx"));
          wb.write(out);
          out.close();
       }
       catch (IOException ie) {
          ie.printStackTrace();
       }
    }

    /*
    // This method is using W3C's classes, so I am commenting it out.
    public static String Connect_to_File(final String Path) throws Exception {

        String result = "";
        try{
            //Sets the file to be read to the one passed, test.xml, using the path passed earlier
            File inputFile = new File(Path);
            //These prepare the XML file to be read by the program
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            //Creates a list of nodes with the XML Element name "Input" from the earlier input file
            NodeList nList = doc.getElementsByTagName("Input");

            //A list of strings is created, length of 2, as only 2 things are ever pulled: a Username and Password
            String[] MessageInfo = new String[2];

            
            / *
             * This goes through the list of nodes established earlier
             * and, for each node, if it matches the type we want, SQL,
             * then we pull the data from the node/message, Username and Password.
             * /
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Node username = eElement.getElementsByTagName("Opener").item(0);
                    MessageInfo[0] = username.getTextContent();

                    Node password = eElement.getElementsByTagName("Closer").item(0);
                    MessageInfo[1] = password.getTextContent();
                }
            }

            //The Username and Password is concatenated
            String Message = MessageInfo[0] + " " + MessageInfo[1];

            //The username and password is tested. If it reads "Hello World", it reads "Success". 
            if (Message.equals("Hello World")) {
                result = "Success";
            } //If you get this message, then, somewhere, the message failed. 
            else result = "A file was connected to, it appears to have the wrong contents. \nCheck if any modifications have occurred to the program's target/downloads";
        }
        //This is the exception in case anything happens that forces the method to fail for reasons other than the data not matching up
        catch (Exception e) {
            result = "Failure";
            e.printStackTrace();;
        }
        return result;
    }
    */
    
    public static String Connect_to_SQL(String[] LoginInfo) throws Exception {
        
        String result = "";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true"); 
            Statement stmt = conn.createStatement(); ) {
        
            String DropTable, CreateTable, Statement, TestDatabase, TestAttribute;

            //The following preps the creation of the database, the attributes, and the creation strings 
            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            //The table we need is first dropped, then created with the propper attributes. 
            //The table is  dropped to prevent SQL from running into an error where the table 
            //is already made but wasn't dropped since its creation. 
            DropTable = "DROP TABLE IF EXISTS " + TestDatabase + ";";
            CreateTable = "CREATE TABLE " + TestDatabase + " (" + TestAttribute + " VARCHAR(255) PRIMARY KEY)";
    
            stmt.execute(DropTable);
        	stmt.execute(CreateTable);

            //This sets up what will be a prepared string into the test DB.
            Statement = "INSERT INTO " + TestDatabase + " VALUES (?)";

            PreparedStatement preparedStatement = conn.prepareStatement(Statement);

            //This fills the previos PS with the data "Hello SQL"
            preparedStatement.setString(1, "Hello SQL");
            preparedStatement.execute();
         
            conn.close();

            result = "Success";
        } 
            catch (SQLException ex) 
        { 
        //If any errors happen, this is return. There's no need for a failure condition in the earlier part
        //as any error that happens would result in SQL getting the error, thus returning this. 
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }

    public static String Output_from_SQL(String[] LoginInfo) throws Exception {

        String result;
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
            + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
            + "&createDatabaseIfNotExist=true" + "&useSSL=true");
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ) {

            //This gets sets up the request from the testDB for the Test attribute
            String TestDatabase, TestAttribute;
            TestDatabase = "TestDB";
            TestAttribute = "TestAttribute";

            //This requests the the test attribute from the testDB, which returns everything in it
            String strSelect = "SELECT " + TestAttribute + " FROM " + TestDatabase;
            ResultSet rset = stmt.executeQuery(strSelect);
            rset.first();
            //This sets the pointer to read from the first entry in the result set

            //The result set's first entry is looked at. If it's not null, then the retrieval was sucessful.
            if (rset.getString(1).equals("Hello SQL")) {
                result = "Success";
            } 
            else result = "An SQL Database was connected to, but it appears to have the wrong contents.";
            //This is here in case the earler attempt to put items in the database put something that wasn't "Hello SQL"
        } catch (SQLException ex) { 
            //In the case of there being nothing to retrieve, or no database named "Testdb", 
            //or some other error, then this is returned
            result = "Failure";
            ex.printStackTrace();
        }

        return result;
    }
    
    public static String Connect_to_Excel(String[] LoginInfo, String Path) throws Exception {
        
        String result = "failure";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Prospectus_DB" 
        + "?user=" + LoginInfo[0] + "&password=" + LoginInfo[1] + "&allowMultiQueries=true" 
        + "&createDatabaseIfNotExist=true" + "&useSSL=true"); 
        Statement stmt = conn.createStatement();)	  
        { 
            //The following 
            Scanner myObj = new Scanner(System.in);
            XSSFWorkbook workbook = new XSSFWorkbook(); // workbook object
            XSSFSheet spreadsheet = workbook.createSheet("Test Sheet"); // spreadsheet object
            XSSFRow row; // creating a row object

            //This queries all the info in the TestDB Database and returns it as a resultset
            String strSelect = "SELECT * FROM TestDB";
            ResultSet rset = stmt.executeQuery(strSelect);

            //This gets the length and width of the database result set to know how long to go for in the loop below.
            ResultSetMetaData rsmd = rset.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            String columnValue = "";
            int rowNumber = 0;
            String ColumnName = rsmd.getColumnName(1);
            row = spreadsheet.createRow(rowNumber++);

            //This goes through each entry in the resultset and puts it, entry by entry, into the Excel sheet.
            while (rset.next()) 
            {
                for (int i = 1; i <= columnsNumber; i++) // this loop populates a cell with data
                {
                    if (rsmd.getColumnName(i) == rsmd.getColumnName(1)) //this detects if the column at the current is equal to the first entry. If so, that means we need a new row
                    {
                        row = spreadsheet.createRow(rowNumber++); // This line create a new row
                    }
                    columnValue = rset.getString(i);
                    row.createCell(i).setCellValue(columnValue);
                } 
            } 
            // This writes the workbook into an excel, with the filepath listed below. The workbook is then quit out of and closed.
            FileOutputStream out = new FileOutputStream(new File(Path + File.separator + "target" + File.separator + "GFGsheet.xlsx")); //C:\Users\sleep\Desktop\Excel
            workbook.write(out);
            out.close();
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return result;
    }

    public static String EstablishFilePath() throws Exception {
        try {

            //This creates a dummy file that starts as the basis for creating the filepath in the base of the program
            File s = new File("f.txt");
            String FilePath = "";
            
            //This gets the filepath of the dummy file and transforms it into characters, so it can be modified.
            //The modification snips off the charcters "f.txt" so that the only path left is the base filepath
            char[] tempChar = s.getAbsolutePath().toCharArray();
            char[] newChar = new char[tempChar.length - 6];
            for (int i = 0; i < newChar.length; i++) {
                newChar[i] = tempChar[i];
            }
            //This makes the filepath into a string, minus the "f.txt" bit
            FilePath = String.valueOf(newChar);
            //System.out.println(FilePath);
            //This returns the filepath
            return FilePath;

	    } catch (Exception e) {
            e.printStackTrace();;
            return "failed to find filepath";
        }
    }

    /*
    // This method is using W3C's classes, so I am commenting it out.
    public static String[] GetLoginInfo(String Location, String[] Elements) throws Exception{	
        
        String[] LoginInfo = new String[2];
        try {

            //Sets the file to be read to the one passed, test.xml, using the path passed earlier
            File inputFile = new File(Location);
            //These prepare the XML file to be read by the program
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            
            //This gets the elements from the document that are Login and makes it into a list of nodes
            NodeList nList = doc.getElementsByTagName("Login");

            //This prepares the Login info to be received 
            
            / *This goes through the list of nodes and matches it to nodes that are have login info for SQL
            * When it finds the SQL node, it logs information it has, the Username and Password, it puts it
            * into the prepared string list.
            * /
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    Node username = eElement.getElementsByTagName("Username").item(0);
                    LoginInfo[0] = username.getTextContent();

                    Node password = eElement.getElementsByTagName("Password").item(0);
                    LoginInfo[1] = password.getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // The Username and password is passed on
        return LoginInfo;
    }
    */

    /*
    // The methods used in this method have been commented out.
    public static String[] DetermineSQLLogin(String Location) throws Exception {

		String[] result = {"", ""};
		String[] SQLElementList = { "Login", "Username", "Password" };
		File LoginInfo = new File(Location + "LoginInfo.xml");

		if(LoginInfo.isFile()) {
			result = GetLoginInfo(Location + "LoginInfo.xml", SQLElementList);
		} else {
			result = GetLoginInfo(Location + "LoginInfoTemplate.xml", SQLElementList);
		}
		return result;
	}
	*/
}
