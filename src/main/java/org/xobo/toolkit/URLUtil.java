package org.xobo.toolkit;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

public class URLUtil {
  public static String encode(String value) {
    return URI.create(value).toASCIIString();
  }

  public static String decode(String value) {
    return decode(value, "UTF-8");
  }

  public static String decode(String value, String charset) {
    String encodedValue;
    try {
      encodedValue = URLDecoder.decode(value, charset);
    } catch (UnsupportedEncodingException e1) {
      throw new RuntimeException(e1);
    }
    return encodedValue;

  }

  public static String getContentDisposition(String fileName) {
    String encodedFilename = encode(fileName);

    return "attachment;filename=\"" + encodedFilename + "\";filename*=utf-8''" + encodedFilename;
  }

  public static void main(String[] args) {
    System.out.println(URI.create("周兵").toASCIIString());
  }

  public static String getWebAppPath(HttpServletRequest request) {
    if (request == null) {
      return "";
    }

    String scheme = request.getScheme();// http
    String serverName = request.getServerName();// hostname.com
    int portNumber = request.getServerPort();// 80

    String portNumberStr = "";
    if (portNumber != 80 && portNumber != 443) {
      portNumberStr = ":" + portNumber;
    }
    String contextPath = request.getContextPath(); // /mywebapp
    return scheme + "://" + serverName + portNumberStr + contextPath;
  }

  public static String getURL(HttpServletRequest req) {

    String scheme = req.getScheme(); // http
    String serverName = req.getServerName(); // hostname.com
    int serverPort = req.getServerPort(); // 80
    String contextPath = req.getContextPath(); // /mywebapp
    String servletPath = req.getServletPath(); // /servlet/MyServlet
    String pathInfo = req.getPathInfo(); // /a/b;c=123
    String queryString = req.getQueryString(); // d=789

    // Reconstruct original requesting URL
    StringBuilder url = new StringBuilder();
    url.append(scheme).append("://").append(serverName);

    if (serverPort != 80 && serverPort != 443) {
      url.append(":").append(serverPort);
    }

    url.append(contextPath).append(servletPath);

    if (pathInfo != null) {
      url.append(pathInfo);
    }
    if (queryString != null) {
      url.append("?").append(queryString);
    }
    return url.toString();
  }
}
