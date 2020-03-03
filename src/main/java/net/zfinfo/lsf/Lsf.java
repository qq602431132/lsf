package net.zfinfo.lsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import net.zfinfo.util.IPUtil1;
import net.zfinfo.util.NanoFileUpload;
import net.zfinfo.util.ProcessUtils;
import net.zfinfo.util.ProcessUtils.Result;
import net.zfinfo.util.RandomUtils;

/**
 * 罗斯福 下载 上传 创建目录 删除文件/目录 md5
 * 
 * @author kanbuxiaqu@outlook.com
 */
public class Lsf extends NanoHTTPD {
	private static final String buibui = "202003021835";
	private static final Logger LOG = Logger.getLogger(Lsf.class.getName());
	File baseDir;
	NanoFileUpload nanoFileUploader;
	private final static String sepa = File.separator;
	boolean isDelFlag;// false※关闭 true※开启
	private final static String DELETE = "delete";
	private final static String IFDEL = "ifDel";
	private final static String GETMD5 = "getMD5";
	private final static String MKDIR = "mkDir";
	private final static String DOCMD = "docmd";
	private final static String CDDIR = "cddir";

	private static List<String> logSkr = new ArrayList<String>();
	static {
		logSkr.add(".! ❀ ♀ ♂ ― ￣ _ @ &");
		logSkr.add("# * ■ § № ○ ● → ※ ▲ △");
		logSkr.add("← ◎ ↑ ◇ ↓ ◆ 〓 □ ¤ ℃");
		logSkr.add("°‰ € ∑ の ≌ つ Θ 阝");
		logSkr.add("丿 § 、 ℃ ☆ ★ 丶 _ 灬");
		logSkr.add("↓ * ____ i 卩 巛 艹 彡");
		logSkr.add("丨 廾 宀 ≮ ≯ ° ╮ ˊ");
		logSkr.add("￠ ⊙ メ ︶ ㄣ ╭");
		logSkr.add("ァ ↗ ↘ ㄟ 乁 ~ ■");
	}

	private static String randomASkr() {
		return RandomUtils.getRandomElement(logSkr);
	}

	public static String getIP() throws UnknownHostException {
		return IPUtil1.getLocalIP();
	}

