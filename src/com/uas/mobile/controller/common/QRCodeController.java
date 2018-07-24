package com.uas.mobile.controller.common;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;


/**
 * 二维码的生成与解析
 * @author suntg
 * @date 2014年12月8日20:03:42
 *
 */
@Controller("mobileQRCodeController")
public class QRCodeController {
	
	private static final int QUIET_ZONE_SIZE = 1;
	/**
	 * 把字符串信息转换成二维码图片
	 * @param request
	 * @param response
	 * @param code 需要转换的信息字符串
	 * @return 生成图片字节流，通过请求输出
	 */
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@RequestMapping("/mobile/qr/encode.action")
	@ResponseBody
	public void encode2QRCode(HttpServletRequest request, HttpServletResponse response,	String code) {
		OutputStream os = null;
		try {
			code = new String(code.getBytes("utf-8"),"iso-8859-1");
			os = response.getOutputStream();
			response.setCharacterEncoding("utf-8");
		    response.setContentType("image/jpeg");
		    Hashtable hints = new Hashtable();
	        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // 指定纠错等级 
		    BitMatrix matrix = encode(code, BarcodeFormat.QR_CODE,200, 200, hints);
		    BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
			ImageIO.write(image, "png", os);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 解析二维码
	 * @param request
	 * @param response
	 * @param session
	 * @param emcode 员工编号
	 * @return
	 */
	@RequestMapping("/mobile/qr/decode.action")
	@ResponseBody
	public Map<String, Object> decodeFromQRCode(HttpServletRequest request, HttpServletResponse response,
			String code) {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("result","");
		return modelMap;
	}
	
	public BitMatrix encode(String contents, BarcodeFormat format, int width, int height)
			throws WriterException {
		return encode(contents, format, width, height, null);
	}

	public BitMatrix encode(String contents, BarcodeFormat format, int width, int height,
			Map<EncodeHintType,?> hints) throws WriterException {
	    if (contents.isEmpty()) {
	    	throw new IllegalArgumentException("Found empty contents");
	    }

	    if (format != BarcodeFormat.QR_CODE) {
	    	throw new IllegalArgumentException("Can only encode QR_CODE, but got " + format);
	    }

	    if (width < 0 || height < 0) {
	    	throw new IllegalArgumentException("Requested dimensions are too small: " + width + 'x' +height);
	    }

	    ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
	    int quietZone = QUIET_ZONE_SIZE;
	    if (hints != null) {
	    	ErrorCorrectionLevel requestedECLevel = (ErrorCorrectionLevel) hints.get(EncodeHintType.ERROR_CORRECTION);
	    	if (requestedECLevel != null) {
	    		errorCorrectionLevel = requestedECLevel;
	    	}
	    	Integer quietZoneInt = (Integer) hints.get(EncodeHintType.MARGIN);
	    	if (quietZoneInt != null) {
	    		quietZone = quietZoneInt;
	    	}
	    }

	    QRCode code = Encoder.encode(contents, errorCorrectionLevel, hints);
	    return renderResult(code, width, height, quietZone);
	}

	// Note that the input matrix uses 0 == white, 1 == black, while the output matrix uses
	// 0 == black, 255 == white (i.e. an 8 bit greyscale bitmap).
	private static BitMatrix renderResult(QRCode code, int width, int height, int quietZone) {
	    ByteMatrix input = code.getMatrix();
	    if (input == null) {
	      throw new IllegalStateException();
	    }
	    int inputWidth = input.getWidth();
	    int inputHeight = input.getHeight();
	    int qrWidth = inputWidth + (quietZone * 2);
	    int qrHeight = inputHeight + (quietZone * 2);
	    int outputWidth = Math.max(width, qrWidth);
	    int outputHeight = Math.max(height, qrHeight);
	
	    int multiple = Math.min(outputWidth / qrWidth, outputHeight / qrHeight);
	    // Padding includes both the quiet zone and the extra white pixels to accommodate the requested
	    // dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
	    // If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
	    // handle all the padding from 100x100 (the actual QR) up to 200x160.
	    int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
	    int topPadding = (outputHeight - (inputHeight * multiple)) / 2;
	
	    BitMatrix output = new BitMatrix(outputWidth, outputHeight);
	
	    for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
	      // Write the contents of this row of the barcode
	      for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
	        if (input.get(inputX, inputY) == 1) {
	          output.setRegion(outputX, outputY, multiple, multiple);
	        }
	      }
	    }
	    return output;
	}

}
