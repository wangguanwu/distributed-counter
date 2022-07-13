package org.gw.raft.remote.serialize;

import com.alipay.remoting.serialization.HessianSerializer;
import com.alipay.remoting.serialization.Serializer;

/**
 * @author guanwu
 * @created on 2022-07-12 17:13:58
 **/
public class SerializerManager {
    private static Serializer[] serializers = new Serializer[5];
    public static final byte Hessian2 = 1;

    public SerializerManager() {
    }

    public static Serializer getSerializer(int idx) {
        return serializers[idx];
    }

    public static void addSerializer(int idx, Serializer serializer) {
        if (serializers.length <= idx) {
            Serializer[] newSerializers = new Serializer[idx + 5];
            System.arraycopy(serializers, 0, newSerializers, 0, serializers.length);
            serializers = newSerializers;
        }

        serializers[idx] = serializer;
    }

    static {
        addSerializer(1, new HessianSerializer());
    }
}

