package org.jcodec.movtool.streaming.tracks;

import org.jcodec.common.SeekableByteChannel;

import java.io.IOException;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * A pool of open data channels used to read data
 * 
 * @author The JCodec project
 * 
 */
public interface ByteChannelPool {

    SeekableByteChannel getChannel() throws IOException;

    void close();

}
