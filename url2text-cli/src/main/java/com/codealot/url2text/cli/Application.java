package com.codealot.url2text.cli;

import static java.util.Arrays.asList;
import static com.codealot.url2text.Constants.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codealot.url2text.Url2Text;
import com.codealot.url2text.Url2TextResponse;
import com.codealot.url2text.Constants.OutputFormat;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * Command line utility to fetch the content of a URL as text, with
 * optional metadata, in plain text, JSON, or XML format.
 * <p>
 * For usage see {@link #main(String[])}.
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
public class Application {

    // FUTURE add Url2Text as the parent POM
    
    public static final String CSS = "css";
    public static final String DO_NOT_TRACK = "do-not-track";
    public static final String JAVASCRIPT = "javascript";
    public static final String INSECURE_SSL = "insecure-ssl";
    public static final String NO_REDIRECT = "no-redirect";
    public static final String NO_COOKIES = "no-cookies";
    public static final String INCLUDE_HEADERS = "include-headers";
    public static final String INCLUDE_METADATA = "include-metadata";
    public static final String HTTP_TIMEOUT = "http-timeout";
    public static final String MAX_LENGTH = "max-length";
    public static final String OUTPUT_FORMAT = "output-format";
    public static final String OUTPUT_FILE = "output-file";
    public static final String VERSION = "version";
    public static final String HELP = "help";
    
    // SLF4J logger instance
    private static final Logger LOG = LoggerFactory
            .getLogger(Application.class);

    /**
     * Basic usage is of the form:
     * <p>
     * <blockquote>
     * 
     * <pre>
     * java [JVM options] -jar Url2Text.jar [options] url
     * 
     * The following command line options are available:
     * 
     *  Option                             Description                        
     *  ------                             -----------                        
     *  -?, -h, --help                     show help                          
     *  --css                              Enable CSS support                 
     *  --do-not-track                     Enable Do Not Track support        
     *  --http-timeout <Integer: seconds>  HTTP transaction timeout
     *  --include-headers                  Include HTTP response headers      
     *  --include-metadata                 Include content metadata           
     *  --insecure-ssl                     Ignore server certificates         
     *  --javascript                       Enable Javascript
     *  --max-length                       Maximum Content-Length                  
     *  --no-cookies                       Disable cookie support             
     *  --no-redirect                      Disable redirection                
     *  --output-file <File: file>         File to receive output               
     *  --output-format                    One of PLAIN, JSON                     
     *  --version                          Print version to stdout
     * </pre>
     * 
     * </blockquote>
     * 
     * @param args
     *            command line arguments
     */
    public static void main(final String[] args) {

        // parse the options
        OptionSet options = null;
        try {
            options = parseArgs(args);
        }
        catch (IOException e1) {
            LOG.error("Error processing command line.");
            System.exit(2);
        }

        // make sure url is a URL
        final List<?> nonopts = options.nonOptionArguments();
        if (nonopts.size() != 1) {
            LOG.error("Command line incomplete or ambiguous");
            System.exit(1);
        }
        URL url = null;
        try {
            url = new URL(nonopts.get(0).toString());
        }
        catch (MalformedURLException e) {
            LOG.error(nonopts.get(0).toString() + " not a valid URL.", e);
            System.exit(5);
        }

        // create operational object
        final Url2Text fetcher = new Url2Text();

        // configure object
        if (options.has(CSS)) {
            fetcher.setCssEnabled(true);
        }
        if (options.has(DO_NOT_TRACK)) {
            fetcher.setDoNotTrackEnabled(true);
        }
        if (options.has(JAVASCRIPT)) {
            fetcher.setJavascriptEnabled(true);
        }
        if (options.has(INSECURE_SSL)) {
            fetcher.setUseInsecureSSL(true);
        }
        if (options.has(NO_REDIRECT)) {
            fetcher.setRedirectEnabled(false);
        }
        if (options.has(NO_COOKIES)) {
            fetcher.setCookiesEnabled(false);
        }
        if (options.has(INCLUDE_HEADERS)) {
            fetcher.setIncludeHeaders(true);
        }
        if (options.has(INCLUDE_METADATA)) {
            fetcher.setIncludeMetadata(true);
        }
        if (options.has(HTTP_TIMEOUT)) {
            final Integer timeout = (Integer) options.valueOf(HTTP_TIMEOUT);
            fetcher.setNetworkTimeout(timeout);
        }
        if (options.has(MAX_LENGTH)) {
            final Long maxLength = (Long) options.valueOf(MAX_LENGTH);
            fetcher.setMaxContentLength(maxLength);
        }
        
        // determine output format
        OutputFormat outputFormat = OutputFormat.PLAIN;
        if (options.has(OUTPUT_FORMAT)) {
            outputFormat = (OutputFormat) options.valueOf(OUTPUT_FORMAT);
        }

        // issue fetch command
        try {
            Url2TextResponse response = fetcher.contentAsText(url, null);
            
            if (options.has(OUTPUT_FILE)) {
                // output to output-file
                try (PrintWriter out = new PrintWriter(
                        (File) options.valueOf(OUTPUT_FILE))) {
                    
                    out.print(response.asFormat(outputFormat));
                }
            }
            else {
                System.out.print(response.asFormat(outputFormat));
            }
        }
        catch (Exception e) {
            LOG.error("Error producing output.", e);
            System.exit(8);
        }
    }

    /**
     * Method to simplify testing of the CLI parser.
     * 
     * @param args
     * @return
     * @throws IOException
     */
    protected static OptionSet parseArgs(final String[] args)
            throws IOException {

        final OptionParser parser = new OptionParser() {
            {
                accepts(CSS, "Enable CSS support");
                accepts(DO_NOT_TRACK, "Enable Do Not Track support");
                accepts(JAVASCRIPT, "Enable Javascript");
                accepts(NO_REDIRECT, "Disable redirection");
                accepts(INSECURE_SSL, "Ignore server certificates");
                accepts(NO_COOKIES, "Disable cookie support");
                accepts(INCLUDE_HEADERS, "Include HTTP response headers");
                accepts(INCLUDE_METADATA, "Include content metadata");
                accepts(OUTPUT_FORMAT, "One of PLAIN, JSON")
                        .withRequiredArg().ofType(OutputFormat.class);
                accepts(OUTPUT_FILE, "File to receive output")
                        .withRequiredArg().ofType(File.class)
                        .describedAs("file");
                accepts(HTTP_TIMEOUT, "HTTP transaction timeout")
                        .withRequiredArg().ofType(Integer.class)
                        .describedAs("seconds");
                accepts(MAX_LENGTH, "Maximum Content-Length").withRequiredArg()
                        .ofType(Long.class);

                // following are non-operational options
                acceptsAll(asList("h", "?", "help"), "show help").forHelp();
                accepts(VERSION, "Print version to stdout");
            }
        };
        parser.posixlyCorrect(true);

        // parse the options
        final OptionSet options = parser.parse(args);

        // deal with the non-operational options
        if (options.has(HELP)) {
            parser.printHelpOn(System.out);
            System.exit(0);
        }
        if (options.has(VERSION)) {
            System.out.println(APP_VERSION);
            System.exit(0);
        }

        return options;
    }
}
