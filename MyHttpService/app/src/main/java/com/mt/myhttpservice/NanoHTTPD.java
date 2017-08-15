//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mt.myhttpservice;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

public abstract class NanoHTTPD {
    public static final int SOCKET_READ_TIMEOUT = 5000;
    public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_HTML = "text/html";
    private static final String QUERY_STRING_PARAMETER = "NanoHttpd.QUERY_STRING";
    private final String hostname;
    private final int myPort;
    private ServerSocket myServerSocket;
    private Set<Socket> openConnections;
    private Thread myThread;
    private NanoHTTPD.AsyncRunner asyncRunner;
    private NanoHTTPD.TempFileManagerFactory tempFileManagerFactory;

    public NanoHTTPD(int port) {
        this((String)null, port);
    }

    public NanoHTTPD(String hostname, int port) {
        this.openConnections = new HashSet();
        this.hostname = hostname;
        this.myPort = port;
        this.setTempFileManagerFactory(new NanoHTTPD.DefaultTempFileManagerFactory());
        this.setAsyncRunner(new NanoHTTPD.DefaultAsyncRunner());
    }

    private static final void safeClose(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    private static final void safeClose(Socket closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    private static final void safeClose(ServerSocket closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {
                ;
            }
        }

    }

    public void start() throws IOException {
        this.myServerSocket = new ServerSocket();
        this.myServerSocket.bind(this.hostname != null?new InetSocketAddress(this.hostname, this.myPort):new InetSocketAddress(this.myPort));
        this.myThread = new Thread(new Runnable() {
            public void run() {
                do {
                    try {
                        final Socket e = NanoHTTPD.this.myServerSocket.accept();
                        NanoHTTPD.this.registerConnection(e);
                        e.setSoTimeout(5000);
                        final InputStream inputStream = e.getInputStream();
                        NanoHTTPD.this.asyncRunner.exec(new Runnable() {
                            public void run() {
                                OutputStream outputStream = null;

                                try {
                                    outputStream = e.getOutputStream();
                                    NanoHTTPD.TempFileManager ex = NanoHTTPD.this.tempFileManagerFactory.create();
                                    NanoHTTPD.HTTPSession session = NanoHTTPD.this.new HTTPSession(ex, inputStream, outputStream, e.getInetAddress());

                                    while(!e.isClosed()) {
                                        session.execute();
                                    }
                                } catch (Exception var7) {
                                    if(!(var7 instanceof SocketException) || !"NanoHttpd Shutdown".equals(var7.getMessage())) {
                                        var7.printStackTrace();
                                    }
                                } finally {
                                    NanoHTTPD.safeClose((Closeable)outputStream);
                                    NanoHTTPD.safeClose((Closeable)inputStream);
                                    NanoHTTPD.safeClose(e);
                                    NanoHTTPD.this.unRegisterConnection(e);
                                }

                            }
                        });
                    } catch (IOException var3) {
                        ;
                    }
                } while(!NanoHTTPD.this.myServerSocket.isClosed());

            }
        });
        this.myThread.setDaemon(true);
        this.myThread.setName("NanoHttpd Main Listener");
        this.myThread.start();
    }

    public void stop() {
        try {
            safeClose(this.myServerSocket);
            this.closeAllConnections();
            if(this.myThread != null) {
                this.myThread.join();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public synchronized void registerConnection(Socket socket) {
        this.openConnections.add(socket);
    }

    public synchronized void unRegisterConnection(Socket socket) {
        this.openConnections.remove(socket);
    }

    public synchronized void closeAllConnections() {
        Iterator i$ = this.openConnections.iterator();

        while(i$.hasNext()) {
            Socket socket = (Socket)i$.next();
            safeClose(socket);
        }

    }

    public final int getListeningPort() {
        return this.myServerSocket == null?-1:this.myServerSocket.getLocalPort();
    }

    public final boolean wasStarted() {
        return this.myServerSocket != null && this.myThread != null;
    }

    public final boolean isAlive() {
        return this.wasStarted() && !this.myServerSocket.isClosed() && this.myThread.isAlive();
    }

    /** @deprecated */
    @Deprecated
    public NanoHTTPD.Response serve(String uri, NanoHTTPD.Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {
        return new NanoHTTPD.Response(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "Not Found");
    }

    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        HashMap files = new HashMap();
        NanoHTTPD.Method method = session.getMethod();
        if(NanoHTTPD.Method.PUT.equals(method) || NanoHTTPD.Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException var5) {
                return new NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "SERVER INTERNAL ERROR: IOException: " + var5.getMessage());
            } catch (NanoHTTPD.ResponseException var6) {
                return new NanoHTTPD.Response(var6.getStatus(), "text/plain", var6.getMessage());
            }
        }

        Map parms = session.getParms();
        parms.put("NanoHttpd.QUERY_STRING", session.getQueryParameterString());
        return this.serve(session.getUri(), method, session.getHeaders(), parms, files);
    }

    protected String decodePercent(String str) {
        String decoded = null;

        try {
            decoded = URLDecoder.decode(str, "UTF8");
        } catch (UnsupportedEncodingException var4) {
            ;
        }

        return decoded;
    }

    protected Map<String, List<String>> decodeParameters(Map<String, String> parms) {
        return this.decodeParameters((String)parms.get("NanoHttpd.QUERY_STRING"));
    }

    protected Map<String, List<String>> decodeParameters(String queryString) {
        HashMap parms = new HashMap();
        if(queryString != null) {
            StringTokenizer st = new StringTokenizer(queryString, "&");

            while(st.hasMoreTokens()) {
                String e = st.nextToken();
                int sep = e.indexOf(61);
                String propertyName = sep >= 0?this.decodePercent(e.substring(0, sep)).trim():this.decodePercent(e).trim();
                if(!parms.containsKey(propertyName)) {
                    parms.put(propertyName, new ArrayList());
                }

                String propertyValue = sep >= 0?this.decodePercent(e.substring(sep + 1)):null;
                if(propertyValue != null) {
                    ((List)parms.get(propertyName)).add(propertyValue);
                }
            }
        }

        return parms;
    }

    public void setAsyncRunner(NanoHTTPD.AsyncRunner asyncRunner) {
        this.asyncRunner = asyncRunner;
    }

    public void setTempFileManagerFactory(NanoHTTPD.TempFileManagerFactory tempFileManagerFactory) {
        this.tempFileManagerFactory = tempFileManagerFactory;
    }

    public class CookieHandler implements Iterable<String> {
        private HashMap<String, String> cookies = new HashMap();
        private ArrayList<Cookie> queue = new ArrayList();

        public CookieHandler(Map<String, String> httpHeaders) {
            String raw = (String)httpHeaders.get("cookie");
            if(raw != null) {
                String[] tokens = raw.split(";");
                String[] arr$ = tokens;
                int len$ = tokens.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    String token = arr$[i$];
                    String[] data = token.trim().split("=");
                    if(data.length == 2) {
                        this.cookies.put(data[0], data[1]);
                    }
                }
            }

        }

        public Iterator<String> iterator() {
            return this.cookies.keySet().iterator();
        }

        public String read(String name) {
            return (String)this.cookies.get(name);
        }

        public void set(String name, String value, int expires) {
            this.queue.add(new NanoHTTPD.Cookie(name, value, NanoHTTPD.Cookie.getHTTPTime(expires)));
        }

        public void set(NanoHTTPD.Cookie cookie) {
            this.queue.add(cookie);
        }

        public void delete(String name) {
            this.set(name, "-delete-", -30);
        }

        public void unloadQueue(NanoHTTPD.Response response) {
            Iterator i$ = this.queue.iterator();

            while(i$.hasNext()) {
                NanoHTTPD.Cookie cookie = (NanoHTTPD.Cookie)i$.next();
                response.addHeader("Set-Cookie", cookie.getHTTPHeader());
            }

        }
    }

    public static class Cookie {
        private String n;
        private String v;
        private String e;

        public Cookie(String name, String value, String expires) {
            this.n = name;
            this.v = value;
            this.e = expires;
        }

        public Cookie(String name, String value) {
            this(name, value, 30);
        }

        public Cookie(String name, String value, int numDays) {
            this.n = name;
            this.v = value;
            this.e = getHTTPTime(numDays);
        }

        public String getHTTPHeader() {
            String fmt = "%s=%s; expires=%s";
            return String.format(fmt, new Object[]{this.n, this.v, this.e});
        }

        public static String getHTTPTime(int days) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            calendar.add(Calendar.DAY_OF_MONTH, days);
            return dateFormat.format(calendar.getTime());
        }
    }

