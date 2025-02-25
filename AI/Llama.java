package Server.AI;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.json.JSONObject;

import javax.swing.*;

public class Llama {
    private final HttpClient client;

    public Llama() {
        this.client = HttpClient.newHttpClient();
    }

    // 原来的 sendMessage 方法
    public void sendMessage(String userInput, java.util.function.Consumer<String> onResponse) {
        String jsonPayload = String.format("""
                {
                  "model": "llama3.1",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """, userInput);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        StringBuilder fullResponse = new StringBuilder(); // 用于存储完整的响应内容
        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAcceptAsync(response -> {
                    response.body().forEach(line -> {
                        JSONObject json = new JSONObject(line);
                        if (json.getBoolean("done")) {
                            fullResponse.append("\n\n"); // 响应结束时换两行
                            onResponse.accept(fullResponse.toString());
                            return;
                        }
                        String content = json.getJSONObject("message").getString("content");
                        fullResponse.append(content);
                        onResponse.accept(fullResponse.toString()); // 传递完整的累积内容
                    });
                });
    }

    public void generateSummary(String article, java.util.function.Consumer<String> onResponse) {
        String jsonPayload = String.format("""
            {
              "model": "llama3.1",
              "task": "summarization",
              "messages": [
                {
                  "role": "user",
                  "content": "请为以下文章生成一个简短的摘要，两句话(直接写出语句，不要有任何无关的话)：\\n\\n%s"
                }
              ]
            }
            """, article);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        System.out.println("Sending request to Llama model...");

        StringBuilder fullResponse = new StringBuilder(); // 用于存储完整的响应内容
        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAcceptAsync(response -> {
                    System.out.println("Received response with status code: " + response.statusCode());
                    response.body().forEach(line -> {
                        try {
                            //System.out.println("Processing line: " + line); // 输出每一行响应
                            JSONObject json = new JSONObject(line);
                            // 检查 "done" 字段是否存在并为 true
                            if (json.has("done") && json.getBoolean("done")) {
                                System.out.println("Response completed. Final summary: " + fullResponse);
                                // 在接收到 "done": true 时，完整摘要已经生成，调用回调一次
                                SwingUtilities.invokeLater(() -> onResponse.accept(fullResponse.toString()));
                                return;
                            }
                            // 检查 "content" 字段是否存在
                            if (json.has("message") && json.getJSONObject("message").has("content")) {
                                String content = json.getJSONObject("message").getString("content");
                                fullResponse.append(content);
                            }
                        } catch (Exception e) {
                            System.out.println("Error processing response line: " + e.getMessage());
                        }
                    });
                })
                .exceptionally(ex -> {
                    // 捕获并处理异常
                    System.out.println("Request failed: " + ex.getMessage());
                    return null;
                });
    }