	public Lsf(int port, URI uri) {
		super(port);
		this.baseDir = new File(uri);
		this.nanoFileUploader = new NanoFileUpload(new DiskFileItemFactory());
		try {
			LOG.info(randomASkr() + "开始运行※" + "共享目录为※" + baseDir.getCanonicalPath() + "丶访问地址※http://" + getIP() + ":"
					+ port + "/");
		} catch (Exception e) {
			LOG.info(randomASkr() + "启动失败※" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			int porta = 1234;
			File patha = new File(new File("").toURI());
			int parseInt;
			if (args.length > 0) {
				try {
					parseInt = Integer.parseInt(args[0]);
					if (parseInt > 0 && parseInt <= 65535) {
						porta = parseInt;
						patha = (args.length > 1 && args.length < 3) ? new File(args[1]) : patha;
					}
				} catch (NumberFormatException e) {
					parseInt = Integer.parseInt(args[1]);
					if (parseInt > 0 && parseInt <= 65535) {
						porta = parseInt;
						patha = (args.length > 1 && args.length < 3) ? new File(args[0]) : patha;
					}
				}
				LOG.warning(randomASkr() + "存在传入参数丶将使用目标目录※" + patha + "丶端口号※" + porta + " 开启服务");
			} else {
				LOG.warning(randomASkr() + "没有传入参数丶使用默认端口号和共享目录路径开启服务");
			}
			if (!patha.exists() || !patha.isDirectory()) {
				LOG.warning(randomASkr() + "无法打开共享目录※" + patha.getAbsolutePath());
				return;
			}
			Lsf app = new Lsf(porta, patha.toURI());
			app.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			long start = System.currentTimeMillis();
			Thread.sleep(100L);
			while (!app.wasStarted()) {
				Thread.sleep(100L);
				if (System.currentTimeMillis() - start > 2000) {
					LOG.warning(randomASkr() + "无法启动共享服务器");
				}
			}
		} catch (Exception e) {
			LOG.warning(randomASkr() + "服务器启动异常※" + e.getMessage());
		}
	}

	public HTTPSession createSession(TempFileManager tempFileManager, InputStream inputStream,
			OutputStream outputStream) {
		return new HTTPSession(tempFileManager, inputStream, outputStream);
	}

	public HTTPSession createSession(TempFileManager tempFileManager, InputStream inputStream,
			OutputStream outputStream, InetAddress inetAddress) {
		return new HTTPSession(tempFileManager, inputStream, outputStream, inetAddress);
	}

	@Override
	public Response serve(IHTTPSession session) {
		if (session.getUri().contains("/favicon.ico")) {
			return null;
		}
		StringBuffer msg = new StringBuffer();// 新的返回信息
		Map<String, String> parms = session.getParms();
		LOG.info(randomASkr() + "method※ " + session.getMethod() + "|uri※ " + session.getUri() + "|parms※ " + parms);
		try {
			File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
			if (session.getMethod() == Method.GET) { // 处理GET请求
				if (parms.containsKey(DELETE)) { // 处理删除文件操作；
					return deal_delFile(session, parms, msg);
				} else if (parms.containsKey(GETMD5)) { // 处理获取md5操作；
					return deal_getMD5(session, parms, msg);
				} else if (parms.containsKey(DOCMD)) { // 处理创建文件夹操作；
					return deal_docmd(session, parms, msg);
				} else if (parms.containsKey(MKDIR)) { // 处理创建文件夹操作；
					return deal_makeDir(session, parms, msg);
				} else if (parms.containsKey(IFDEL)) {
					return deal_ifdel(session, parms, msg);
				} else {
					if (reqFile.exists() && reqFile.isFile() && !FileUtils.isSymlink(reqFile)) {
						return render200(session, reqFile);
					} else if (reqFile.exists() && reqFile.isDirectory()) {
						list_directory(reqFile, msg);
						return render200(session, msg.toString());
					} else if (reqFile.exists() && FileUtils.isSymlink(reqFile)) {
						list_directory(reqFile, msg);
						return render200(session, msg.toString());
					} else {
						LOG.info(randomASkr() + "目录不存在丶返回首页※" + reqFile.getAbsolutePath());
						list_directory(baseDir, msg);
						return render200(session, msg.toString());
					}
				}
			} else if (session.getMethod() == Method.POST && NanoFileUpload.isMultipartContent(session)) { // 处理POST请求
				List<FileItem> parseRequest = nanoFileUploader.parseRequest(session);
				FileItem fileItem = parseRequest.get(0);
				String fname = fileItem.getName();
				if ("".equals(fname)) {
					LOG.info(randomASkr() + "上传文件失败※" + session.getUri());
					list_directory(reqFile, msg);
					return render200(session, msg.toString());
				} else {
					InputStream is = fileItem.getInputStream();
					File tgFile = new File(FilenameUtils.normalize(reqFile.getAbsolutePath() + sepa + fname));
					while (tgFile.exists()) {
						tgFile = new File(FilenameUtils
								.normalize(reqFile.getAbsolutePath() + "" + sepa + "_" + tgFile.getName()));
					}
					LOG.info(randomASkr() + "上传文件※" + tgFile.getName());
					FileOutputStream os = FileUtils.openOutputStream(tgFile, false);
					Streams.copy(is, os, true);
					is.close();
					os.flush();
					os.close();
					LOG.info(randomASkr() + "上传文件执行结果※" + tgFile.exists());
					list_directory(reqFile, msg);
					return render200(session, msg.toString());
				}
			}
		} catch (FileNotFoundException e) {
			LOG.info(randomASkr() + "文件未找到异常※" + e.getMessage());
		} catch (URISyntaxException e) {
			LOG.info(randomASkr() + "URI语法异常※" + e.getMessage());
		} catch (IOException e) {
			LOG.info(randomASkr() + "IO异常※" + e.getMessage());
		} catch (FileUploadException e) {
			LOG.info(randomASkr() + "文件上传异常※" + e.getMessage());
		} catch (ResponseException e) {
			LOG.info(randomASkr() + "响应异常※" + e.getMessage());
		}
		return render404();
	}

	private Response deal_ifdel(IHTTPSession session, Map<String, String> parms, StringBuffer msg)
			throws URISyntaxException, IOException {
		File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
		isDelFlag = !isDelFlag;
		LOG.info(randomASkr() + "设置删除功能※" + isDelFlag);
		list_directory(reqFile, msg);
		return render200(session, msg.toString());
	}

	public void do_buildJS(StringBuffer msg) {
		msg.append("<script type=\"text/javascript\">");
		msg.append("function callBackMD5(murl,domId){\r\n"
				+ "http.get({url:murl,timeout:100000},function(err,result){document.getElementById(domId).innerHTML=result;});\r\n"
				+ "}");
		msg.append("var http = {};\n");
		msg.append("http.quest = function (option, callback) {\n");
		msg.append("    var url = option.url;\n");
		msg.append("    var method = option.method;\n");
		msg.append("    var data = option.data;\n");
		msg.append("    var timeout = option.timeout || 0;\n");
		msg.append("    var xhr = new XMLHttpRequest();\n");
		msg.append("    (timeout > 0) && (xhr.timeout = timeout);\n");
		msg.append("    xhr.onreadystatechange = function () {\n");
		msg.append("        if (xhr.readyState == 4) {\n");
		msg.append("            if (xhr.status >= 200 && xhr.status < 400) {\n");
		msg.append("            var result = xhr.responseText;\n");
		msg.append("            try {result = JSON.parse(xhr.responseText);} catch (e) {}\n");
		msg.append("                callback && callback(null, result);\n");
		msg.append("            } else {\n");
		msg.append("                callback && callback('status: ' + xhr.status);\n");
		msg.append("            }\n");
		msg.append("        }\n");
		msg.append("    }.bind(this);\n");
		msg.append("    xhr.open(method, url, true);\n");
		msg.append("    if(typeof data === 'object'){\n");
		msg.append("        try{\n");
		msg.append("            data = JSON.stringify(data);\n");
		msg.append("        }catch(e){}\n");
		msg.append("    }\n");
		msg.append("    xhr.send(data);\n");
		msg.append("    xhr.ontimeout = function () {\n");
		msg.append("        callback && callback('timeout');\n");
		msg.append(
				"        console.log('%c连%c接%c超%c时', 'color:red', 'color:orange', 'color:purple', 'color:green');\n");
		msg.append("    };\n");
		msg.append("};\n");
		msg.append("http.get = function (url, callback) {\n");
		msg.append("    var option = url.url ? url : { url: url };\n");
		msg.append("    option.method = 'get';\n");
		msg.append("    this.quest(option, callback);\n");
		msg.append("};\n");
		msg.append("http.post = function (option, callback) {\n");
		msg.append("    option.method = 'post';\n");
		msg.append("    this.quest(option, callback);\n");
		msg.append("};\n");
		msg.append("</script>");
	}

	public void do_buildHeadMessage(StringBuffer msg, String title) {
		msg.append(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html>\n<head><STYLE><!--H1 {font-family: Tahoma, Arial, sans-serif;color: white;background-color: #525D76;font-size: 22px;}H2 {font-family: Tahoma, Arial, sans-serif;color: white;background-color: #525D76;font-size: 16px;}H3 {font-family: Tahoma, Arial, sans-serif;color: white;background-color: #525D76;font-size: 14px;}BODY {font-family: Tahoma, Arial, sans-serif;color: black;background-color: white;}B {font-family: Tahoma, Arial, sans-serif;color: white;background-color: #525D76;}P {font-family: Tahoma, Arial, sans-serif;background: white;color: black;font-size: 12px;}A {color: black;}A.name {color: black;}HR {color: #525D76;}--></STYLE>");
		msg.append("<title>" + title + "-" + buibui + "</title>\n");
		msg.append("<link rel=\"shortcut icon\" href=\"favicon.ico\" type=\"image/x-icon\">");
		do_buildJS(msg);
		msg.append("</head>\n");
	}

	private Response deal_delFile(IHTTPSession session, Map<String, String> parms, StringBuffer msg)
			throws URISyntaxException, IOException {
		String delFileName = parms.get(DELETE);
		File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
		File tgFile = new File(FilenameUtils.normalize(reqFile.getAbsolutePath() + sepa + delFileName));
		LOG.info(randomASkr() + "删除文件※" + tgFile.getName());
		LOG.info(randomASkr() + "删除功能状态※" + isDelFlag);
		if (isDelFlag) {
			LOG.info(randomASkr() + "删除文件执行结果※" + FileUtils.deleteQuietly(tgFile));
			list_directory(reqFile, msg);
			return render200(session, msg.toString());
		} else {
			LOG.info(randomASkr() + "删除文件执行结果※false");
			list_directory(reqFile, msg);
			return render200(session, msg.toString());
		}
	}

	private Response deal_getMD5(IHTTPSession session, Map<String, String> parms, StringBuffer msg)
			throws URISyntaxException, IOException {
		String delFileName = parms.get(GETMD5);
		File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
		File tgFile = new File(FilenameUtils.normalize(reqFile.getAbsolutePath() + sepa + delFileName));
		LOG.info(randomASkr() + "获取MD5※" + tgFile.getName());
		FileInputStream is = FileUtils.openInputStream(tgFile);
		String md5Hex = DigestUtils.md5Hex(is);
		is.close();
		return render200(session, "" + md5Hex);
	}

	private Response deal_docmd(IHTTPSession session, Map<String, String> parms, StringBuffer msg) throws IOException {
		List<String> cmds = new ArrayList<String>();
		cmds.add("sh");
		cmds.add("-c");
		String cmdstr = parms.get(DOCMD);
		cmds.add(cmdstr);
		String cddir = "".equals(parms.get(CDDIR))
				? FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri())
				: parms.get(CDDIR);
		File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
		LOG.info(randomASkr() + "执行命令※" + cmdstr + ":" + cddir);
		if (!"".equals(cmdstr)) {
			try {
				Result r = ProcessUtils.run(new File(cddir), cmds);
				if (r.code == 0) {
					LOG.info(randomASkr() + "执行命令成功※" + cmdstr);
					msg.append("</form></td>");
					msg.append("<td><form name=\"zxcmdform\" method=\"get\">");
					msg.append("<input type=\"text\" name=\"" + CDDIR + "\" value=\"" + cddir + "\" />");
					msg.append("<input type=\"text\" name=\"" + DOCMD + "\" value=\"" + cmdstr + "\" />");
					msg.append("<input type=\"submit\" name=\"btn_zxcmd\" value=\"执行CMD\" />  ");
					msg.append("</form></td>");
					return render200(session, msg.toString() + "执行返回code※" + r.code + "</br>"
							+ r.data.replaceAll(System.lineSeparator(), "</br>"));
				} else {
					list_directory(reqFile, msg);
					msg.append("</form></td>");
					msg.append("<td><form name=\"zxcmdform\" method=\"get\">");
					msg.append("<input type=\"text\" name=\"" + CDDIR + "\" value=\"" + cddir + "\" />");
					msg.append("<input type=\"text\" name=\"" + DOCMD + "\" value=\"" + cmdstr + "\" />");
					msg.append("<input type=\"submit\" name=\"btn_zxcmd\" value=\"执行CMD\" />  ");
					msg.append("</form></td>");
					return render200(session, msg.toString() + "执行命令失败code※" + r.code + "</br>"
							+ r.data.replaceAll(System.lineSeparator(), "</br>"));
				}
			} catch (Exception e) {
				list_directory(reqFile, msg);
				return render200(session, msg.toString());
			}
		} else {
			list_directory(reqFile, msg);
			return render200(session, msg.toString());
		}

	}

	private Response deal_makeDir(IHTTPSession session, Map<String, String> parms, StringBuffer msg)
			throws URISyntaxException, IOException, ResponseException {
		String dirName = parms.get(MKDIR);
		File reqFile = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + session.getUri()));
		File tgDir = new File(FilenameUtils.normalize(reqFile.getAbsolutePath() + sepa + dirName));
		if (dirName == null || "".equals(dirName)) {
			LOG.info(randomASkr() + "创建目录丶参数为空※" + tgDir.getName());
			LOG.info(randomASkr() + "创建目录执行结果※false");
			list_directory(reqFile, msg);
			return render200(session, msg.toString());
		} else if (tgDir.exists() || tgDir.mkdirs()) {
			LOG.info(randomASkr() + "创建目录※" + tgDir.getName());
			LOG.info(randomASkr() + "创建目录执行结果※" + tgDir.exists());
			list_directory(reqFile, msg);
			return render200(session, msg.toString());
		} else {
			LOG.info(randomASkr() + "创建目录执行结果※" + tgDir.exists());
			return render500("创建目录失败");
		}
	}

