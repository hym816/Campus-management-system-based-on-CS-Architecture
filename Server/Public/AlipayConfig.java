package Server.Public;

import com.alipay.easysdk.kernel.Config;

public class AlipayConfig {

    public static Config getOptions() {
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com"; // 使用沙箱环境地址
        config.signType = "RSA2";
        config.appId = "9021000140662704"; // 替换为你的应用 App ID
        config.merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCd0+sGXPEHDQb8hThE1VBGfDzykPFbj0WHPRPKlJnRW4Xq8WBI7lHK1N8tKt3CJG0qfsDvON9yDo89Sjl8ngiDdfAVHLT3xR1ey2RdCljTibkrUtwB3PEmLf6cY+n6ejjygDUeTvZehVs1NcuPtHYQTrNlxJOSwThIG5BVVJgFaZNJCV6lBoXjMhjsrmx2lMfsrjI3/ZqbfDgALsqZhEXN0JnWr2ufqYuN2Cp2gqhH0aFLGtlw/a/cAjGVx8bQ3hNr+ZdcdBnwsGfgr+XCXdinN27soWZG/XwfWLAeYvpfwDOVaPZwrB22667rWX5sXk9iMB4ww59uQPpM4OFMmiV/AgMBAAECggEAJr70zs4YWevH0EyAQN3BZFxxtfcBWcH1hrrm1e/fRzdZzBDP23euWsYht/vzDwtURX9yrAfhSyXp4lkLfd1qJ6PXecdfYghhKnlyPSkiH2SLMeiYnFh6Vy4peIlK0rQYsOfqonMuJoxElO6M982Mwnofrpcwx3Fp+MHTkC9BVupuSImua1PoTOTPE03Q6oLUmVAItdV2chXUQdqcbvUcEI0OAfxfZn1OBR0hO2vPuk06dNbSCOf0ZMmLosaxtdyHlXKjJxKLguqz/LBqonPTwwrSiJ1snI4asqHZIhdljpU201jto9ztp+7L6liXTR1ZCbzJjsfdZgPS/pPc7C4J0QKBgQDYaO0N3EN7uonRzxT7k8OBGtox9KPTMMaq4nhwGhFv3SdNYvJ6Em2ITI8iMN77CEXB7NiCKxG8LyuVd5/9UC9311bbXKtD1caZwrM2wNTrQK2j+ag+DyOYEm74ACan74T4FQ0ciwd0F2po6yQ5JID4JHe5soTbQHc/+wgL4R73NwKBgQC6s26D2BRWYMaCl0rNiI49YcDgAzmBim6tDjNhpiL90da/ttzS9MJgjah2xMFtypj8yZf6g3ObxKy5W6Xo5zIsO3rl+nFs2/K3lImC1niVNIj43NtiEptayUO35XR6sd6zWo6X1fotuPTHAkjQyA2dI5CHkaaFtlY99wrZ+EhX+QKBgCIZzpjrh3q8tzDnd7/41crHR3Ctww7pVbThwOaxROQirinEmL7hnn7myXsyxkA6Wnunbap9TOshZ7XCxuQsuX4h4Z7paFdlHUlnY8QR+LslUvdOxNMaoZo59WCA7ZZy6LoK3ykLQ15ovizQOMIEXYo8fN03IzZFX0Umsmrky3NbAoGAKC9ayDIIHpdx4sXhqd+MjhWuSAkppYJAVCtMT+ZMfcleqaEYYqef9txGuFmdMJ5801woxBjrF0n7y2G4kirBF9gtmbaQF1g4I99qwaicB7FpEfenmLJckevkEX7n1UjXoE8Lhg7ebFz1AqixN473ryzqfOOSrhES+v9jbIsONzECgYEAi0ZCD/2TGFY9+uFGb8KW9p8WBDCecmJ5xXLkFPCW/eguX3ta/R4KAbWmBrABacJcHA05VFoaLkOkV0LlIJehMs6657jHL85rY/6mL7KRkMz+F8xaVqaFzcmsKpEb13p6/DSZVHMK4B/gx0gxp4ZYI6zTm9kCAjfGk4WSrZqYcBg=";
        config.alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvaFZqhmMR1kJNLM6gX6R+cIZ2Sna+ulsyeldN1tJE3Xe+3hC+YiLYZz2d9Lg0/KXevo937MwY98eTCCg2n+m/SSX6NX9yfG+lD19561gkZ+Pt2eUEYsG+2z9Pi0XONrZKH7q7/BmelAkY2Nx5u9nRRG9QiwyPMF4n56Ghb3DRecyI6+SFOxh0HAMLsWbBX8VZ5dM8SRnj/OmblTh4Bujf6CvvyUs6TgRLVMwbslJZ50ZeXzXjnIK+j+0Vc4Lx9DtfEoCnPuJulnlz3MhZHC44mKaz7lqjwdKkDNlUgvQ+FElXbAYd4psnLqeLG5XEhe4xhUVf/79n8RMzmxsUPkfUwIDAQAB";
        config.notifyUrl = "http://10.208.95.74:8080/notify";
        return config;
    }
}
