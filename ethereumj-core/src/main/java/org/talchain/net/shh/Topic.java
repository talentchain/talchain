package org.talchain.net.shh;

import org.talchain.util.RLP;
import org.spongycastle.util.encoders.Hex;
import org.talchain.crypto.HashUtil;

import java.util.Arrays;

import static org.talchain.crypto.HashUtil.sha3;

/**
 * @author by Konstantin Shabalin
 */
public class Topic {
    private String originalTopic;
    private byte[] fullTopic;
    private byte[] abrigedTopic = new byte[4];

    public Topic(byte[] data) {
        this.abrigedTopic = data;
    }

    public Topic(String data) {
        originalTopic = data;
        fullTopic = HashUtil.sha3(RLP.encode(originalTopic));
        this.abrigedTopic = buildAbrigedTopic(fullTopic);
    }

    public byte[] getBytes() {
        return abrigedTopic;
    }

    private byte[] buildAbrigedTopic(byte[] data) {
        byte[] hash = HashUtil.sha3(data);
        byte[] topic = new byte[4];
        System.arraycopy(hash, 0, topic, 0, 4);
        return topic;
    }

    public static Topic[] createTopics(String ... topicsString) {
        if (topicsString == null) return new Topic[0];
        Topic[] topics = new Topic[topicsString.length];
        for (int i = 0; i < topicsString.length; i++) {
            topics[i] = new Topic(topicsString[i]);
        }
        return topics;
    }

    public byte[] getAbrigedTopic() {
        return abrigedTopic;
    }

    public byte[] getFullTopic() {
        return fullTopic;
    }

    public String getOriginalTopic() {
        return originalTopic;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Topic))return false;
        return Arrays.equals(this.abrigedTopic, ((Topic) obj).getBytes());
    }

    @Override
    public String toString() {
        return "#" + Hex.toHexString(abrigedTopic) + (originalTopic == null ? "" : "(" + originalTopic + ")");
    }
}
