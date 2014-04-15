package com.codealot.textstore;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.input.ReaderInputStream;

/**
 * Simple storage system, using files on the default FileSystem.
 * <p>
 * Text is hashed using SHA-1 then stored as a file named with the hex of that
 * hash. This hex is also the text id. All files are stored in a directory set
 * during construction.
 * <p>
 * Although it should be possible to have multiple instances of this class using
 * the same storage path, this is not recommended.
 * <p>
 * Note that no attempt is made to clean or check the provided text (other than
 * it, or its Reader, not being null).
 * 
 * @author jacobsp
 * 
 *         <p>
 *         Copyright (C) 2014 Codealot Limited.
 * 
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 */
public class FileStore implements TextStore
{
    // path to the root of the file store. Must be a directory.
    private final String storeRoot;

    /**
     * Create a FileStore based on the given directory.
     * 
     * @param storeRoot
     *            must be a directory
     */
    public FileStore(final Path storeRoot)
    {
        Objects.requireNonNull(storeRoot, "Storage path not given.");

        if (!Files.isDirectory(storeRoot))
        {
            throw new IllegalArgumentException("Path " + storeRoot
                    + " is not a directory.");
        }
        this.storeRoot = storeRoot.toString();
    }

    @Override
    public String getText(final String hash) throws IOException
    {
        checkHash(hash);

        final Path textPath = Paths.get(this.storeRoot, hash);
        if (!Files.isReadable(textPath))
        {
            throw new IOException("Id " + hash + " has no readable content.");
        }
        final byte[] bytes = Files.readAllBytes(textPath);

        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public String storeText(final String text) throws IOException
    {
        Objects.requireNonNull(text, "No text provided");

        // make the digester
        final MessageDigest digester = getDigester();

        // make the hex hash
        final byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        digester.update(textBytes);
        final String hash = byteToHex(digester.digest());

        // store the text
        final Path textPath = Paths.get(this.storeRoot, hash);
        if (!Files.exists(textPath))
        {
            try
            {
                Files.write(textPath, textBytes);
            }
            catch (IOException e)
            {
                // make certain the file is not left on disk
                Files.deleteIfExists(textPath);
                // now re-throw the exception
                throw e;
            }
        }
        return hash;
    }

    @Override
    public void deleteText(final String hash) throws IOException
    {
        checkHash(hash);

        final Path textPath = Paths.get(this.storeRoot, hash);
        Files.delete(textPath);
    }

    private MessageDigest getDigester()
    {
        try
        {
            return MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private void checkHash(final String hash)
    {
        Objects.requireNonNull(hash, "No id provided");
        if (!hash.matches("[A-F0-9]{40}"))
        {
            throw new IllegalArgumentException("Bad id: " + hash);
        }
    }

    private static String byteToHex(final byte[] hash)
    {
        final Formatter formatter = new Formatter();
        for (final byte b : hash)
        {
            formatter.format("%02X", b);
        }
        final String result = formatter.toString();
        formatter.close();
        return result;
    }

    @Override
    public String storeText(final Reader reader) throws IOException
    {
        Objects.requireNonNull(reader, "No reader provided");

        // make the digester
        final MessageDigest digester = getDigester();

        // make temp file
        final Path textPath = Paths.get(this.storeRoot, UUID.randomUUID()
                .toString());

        // stream to file, building digest
        final ReaderInputStream readerAsBytes = new ReaderInputStream(reader,
                StandardCharsets.UTF_8);
        try
        {
            final byte[] bytes = new byte[1024];
            int readLength = 0;

            while ((readLength = readerAsBytes.read(bytes)) > 0)
            {
                digester.update(bytes, 0, readLength);
                
                final byte[] readBytes = Arrays.copyOf(bytes, readLength);
                Files.write(textPath, readBytes, StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
            // make the hash
            final String hash = byteToHex(digester.digest());

            // store the text, if new
            final Path finalPath = Paths.get(this.storeRoot, hash);
            if (!Files.exists(finalPath))
            {
                // rename the file
                Files.move(textPath, finalPath);
            }
            else {
                // already existed, so delete uuid named one
                Files.deleteIfExists(textPath);
            }
            return hash;
        }
        finally
        {
            if (readerAsBytes != null)
            {
                readerAsBytes.close();
            }
        }
    }

}
