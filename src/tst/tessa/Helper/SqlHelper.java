package tst.tessa.Helper;

import tst.tessa.FileObject;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class SqlHelper
{

    /**
     * Pfgbcm файла из бд
     * @param sdSource A variable of type SQLServerDataSource.
     * @param fileInfo A variable of type FileObject.
     */
    public static void InsertIntoTable(SQLServerDataSource sdSource, FileObject fileInfo)
    {
        //подключаемся к бд
        try(Connection con = sdSource.getConnection();
            Statement stmt = con.createStatement())
        {
            String SQL = InsertNewFileCommand(fileInfo);
            //выполняем комманду
            stmt.execute(SQL);
        }
        catch (SQLException e)
        {
            //todo: log
            e.printStackTrace();
        }
    }

    /**
     * Удаление файла из бд
     * @param sdSource A variable of type SQLServerDataSource.
     * @param fileInfo A variable of type FileObject.
     */
    public static void DeleteFromTable(SQLServerDataSource sdSource, FileObject fileInfo)
    {
        //подключаемся к бд
        try(Connection con = sdSource.getConnection();
            Statement stmt = con.createStatement())
        {
            String SQL = DeleteFileCommand(fileInfo);
            //выполняем комманду
            stmt.execute(SQL);
        }
        catch (SQLException e)
        {
            //todo: log
            e.printStackTrace();
        }
    }

    /**
     * комманда для записи нового файла
     * @param fileInfo A variable of type FileObject.
     */
    private static String InsertNewFileCommand(FileObject fileInfo)
    {
        return String.format("INSERT INTO TextTable " +
                "VALUES ('%s', '%s', '%s', '%s')",
                fileInfo.CardID,  fileInfo.FileID,  UUID.randomUUID(), fileInfo.TxtContent);
    }

    /**
     * команда для удаления файла
     * @param fileInfo A variable of type FileObject.
     */
    private static String DeleteFileCommand(FileObject fileInfo)
    {
        return String.format("DELETE FROM TextTable " +
                "WHERE CardID = '%s' and FileID = '%s'",
                fileInfo.CardID,  fileInfo.FileID);
    }
}
