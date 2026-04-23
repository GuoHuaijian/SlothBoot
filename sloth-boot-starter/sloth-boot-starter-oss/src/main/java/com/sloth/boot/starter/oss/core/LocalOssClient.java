package com.sloth.boot.starter.oss.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.sloth.boot.starter.oss.config.OssProperties;
import com.sloth.boot.starter.oss.model.OssFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * 本地文件系统 OSS 实现。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
public class LocalOssClient implements OssClient {

    private final OssProperties ossProperties;

    /**
     * 构造函数。
     *
     * @param ossProperties OSS 配置
     */
    public LocalOssClient(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Override
    public String upload(String path, InputStream inputStream) {
        File file = new File(resolveBaseDir(), path);
        FileUtil.mkParentDirs(file);
        FileUtil.writeFromStream(inputStream, file);
        return buildUrl(path);
    }

    @Override
    public void download(String path, OutputStream outputStream) {
        File file = new File(resolveBaseDir(), path);
        IoUtil.copy(FileUtil.getInputStream(file), outputStream);
    }

    @Override
    public void delete(String path) {
        FileUtil.del(new File(resolveBaseDir(), path));
    }

    @Override
    public String getPresignedUrl(String path, int expireMinutes) {
        return buildUrl(path);
    }

    @Override
    public List<OssFile> listFiles(String prefix) {
        File baseDir = prefix == null || prefix.isBlank()
                ? resolveBaseDir()
                : new File(resolveBaseDir(), prefix);
        List<OssFile> result = new ArrayList<>();
        if (!baseDir.exists()) {
            return result;
        }
        for (File file : FileUtil.loopFiles(baseDir)) {
            OssFile ossFile = new OssFile();
            ossFile.setName(file.getName());
            String relativePath = resolveBaseDir().toPath().relativize(file.toPath()).toString();
            ossFile.setPath(relativePath);
            ossFile.setUrl(buildUrl(relativePath));
            ossFile.setSize(file.length());
            ossFile.setLastModified(LocalDateTime.ofInstant(file.toPath().toFile().lastModified() == 0
                    ? java.time.Instant.now()
                    : java.time.Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault()));
            result.add(ossFile);
        }
        return result;
    }

    private File resolveBaseDir() {
        String endpoint = ossProperties.getEndpoint();
        return endpoint == null || endpoint.isBlank() ? new File("./data/oss") : new File(endpoint);
    }

    private String buildUrl(String path) {
        String domain = ossProperties.getDomain();
        if (domain == null || domain.isBlank()) {
            return new File(resolveBaseDir(), path).toURI().toString();
        }
        return domain.endsWith("/") ? domain + path : domain + "/" + path;
    }
}
