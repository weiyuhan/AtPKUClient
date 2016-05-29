package atpku.client;

import android.app.Application;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;

/**
 * Created by wyh on 2016/5/29.
 */
public class AtPKUApplication extends Application
{
    public OSS oss;
    @Override
    public void onCreate() {
        super.onCreate();

        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";

// 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("bymTgYKiXlNuD7vJ", "Lu79QyOYksYyQikGMRPFTOk5dKsHji");
        oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider);
    }
}
