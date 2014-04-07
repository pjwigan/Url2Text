package com.codealot.url2text.cli;

import static org.junit.Assert.*;
import static com.codealot.url2text.Constants.*;

import java.io.File;
import java.io.IOException;

import joptsimple.OptionSet;
import joptsimple.OptionException;

import org.junit.Test;

import com.codealot.url2text.cli.Application;

public class ApplicationTest {

    @Test
    public void testParseARgsNoOpts() throws IOException {

        String[] args = {};
        OptionSet options = Application.parseArgs(args);
        assertEquals(false, options.has(Application.CSS));
        assertEquals(false, options.has(Application.DO_NOT_TRACK));
        assertEquals(false, options.has(Application.INCLUDE_HEADERS));
        assertEquals(false, options.has(Application.INCLUDE_METADATA));
        assertEquals(false, options.has(Application.INSECURE_SSL));
        assertEquals(false, options.has(Application.JAVASCRIPT));
        assertEquals(false, options.has(Application.NO_COOKIES));
        assertEquals(false, options.has(Application.NO_REDIRECT));
        assertEquals(false, options.has(Application.OUTPUT_FILE));
        assertEquals(false, options.has(Application.OUTPUT_FORMAT));
        assertEquals(false, options.has(Application.HTTP_TIMEOUT));
        assertEquals(false, options.has(Application.MAX_LENGTH));
    }

    @Test
    public void testParseArgsBooleanOpts() throws IOException {

        String[] args = { "--css", "--do-not-track", "--include-headers",
                "--include-metadata", "--insecure-ssl", "--javascript",
                "--no-cookies", "--no-redirect" };
        OptionSet options = Application.parseArgs(args);
        assertEquals(true, options.has(Application.CSS));
        assertEquals(true, options.has(Application.DO_NOT_TRACK));
        assertEquals(true, options.has(Application.INCLUDE_HEADERS));
        assertEquals(true, options.has(Application.INCLUDE_METADATA));
        assertEquals(true, options.has(Application.INSECURE_SSL));
        assertEquals(true, options.has(Application.JAVASCRIPT));
        assertEquals(true, options.has(Application.NO_COOKIES));
        assertEquals(true, options.has(Application.NO_REDIRECT));
    }

    @Test(expected = OptionException.class)
    public void testParseArgsOutputFormatRequired() throws IOException {

        String[] args = { "--output-format" };
        Application.parseArgs(args);
    }

    @Test
    public void testParseArgsOutputFormat() throws IOException {

        OptionSet options = Application.parseArgs(new String[] {
                "--output-format", OutputFormat.PLAIN.toString() });
        assertEquals(OutputFormat.PLAIN, options.valueOf(Application.OUTPUT_FORMAT));

        options = Application.parseArgs(new String[] { "--output-format",
                OutputFormat.JSON.toString() });
        assertEquals(OutputFormat.JSON, options.valueOf(Application.OUTPUT_FORMAT));
    }

    @Test(expected = OptionException.class)
    public void testParseArgsOutputFileRequired() throws IOException {

        String[] args = { "--output-file" };
        Application.parseArgs(args);
    }

    @Test(expected = OptionException.class)
    public void testParseArgsMaxLengthRequired() throws IOException {

        String[] args = { "--max-length" };
        Application.parseArgs(args);
    }

    @Test
    public void testParseArgsOutputFile() throws IOException {

        String[] args = { "--output-file", "path.out" };
        OptionSet options = Application.parseArgs(args);
        File f = (File) options.valueOf(Application.OUTPUT_FILE);
        assertEquals("path.out", f.getName());
    }

    @Test(expected = OptionException.class)
    public void testParseArgsTimeoutRequired() throws IOException {

        String[] args = { "--http-timeout" };
        Application.parseArgs(args);
    }

    @Test(expected = OptionException.class)
    public void testParseArgsTimeoutNotInt() throws IOException {

        String[] args = { "--http-timeout", "bananas" };
        OptionSet options = Application.parseArgs(args);
        options.valueOf(Application.HTTP_TIMEOUT);
    }

    @Test
    public void testParseArgsTimeout() throws IOException {

        String[] args = { "--http-timeout", "50" };
        OptionSet options = Application.parseArgs(args);
        assertEquals(50, options.valueOf(Application.HTTP_TIMEOUT));
    }

    @Test(expected = OptionException.class)
    public void testParseArgsMaxLengthNotInt() throws IOException {

        String[] args = { "--max-length", "bananas" };
        OptionSet options = Application.parseArgs(args);
        options.valueOf(Application.MAX_LENGTH);
    }

    @Test
    public void testParseArgsMaxLength() throws IOException {

        String[] args = { "--max-length", "50" };
        OptionSet options = Application.parseArgs(args);
        long val = (long) options.valueOf(Application.MAX_LENGTH);
        assertEquals(50L, val);
    }
    
    /**
     * Was present in earlier versions but has been removed.
     * 
     * @throws IOException
     */
    @Test(expected = OptionException.class)
    public void testParseArgsLogLevelRequired() throws IOException {

        String[] args = { "--log-level" };
        Application.parseArgs(args);
    }


}
