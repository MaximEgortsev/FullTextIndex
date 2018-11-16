package tst.tessa;

import tst.tessa.Helper.FileStateEnum;
import tst.tessa.Helper.FileTypeEnum;

import java.util.UUID;


public class FileObject
{

    /**
     * ID карточки
     */
    public UUID CardID;
    /**
     * ID файла
     */
    public UUID FileID;
    /**
     * контент файла
     */
    public String TxtContent;
    /**
     * имя файла
     */
    private String Name;
    /**
     * путь к файлу
     */
    String Path;

    /**
     * состояние файла в тессе
     */
    FileStateEnum State;
    /**
     * тип файла
     */
    FileTypeEnum Type;

     FileObject(UUID cardID, UUID fileID,
               String name, String filePath, FileStateEnum state)
    {
        CardID = cardID;
        FileID = fileID;
        Name = name;
        State = state;
        Path = filePath;
        Type = GetFileType();
    }

    FileObject(UUID cardID, UUID fileID,
               String filePath, FileStateEnum state)
    {
        CardID = cardID;
        FileID = fileID;
        State = state;
        Path = filePath;
        Name = Path;
        Type = GetFileType();
    }

    /**
     * Возвращает тип файла
     * @return FileTypeEnum
     */
    private FileTypeEnum GetFileType()
    {
        if (Name.endsWith(".doc") || Name.endsWith(".docx"))
        {
            return FileTypeEnum.Word;
        }
        else if (Name.endsWith(".xls") || Name.endsWith(".xlsx"))
        {
            return FileTypeEnum.Excel;
        }
        else if (Name.endsWith(".vsd") || Name.endsWith(".vsdx"))
        {
            return FileTypeEnum.Visio;
        }
        else if (Name.endsWith(".ppt") || Name.endsWith(".pptx"))
        {
            return FileTypeEnum.PowerPoint;
        }
        else if (Name.endsWith(".mpp"))
        {
            return FileTypeEnum.Project;
        }
        else if (Name.endsWith(".pdf"))
        {
            return FileTypeEnum.Pdf;
        }
        else if (Name.endsWith(".odt"))
        {
            return FileTypeEnum.Odt;
        }
        else
        {
            return FileTypeEnum.None;
        }
    }
}
