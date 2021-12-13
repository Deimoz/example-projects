package info.kgeorgiy.ja.vircev.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService extractors, downloaders;
    public static final int BASE_DEPTH = 1;
    public static final int BASE_DOWNLOADS = 10;
    public static final int BASE_EXTRACTORS = 10;
    public static final int BASE_PERHOST = 10;
    public static final int WAIT_TIME = 10;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.downloaders = Executors.newFixedThreadPool(downloaders);
    }

    @Override
    public Result download(String url, int depth) {
        Set<String> visitedLinks = ConcurrentHashMap.newKeySet();
        visitedLinks.add(url);
        Set<String> downloadedLinks = ConcurrentHashMap.newKeySet();
        ConcurrentHashMap<String, IOException> errors = new ConcurrentHashMap<>();
        Phaser phaser = new Phaser(1);
        downloadLinks(url, depth, phaser, visitedLinks, downloadedLinks, errors);
        phaser.arriveAndAwaitAdvance();
        return new Result(new ArrayList<>(downloadedLinks), errors);
    }

    private void downloadLinks(String url, int depth, Phaser phaser,
                               Set<String> visitedLinks,
                               Set<String> downloadedLinks,
                               ConcurrentMap<String, IOException> errors) {
        Runnable downloadTask = () -> {
            try {
                Document document = downloader.download(url);
                downloadedLinks.add(url);
                if (depth > 1) {
                    Runnable extractorTask = () -> {
                        try {
                            List<String> shouldVisit = new ArrayList<>();
                            for (String newUrl : document.extractLinks()) {
                                if (visitedLinks.add(newUrl)) {
                                    shouldVisit.add(newUrl);
                                }
                            }
                            for (String newUrl : shouldVisit) {
                                downloadLinks(newUrl, depth - 1, phaser, visitedLinks, downloadedLinks, errors);
                            }
                        } catch (IOException e) {
                            errors.put(url, e);
                        }
                        phaser.arrive();
                    };

                    phaser.register();
                    extractors.submit(extractorTask);
                }
            } catch (IOException e) {
                errors.put(url, e);
            }
            phaser.arrive();
        };

        phaser.register();
        downloaders.submit(downloadTask);
    }

    @Override
    public void close() {
        downloaders.shutdown();
        try {
            downloaders.awaitTermination(WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
        extractors.shutdown();
        try {
            extractors.awaitTermination(WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
    }

    public static int getParameter(String[] args, int i, int baseValue) {
        return args.length > i ? Integer.parseInt(args[i]) : baseValue;
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.err.println("Should be \"WebCrawler url [depth [downloads [extractors [perHost]]]]\"");
            return;
        }
        String url = args[0];
        int depth = getParameter(args, 1, BASE_DEPTH);
        int downloads = getParameter(args, 2, BASE_DOWNLOADS);
        int extractors = getParameter(args, 3, BASE_EXTRACTORS);
        int perHost = getParameter(args, 4, BASE_PERHOST);
        try {
            WebCrawler crawler = new WebCrawler(new CachingDownloader(), downloads, extractors, perHost);
            crawler.download(url, depth);
        } catch (IOException e) {
            System.out.println("Error while creating downloader: " + e.getMessage());
        }
    }
}
