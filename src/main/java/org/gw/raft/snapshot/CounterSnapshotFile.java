package org.gw.raft.snapshot;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author guanwu
 * @created on 2022-07-13 14:16:12
 **/
public class CounterSnapshotFile {
    private static final Logger LOG = LoggerFactory.getLogger(CounterSnapshotFile.class);


    private String              path;

    public CounterSnapshotFile(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public boolean save(final long value) {
        try {
            FileUtils.writeStringToFile(new File(path), String.valueOf(value), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            LOG.error("Fail to save snapshot", e);
            return false;
        }
    }

    public long load() throws IOException {
        final String s = FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
        if (!StringUtils.isBlank(s)) {
            return Long.parseLong(s);
        }
        throw new IOException("Fail to load snapshot from " + path + ",content: " + s);
    }
}
