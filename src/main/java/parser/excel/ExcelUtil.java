package parser.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import parser.connection.DbConnection;
import parser.entity.ResultRow;
import parser.entity.User;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ExcelUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);
    static Map<Long, String> loadedUsers = new HashMap<>();
    static Map<Long, String> loadedViews = new HashMap<>();
    static Map<Long, String> loadedActions = new HashMap<>();

    public static void createExcel(List<ResultRow> resultRowList, Set<Long> userIds, Set<Long> actionIds) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("parse result");

        int rowCount = 0;
        Row header = sheet.createRow(rowCount++);

        loadNeededUsers(userIds);
        loadNeededActionsAndViews(actionIds);

        header.createCell(0).setCellValue("#");
        header.createCell(1).setCellValue("date");
        header.createCell(2).setCellValue("action");
        header.createCell(3).setCellValue("document");
        header.createCell(4).setCellValue("view");
        header.createCell(5).setCellValue("user");
        rowCount++;
        for (ResultRow resultRow : resultRowList) {
            Row row = sheet.createRow(rowCount++);

            Cell countCell = row.createCell(0, CellType.NUMERIC);
            countCell.setCellValue(rowCount - 2);

            Cell dateCell = row.createCell(1, CellType.STRING);
            logger.warn("date: {}", resultRow.getDate());
            dateCell.setCellValue(resultRow.getDate() == null ? "" : new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(resultRow.getDate()));

            Cell actionCell = row.createCell(2, CellType.STRING);
            actionCell.setCellValue(loadedActions.get(resultRow.getActionId()));

            Cell documentCell = row.createCell(3, CellType.STRING);
            documentCell.setCellValue(resultRow.getDocument().toString());

            Cell viewCell = row.createCell(4, CellType.STRING);
            viewCell.setCellValue(loadedViews.get(resultRow.getView().getId()));

            Cell userCell = row.createCell(5, CellType.STRING);
            userCell.setCellValue(loadedUsers.get(resultRow.getUser().getId()));
        }

        try {
            String path = System.getProperty("user.home") + "\\documents\\logparser" + new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date()) + ".xls";
            workbook.write(new FileOutputStream(path));
            Desktop desktop = Desktop.getDesktop();
            desktop.open(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadNeededActionsAndViews(Set<Long> actionIds) {
        try {
            String sql = "select a.id, a.view_id, (xpath('/action/title/text()', a.structure))[1]::text as title, v.name as view_name \n" +
                    "from actions a \n" +
                    "left join public.views v on a.view_id = v.id \n" +
                    "where v.type_id = 'view'\n" +
                    "and a.id in " + actionIds.toString().replace("[", "(").replace("]", ")") + "\n" +
                    "order by a.id";
            logger.info("sql: {}", sql);
            ResultSet resultSet = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql);
            while (resultSet.next()) {
                loadedViews.put(resultSet.getLong("view_id"), resultSet.getString("view_name"));
                loadedActions.put(resultSet.getLong("id"), resultSet.getString("title"));
                logger.info("id: {}, title: {}, view: {}", resultSet.getLong("id"), resultSet.getString("title"), resultSet.getString("view_name"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void loadNeededUsers(Set<Long> ids) {
        try {
            String sql = "select * from users u where u.id in " + ids.toString().replace("[", "(").replace("]", ")") + " and u.is_actual";
            logger.info("sql: {}", sql);
            ResultSet resultSet = DbConnection.getInstance().getConnection().createStatement().executeQuery(sql);
            while (resultSet.next()) {
                loadedUsers.put(resultSet.getLong("id"), resultSet.getString("fio"));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
