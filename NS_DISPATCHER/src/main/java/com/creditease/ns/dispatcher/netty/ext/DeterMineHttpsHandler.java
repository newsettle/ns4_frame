package com.creditease.ns.dispatcher.netty.ext;

import com.creditease.ns.log.NsLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DeterMineHttpsHandler  extends  ChannelInboundHandlerAdapter {
	private static NsLog httpsLog = NsLog.getFramLog("Dispatcher", "DeterMineHttpsHandler");

	private int packetLength;
	private boolean isHttps;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ByteBuf) {
			ByteBuf in = (ByteBuf)msg;
			ByteBuf buffer = in.copy();
			final int startOffset = buffer.readerIndex();
			final int endOffset = buffer.writerIndex();
			int offset = startOffset;
			int totalLength = 0;
			
			boolean nonSslRecord = false;
			
			while (totalLength < ExtSSLConstants.MAX_ENCRYPTED_PACKET_LENGTH) {
				final int readableBytes = endOffset - offset;
				if (readableBytes < 5) {
					break;
				}
				
				final int packetLength = getEncryptedPacketLength(buffer, offset);
				if (packetLength == -1) {
					nonSslRecord = true;
					break;
				}
				
				assert packetLength > 0;
				
				if (packetLength > readableBytes) {
					// wait until the whole packet can be read
					this.packetLength = packetLength;
					break;
				}
				
				int newTotalLength = totalLength + packetLength;
				if (newTotalLength > ExtSSLConstants.MAX_ENCRYPTED_PACKET_LENGTH) {
					// Don't read too much.
					break;
				}
				
				// We have a whole packet.
				// Increment the offset to handle the next packet.
				offset += packetLength;
				totalLength = newTotalLength;
			}
			
			if (nonSslRecord) {
				isHttps = false;
			} else {
				isHttps = true;
			}
			
			if (buffer != null) {
				buffer.release();
			}
		}
		
		ctx.fireChannelRead(msg);
	}


	private static int getEncryptedPacketLength(ByteBuf buffer, int offset) {
		int packetLength = 0;

		// SSLv3 or TLS - Check ContentType
		boolean tls;
		switch (buffer.getUnsignedByte(offset)) {
		case 20:  // change_cipher_spec
		case 21:  // alert
		case 22:  // handshake
		case 23:  // application_data
			tls = true;
			break;
		default:
			// SSLv2 or bad data
			tls = false;
		}

		if (tls) {
			// SSLv3 or TLS - Check ProtocolVersion
			int majorVersion = buffer.getUnsignedByte(offset + 1);
			if (majorVersion == 3) {
				// SSLv3 or TLS
				packetLength = buffer.getUnsignedShort(offset + 3) + 5;
				if (packetLength <= 5) {
					// Neither SSLv3 or TLSv1 (i.e. SSLv2 or bad data)
					tls = false;
				}
			} else {
				// Neither SSLv3 or TLSv1 (i.e. SSLv2 or bad data)
				tls = false;
			}
		}

		if (!tls) {
			// SSLv2 or bad data - Check the version
			boolean sslv2 = true;
			int headerLength = (buffer.getUnsignedByte(offset) & 0x80) != 0 ? 2 : 3;
			int majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
			if (majorVersion == 2 || majorVersion == 3) {
				// SSLv2
				if (headerLength == 2) {
					packetLength = (buffer.getShort(offset) & 0x7FFF) + 2;
				} else {
					packetLength = (buffer.getShort(offset) & 0x3FFF) + 3;
				}
				if (packetLength <= headerLength) {
					sslv2 = false;
				}
			} else {
				sslv2 = false;
			}

			if (!sslv2) {
				return -1;
			}
		}
		return packetLength;
	}


	public boolean isHttps() {
		return isHttps;
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		httpsLog.error("出现异常",cause);
	}
}
