package Server.Public;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlipayService {

    String orderId;
    String subject;
    String amount;

    AlipayService(String a, String b, String c) {
        orderId = a;
        subject = b;
        amount = c;
        Factory.setOptions(AlipayConfig.getOptions());
    }

    /**
     * 生成收款二维码并返回字节数组
     *
     * @return byte[] 包含二维码图像的字节数组
     */
    public byte[] generatePaymentQRCode() {
        try {
            // 调用支付宝当面付接口生成二维码
            AlipayTradePrecreateResponse response = Factory.Payment.FaceToFace().preCreate(subject, orderId, amount);

            if (ResponseChecker.success(response)) {
                // 输出二维码的 URL
                System.out.println("调用成功，生成的二维码 URL：" + response.qrCode);

                // 将二维码 URL 生成二维码图像并转换为字节数组
                byte[] qrCodeImage = generateQRCodeImage(response.qrCode, 300, 300);
                System.out.println("二维码已成功生成，并转换为字节数组");

                return qrCodeImage;
            } else {
                // 处理失败情况
                System.err.println("调用失败，原因：" + response.msg + "，" + response.subMsg);
                return null; // 返回 null 表示失败
            }
        } catch (Exception e) {
            // 处理异常情况
            e.printStackTrace();
            System.err.println("调用遭遇异常，原因：" + (e.getMessage() != null ? e.getMessage() : "未知错误"));
            return null; // 返回 null 表示异常
        }
    }

    /**
     * 生成二维码图像并转换为字节数组
     *
     * @param text   要编码的文本（这里是收款二维码的 URL）
     * @param width  二维码的宽度
     * @param height 二维码的高度
     * @return 二维码的字节数组
     * @throws Exception 如果生成二维码失败
     */
    public byte[] generateQRCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        // 将二维码图像转换为 BufferedImage
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 使用 ByteArrayOutputStream 将图像转换为字节数组
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }


    // 定时任务的线程池
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    /**
     * 开始轮询查询支付状态
     *
     * @param orderId 订单号
     * @param maxAttempts 最大轮询次数
     * @param intervalSeconds 每次查询间隔（秒）
     */
    public static void startPollingPaymentStatus(String orderId, int maxAttempts, int intervalSeconds) {
        final int[] attemptCount = {0}; // 使用数组来更新值，因为lambda表达式不允许直接修改局部变量

        // 定时任务
        Runnable pollTask = () -> {
            attemptCount[0]++;
            System.out.println("查询第 " + attemptCount[0] + " 次");

            boolean isPaid = isPaymentSuccessful(orderId);

            if (isPaid) {
                System.out.println("支付成功，停止查询。");
                Global.setVerificationSuccess(true);
                scheduler.shutdown(); // 支付成功，停止轮询
            } else if (attemptCount[0] >= maxAttempts) {
                System.out.println("支付未成功，达到最大查询次数，停止查询。");
                scheduler.shutdown(); // 超过最大次数，停止轮询
            }
        };

        // 启动定时任务
        scheduler.scheduleAtFixedRate(pollTask, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    // Example usage
//    public static void main(String[] args) {
//        AlipayService alipayService = new AlipayService("order123", "test subject", "100.00");
//        int maxAttempts = 10; // 最大查询次数
//        int intervalSeconds = 5; // 查询间隔为5秒
//
//        alipayService.startPollingPaymentStatus("order123", maxAttempts, intervalSeconds);
//    }

    // 订单支付状态查询
    public static boolean isPaymentSuccessful(String orderId) {
        // 使用AlipayTradeQuery进行查询
        try {
            AlipayTradeQueryResponse response = Factory.Payment.Common().query(orderId);
            if (ResponseChecker.success(response) && "TRADE_SUCCESS".equals(response.tradeStatus)) {
                return true; // 支付成功
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // 支付未完成或失败
    }


}
