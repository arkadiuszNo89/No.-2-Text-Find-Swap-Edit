package application;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;


public class AppController 
{

    @FXML
    private TextArea textArea;

    @FXML
    private TextField fieldSearch;

    @FXML
    private Button buttonSearch;

    @FXML
    private TextField fieldChange;

    @FXML
    private Button buttonChange;

    @FXML
    private Button buttonManual;

    @FXML
    private Button buttonSemi;

    @FXML
    private Button buttonAuto;

    @FXML
    private Button buttonLoad;

    @FXML
    private Button buttonSave;

    @FXML
    private ChoiceBox <String> choiceBoxCon;

    @FXML
    private Button buttonSearchCon;

    @FXML
    private Button buttonCast;

    @FXML
    private BorderPane root;
    
    @FXML
    private VBox vBox;
    
    private Button [] tab;
    private List <Button> lista;
    private int searchFrom = 0, start = 0, end = 0;
    private final String stateOn = "-fx-background-color: khaki";
    private final String stateOff = "-fx-background: #f4f4f4";
    
    public void initialize()
    {
    	tab = new Button [] {buttonSave, buttonLoad, buttonSearch, buttonChange, buttonAuto, 
				buttonSemi, buttonManual, buttonSearchCon, buttonCast};
    	
    	lista = List.of(buttonAuto, buttonSemi, buttonManual);	// TEGO TYLE SZUKA£EM !!!!!!!!!!!!
    	
    	init();
    	addActions();
    }
        
	public  void search (ActionEvent event)
	{
		finder();
		if (start == -1)
		{
			searchFrom = 0;
			finder();
			if (start == -1) textArea.deselect();
		}
	}
	
	public  void change (ActionEvent event)
	{	
		if (!fieldChange.getText().isEmpty())
		{
			if (textArea.getSelectedText().isEmpty()) buttonSearch.fire();
			else if (buttonSemi.getStyle().equals(stateOn) && !textArea.getSelectedText().isEmpty()
					&& textArea.getSelectedText().toString().equals(fieldChange.getText())) buttonSearch.fire();	// #2
			
			if (!textArea.getSelectedText().isEmpty() && buttonAuto.getStyle().equals(stateOff))
			{
				int a = textArea.getSelection().getStart();
				int b = a + fieldChange.getText().length();
				textArea.replaceSelection(fieldChange.getText());
				textArea.selectRange(a, b); 
			}
			while (!textArea.getSelectedText().isEmpty() && buttonAuto.getStyle() == stateOn)																			// #3
			{
				textArea.replaceSelection(fieldChange.getText());
				buttonSearch.fire();
			}
		}
	}
	