	private void list_directory(File showDir, StringBuffer msg) throws IOException {
		File[] flist = showDir.listFiles();
		do_buildHeadMessage(msg, "文件列表 " + showDir.getCanonicalPath());
		msg.append("<body>\n<h1>文件列表 " + showDir.getCanonicalPath() + "</h1>\n");
		msg.append("<HR size=\"1\" noshade=\"noshade\">\n");
		msg.append("<table><tr><td>");
		msg.append("<input name=\"bt_fh\" type=\"button\" value=\"返回\" onClick=\"javascript:history.back();\"></td>");
		msg.append("<td><form ENCTYPE=\"multipart/form-data\" name=\"fileul\" method=\"post\">");
		msg.append("<input name=\"file\" type=\"file\"/>");
		msg.append("<input name=\"btn_sc\" type=\"submit\" value=\"上传\"/>");
		msg.append("</form></td>");
		msg.append("<td><form name=\"xjwjj\" method=\"get\">");
		msg.append("<input type=\"text\" name=\"" + MKDIR + "\" />");
		msg.append("<input type=\"submit\" name=\"btn_xjwjj\" value=\"新建文件夹\" />  ");
		msg.append("</form></td>");
		msg.append("<td><form name=\"szifdel\" method=\"get\">");
		msg.append("<input type=\"hidden\" name=\"" + IFDEL + "\" value=\"" + randomASkr() + "\" ></input>");
		String isDelstr = (isDelFlag) ? "关闭删除" + "</button><font color=\"RED\">删除功能已开启，请谨慎操作！！！</font>"
				: "开启删除" + "</button>";
		msg.append("<button name=\"btn_kgdel\" type=\"submit\" >" + isDelstr + "</button>");
		msg.append("</form></td>");
		msg.append("<td><input name=\"fhsy\" type=\"button\" value=\"返回首页\" onClick=\"location='/'\"></td>");
		msg.append("</tr></table>");
		msg.append("<HR size=\"1\" noshade=\"noshade\">");
		msg.append(
				"<table border=0 width=\"100%\" cellspacing=\"0\" cellpadding=\"5\" align=\"center\" style=\"overflow: scroll;word-break: keep-all\">"
						+ "<tr bgcolor=\"#00DB00\">" + "<td width=\"5%\">序号</td>" + "<td width=\"30%\">文件名</td>"
						+ "<td width=\"15%\">文件大小</td>" + "<td  width=\"20%\">文件创建时间</td>"
						+ "<td align=\"center\">MD5</td>" + "<td width=\"10%\">操作</td>" + "</tr>");
		int idn = 1;// 序号
		if (flist != null && flist.length > 0) {
			for (File af : flist) {
				String colorName;
				String linkName;
				String name = af.getName();
				colorName = linkName = name;
				String fsize = "";
				String flastmodft = "";
				if (idn % 2 == 0) {
					msg.append("<tr>");
				} else {
					msg.append("<tr bgcolor=\"#eeeeee\">");
				}
				// # Note: a link to a directory displays with @ and links with /
				String emd5 = "<a id='a_" + idn + "' onclick=\"callBackMD5('?" + GETMD5 + "=" + linkName + "','a_" + idn
						+ "')\">MD5</a>";
				if (af.isFile()) {
					fsize = getFormatSize(FileUtils.sizeOf(af));
					DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
					BasicFileAttributes attributes = Files.readAttributes(af.toPath(), BasicFileAttributes.class);
					LocalDateTime fileCreationTime = LocalDateTime.ofInstant(attributes.creationTime().toInstant(),
							ZoneId.systemDefault());
					LocalDateTime fileLastModifiedTime = LocalDateTime
							.ofInstant(attributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());
					flastmodft = "" + dateTimeFormatter.format(fileCreationTime);
				}
				if (af.isDirectory()) {
					colorName = "<span style=\"background-color: #CEFFCE;\">" + name + "/</span>";
					linkName = name + "/";
					emd5 = "";
				} else if (FileUtils.isSymlink(af)) {
					colorName = "<span>" + name + "@</span>";
					linkName = name + "/";
					emd5 = "";
				}
				String is_a;
				if (isDelFlag) {
					is_a = "<a style=\"background-color: #CEFFCE;\" href=\"?delete=" + linkName + "\">";
				} else {
					is_a = "<a>";
				}
				msg.append("<td>" + idn + "</td>" + "<td><a href=\"" + linkName + "\">" + colorName + "</a></td>"
						+ "<td>" + fsize + "</td>" + "<td>" + flastmodft + "</td>" + "<td align=\"center\">" + emd5
						+ "</td>" + "<td>" + is_a + "删除</a></td>" + "</tr>\n");
				idn++;
			}
		}
		msg.append("</table>\n<HR size=\"1\" noshade=\"noshade\">\n<span onClick=\"location='/?" + DOCMD + "=ls&&"
				+ CDDIR + "='\" ><h2>Powered By kanbuxiaqu@outlook.com</h2></span>\n</body>\n</html>\n");
	}

