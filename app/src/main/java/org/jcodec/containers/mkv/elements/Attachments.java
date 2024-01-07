package org.jcodec.containers.mkv.elements;

import org.jcodec.containers.mkv.Reader;
import org.jcodec.containers.mkv.Type;
import org.jcodec.containers.mkv.ebml.MasterElement;

import java.util.Arrays;

public class Attachments extends MasterElement {

    public Attachments(byte[] typeId) {
        super(typeId);
        if (!Arrays.equals(Type.Attachments.id, typeId))
            throw new IllegalArgumentException(Reader.printAsHex(typeId));
    }
    
}