	public  void load (ActionEvent event)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("*.txt","*.txt"));
		File file = fileChooser.showOpenDialog(null);
		
		if (file != null)
		{
			try
			{
				String text = Files.readString(Paths.get(file.getPath()));
				textArea.setText(text);			
			}
			catch (Exception e) 
			{
				textArea.setText("Nieudane ³adowanie pliku");
			}
		}
	}
	
	public  void save (ActionEvent event)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("*.txt","*.txt"));
		File file = fileChooser.showSaveDialog(null);
		
		if (file != null)
		{
			try
			{
				FileWriter writer = new FileWriter(file);
				writer.write(textArea.getText());
				writer.close();
			}
			catch (Exception e) 
			{
				fieldSearch.setText("Nieudany zapis");
				textArea.appendText("Nieudany zapis");
				buttonSearch.fire();
			}
		}
	}
	
	public  void searchCon (ActionEvent event)
	{
		if (choiceBoxCon.getValue() != null)
		{
			switch (choiceBoxCon.getValue().toString())
			{
				case "Wyrazy s¹siaduj¹ce (+)":
					
					finderPlus(2);
					if (start == -1)
					{
						searchFrom = 0;
						finderPlus(2);
						if (start == -1) textArea.deselect();
					}
					break;
					
				case "Wyrazy s¹siaduj¹ce (++)":
					
					finderPlus(4);
					if (start == -1)
					{
						searchFrom = 0;
						finderPlus(4);
						if (start == -1) textArea.deselect();
					}
					break;
					
				case "Zdania kontekstowe":
					
					finderSentence();
					if (start == -1)
					{
						searchFrom = 0;
						finderSentence();;
						if (start == -1) textArea.deselect();
					}
					break;
			}
		}
	}
	
	public void cast (ActionEvent event)
	{
		if (choiceBoxCon.getValue() != null)
		{
			searchFrom = 0;
			int x = 0;
			Queue <String> kolejka = new ArrayDeque<String>();
			
			while (true)
			{
				buttonSearchCon.fire();
				if (textArea.getSelection() == null) break;
				if (textArea.getSelection().getStart() < x) break;
				
				x = textArea.getSelection().getStart();
				kolejka.add(textArea.getSelectedText());
			}			
			textArea.clear();
			while(kolejka.peek() != null) 
			{
				textArea.appendText(kolejka.poll()+"\n");
			}
		}
	}
    
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void finder()
	{
			if (!fieldSearch.getText().isEmpty())
			{
				String theWord = fieldSearch.getText();
				start = textArea.getText().indexOf(theWord, searchFrom);
				end = start + theWord.length();
				textArea.selectRange(start, end);
				searchFrom = end;
			}
	}
	
	public void finderPlus(int x)
	{
		finder();
		if (!fieldSearch.getText().isEmpty())
		{
			int startPlus = start -1;
			int endPlus = end +1;
			int i = x;
			int j = 0;
			
			while (startPlus >= 0)
			{
				if (textArea.getText().charAt(startPlus) == ' '
						|| textArea.getText().charAt(startPlus) == '\n') i--;
				if (i == 0)
				{
//					textArea.selectRange(startPlus, end);
					break;
				}
				startPlus--;
			}
			while (endPlus < textArea.getText().length() - 1)
			{
				if (textArea.getText().charAt(endPlus) == ' '
						|| textArea.getText().charAt(endPlus) == '\n') j++;
				if (j == x)
				{
					break;
				}
				endPlus++;
			}
			textArea.selectRange(startPlus+1, endPlus);
		}	
	}
	
	public void finderSentence()
	{
		finder();
		if (!fieldSearch.getText().isEmpty())
		{
			int startPlus = start -1;
			int endPlus = end +1;
			
			while (startPlus >= 0)
			{
				if (textArea.getText().charAt(startPlus) == '.'
						|| textArea.getText().charAt(startPlus) == '\n') break;
				startPlus--;
			}
			while (endPlus < textArea.getText().length() - 1)
			{
				if (textArea.getText().charAt(endPlus) == '.'
						|| textArea.getText().charAt(endPlus) == '\n') break;
				endPlus++;
			}
			textArea.selectRange(startPlus+1, endPlus);
		}	
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private void addActions()
	{
    	buttonSearch.setOnAction(this::search);
    	buttonChange.setOnAction(this::change);
    	buttonLoad.setOnAction(this::load);
    	buttonSave.setOnAction(this::save);
    	buttonSearchCon.setOnAction(this::searchCon);
    	buttonCast.setOnAction(this::cast);
    	
    	EventHandler <ActionEvent> eventFocus = (ActionEvent e) ->
    	{
    		for (Button button : lista) ((Button)button).setStyle(stateOff);
    		((Button)e.getSource()).setStyle(stateOn);
    	};
    	
    	buttonAuto.setOnAction(eventFocus);
    	buttonSemi.setOnAction(eventFocus);
    	buttonManual.setOnAction(eventFocus);
	}
	
    private void init()
    {	
    	for (Button button : tab) button.prefWidthProperty().bind(root.widthProperty());
    	for (Button button : tab) button.prefHeightProperty().bind(vBox.heightProperty());
    	
    	choiceBoxCon.prefWidthProperty().bind(root.widthProperty());	
    	textArea.prefWidthProperty().bind(root.widthProperty());
    	textArea.prefHeightProperty().bind(root.heightProperty());
    	vBox.setPrefHeight(78);
    	vBox.prefWidthProperty().bind(root.widthProperty());	
    	fieldChange.prefWidthProperty().bind(root.widthProperty());
    	fieldSearch.prefWidthProperty().bind(root.widthProperty());
    	
    	textArea.setWrapText(true);
    	buttonManual.setStyle(stateOn);
    	buttonSemi.setStyle(stateOff);
    	buttonAuto.setStyle(stateOff);
    	
    	choiceBoxCon.getItems().addAll("Wyrazy s¹siaduj¹ce (+)", "Wyrazy s¹siaduj¹ce (++)", "Zdania kontekstowe");
    } 
}
