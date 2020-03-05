package com.ityongman.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @Author shedunze
 * @Date 2020-03-04 11:24
 * @Description server端 http处理类
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private String WEB_ROOT = "webroot" ;
    // 根路径位置
    private URL baseUrl = HttpServerHandler.class.getResource("");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        String uri = req.getUri() ; // req.uri()

        //1. 创建文件
        RandomAccessFile file = null ;
        try {
            String page = uri.equals("/") ? "chat.html" : uri;
            file = new RandomAccessFile(getResource(page), "r");
        } catch (Exception e) {
            ctx.fireChannelRead(req.retain());
            return ;
        }

        //2. 创建响应信息
        HttpResponse response = new DefaultHttpResponse(req.protocolVersion(), HttpResponseStatus.OK) ;
        String contextType = "text/html;" ;
        if (uri.endsWith(".css")) {
            contextType = "text/css;" ;
        } else if (uri.endsWith(".js")) {
            contextType = "text/javascript;" ;
        } else if(uri.toLowerCase().matches(".*\\.(jif|jpg|png)$")) {// 以图片格式结尾
            String picType = uri.substring(uri.lastIndexOf(".")) ;
            contextType = "image/" + picType +";";
        }

        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8");
        boolean keepAlive = HttpHeaders.isKeepAlive(req);
        if(keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length()) ;
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length())) ;

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE) ;
        }

        file.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(ctx.channel().remoteAddress() + " 发生了异常");

        cause.printStackTrace();
        ctx.close();
    }

    private File getResource(String fileName) throws URISyntaxException {
        String basePath = baseUrl.toURI().toString();
        int start = basePath.indexOf("classes/");
        // 去掉转意符
        basePath = (basePath.substring(0, start) + "/classes/").replaceAll("/+", "/") ;

        String path = basePath + WEB_ROOT + "/" + fileName ;
        path = path.contains("file:") ? path.substring(5) : path ;
        path.replaceAll("/+", "/") ;

        return new File(path) ;
    }
}
