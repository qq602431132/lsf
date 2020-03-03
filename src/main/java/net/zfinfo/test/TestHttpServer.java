package net.zfinfo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import fi.iki.elonen.NanoHTTPD;

public class TestHttpServer extends NanoHTTPD {

    private File webRoot;

    public TestHttpServer(int port, File webRoot) throws Exception {
        super(port);
        if (webRoot == null) {
            throw new Exception("网站路径：null");
        }
        if (webRoot.exists() && webRoot.isDirectory()) {
            this.webRoot = webRoot;
        } else {
            throw new Exception("网站路径不存在：" + webRoot.getAbsolutePath());
        }
    }

    @Override
    public Response serve(String uri, Method method, java.util.Map<String, String> headers, java.util.Map<String, String> parms, java.util.Map<String, String> files) {

        File file = new File(webRoot, uri);
        if (file.exists()) {
            if (file.isFile()) {
                return render200(uri, file);
            } else {
                if (uri.endsWith("/")) {
                    File indexFile = new File(file, "index.html");
                    if (indexFile.exists()) {
                        return render200(uri + "index.html", indexFile);
                    } else {
                        return render404();
                    }
                } else {
                    return render301(uri + "/");
                }
            }
        } else {
            return render404();
        }
    }

    private Response render404() {
        File file = new File(webRoot, "404.html");
        if (file.exists()) {
            try {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, new FileInputStream(file), file.length());
            } catch (FileNotFoundException e) {
                return render500(e.getMessage());
            }
        } else {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML, null);
        }
    }

    private Response render500(String text) {
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, text);
    }

    private Response render200(String uri, File file) {
        try {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, NanoHTTPD.getMimeTypeForFile(uri), new FileInputStream(file), file.length());
        } catch (FileNotFoundException e) {
            return render500(e.getMessage());
        }
    }

    private Response render301(String next) {
        Response res = newFixedLengthResponse(Response.Status.REDIRECT, NanoHTTPD.MIME_HTML, null);
        res.addHeader("Location", next);
        return res;
    }

}