package com.codealot.textstore;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileStoreTest
{
    private Path storeRoot;
    private FileStore fileStore;

    @Before
    public void setUp() throws IOException
    {
        this.storeRoot = Files.createTempDirectory("FileStore-");
        this.fileStore = new FileStore(storeRoot);
    }

    @After
    public void tearDown() throws IOException
    {
        if (this.storeRoot != null)
        {
            // dir must be empty
            File[] files = storeRoot.toFile().listFiles();
            for (int i = 0; i < files.length; i++)
            {
                files[i].delete();
            }
            Files.deleteIfExists(storeRoot);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testFileStoreNull()
    {
        new FileStore(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileStoreNotExist()
    {
        Path notExists = Paths.get("not-a-file");
        new FileStore(notExists);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileStoreNotDirectory() throws IOException
    {

        Path notADir = null;
        try
        {
            notADir = Files.createTempFile("fs", "tmp");
            new FileStore(notADir);
        }
        finally
        {
            if (notADir != null)
            {
                Files.deleteIfExists(notADir);
            }
        }
    }

    @Test
    public void testFileStore() throws IOException
    {
        Path isADir = null;
        try
        {
            isADir = Files.createTempDirectory("fs");
            new FileStore(isADir);
        }
        finally
        {
            if (isADir != null)
            {
                Files.deleteIfExists(isADir);
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetTextNullId() throws IOException
    {
        fileStore.getText(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTextEmptyId() throws IOException
    {
        fileStore.getText("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTextShortId() throws IOException
    {
        fileStore.getText("ABCD1234");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTextNotHexId() throws IOException
    {
        fileStore.getText("123456789012345678901234567890123456789Z");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTextLongId() throws IOException
    {
        fileStore.getText("123456789012345678901234567890123456789012345");
    }

    @Test(expected = IOException.class)
    public void testGetTextIdUnknown() throws IOException
    {
        fileStore.getText("1234567890123456789012345678901234567890");
    }

    @Test
    public void testGetText() throws IOException
    {
        String text = "This is a test";
        String id = fileStore.storeText(text);
        assertEquals(text, fileStore.getText(id));
    }

    @Test(expected = NullPointerException.class)
    public void testStoreTextNull() throws IOException
    {
        fileStore.storeText(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreTextEmpty() throws IOException
    {
        fileStore.storeText("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreTextWhitespace() throws IOException
    {
        fileStore.storeText("           \n\r\t");
    }

    @Test
    public void testStoreText() throws IOException
    {
        String text = "This is a test";
        String id = fileStore.storeText(text);
        assertEquals(text, fileStore.getText(id));
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteTextNullId() throws IOException
    {
        fileStore.deleteText(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextEmptyId() throws IOException
    {
        fileStore.deleteText("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextShortId() throws IOException
    {
        fileStore.deleteText("ABCDEF12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextLongId() throws IOException
    {
        fileStore.deleteText("123456789012345678901234567890123456789012345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteTextNotHexId() throws IOException
    {
        fileStore.deleteText("123456789012345678901234567890123456789Z");
    }

    @Test
    public void testDeleteText() throws IOException
    {
        String text = "This is a test";
        String id = fileStore.storeText(text);
        Path testPath = Paths.get(storeRoot.toString(), id);
        assertTrue(Files.exists(testPath));
        fileStore.deleteText(id);
        assertFalse(Files.exists(testPath));
    }
}