    protected class HTTPSession implements NanoHTTPD.IHTTPSession {
        public static final int BUFSIZE = 8192;
        private final NanoHTTPD.TempFileManager tempFileManager;
        private final OutputStream outputStream;
        private PushbackInputStream inputStream;
        private int splitbyte;
        private int rlen;
        private String uri;
        private NanoHTTPD.Method method;
        private Map<String, String> parms;
        private Map<String, String> headers;
        private NanoHTTPD.CookieHandler cookies;
        private String queryParameterString;

        public HTTPSession(NanoHTTPD.TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
            this.tempFileManager = tempFileManager;
            this.inputStream = new PushbackInputStream(inputStream, 8192);
            this.outputStream = outputStream;
        }

        public HTTPSession(NanoHTTPD.TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
            this.tempFileManager = tempFileManager;
            this.inputStream = new PushbackInputStream(inputStream, 8192);
            this.outputStream = outputStream;
            String remoteIp = !inetAddress.isLoopbackAddress() && !inetAddress.isAnyLocalAddress()?inetAddress.getHostAddress().toString():"127.0.0.1";
            this.headers = new HashMap();
            this.headers.put("remote-addr", remoteIp);
            this.headers.put("http-client-ip", remoteIp);
        }

        public void execute() throws IOException {
            try {
                NanoHTTPD.Response r;
                try {
                    byte[] re = new byte[8192];
                    this.splitbyte = 0;
                    this.rlen = 0;
                    boolean r2 = true;

                    int r3;
                    try {
                        r3 = this.inputStream.read(re, 0, 8192);
                    } catch (Exception var12) {
                        NanoHTTPD.safeClose((Closeable)this.inputStream);
                        NanoHTTPD.safeClose((Closeable)this.outputStream);
                        throw new SocketException("NanoHttpd Shutdown");
                    }

                    if(r3 == -1) {
                        NanoHTTPD.safeClose((Closeable)this.inputStream);
                        NanoHTTPD.safeClose((Closeable)this.outputStream);
                        throw new SocketException("NanoHttpd Shutdown");
                    }

                    while(r3 > 0) {
                        this.rlen += r3;
                        this.splitbyte = this.findHeaderEnd(re, this.rlen);
                        if(this.splitbyte > 0) {
                            break;
                        }

                        r3 = this.inputStream.read(re, this.rlen, 8192 - this.rlen);
                    }

                    if(this.splitbyte < this.rlen) {
                        this.inputStream.unread(re, this.splitbyte, this.rlen - this.splitbyte);
                    }

                    this.parms = new HashMap();
                    if(null == this.headers) {
                        this.headers = new HashMap();
                    }

                    BufferedReader r4 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(re, 0, this.rlen)));
                    HashMap pre = new HashMap();
                    this.decodeHeader(r4, pre, this.parms, this.headers);
                    this.method = NanoHTTPD.Method.lookup((String)pre.get("method"));
                    if(this.method == null) {
                        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error.");
                    }

                    this.uri = (String)pre.get("uri");
                    this.cookies = NanoHTTPD.this.new CookieHandler(this.headers);
                    NanoHTTPD.Response r1 = NanoHTTPD.this.serve(this);
                    if(r1 == null) {
                        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: Serve() returned a null response.");
                    }

                    this.cookies.unloadQueue(r1);
                    r1.setRequestMethod(this.method);
                    r1.send(this.outputStream);
                } catch (SocketException var13) {
                    throw var13;
                } catch (SocketTimeoutException var14) {
                    throw var14;
                } catch (IOException var15) {
                    r = new NanoHTTPD.Response(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "SERVER INTERNAL ERROR: IOException: " + var15.getMessage());
                    r.send(this.outputStream);
                    NanoHTTPD.safeClose((Closeable)this.outputStream);
                } catch (NanoHTTPD.ResponseException var16) {
                    r = new NanoHTTPD.Response(var16.getStatus(), "text/plain", var16.getMessage());
                    r.send(this.outputStream);
                    NanoHTTPD.safeClose((Closeable)this.outputStream);
                }
            } finally {
                this.tempFileManager.clear();
            }

        }

        public void parseBody(Map<String, String> files) throws IOException, NanoHTTPD.ResponseException {
            RandomAccessFile randomAccessFile = null;
            BufferedReader in = null;

            try {
                randomAccessFile = this.getTmpBucket();
                long size;
                if(this.headers.containsKey("content-length")) {
                    size = (long) Integer.parseInt((String)this.headers.get("content-length"));
                } else if(this.splitbyte < this.rlen) {
                    size = (long)(this.rlen - this.splitbyte);
                } else {
                    size = 0L;
                }

                byte[] buf = new byte[512];

                while(this.rlen >= 0 && size > 0L) {
                    this.rlen = this.inputStream.read(buf, 0, (int) Math.min(size, 512L));
                    size -= (long)this.rlen;
                    if(this.rlen > 0) {
                        randomAccessFile.write(buf, 0, this.rlen);
                    }
                }

                MappedByteBuffer fbuf = randomAccessFile.getChannel().map(MapMode.READ_ONLY, 0L, randomAccessFile.length());
                randomAccessFile.seek(0L);
                FileInputStream bin = new FileInputStream(randomAccessFile.getFD());
                in = new BufferedReader(new InputStreamReader(bin));
                if(NanoHTTPD.Method.POST.equals(this.method)) {
                    String contentType = "";
                    String contentTypeHeader = (String)this.headers.get("content-type");
                    StringTokenizer st = null;
                    if(contentTypeHeader != null) {
                        st = new StringTokenizer(contentTypeHeader, ",; ");
                        if(st.hasMoreTokens()) {
                            contentType = st.nextToken();
                        }
                    }

                    String postLine;
                    if("multipart/form-data".equalsIgnoreCase(contentType)) {
                        if(!st.hasMoreTokens()) {
                            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but boundary missing. Usage: GET /example/file.html");
                        }

                        postLine = "boundary=";
                        int postLineBuffer = contentTypeHeader.indexOf(postLine) + postLine.length();
                        String pbuf = contentTypeHeader.substring(postLineBuffer, contentTypeHeader.length());
                        if(pbuf.startsWith("\"") && pbuf.endsWith("\"")) {
                            pbuf = pbuf.substring(1, pbuf.length() - 1);
                        }

                        this.decodeMultipartData(pbuf, fbuf, in, this.parms, files);
                    } else {
                        postLine = "";
                        StringBuilder postLineBuffer1 = new StringBuilder();
                        char[] pbuf1 = new char[512];

                        for(int read = in.read(pbuf1); read >= 0 && !postLine.endsWith("\r\n"); read = in.read(pbuf1)) {
                            postLine = String.valueOf(pbuf1, 0, read);
                            postLineBuffer1.append(postLine);
                        }

                        postLine = postLineBuffer1.toString().trim();
                        if("application/x-www-form-urlencoded".equalsIgnoreCase(contentType)) {
                            this.decodeParms(postLine, this.parms);
                        } else if(postLine.length() != 0) {
                            files.put("postData", postLine);
                        }
                    }
                } else if(NanoHTTPD.Method.PUT.equals(this.method)) {
                    files.put("content", this.saveTmpFile(fbuf, 0, fbuf.limit()));
                }
            } finally {
                NanoHTTPD.safeClose((Closeable)randomAccessFile);
                NanoHTTPD.safeClose((Closeable)in);
            }

        }

        private void decodeHeader(BufferedReader in, Map<String, String> pre, Map<String, String> parms, Map<String, String> headers) throws NanoHTTPD.ResponseException {
            try {
                String ioe = in.readLine();
                if(ioe != null) {
                    StringTokenizer st = new StringTokenizer(ioe);
                    if(!st.hasMoreTokens()) {
                        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
                    } else {
                        pre.put("method", st.nextToken());
                        if(!st.hasMoreTokens()) {
                            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
                        } else {
                            String uri = st.nextToken();
                            int qmi = uri.indexOf(63);
                            if(qmi >= 0) {
                                this.decodeParms(uri.substring(qmi + 1), parms);
                                uri = NanoHTTPD.this.decodePercent(uri.substring(0, qmi));
                            } else {
                                uri = NanoHTTPD.this.decodePercent(uri);
                            }

                            if(st.hasMoreTokens()) {
                                for(String line = in.readLine(); line != null && line.trim().length() > 0; line = in.readLine()) {
                                    int p = line.indexOf(58);
                                    if(p >= 0) {
                                        headers.put(line.substring(0, p).trim().toLowerCase(Locale.US), line.substring(p + 1).trim());
                                    }
                                }
                            }

                            pre.put("uri", uri);
                        }
                    }
                }
            } catch (IOException var11) {
                throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + var11.getMessage(), var11);
            }
        }

        private void decodeMultipartData(String boundary, ByteBuffer fbuf, BufferedReader in, Map<String, String> parms, Map<String, String> files) throws NanoHTTPD.ResponseException {
            try {
                int[] ioe = this.getBoundaryPositions(fbuf, boundary.getBytes());
                int boundarycount = 1;
                String mpline = in.readLine();

                while(true) {
                    HashMap item;
                    do {
                        if(mpline == null) {
                            return;
                        }

                        if(!mpline.contains(boundary)) {
                            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but next chunk does not start with boundary. Usage: GET /example/file.html");
                        }

                        ++boundarycount;
                        item = new HashMap();

                        for(mpline = in.readLine(); mpline != null && mpline.trim().length() > 0; mpline = in.readLine()) {
                            int contentDisposition = mpline.indexOf(58);
                            if(contentDisposition != -1) {
                                item.put(mpline.substring(0, contentDisposition).trim().toLowerCase(Locale.US), mpline.substring(contentDisposition + 1).trim());
                            }
                        }
                    } while(mpline == null);

                    String var18 = (String)item.get("content-disposition");
                    if(var18 == null) {
                        throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.BAD_REQUEST, "BAD REQUEST: Content type is multipart/form-data but no content-disposition info found. Usage: GET /example/file.html");
                    }

                    StringTokenizer st = new StringTokenizer(var18, ";");
                    HashMap disposition = new HashMap();

                    String pname;
                    while(st.hasMoreTokens()) {
                        pname = st.nextToken().trim();
                        int value = pname.indexOf(61);
                        if(value != -1) {
                            disposition.put(pname.substring(0, value).trim().toLowerCase(Locale.US), pname.substring(value + 1).trim());
                        }
                    }

                    pname = (String)disposition.get("name");
                    pname = pname.substring(1, pname.length() - 1);
                    String var19 = "";
                    int offset;
                    if(item.get("content-type") == null) {
                        while(mpline != null && !mpline.contains(boundary)) {
                            mpline = in.readLine();
                            if(mpline != null) {
                                offset = mpline.indexOf(boundary);
                                if(offset == -1) {
                                    var19 = var19 + mpline;
                                } else {
                                    var19 = var19 + mpline.substring(0, offset - 2);
                                }
                            }
                        }
                    } else {
                        if(boundarycount > ioe.length) {
                            throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "Error processing request");
                        }

                        offset = this.stripMultipartHeaders(fbuf, ioe[boundarycount - 2]);
                        String path = this.saveTmpFile(fbuf, offset, ioe[boundarycount - 1] - offset - 4);
                        files.put(pname, path);
                        var19 = (String)disposition.get("filename");
                        var19 = var19.substring(1, var19.length() - 1);

                        do {
                            mpline = in.readLine();
                        } while(mpline != null && !mpline.contains(boundary));
                    }

                    parms.put(pname, var19);
                }
            } catch (IOException var17) {
                throw new NanoHTTPD.ResponseException(NanoHTTPD.Response.Status.INTERNAL_ERROR, "SERVER INTERNAL ERROR: IOException: " + var17.getMessage(), var17);
            }
        }

        private int findHeaderEnd(byte[] buf, int rlen) {
            for(int splitbyte = 0; splitbyte + 3 < rlen; ++splitbyte) {
                if(buf[splitbyte] == 13 && buf[splitbyte + 1] == 10 && buf[splitbyte + 2] == 13 && buf[splitbyte + 3] == 10) {
                    return splitbyte + 4;
                }
            }

            return 0;
        }

        private int[] getBoundaryPositions(ByteBuffer b, byte[] boundary) {
            int matchcount = 0;
            int matchbyte = -1;
            ArrayList matchbytes = new ArrayList();

            for(int ret = 0; ret < b.limit(); ++ret) {
                if(b.get(ret) == boundary[matchcount]) {
                    if(matchcount == 0) {
                        matchbyte = ret;
                    }

                    ++matchcount;
                    if(matchcount == boundary.length) {
                        matchbytes.add(Integer.valueOf(matchbyte));
                        matchcount = 0;
                        matchbyte = -1;
                    }
                } else {
                    ret -= matchcount;
                    matchcount = 0;
                    matchbyte = -1;
                }
            }

            int[] var8 = new int[matchbytes.size()];

            for(int i = 0; i < var8.length; ++i) {
                var8[i] = ((Integer)matchbytes.get(i)).intValue();
            }

            return var8;
        }

        private String saveTmpFile(ByteBuffer b, int offset, int len) {
            String path = "";
            if(len > 0) {
                FileOutputStream fileOutputStream = null;

                try {
                    NanoHTTPD.TempFile e = this.tempFileManager.createTempFile();
                    ByteBuffer src = b.duplicate();
                    fileOutputStream = new FileOutputStream(e.getName());
                    FileChannel dest = fileOutputStream.getChannel();
                    src.position(offset).limit(offset + len);
                    dest.write(src.slice());
                    path = e.getName();
                } catch (Exception var12) {
                    throw new Error(var12);
                } finally {
                    NanoHTTPD.safeClose((Closeable)fileOutputStream);
                }
            }

            return path;
        }

        private RandomAccessFile getTmpBucket() {
            try {
                NanoHTTPD.TempFile e = this.tempFileManager.createTempFile();
                return new RandomAccessFile(e.getName(), "rw");
            } catch (Exception var2) {
                throw new Error(var2);
            }
        }

        private int stripMultipartHeaders(ByteBuffer b, int offset) {
            int i;
            for(i = offset; i < b.limit(); ++i) {
                if(b.get(i) == 13) {
                    ++i;
                    if(b.get(i) == 10) {
                        ++i;
                        if(b.get(i) == 13) {
                            ++i;
                            if(b.get(i) == 10) {
                                break;
                            }
                        }
                    }
                }
            }

            return i + 1;
        }

        private void decodeParms(String parms, Map<String, String> p) {
            if(parms == null) {
                this.queryParameterString = "";
            } else {
                this.queryParameterString = parms;
                StringTokenizer st = new StringTokenizer(parms, "&");

                while(st.hasMoreTokens()) {
                    String e = st.nextToken();
                    int sep = e.indexOf(61);
                    if(sep >= 0) {
                        p.put(NanoHTTPD.this.decodePercent(e.substring(0, sep)).trim(), NanoHTTPD.this.decodePercent(e.substring(sep + 1)));
                    } else {
                        p.put(NanoHTTPD.this.decodePercent(e).trim(), "");
                    }
                }

            }
        }

        public final Map<String, String> getParms() {
            return this.parms;
        }

        public String getQueryParameterString() {
            return this.queryParameterString;
        }

        public final Map<String, String> getHeaders() {
            return this.headers;
        }

        public final String getUri() {
            return this.uri;
        }

        public final NanoHTTPD.Method getMethod() {
            return this.method;
        }

        public final InputStream getInputStream() {
            return this.inputStream;
        }

        public NanoHTTPD.CookieHandler getCookies() {
            return this.cookies;
        }
    }

    public interface IHTTPSession {
        void execute() throws IOException;

        Map<String, String> getParms();

        Map<String, String> getHeaders();

        String getUri();

        String getQueryParameterString();

        NanoHTTPD.Method getMethod();

        InputStream getInputStream();

        NanoHTTPD.CookieHandler getCookies();

        void parseBody(Map<String, String> var1) throws IOException, NanoHTTPD.ResponseException;
    }

    private class DefaultTempFileManagerFactory implements NanoHTTPD.TempFileManagerFactory {
        private DefaultTempFileManagerFactory() {
        }

        public NanoHTTPD.TempFileManager create() {
            return new NanoHTTPD.DefaultTempFileManager();
        }
    }

    public static final class ResponseException extends Exception {
        private final NanoHTTPD.Response.Status status;

        public ResponseException(NanoHTTPD.Response.Status status, String message) {
            super(message);
            this.status = status;
        }

        public ResponseException(NanoHTTPD.Response.Status status, String message, Exception e) {
            super(message, e);
            this.status = status;
        }

        public NanoHTTPD.Response.Status getStatus() {
            return this.status;
        }
    }

    public static class Response {
        private NanoHTTPD.Response.IStatus status;
        private String mimeType;
        private InputStream data;
        private Map<String, String> header;
        private NanoHTTPD.Method requestMethod;
        private boolean chunkedTransfer;

        public Response(String msg) {
            this(NanoHTTPD.Response.Status.OK, "text/html", (String)msg);
        }

        public Response(NanoHTTPD.Response.IStatus status, String mimeType, InputStream data) {
            this.header = new HashMap();
            this.status = status;
            this.mimeType = mimeType;
            this.data = data;
        }

        public Response(NanoHTTPD.Response.IStatus status, String mimeType, String txt) {
            this.header = new HashMap();
            this.status = status;
            this.mimeType = mimeType;

            try {
                this.data = txt != null?new ByteArrayInputStream(txt.getBytes("UTF-8")):null;
            } catch (UnsupportedEncodingException var5) {
                var5.printStackTrace();
            }

        }

        public void addHeader(String name, String value) {
            this.header.put(name, value);
        }

        public String getHeader(String name) {
            return (String)this.header.get(name);
        }

        protected void send(OutputStream outputStream) {
            String mime = this.mimeType;
            SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss \'GMT\'", Locale.US);
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                if(this.status == null) {
                    throw new Error("sendResponse(): Status can\'t be null.");
                }

                PrintWriter ioe = new PrintWriter(outputStream);
                ioe.print("HTTP/1.1 " + this.status.getDescription() + " \r\n");
                if(mime != null) {
                    ioe.print("Content-Type: " + mime + "\r\n");
                }

                if(this.header == null || this.header.get("Date") == null) {
                    ioe.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");
                }

                if(this.header != null) {
                    Iterator pending = this.header.keySet().iterator();

                    while(pending.hasNext()) {
                        String key = (String)pending.next();
                        String value = (String)this.header.get(key);
                        ioe.print(key + ": " + value + "\r\n");
                    }
                }

                this.sendConnectionHeaderIfNotAlreadyPresent(ioe, this.header);
                if(this.requestMethod != NanoHTTPD.Method.HEAD && this.chunkedTransfer) {
                    this.sendAsChunked(outputStream, ioe);
                } else {
                    int pending1 = this.data != null?this.data.available():0;
                    this.sendContentLengthHeaderIfNotAlreadyPresent(ioe, this.header, pending1);
                    ioe.print("\r\n");
                    ioe.flush();
                    this.sendAsFixedLength(outputStream, pending1);
                }

                outputStream.flush();
                NanoHTTPD.safeClose((Closeable)this.data);
            } catch (IOException var8) {
                ;
            }

        }

        protected void sendContentLengthHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header, int size) {
            if(!this.headerAlreadySent(header, "content-length")) {
                pw.print("Content-Length: " + size + "\r\n");
            }

        }

        protected void sendConnectionHeaderIfNotAlreadyPresent(PrintWriter pw, Map<String, String> header) {
            if(!this.headerAlreadySent(header, "connection")) {
                pw.print("Connection: keep-alive\r\n");
            }

        }

        private boolean headerAlreadySent(Map<String, String> header, String name) {
            boolean alreadySent = false;

            String headerName;
            for(Iterator i$ = header.keySet().iterator(); i$.hasNext(); alreadySent |= headerName.equalsIgnoreCase(name)) {
                headerName = (String)i$.next();
            }

            return alreadySent;
        }

        private void sendAsChunked(OutputStream outputStream, PrintWriter pw) throws IOException {
            pw.print("Transfer-Encoding: chunked\r\n");
            pw.print("\r\n");
            pw.flush();
            short BUFFER_SIZE = 16384;
            byte[] CRLF = "\r\n".getBytes();
            byte[] buff = new byte[BUFFER_SIZE];

            int read;
            while((read = this.data.read(buff)) > 0) {
                outputStream.write(String.format("%x\r\n", new Object[]{Integer.valueOf(read)}).getBytes());
                outputStream.write(buff, 0, read);
                outputStream.write(CRLF);
            }

            outputStream.write(String.format("0\r\n\r\n", new Object[0]).getBytes());
        }

        private void sendAsFixedLength(OutputStream outputStream, int pending) throws IOException {
            if(this.requestMethod != NanoHTTPD.Method.HEAD && this.data != null) {
                short BUFFER_SIZE = 16384;

                int read;
                for(byte[] buff = new byte[BUFFER_SIZE]; pending > 0; pending -= read) {
                    read = this.data.read(buff, 0, pending > BUFFER_SIZE?BUFFER_SIZE:pending);
                    if(read <= 0) {
                        break;
                    }

                    outputStream.write(buff, 0, read);
                }
            }

        }

        public NanoHTTPD.Response.IStatus getStatus() {
            return this.status;
        }

        public void setStatus(NanoHTTPD.Response.Status status) {
            this.status = status;
        }

        public String getMimeType() {
            return this.mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }

        public InputStream getData() {
            return this.data;
        }

        public void setData(InputStream data) {
            this.data = data;
        }

        public NanoHTTPD.Method getRequestMethod() {
            return this.requestMethod;
        }

        public void setRequestMethod(NanoHTTPD.Method requestMethod) {
            this.requestMethod = requestMethod;
        }

        public void setChunkedTransfer(boolean chunkedTransfer) {
            this.chunkedTransfer = chunkedTransfer;
        }

        public static enum Status implements NanoHTTPD.Response.IStatus {
            SWITCH_PROTOCOL(101, "Switching Protocols"),
            OK(200, "OK"),
            CREATED(201, "Created"),
            ACCEPTED(202, "Accepted"),
            NO_CONTENT(204, "No Content"),
            PARTIAL_CONTENT(206, "Partial Content"),
            REDIRECT(301, "Moved Permanently"),
            NOT_MODIFIED(304, "Not Modified"),
            BAD_REQUEST(400, "Bad Request"),
            UNAUTHORIZED(401, "Unauthorized"),
            FORBIDDEN(403, "Forbidden"),
            NOT_FOUND(404, "Not Found"),
            METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
            RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
            INTERNAL_ERROR(500, "Internal Server Error");

            private final int requestStatus;
            private final String description;

            private Status(int requestStatus, String description) {
                this.requestStatus = requestStatus;
                this.description = description;
            }

            public int getRequestStatus() {
                return this.requestStatus;
            }

            public String getDescription() {
                return "" + this.requestStatus + " " + this.description;
            }
        }

        public interface IStatus {
            int getRequestStatus();

            String getDescription();
        }
    }

    public static class DefaultTempFile implements NanoHTTPD.TempFile {
        private File file;
        private OutputStream fstream;

        public DefaultTempFile(String tempdir) throws IOException {
            this.file = File.createTempFile("NanoHTTPD-", "", new File(tempdir));
            this.fstream = new FileOutputStream(this.file);
        }

        public OutputStream open() throws Exception {
            return this.fstream;
        }

        public void delete() throws Exception {
            NanoHTTPD.safeClose((Closeable)this.fstream);
            this.file.delete();
        }

        public String getName() {
            return this.file.getAbsolutePath();
        }
    }

    public static class DefaultTempFileManager implements NanoHTTPD.TempFileManager {
        private final String tmpdir = System.getProperty("java.io.tmpdir");
        private final List<TempFile> tempFiles = new ArrayList();

        public DefaultTempFileManager() {
        }

        public NanoHTTPD.TempFile createTempFile() throws Exception {
            NanoHTTPD.DefaultTempFile tempFile = new NanoHTTPD.DefaultTempFile(this.tmpdir);
            this.tempFiles.add(tempFile);
            return tempFile;
        }

        public void clear() {
            Iterator i$ = this.tempFiles.iterator();

            while(i$.hasNext()) {
                NanoHTTPD.TempFile file = (NanoHTTPD.TempFile)i$.next();

                try {
                    file.delete();
                } catch (Exception var4) {
                    ;
                }
            }

            this.tempFiles.clear();
        }
    }

    public static class DefaultAsyncRunner implements NanoHTTPD.AsyncRunner {
        private long requestCount;

        public DefaultAsyncRunner() {
        }

        public void exec(Runnable code) {
            ++this.requestCount;
            Thread t = new Thread(code);
            t.setDaemon(true);
            t.setName("NanoHttpd Request Processor (#" + this.requestCount + ")");
            t.start();
        }
    }

    public interface TempFile {
        OutputStream open() throws Exception;

        void delete() throws Exception;

        String getName();
    }

    public interface TempFileManager {
        NanoHTTPD.TempFile createTempFile() throws Exception;

        void clear();
    }

    public interface TempFileManagerFactory {
        NanoHTTPD.TempFileManager create();
    }

    public interface AsyncRunner {
        void exec(Runnable var1);
    }

    public static enum Method {
        GET,
        PUT,
        POST,
        DELETE,
        HEAD,
        OPTIONS;

        private Method() {
        }

        static NanoHTTPD.Method lookup(String method) {
            NanoHTTPD.Method[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                NanoHTTPD.Method m = arr$[i$];
                if(m.toString().equalsIgnoreCase(method)) {
                    return m;
                }
            }

            return null;
        }
    }
}
