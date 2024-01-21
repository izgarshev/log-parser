package parser.controller;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.entity.ResultRow;
import parser.excel.ExcelUtil;
import parser.parserIF.ParserIF;
import parser.utils.Parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//full.log-2023-12-06.3.gz:06.12.2023 13:49:20.298  INFO [worker:496-action-8525] -
// Requested view: (typeId: 'view', id: '92', versionId: '1')
// for document: (typeId: 'document-incoming', id: '12040007', versionId: '1')
// from user: (typeId: 'user', id: '31138', versionId: '1')
public class Controller {
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    ParserIF parser = new Parser();

    public static final String NOT_FOUND = "Нет данных";

    private final TextField views = new TextField();
    private final TextField users = new TextField();
    private final TextField actions = new TextField();
    private final TextField filePath = new TextField();

    private final Button viewsBtn = new Button(". . .");
    private final Button usersBtn = new Button(". . .");
    private final Button actionsBtn = new Button(". . .");
    private final Button filePathBtn = new Button(". . .");
    private final Button start = new Button("Начать");

    private File file = null;

    public static AnchorPane mainPane = new AnchorPane();
    VBox vBox = new VBox();
    HBox viewsBox = new HBox();
    HBox usersBox = new HBox();
    HBox actionsBox = new HBox();
    HBox filePathBox = new HBox();

    public void initialize() {
        mainPane.getChildren().add(vBox);
        AnchorPane.setLeftAnchor(vBox, 0d);
        AnchorPane.setTopAnchor(vBox, 0d);
        AnchorPane.setRightAnchor(vBox, 0d);
        AnchorPane.setBottomAnchor(vBox, 0d);

        viewsBox.getChildren().addAll(views, viewsBtn);
        usersBox.getChildren().addAll(users, usersBtn);
        actionsBox.getChildren().addAll(actions, actionsBtn);
        filePathBox.getChildren().addAll(filePath, filePathBtn);

        filePath.setEditable(false);

        filePathBtn.setOnAction(event -> {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\documents"));
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Text files", "*.txt"));
            file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
            if (file != null) filePath.setText(file.getPath());
        });

        start.setOnAction(event -> {
            if (file != null) {
                BufferedReader reader;

                try {
                    reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    List<ResultRow> list = new ArrayList<>();
                    int count = 1;
                    Set<Long> actionIds = new HashSet<>();
                    Set<Long> userIds = new HashSet<>();


                    while (line != null) {
                        logger.debug(count + ": " + line);
                        ResultRow row = new ResultRow()
                                .setDate(parser.parseDate(line))
                                .setActionId(parser.parseActionId(line))
                                .setDocument(parser.parseDocument(line))
                                .setUser(parser.parseUser(line))
                                .setView(parser.parseView(line));
                        if (row.getActionId() != null) {
                            list.add(row);
                            actionIds.add(row.getActionId());
                            if (row.getUser() != null) {
                                userIds.add(row.getUser().getId());
                            }
                        }
                        // read next line
                        line = reader.readLine();
                        count++;
                    }
                    logger.info("action id's: {}", actionIds);
                    logger.info("==================FINISH==================");
                    ExcelUtil.createExcel(list, userIds, actionIds);

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        vBox.getChildren().addAll(viewsBox, usersBox, actionsBox, filePathBox, start);
    }

}
