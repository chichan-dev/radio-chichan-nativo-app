package org.jcodec.containers.mkv.elements;

import org.jcodec.containers.mkv.Reader;
import org.jcodec.containers.mkv.Type;
import org.jcodec.containers.mkv.ebml.MasterElement;

import java.util.Arrays;

public class TrackEntry extends MasterElement {

    public TrackEntry(byte[] type) {
        super(type);
        if (!Arrays.equals(Type.TrackEntry.id, type))
            throw new IllegalArgumentException(Reader.printAsHex(type));
    }

}
