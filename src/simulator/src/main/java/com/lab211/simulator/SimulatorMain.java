package com.lab211.simulator;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SimulatorMain {

    private record Config(String server, int users, int orders, int concurrency, List<Integer> variants,
                         String email, String password, String voucher) {}

    public static void main(String[] args) throws Exception {
        Config cfg = parseArgs(args);
        System.out.println("Server: " + cfg.server);
        System.out.println("Users: " + cfg.users + " Orders: " + cfg.orders + " Concurrency: " + cfg.concurrency);
        System.out.println("Variants: " + cfg.variants);

        CookieManager cookies = new CookieManager();
        cookies.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .cookieHandler(cookies)
                .build();

        login(client, cfg);

        // Warmup health
        try {
            HttpRequest health = HttpRequest.newBuilder(URI.create(cfg.server + "/api/health")).GET().build();
            client.send(health, HttpResponse.BodyHandlers.discarding());
            System.out.println("Health ok");
        } catch (Exception ex) {
            System.err.println("Health check failed: " + ex.getMessage());
        }

        ExecutorService pool = Executors.newFixedThreadPool(cfg.concurrency);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        AtomicLong totalNanos = new AtomicLong();
        Random rnd = new Random();

        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < cfg.orders; i++) {
            final int idx = i;
            tasks.add(CompletableFuture.runAsync(() -> {
                String body = buildOrderPayload(cfg, rnd);
                HttpRequest req = HttpRequest.newBuilder(URI.create(cfg.server + "/api/orders"))
                        .timeout(Duration.ofSeconds(5))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                long start = System.nanoTime();
                try {
                    HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                    long dur = System.nanoTime() - start;
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        success.incrementAndGet();
                        totalNanos.addAndGet(dur);
                    } else {
                        fail.incrementAndGet();
                        System.err.println("Fail " + resp.statusCode() + ": " + resp.body());
                    }
                } catch (Exception ex) {
                    fail.incrementAndGet();
                    System.err.println("Error: " + ex.getMessage());
                }
            }, pool));
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
        pool.shutdown();

        int ok = success.get();
        int ko = fail.get();
        double avgMs = ok == 0 ? 0 : (totalNanos.get() / 1_000_000.0) / ok;
        System.out.println("Done. Success=" + ok + " Fail=" + ko + " Avg ms=" + String.format("%.2f", avgMs));
    }

    private static String buildOrderPayload(Config cfg, Random rnd) {
        int variantId = cfg.variants.get(rnd.nextInt(cfg.variants.size()));
        int qty = rnd.nextInt(3) + 1;
        String voucherFragment = cfg.voucher == null ? "" : """
              ,"voucherCode":"%s"
            """.formatted(cfg.voucher);
        return """
            {
              "items":[{"variantId":%d,"quantity":%d}]%s
            }
            """.formatted(variantId, qty, voucherFragment);
    }

    private static Config parseArgs(String[] args) {
        String server = "http://localhost:8080/ecommerce-sim";
        int users = 10;
        int orders = 100;
        int concurrency = 10;
        List<Integer> variants = new ArrayList<>(List.of(1));
        String email = "user@local";
        String password = "123456";
        String voucher = null;

        for (String a : args) {
            if (a.startsWith("--server=")) server = a.substring("--server=".length());
            else if (a.startsWith("--users=")) users = Integer.parseInt(a.substring("--users=".length()));
            else if (a.startsWith("--orders=")) orders = Integer.parseInt(a.substring("--orders=".length()));
            else if (a.startsWith("--concurrency=")) concurrency = Integer.parseInt(a.substring("--concurrency=".length()));
            else if (a.startsWith("--variants=")) {
                variants = Arrays.stream(a.substring("--variants=".length()).split(","))
                        .map(String::trim).filter(s -> !s.isEmpty()).map(Integer::parseInt).toList();
            } else if (a.startsWith("--email=")) {
                email = a.substring("--email=".length());
            } else if (a.startsWith("--password=")) {
                password = a.substring("--password=".length());
            } else if (a.startsWith("--voucher=")) {
                voucher = a.substring("--voucher=".length());
            }
        }
        if (variants.isEmpty()) variants = List.of(1);
        return new Config(server, users, orders, concurrency, variants, email, password, voucher);
    }

    private static void login(HttpClient client, Config cfg) throws Exception {
        String form = "email=" + URLEncoder.encode(cfg.email, "UTF-8") +
                "&password=" + URLEncoder.encode(cfg.password, "UTF-8");
        HttpRequest req = HttpRequest.newBuilder(URI.create(cfg.server + "/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();
        HttpResponse<Void> resp = client.send(req, HttpResponse.BodyHandlers.discarding());
        int code = resp.statusCode();
        if (code != 200 && code != 302) {
            throw new IllegalStateException("Login failed, status=" + code);
        }
        System.out.println("Login ok (" + code + ")");
    }
}
