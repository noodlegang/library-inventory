package com.example.test2;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.Locale;

public class HelloController {
    //scene 1 properties
    @FXML
    private TextField searchEntry;
    @FXML
    private ListView listview;
    @FXML
    private Button btnDelete;
    @FXML
    private Button search;
    @FXML
    private Button save;
    @FXML
    private Button edit;
    @FXML
    private Button load;
    @FXML
    private Label welcomeText;

    //scene 2 properties
    @FXML
    private Label res;
    @FXML
    private ListView nameRes;
    @FXML
    private Button add;
    @FXML
    private Button close;
    @FXML
    private Label error;

    @FXML
    protected void addToListView(ListView lv, String text) {
        lv.getItems().add(text);
        searchEntry.clear();
    }

    @FXML
    public void onSaveToFile() {
        Object[] allItems = listview.getItems().toArray();
        String list = setVoid();
        for (int i=0; i<allItems.length; i++) {
            list = list + allItems[i].toString() + "; ";
        }
        try {
            FileWriter writer = new FileWriter("Data.txt");
            writer.write(list);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String setVoid() {
        return "";
    }

    public void Stop() {
        System.exit(0);
    }

    public void onDeleteSelected(){
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                final int selectedIdx = listview.getSelectionModel().getSelectedIndex();
                if (selectedIdx != -1) {
                    String itemToRemove = (String) listview.getSelectionModel().getSelectedItem();

                    final int newSelectedIdx =
                            (selectedIdx == listview.getItems().size() - 1)
                                    ? selectedIdx - 1
                                    : selectedIdx;

                    listview.getItems().remove(selectedIdx);
                    listview.getSelectionModel().select(newSelectedIdx);
                    System.out.println("Selected index: " + selectedIdx);
                    System.out.println("Deleted item: " + itemToRemove);
                }
            }
        });
    }

    public void editRow() {
        listview.setEditable(true);
        listview.setCellFactory(TextFieldListCell.forListView());
    }


    public void LoadInfo() {
        HashSet<String> items = new HashSet<>();
        String book = setVoid();
        try {
            FileReader reader = new FileReader("Data.txt");
            int count = reader.read();
            do {
                if (count == ';') {
                    items.add(book);
                    book = setVoid();
                } else {
                    book = book + (char)count;
                }
                count = reader.read();
            } while (!(count==-1));
        } catch (IOException e) {
            System.out.println("Things went wrong");
        }
        for (String LoadedTask:items) {
            listview.getItems().add(LoadedTask);
        }
    }

    public void switchScenes() {
        if (btnDelete.isVisible()) {
            //switches from scene 1 to scene 2
            setSceneVisibility(false, welcomeText, listview, searchEntry, search, save, btnDelete, edit, load); //hides scene 1
            setSceneVisibility(true, res, nameRes, add, close); //reveals scene 2
            sendHTTPRequest(searchEntry.getText());
        } else {
            //switches from scene 2 to scene 1
            setSceneVisibility(false, res, nameRes, add, close); //hides scene 2
            setSceneVisibility(true, welcomeText, listview, searchEntry, search, save, btnDelete, edit, load); //reveals scene 1
            nameRes.getItems().clear();
            error.setVisible(false);
        }
    }

    private void sendHTTPRequest(String text) {
        /*
        search examples
        1) https://openlibrary.org/search?q=ville&mode=everything
        2) https://openlibrary.org/search?q=james+bond&mode=everything
         */
        //HttpURLConnection con = null;
        text = text.toLowerCase(Locale.ROOT).trim();
        String request = text.replace(' ', '+');
        System.out.println(request);
        String query = "https://openlibrary.org/search?q="+request+"&mode=everything";
        System.out.println(query);

        try {
            Document doc = Jsoup.connect(query).get();
            System.out.printf("Title: %s\n", doc.title());
            if (!doc.getElementsByClass("red").text().equals("No results found.")) {
                Elements repositories = doc.getElementsByClass("searchResultItem");
                for (Element repository : repositories) {
                    // Extract the title
                    String repositoryTitle = repository.getElementsByClass("booktitle").text();

                    // Extract the publication year
                    String repositoryPublicationYear = repository.getElementsByClass("publishedYear").text();

                    // Extract the authors
                    String repositoryAuthor = repository.getElementsByClass("bookauthor").text();

                    // Format and print the information to the console
                    String result = "\"" + repositoryTitle + "\"" + " " + repositoryAuthor + ", " + repositoryPublicationYear + ".";
                    System.out.println(result);
                    addToListView(nameRes, result);
                    System.out.println("\n");
                }
            } else {
                nameRes.setVisible(false);
                error.setVisible(true);
                error.setText("No results found.");
            }
            /*
            con = (HttpURLConnection) new URL(query).openConnection();
            con.setRequestMethod("GET");
            con.connect();

            StringBuilder sb = new StringBuilder();
            System.out.println(con.getContent());
            System.out.println(con.getDate());
            System.out.println(con.getResponseCode());

            if (HttpURLConnection.HTTP_OK == con.getResponseCode()) {
                BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = bf.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                System.out.println(sb);
            }
            */
        } catch (Throwable cause) {
            cause.printStackTrace();
        }
    }

    private void setSceneVisibility(boolean b, Label res, ListView nameRes, Button add, Button close) {
        res.setVisible(b);
        nameRes.setVisible(b);
        add.setVisible(b);
        close.setVisible(b);
    }

    private void setSceneVisibility(boolean b, Label welcomeText, ListView listview, TextField toDoThing, Button search,
                                    Button save, Button btnDelete, Button edit, Button load) {
        welcomeText.setVisible(b);
        listview.setVisible(b);
        toDoThing.setVisible(b);
        search.setVisible(b);
        save.setVisible(b);
        btnDelete.setVisible(b);
        edit.setVisible(b);
        load.setVisible(b);
    }

    public void addToInventory(ActionEvent actionEvent) {
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (listview.getItems().isEmpty()) {
                    ObservableList<String> topics;
                    topics = nameRes.getSelectionModel().getSelectedItems();
                    System.out.println(topics);
                    String item = topics.toString().replace("[", "").replace("]", "");
                    addToListView(listview, item);
                } else {
                if (listviewContains()) {
                    ObservableList<String> topics;
                    topics = nameRes.getSelectionModel().getSelectedItems();
                    System.out.println(topics);
                    String item = topics.toString().replace("[", "").replace("]", "");
                    addToListView(listview, item);
                } else {
                    nameRes.setVisible(false);
                    error.setVisible(true);
                    error.setText("This item is already on the list");
                }
                }
            }
        });
    }

    private boolean listviewContains () {
        Object[] allItems = listview.getItems().toArray();
        for (int i=0; i<allItems.length; i++) {
            String list;
            list = allItems[i].toString();
            String str = nameRes.getSelectionModel().getSelectedItems().toString();
            str = str.replace("[", "").replace("]", "");
            System.out.println("String list: "+list);
            System.out.println("String str: " +str);
            if (list.equals(str)) {
                return false;
            }
        }
        return true;
    }
}