    public void makePolite(String text, java.util.function.Consumer<String> onResponse) {
        String jsonPayload = String.format("""
            {
              "model": "llama3.1",
              "task": "politeness",
              "messages": [
                {
                  "role": "user",
                  "content": "请将以下句子变得更礼貌(直接写出语句，不要有任何无关的话和标点)：\\n\\n%s"
                }
              ]
            }
            """, text);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAcceptAsync(response -> {
                    StringBuilder fullResponse = new StringBuilder();
                    response.body().forEach(line -> {
                        try {
                            JSONObject json = new JSONObject(line);
                            if (json.has("done") && json.getBoolean("done")) {
                                SwingUtilities.invokeLater(() -> onResponse.accept(fullResponse.toString()));
                                return;
                            }
                            if (json.has("message") && json.getJSONObject("message").has("content")) {
                                String content = json.getJSONObject("message").getString("content");
                                fullResponse.append(content);
                            }
                        } catch (Exception e) {
                            System.out.println("Error processing response line: " + e.getMessage());
                        }
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("Request failed: " + ex.getMessage());
                    return null;
                });
    }

    // New method to make speech more concise
    public void makeConcise(String text, java.util.function.Consumer<String> onResponse) {
        String jsonPayload = String.format("""
            {
              "model": "llama3.1",
              "task": "conciseness",
              "messages": [
                {
                  "role": "user",
                  "content": "请将以下句子变得更简洁(直接写出语句，不要有任何无关的话和标点)：\\n\\n%s"
                }
              ]
            }
            """, text);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAcceptAsync(response -> {
                    StringBuilder fullResponse = new StringBuilder();
                    response.body().forEach(line -> {
                        try {
                            JSONObject json = new JSONObject(line);
                            if (json.has("done") && json.getBoolean("done")) {
                                SwingUtilities.invokeLater(() -> onResponse.accept(fullResponse.toString()));
                                return;
                            }
                            if (json.has("message") && json.getJSONObject("message").has("content")) {
                                String content = json.getJSONObject("message").getString("content");
                                fullResponse.append(content);
                            }
                        } catch (Exception e) {
                            System.out.println("Error processing response line: " + e.getMessage());
                        }
                    });
                })
                .exceptionally(ex -> {
                    System.out.println("Request failed: " + ex.getMessage());
                    return null;
                });
    }




//    public static void main(String[] args) {
//        Llama llama = new Llama();
//
//        String articleContent = "人工智能（AI）是一门通过计算机模拟人类智能的技术，涵盖了机器学习、自然语言处理、计算机视觉、机器人学等多个领域。其核心在于算法和数据，通过大量的数据训练模型，使计算机能够自主进行复杂的任务和决策，例如模式识别、预测分析和自动化控制。人工智能的发展历程可以追溯到上世纪中叶，从早期的规则基础系统到现在的深度学习和强化学习，AI技术在不断进步，逐渐具备了更强的感知、理解和决策能力。目前，人工智能已在多个行业展现出巨大潜力。例如，在医疗领域，AI用于疾病诊断、个性化治疗方案制定以及手术机器人辅助；在金融领域，AI帮助进行风险评估、欺诈检测和市场分析；在制造业中，人工智能助力实现智能化生产和预测性维护。此外，智能语音助手、自动驾驶汽车和个性化推荐系统等应用，更是将AI带入了日常生活中。尽管人工智能带来了显著的效率提升和创新机会，但其快速发展也引发了许多关注和讨论。隐私保护、数据安全、算法偏见以及AI对就业市场的影响等问题，成为社会各界关注的焦点。尤其是在深度学习模型的“黑箱性”导致决策过程不透明的情况下，如何确保AI系统的公平性和可解释性，是一个亟待解决的挑战。未来，随着技术的进一步发展，人工智能可能会在解决全球性问题如气候变化、疾病防控和可持续发展方面发挥更大的作用。然而，为了实现这一愿景，我们需要在技术创新与伦理规范之间找到平衡，确保人工智能的发展能够真正造福全人类。这需要各方的共同努力，包括政府、企业、研究机构以及公众的广泛参与，以构建一个负责任且可持续的AI未来。";
//
//        // 使用 CountDownLatch 来确保主线程等待异步任务完成
//        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
//
//        // 调用 Llama 的 generateSummary 方法生成文章摘要
//        llama.generateSummary(articleContent, summary -> {
//            // 使用 SwingUtilities.invokeLater 确保在事件派发线程中更新 UI 或输出
//            SwingUtilities.invokeLater(() -> {
//                System.out.println("生成的摘要：");
//                System.out.println(summary);
//                latch.countDown(); // 减少计数以释放主线程
//            });
//        });
//
//        try {
//            // 主线程等待，直到 latch 计数变为 0
//            latch.await();
//        } catch (InterruptedException e) {
//            System.out.println("主线程被中断：" + e.getMessage());
//        }
//
//    }

    public static void main(String[] args) {
        Llama llama = new Llama();

        // Example input text
        String politeInput = "你把报告发给我。";
        String conciseInput = "我认为我们可以按照原计划继续进行下去，但是如果你有其他意见的话，可以提出来。";

        // Using CountDownLatch to ensure main thread waits for all async tasks
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(2);

        // Test makePolite method
        llama.makePolite(politeInput, politeResponse -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("原句：" + politeInput);
                System.out.println("礼貌版本：" + politeResponse);
                latch.countDown(); // Reduce count after polite response
            });
        });

        // Test makeConcise method
        llama.makeConcise(conciseInput, conciseResponse -> {
            SwingUtilities.invokeLater(() -> {
                System.out.println("原句：" + conciseInput);
                System.out.println("简洁版本：" + conciseResponse);
                latch.countDown(); // Reduce count after concise response
            });
        });

        try {
            // Main thread waits until both tasks are completed
            latch.await();
        } catch (InterruptedException e) {
            System.out.println("主线程被中断：" + e.getMessage());
        }
    }



}