	public static String getFormatSize(long size) {
		long kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte";
		}
		long megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Long.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
		}
		long gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Long.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
		}
		long teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Long.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
	}

	private Response render200(IHTTPSession session, File reqFile) throws FileNotFoundException {
		return NanoHTTPD.newFixedLengthResponse(Status.OK, NanoHTTPD.getMimeTypeForFile(session.getUri()),
				new FileInputStream(reqFile), reqFile.length());
	}

	private Response render200(IHTTPSession session, String htmlmsg) {
		LOG.info(randomASkr() + "200");
		return NanoHTTPD.newFixedLengthResponse(Status.OK, NanoHTTPD.MIME_HTML, htmlmsg);
	}

	private Response render404() {
		try {
			LOG.warning(randomASkr() + "404");
			File file = new File(FilenameUtils.normalize(baseDir.getCanonicalPath() + "404.html"));
			if (file.exists()) {
				return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML,
						new FileInputStream(file), file.length());
			} else {
				return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, NanoHTTPD.MIME_HTML,
						"404");
			}
		} catch (Exception e) {
			return render500("系统内部异常※" + e.getMessage());
		}
	}

	private Response render301(String uri) {
		Response res = newFixedLengthResponse(Status.REDIRECT, NanoHTTPD.MIME_HTML, "301");
		res.addHeader("Content-Type", "text/html; charset=utf-8");
		res.addHeader("Location", uri);
		LOG.warning(randomASkr() + "301" + uri);
		return res;
	}

	private Response render500(String errmsg) {
		LOG.warning(randomASkr() + "500" + errmsg);
		return NanoHTTPD.newFixedLengthResponse(Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, errmsg);
	}
}