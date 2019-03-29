package com.creditease.ns.dispatcher.netty.ext;


public class ExtSSLConstants{
	private static final int MAX_PLAINTEXT_LENGTH = 16 * 1024; // 2^14
	private static final int MAX_COMPRESSED_LENGTH = MAX_PLAINTEXT_LENGTH + 1024;
	private static final int MAX_CIPHERTEXT_LENGTH = MAX_COMPRESSED_LENGTH + 1024;

	
    static final int MAX_ENCRYPTED_PACKET_LENGTH = MAX_CIPHERTEXT_LENGTH + 5 + 20 + 256;

    static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = MAX_ENCRYPTED_PACKET_LENGTH - MAX_PLAINTEXT_LENGTH;
}
